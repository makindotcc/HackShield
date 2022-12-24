/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.connection.MinecraftConnectionAssociation
 *  io.netty.channel.Channel
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 */
package pl.hackshield.auth.impl.velocity.connection;

import com.velocitypowered.proxy.connection.MinecraftConnectionAssociation;
import io.netty.channel.Channel;
import pl.hackshield.auth.common.network.ConnectionProtocol;

public interface MinecraftConnection {
    public ConnectionProtocol getState();

    public Channel getChannel();

    public MinecraftConnectionAssociation getAssociation();
}

