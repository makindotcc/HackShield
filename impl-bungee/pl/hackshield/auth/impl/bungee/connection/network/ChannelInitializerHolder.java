/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 */
package pl.hackshield.auth.impl.bungee.connection.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.util.function.Supplier;

public interface ChannelInitializerHolder
extends Supplier<ChannelInitializer<Channel>> {
    public void set(ChannelInitializer<Channel> var1);
}

