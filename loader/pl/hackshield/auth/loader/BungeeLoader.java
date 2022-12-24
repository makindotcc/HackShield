/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.BungeeCord
 *  net.md_5.bungee.api.plugin.Plugin
 */
package pl.hackshield.auth.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.PluginLoader;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.implementation.ImplementationUtil;

public class BungeeLoader
extends Plugin
implements HackShieldLoader {
    private static BungeeLoader instance;

    public void onEnable() {
        instance = this;
        String accessToken = this.getAccessToken();
        if (Objects.isNull(accessToken)) {
            this.getLogger().info("Could not load access token! Disabling server...");
            BungeeCord.getInstance().stop();
            return;
        }
        ImplementationData implementationData = ImplementationUtil.getPluginImplementation();
        if (implementationData == null) {
            this.getLogger().warning("Not found implementation! Disabling server...");
            BungeeCord.getInstance().stop();
            return;
        }
        PluginLoader loader = new PluginLoader(this, implementationData, accessToken);
        loader.load();
    }

    private String getAccessToken() {
        String string;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getResourceAsStream("serverToken")));
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
        BungeeCord.getInstance().stop();
    }

    public static BungeeLoader getInstance() {
        return instance;
    }
}

