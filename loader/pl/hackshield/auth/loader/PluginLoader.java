/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader;

import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EnvironmentChannel;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.module.ModuleInfo;
import pl.hackshield.auth.loader.module.ModuleLoader;
import pl.hackshield.auth.loader.update.DownloaderService;

public class PluginLoader {
    private final HackShieldLoader loader;
    private final ImplementationData implementationData;
    private final EndpointManager endpointManager;
    private final DownloaderService updater;
    private final ModuleLoader moduleLoader;

    public PluginLoader(HackShieldLoader loader, ImplementationData implementationData, String accessToken) {
        this.loader = loader;
        this.implementationData = implementationData;
        this.endpointManager = new EndpointManager(EnvironmentChannel.TEST, accessToken, false);
        this.updater = new DownloaderService(loader, this);
        this.moduleLoader = new ModuleLoader(this.updater.getModulesDirectory());
    }

    public void load() {
        boolean needRestart;
        this.loader.getLogger().info("HSMODE: " + System.getProperty("HSMODE", ""));
        if (!System.getProperty("HSMODE", "").equals("local") && (needRestart = this.updater.download())) {
            return;
        }
        this.loadCommonModule(this.loader.getPluginClassLoader());
    }

    private void loadCommonModule(ClassLoader classLoader) {
        this.moduleLoader.loadModule(classLoader, new ModuleInfo("common.jar", "pl.hackshield.auth.common.HackShieldPlugin", "init", new Class[]{HackShieldLoader.class, EndpointManager.class, ModuleLoader.class, ImplementationData.class}, new Object[]{this.loader, this.endpointManager, this.moduleLoader, this.implementationData}));
    }

    public DownloaderService getUpdater() {
        return this.updater;
    }

    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    public ImplementationData getImplementationData() {
        return this.implementationData;
    }
}

