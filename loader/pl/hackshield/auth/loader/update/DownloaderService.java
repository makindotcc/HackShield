/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package pl.hackshield.auth.loader.update;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.PluginLoader;
import pl.hackshield.auth.loader.endpoint.endpoints.Updater;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.update.Versions;
import pl.hackshield.auth.loader.util.HashUtil;
import pl.hackshield.auth.loader.util.JsonUtil;
import pl.hackshield.auth.loader.util.RuntimeUtil;
import pl.hackshield.auth.loader.util.ZipUtil;

public final class DownloaderService {
    private final HackShieldLoader plugin;
    private final PluginLoader loader;
    private final File libsDirectory;
    private final File modulesDirectory;
    private String mirrorURL;

    public DownloaderService(HackShieldLoader plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.libsDirectory = new File(plugin.getPluginDirectory(), "libs");
        this.modulesDirectory = new File(plugin.getPluginDirectory(), "modules");
        this.init();
        this.pickMirror();
    }

    public File getModulesDirectory() {
        return this.modulesDirectory;
    }

    public void init() {
        if (!this.modulesDirectory.exists()) {
            this.modulesDirectory.mkdirs();
        }
        if (!this.libsDirectory.exists()) {
            this.libsDirectory.mkdirs();
        }
    }

    public boolean download() {
        List<String> modules = DownloaderService.getModules(this.loader.getImplementationData());
        try {
            this.plugin.getLogger().info("Modules " + String.join((CharSequence)",", modules));
            return this.start(modules);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public void pickMirror() {
        Updater.IndexMirrorsResponse response;
        try {
            response = this.loader.getEndpointManager().getUpdater().indexMirrors();
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't index mirrors!", e);
            return;
        }
        String[] mirrorURLs = response.getData().getMirrorUrls();
        if (mirrorURLs.length == 0) {
            this.plugin.getLogger().severe("Not found any suitable mirrors!");
            return;
        }
        this.mirrorURL = mirrorURLs[ThreadLocalRandom.current().nextInt(mirrorURLs.length)];
        this.plugin.getLogger().info("Picked mirror: " + this.mirrorURL);
    }

    private static List<String> getModules(ImplementationData implementationData) {
        ArrayList modules = Lists.newArrayList();
        modules.add("common");
        if (implementationData.getPackageName().startsWith("spigot")) {
            modules.add("common-spigot");
        }
        modules.add("impl-" + implementationData.getFileName());
        return modules;
    }

    private static byte[] readAll(InputStream inputStream2) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        while (true) {
            int length;
            if ((length = inputStream2.read(bytes)) < 0) {
                inputStream2.close();
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bytes, 0, length);
        }
    }

    public boolean start(List<String> modules) {
        AtomicBoolean needRestart = new AtomicBoolean(false);
        this.download(this.mirrorURL + "/plugin/versions.json", bytes -> {
            Versions versions;
            try {
                versions = (Versions)JsonUtil.fromJson(new String(DownloaderService.readAll(bytes), StandardCharsets.UTF_8), Versions.class);
            }
            catch (IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, "Couldn't check versions!", e);
                return;
            }
            try {
                if (this.updatedLoader(versions.getModules().get("loader"))) {
                    needRestart.set(true);
                    this.plugin.getLogger().warning("------------------------------------------------------------");
                    this.plugin.getLogger().warning("-                     STOPPING SERVER!                     -");
                    this.plugin.getLogger().warning("- Downloaded new version of loader. Please restart server! -");
                    this.plugin.getLogger().warning("------------------------------------------------------------");
                    this.plugin.stopServer();
                    return;
                }
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to update the loader!", e);
            }
            this.downloadSectionFiles("modules", this.modulesDirectory, modules, versions.getModules());
        });
        return needRestart.get();
    }

    private boolean updatedLoader(Versions.FileInfo info) throws IOException, URISyntaxException {
        File loaderFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        File temp = new File(this.modulesDirectory, info.getFileName() + ".temp");
        boolean downloadedNewerVersion = this.downloadFile("loader", info, this.modulesDirectory, "modules", loaderFile, false);
        if (!downloadedNewerVersion) {
            return false;
        }
        ZipUtil.insertFileInfoZip(temp, "serverToken", this.loader.getEndpointManager().getServerSecret().getBytes(StandardCharsets.UTF_8));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                RuntimeUtil.closeClassLoader(this.loader.getImplementationData(), this.plugin.getPluginClassLoader(), loaderFile);
                Files.copy(temp.toPath(), loaderFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException | IllegalAccessException | NoSuchFieldException e) {
                System.err.println("Cant replace loader jar!");
                e.printStackTrace();
            }
        }));
        return true;
    }

    private boolean needUpdate(File file, Versions.FileInfo info) {
        if (!file.exists()) {
            return true;
        }
        if (info.getFileName().equals("loader.jar")) {
            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[0]);
                if (attr.lastModifiedTime().toMillis() < info.getDate()) {
                    return true;
                }
            }
            catch (IOException e) {
                System.out.println("Error while reading file attributes " + e.getMessage());
            }
            return false;
        }
        try {
            String hash = HashUtil.sha1Hex(Files.readAllBytes(Paths.get(file.getAbsolutePath(), new String[0])));
            return !info.getHash().equals(hash);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void downloadSectionFiles(String sectionName, File targetDirectory, List<String> elementFilter, Map<String, Versions.FileInfo> files) {
        files.entrySet().stream().filter(fi -> elementFilter != null && elementFilter.contains(fi.getKey())).forEach(fi -> {
            Versions.FileInfo info = (Versions.FileInfo)fi.getValue();
            this.downloadFile((String)fi.getKey(), info, targetDirectory, sectionName, new File(targetDirectory, info.getFileName()), true);
        });
    }

    private boolean downloadFile(String name, Versions.FileInfo info, File targetDirectory, String sectionName, File targetFile, boolean replace) {
        if (info.getHash() == null || info.getHash().isEmpty() || info.getFileName() == null || info.getFileName().isEmpty()) {
            this.plugin.getLogger().info("DB " + info.getHash() + " " + info.getFileName());
            return false;
        }
        if (!this.needUpdate(targetFile, info)) {
            this.plugin.getLogger().info("Module '" + name + "' is up to date.");
            return false;
        }
        this.plugin.getLogger().info("Downloading '" + name + "'...");
        this.download(this.mirrorURL + "/plugin/" + sectionName + "/" + info.getFileName(), bytes -> {
            try {
                File temp = new File(targetDirectory, info.getFileName() + ".temp");
                Files.copy(bytes, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.plugin.getLogger().info("Downloaded '" + info.getFileName() + "'. Hash: " + HashUtil.sha1Hex(Files.readAllBytes(temp.toPath())));
                if (replace) {
                    Files.move(temp.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, "Couldn't download '" + info.getFileName() + "'!", e);
            }
        });
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void download(String url, Consumer<InputStream> consumer) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "HackShield-Auth-Plugin");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode / 100 != 2) {
                String errorMessage = "unknown cause";
                if (httpURLConnection.getErrorStream() != null) {
                    errorMessage = new String(DownloaderService.readAll(httpURLConnection.getErrorStream()), StandardCharsets.UTF_8);
                }
                throw new IOException(url + " returned status code " + responseCode + ": " + errorMessage);
            }
            consumer.accept(httpURLConnection.getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}

