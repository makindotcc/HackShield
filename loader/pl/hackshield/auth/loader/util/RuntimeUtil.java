/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.plugin.PluginClassLoader
 *  org.bukkit.plugin.java.PluginClassLoader
 */
package pl.hackshield.auth.loader.util;

import com.velocitypowered.proxy.plugin.PluginClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Set;
import pl.hackshield.auth.loader.implementation.ImplementationData;

public class RuntimeUtil {
    public static void closeClassLoader(ImplementationData implementationData, ClassLoader classLoader, File jarFile) throws NoSuchFieldException, IllegalAccessException, IOException {
        switch (implementationData.getType()) {
            case VELOCITY: {
                RuntimeUtil.closeVelocityClassLoader(jarFile);
                break;
            }
            case BUNGEE: {
                RuntimeUtil.closeBungeeClassLoader(classLoader);
                break;
            }
            case SPIGOT: {
                RuntimeUtil.closeSpigotClassLoader(classLoader);
            }
        }
    }

    private static void closeBungeeClassLoader(ClassLoader classLoader) throws IOException {
        if (!(classLoader instanceof URLClassLoader)) {
            System.out.println("No URLClassLoader!");
            return;
        }
        ((URLClassLoader)classLoader).close();
    }

    private static void closeSpigotClassLoader(ClassLoader classLoader) throws IOException {
        if (!(classLoader instanceof org.bukkit.plugin.java.PluginClassLoader)) {
            System.out.println("No URLClassLoader!");
            return;
        }
        ((org.bukkit.plugin.java.PluginClassLoader)classLoader).close();
    }

    private static void closeVelocityClassLoader(File jarFile) throws NoSuchFieldException, IllegalAccessException {
        Field loaders = PluginClassLoader.class.getDeclaredField("loaders");
        loaders.setAccessible(true);
        ((Set)loaders.get(null)).stream().filter(p -> RuntimeUtil.normalizePath(p.getURLs()[0].getPath()).equals(RuntimeUtil.normalizePath(jarFile.getPath()))).forEach(p -> {
            try {
                System.out.println("Closed class loader for " + RuntimeUtil.normalizePath(p.getURLs()[0].getPath()));
                p.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static String normalizePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.replace("%20", " ").replace("\\", "/");
    }
}

