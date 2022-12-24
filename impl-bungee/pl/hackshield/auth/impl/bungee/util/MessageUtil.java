/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.md_5.bungee.api.chat.BaseComponent
 */
package pl.hackshield.auth.impl.bungee.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public final class MessageUtil {
    private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.get();

    public static BaseComponent[] serialize(Component component) {
        return SERIALIZER.serialize(component);
    }
}

