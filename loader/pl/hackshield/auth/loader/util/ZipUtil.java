/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ZipUtil {
    private static final Map<String, String> PROPERTIES = new HashMap<String, String>();

    private static URI getJarURI(File jarFile) {
        return ZipUtil.getJarURI(jarFile, null);
    }

    private static URI getJarURI(File jarFile, String suffix) {
        return URI.create("jar:file:/" + jarFile.getAbsolutePath().replace(" ", "%20").replace("\\", "/") + (suffix != null ? suffix : ""));
    }

    public static byte[] removeFileFromZip(File zipFile, String fileName) throws IOException {
        byte[] content;
        URI uri = ZipUtil.getJarURI(zipFile);
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, PROPERTIES);){
            Path tokenPath = zipfs.getPath(fileName, new String[0]);
            content = Files.readAllBytes(tokenPath);
            Files.delete(tokenPath);
        }
        return content;
    }

    public static void insertFileInfoZip(File zipFile, String fileName, byte[] content) throws IOException {
        URI uri1 = ZipUtil.getJarURI(zipFile);
        try (FileSystem zipfs = FileSystems.newFileSystem(uri1, PROPERTIES);){
            Path tokenPath = Paths.get(ZipUtil.getJarURI(zipFile, "!/" + fileName));
            Files.copy(new ByteArrayInputStream(content), tokenPath, new CopyOption[0]);
        }
    }

    static {
        PROPERTIES.put("create", "false");
    }
}

