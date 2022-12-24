/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.bungee.wrapper.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.bungee.connection.network.ChannelInitializerHolder;

final class WrappedChannelInitializerHolder
implements ChannelInitializerHolder {
    private final Object instance;
    private final ReflectionUtil.FieldAccessor<Object, ChannelInitializer<Channel>> initializerField;

    WrappedChannelInitializerHolder(Object instance) {
        this.instance = instance;
        this.initializerField = ReflectionUtil.findField(instance.getClass(), (String)"initializer");
    }

    @Override
    public void set(ChannelInitializer<Channel> initializer) {
        this.initializerField.set(this.instance, initializer);
    }

    @Override
    public ChannelInitializer<Channel> get() {
        return (ChannelInitializer)this.initializerField.get(this.instance);
    }
}

