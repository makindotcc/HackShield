/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package pl.hackshield.auth.spigot.common.utils;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static String fixColor(String text) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)text);
    }
}

