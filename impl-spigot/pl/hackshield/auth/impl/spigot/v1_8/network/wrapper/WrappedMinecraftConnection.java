/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  net.minecraft.server.v1_8_R3.EnumProtocol
 *  net.minecraft.server.v1_8_R3.NetworkManager
 *  net.minecraft.server.v1_8_R3.PlayerConnection
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.connection.MinecraftConnection
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.connection.MinecraftConnection;

public final class WrappedMinecraftConnection
implements MinecraftConnection {
    private final NetworkManager connection;

    public WrappedMinecraftConnection(ChannelHandler connection) {
        this.connection = (NetworkManager)connection;
    }

    public ConnectionProtocol getState() {
        switch ((EnumProtocol)this.connection.channel.attr(NetworkManager.c).get()) {
            case HANDSHAKING: {
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

    public Channel getChannel() {
        return this.connection.channel;
    }

    public void disconnect(String msg) {
        ((PlayerConnection)this.connection.getPacketListener()).disconnect(msg);
    }
}

