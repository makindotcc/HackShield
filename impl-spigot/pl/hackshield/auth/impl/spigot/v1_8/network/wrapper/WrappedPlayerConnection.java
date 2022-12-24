/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  net.minecraft.server.v1_8_R3.PlayerConnection
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  pl.hackshield.auth.spigot.common.wrapper.PlayerConnection
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.hackshield.auth.spigot.common.wrapper.PlayerConnection;

public class WrappedPlayerConnection
implements PlayerConnection {
    private final net.minecraft.server.v1_8_R3.PlayerConnection playerConnection;

    public WrappedPlayerConnection(Player player) {
        this.playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
    }

    public Channel getChannel() {
        return this.playerConnection.networkManager.channel;
    }
}

