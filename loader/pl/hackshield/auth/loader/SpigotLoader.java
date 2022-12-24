/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.java.JavaPlugin
 */
package pl.hackshield.auth.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.PluginLoader;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.implementation.ImplementationUtil;

public class SpigotLoader
extends JavaPlugin
implements HackShieldLoader {
    private static SpigotLoader instance;

    public void onEnable() {
        instance = this;
        String accessToken = this.getAccessToken();
        if (Objects.isNull(accessToken)) {
            this.getLogger().warning("Could not load access token! Disabling server...");
            Bukkit.shutdown();
            return;
        }
        ImplementationData implementationData = ImplementationUtil.getPluginImplementation();
        if (implementationData == null) {
            this.getLogger().warning("Not found implementation! Disabling server...");
            Bukkit.shutdown();
            return;
        }
        PluginLoader loader = new PluginLoader(this, implementationData, accessToken);
        loader.load();
    }

    private String getAccessToken() {
        String string;
        InputStream serverTokenStream = this.getResource("serverToken");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(serverTokenStream));
        try {
            string = bufferedReader.readLine();
        }
        catch (Throwable throwable) {
            try {
                try {
                    bufferedReader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        bufferedReader.close();
        return string;
    }

    @Override
    public File getPluginDirectory() {
        return this.getDataFolder();
    }

    @Override
    public void stopServer() {
        Bukkit.shutdown();
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return this.getClassLoader();
    }

    public static SpigotLoader getInstance() {
        return instance;
    }
}

