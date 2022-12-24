/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  net.kyori.adventure.text.Component
 */
package pl.hackshield.auth.impl.velocity.connection.client;

import io.netty.channel.ChannelHandler;
import net.kyori.adventure.text.Component;
import pl.hackshield.auth.impl.velocity.connection.MinecraftConnection;
import pl.hackshield.auth.impl.velocity.connection.protocol.packet.Handshake;

public interface InitialInboundConnection {
    public <T extends ChannelHandler> T getPipelineHandler(String var1);

    public MinecraftConnection getConnection();

    public Handshake getHandshake();

    public void disconnect(Component var1);
}

