/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.implementation;

import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.implementation.ImplementationType;

public class ImplementationUtil {
    public static ImplementationData getPluginImplementation() {
        try {
            Class.forName("net.md_5.bungee.BungeeCord");
            return new ImplementationData(ImplementationType.BUNGEE, "bungee", "bungee");
        }
        catch (Exception exception) {
            try {
                Class.forName("com.velocitypowered.proxy.VelocityServer");
                return new ImplementationData(ImplementationType.VELOCITY, "velocity", "velocity");
            }
            catch (Exception exception2) {
                try {
                    Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                    Object server = bukkitClass.getDeclaredMethod("getServer", new Class[0]).invoke(null, new Object[0]);
                    String version = server.getClass().getName().split("\\.")[3];
                    return new ImplementationData(ImplementationType.SPIGOT, "spigot-" + version.substring(1, version.lastIndexOf("_")).replace("_", "."), "spigot." + version.substring(0, version.lastIndexOf("_")));
                }
                catch (Exception exception3) {
                    return null;
                }
            }
        }
    }
}

