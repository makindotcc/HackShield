/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.impl.velocity.connection.network;

import pl.hackshield.auth.impl.velocity.connection.network.ChannelInitializerHolder;

public interface ConnectionManager {
    public ChannelInitializerHolder getServerChannelInitializer();

    public ChannelInitializerHolder getBackendChannelInitializer();
}

