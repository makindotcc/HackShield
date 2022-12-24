/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  net.md_5.bungee.netty.ChannelWrapper
 *  net.md_5.bungee.netty.HandlerBoss
 *  net.md_5.bungee.netty.PacketHandler
 *  net.md_5.bungee.protocol.MinecraftDecoder
 *  net.md_5.bungee.protocol.Protocol
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.bungee.wrapper.connection;

import io.netty.channel.Channel;
import java.util.Optional;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.Protocol;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.bungee.connection.MinecraftConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;

public final class WrappedMinecraftConnection
implements MinecraftConnection {
    private static final ReflectionUtil.FieldAccessor<HandlerBoss, ChannelWrapper> CHANNEL_WRAPPER_FIELD = ReflectionUtil.findField(HandlerBoss.class, (String)"channel");
    private static final ReflectionUtil.FieldAccessor<ChannelWrapper, Channel> CHANNEL_FIELD = ReflectionUtil.findField(ChannelWrapper.class, (String)"ch");
    private static final ReflectionUtil.FieldAccessor<HandlerBoss, PacketHandler> PACKET_HANDLER_FIELD = ReflectionUtil.findField(HandlerBoss.class, (String)"handler");
    private final HandlerBoss connection;

    public WrappedMinecraftConnection(HandlerBoss connection) {
        this.connection = connection;
    }

    private ChannelWrapper getChannelWrapper() {
        return Optional.ofNullable((ChannelWrapper)CHANNEL_WRAPPER_FIELD.get((Object)this.connection)).orElse(null);
    }

    private Channel getWrappedChannel() {
        return Optional.ofNullable((Channel)CHANNEL_FIELD.get((Object)this.getChannelWrapper())).orElse(null);
    }

    private PacketHandler getPacketHandler() {
        return Optional.ofNullable((PacketHandler)PACKET_HANDLER_FIELD.get((Object)this.connection)).orElse(null);
    }

    @Override
    public ConnectionProtocol getState() {
        Protocol protocol;
        try {
            MinecraftDecoder minecraftDecoder = (MinecraftDecoder)this.getWrappedChannel().pipeline().get(MinecraftDecoder.class);
            protocol = (Protocol)ConnectionUtil.MINECRAFT_DECODER_PROTOCOL.get((Object)minecraftDecoder);
        }
        catch (IllegalAccessException e) {
            return ConnectionProtocol.UNKNOWN;
        }
        switch (protocol) {
            case HANDSHAKE: {
                return ConnectionProtocol.HANDSHAKING;
            }
            case STATUS: {
                return ConnectionProtocol.STATUS;
            }
            case GAME: {
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
        return this.getWrappedChannel();
    }
}

