/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.proxy.ProxyInitializeEvent
 *  com.velocitypowered.api.plugin.Plugin
 *  com.velocitypowered.api.plugin.annotation.DataDirectory
 *  com.velocitypowered.api.proxy.ProxyServer
 */
package pl.hackshield.auth.loader;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.PluginLoader;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.implementation.ImplementationUtil;

@Plugin(id="hackshield_auth", name="HackShield Auth Plugin", version="1.0.0", authors={"HackShield"})
public final class VelocityLoader
implements HackShieldLoader {
    private static VelocityLoader instance;
    private final ProxyServer server;
    private final Logger logger;
    private final File dataFolder;

    @Inject
    public VelocityLoader(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        String accessToken = this.getAccessToken();
        if (Objects.isNull(accessToken)) {
            this.getLogger().info("Could not load access token! Disabling server...");
            this.server.shutdown();
            return;
        }
        ImplementationData implementationData = ImplementationUtil.getPluginImplementation();
        if (implementationData == null) {
            this.getLogger().warning("Not found implementation! Disabling server...");
            this.server.shutdown();
            return;
        }
        PluginLoader loader = new PluginLoader(this, implementationData, accessToken);
        loader.load();
    }

    private InputStream getResourceAsStream(String resourceName) {
        return this.getClass().getClassLoader().getResourceAsStream(resourceName);
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
                this.getLogger().log(Level.SEVERE, "Could not load access token! ", e);
                return null;
            }
        }
        bufferedReader.close();
        return string;
    }

    public static VelocityLoader getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return this.server;
    }

    @Override
    public File getPluginDirectory() {
        return this.dataFolder;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void stopServer() {
        this.server.shutdown();
    }
}

