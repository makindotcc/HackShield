/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package pl.hackshield.auth.common.network.connection;

import io.netty.channel.Channel;
import pl.hackshield.auth.common.network.ConnectionProtocol;

public interface MinecraftConnection {
    public ConnectionProtocol getState();

    public Channel getChannel();

    public void disconnect(String var1);
}

