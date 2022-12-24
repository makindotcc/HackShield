/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.impl.bungee.connection.network;

import pl.hackshield.auth.impl.bungee.connection.network.ChannelInitializerHolder;

public interface ConnectionManager {
    public ChannelInitializerHolder getServerChannelInitializer();

    public ChannelInitializerHolder getBackendChannelInitializer();
}

