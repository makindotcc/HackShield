/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.connection.MinecraftConnection
 *  com.velocitypowered.proxy.connection.MinecraftConnectionAssociation
 *  io.netty.channel.Channel
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 */
package pl.hackshield.auth.impl.velocity.wrapper.connection;

import com.velocitypowered.proxy.connection.MinecraftConnectionAssociation;
import io.netty.channel.Channel;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.impl.velocity.connection.MinecraftConnection;

public final class WrappedMinecraftConnection
implements MinecraftConnection {
    private final com.velocitypowered.proxy.connection.MinecraftConnection connection;

    public WrappedMinecraftConnection(com.velocitypowered.proxy.connection.MinecraftConnection connection) {
        this.connection = connection;
    }

    @Override
    public ConnectionProtocol getState() {
        switch (this.connection.getState()) {
            case HANDSHAKE: {
                return ConnectionProtocol.HANDSHAKING;
            }
            case STATUS: {
                return ConnectionProtocol.STATUS;
            }
            case PLAY: {
                return ConnectionProtocol.PLAY;
            }
            case LOGIN: {
                return ConnectionProtocol.LOGIN;
            }
        }
        return ConnectionProtocol.UNKNOWN;
    }

    @Override
    public Channel getChannel() {
        return this.connection.getChannel();
    }

    @Override
    public MinecraftConnectionAssociation getAssociation() {
        return this.connection.getAssociation();
    }
}

