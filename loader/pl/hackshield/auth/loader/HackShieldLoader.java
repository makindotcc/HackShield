/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader;

import java.io.File;
import java.util.logging.Logger;

public interface HackShieldLoader {
    public File getPluginDirectory();

    public Logger getLogger();

    public void stopServer();

    default public ClassLoader getPluginClassLoader() {
        return this.getClass().getClassLoader();
    }
}

