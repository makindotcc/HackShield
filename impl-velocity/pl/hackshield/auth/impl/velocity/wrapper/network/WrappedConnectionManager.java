/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.proxy.ProxyServer
 *  com.velocitypowered.proxy.network.ConnectionManager
 *  pl.hackshield.auth.common.util.ReflectionUtil
 */
package pl.hackshield.auth.impl.velocity.wrapper.network;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.velocity.connection.network.ChannelInitializerHolder;
import pl.hackshield.auth.impl.velocity.connection.network.ConnectionManager;
import pl.hackshield.auth.impl.velocity.wrapper.network.WrappedChannelInitializerHolder;

public final class WrappedConnectionManager
implements ConnectionManager {
    private final com.velocitypowered.proxy.network.ConnectionManager instance;

    public WrappedConnectionManager(ProxyServer server) {
        this.instance = (com.velocitypowered.proxy.network.ConnectionManager)ReflectionUtil.findField(server.getClass(), (String)"cm").get((Object)server);
    }

    @Override
    public ChannelInitializerHolder getServerChannelInitializer() {
        return new WrappedChannelInitializerHolder((Object)this.instance.getServerChannelInitializer());
    }

    @Override
    public ChannelInitializerHolder getBackendChannelInitializer() {
        return new WrappedChannelInitializerHolder((Object)this.instance.getBackendChannelInitializer());
    }
}

