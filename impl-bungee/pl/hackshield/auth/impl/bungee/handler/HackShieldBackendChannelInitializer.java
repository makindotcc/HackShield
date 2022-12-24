/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelPipeline
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$MethodInvoker
 */
package pl.hackshield.auth.impl.bungee.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;

public final class HackShieldBackendChannelInitializer
extends ChannelInitializer<Channel> {
    private final Implementation plugin;
    private final ReflectionUtil.MethodInvoker<ChannelInitializer<Channel>, Void> initChannelMethod;
    private final ChannelInitializer<Channel> originalInitializer;
    private final boolean serverInitializer;

    public HackShieldBackendChannelInitializer(Implementation plugin, ChannelInitializer<Channel> originalInitializer) {
        this.plugin = plugin;
        this.originalInitializer = originalInitializer;
        this.serverInitializer = originalInitializer.getClass().getName().equals("com.velocitypowered.proxy.network.ServerChannelInitializer");
        this.initChannelMethod = ReflectionUtil.findMethod(ChannelInitializer.class, (String)"initChannel", (Class[])new Class[]{Channel.class});
    }

    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        this.initChannelMethod.invoke(this.originalInitializer, new Object[]{channel});
        HackShieldConnection connection = new HackShieldConnection(this.plugin, pipeline, this.serverInitializer);
        connection.inject(pipeline, false);
        pipeline.addLast("hackshield-handler", (ChannelHandler)connection);
    }
}

