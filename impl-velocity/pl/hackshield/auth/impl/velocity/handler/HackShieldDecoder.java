/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.protocol.ProtocolUtils
 *  com.velocitypowered.proxy.protocol.VelocityConnectionEvent
 *  com.velocitypowered.proxy.protocol.netty.MinecraftDecoder
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PacketFlow
 */
package pl.hackshield.auth.impl.velocity.handler;

import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.VelocityConnectionEvent;
import com.velocitypowered.proxy.protocol.netty.MinecraftDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.WrappedPacketBuffer;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.netty.WrappedMinecraftDecoder;

@ChannelHandler.Sharable
public final class HackShieldDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private HackShieldConnection connection;
    private PacketFlow direction;
    private int lastPacketNr;

    HackShieldDecoder(HackShieldConnection connection) {
        this.connection = connection;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.direction = Optional.ofNullable(ctx.pipeline().get("minecraft-decoder")).map(handler -> (MinecraftDecoder)handler).map(WrappedMinecraftDecoder::new).map(WrappedMinecraftDecoder::getDirection).orElseThrow(() -> new IOException("Not found MinecraftDecoder handler!"));
        super.handlerAdded(ctx);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        ConnectionProtocol connectionProtocol = this.connection.getConnectionState();
        if (this.connection.isServerInitializer() && this.connection.getUser().isHandshakeStateAuthorized()) {
            if (!this.handlePacketNr(ctx, connectionProtocol, byteBuf)) {
                return;
            }
            if (!this.handleHackShieldPacket(ctx, connectionProtocol, byteBuf)) {
                return;
            }
        } else if (!this.connection.isServerInitializer() && this.connection.hasUser() && connectionProtocol != ConnectionProtocol.PLAY && !this.handleHackShieldPacket(ctx, connectionProtocol, byteBuf)) {
            return;
        }
        out.add((Object)byteBuf.retain());
    }

    private boolean handleHackShieldPacket(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, ByteBuf byteBuf) {
        HackShieldPacket hackShieldPacket;
        if (connectionProtocol == ConnectionProtocol.UNKNOWN) {
            return true;
        }
        int readerIndex = byteBuf.readerIndex();
        int packetId = ProtocolUtils.readVarInt((ByteBuf)byteBuf);
        if (packetId < 0 && (hackShieldPacket = connectionProtocol.createPacket(this.direction, packetId, (IPacketBuffer)new WrappedPacketBuffer(byteBuf))) != null) {
            ctx.fireChannelRead((Object)hackShieldPacket);
            return false;
        }
        byteBuf.readerIndex(readerIndex);
        return true;
    }

    private boolean handlePacketNr(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, ByteBuf byteBuf) {
        if (!this.connection.getUser().isAuthorized() || connectionProtocol != ConnectionProtocol.PLAY) {
            return true;
        }
        int packetNr = ProtocolUtils.readVarInt((ByteBuf)byteBuf);
        if (packetNr != this.lastPacketNr + 1) {
            this.connection.closeWith(ctx, Component.text((String)"Clumsy?", (TextColor)NamedTextColor.RED));
            return false;
        }
        this.lastPacketNr = packetNr;
        return true;
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event != VelocityConnectionEvent.COMPRESSION_ENABLED) {
            super.userEventTriggered(ctx, event);
            return;
        }
        this.connection.inject(ctx.pipeline(), true);
        super.userEventTriggered(ctx, event);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.connection.cleanup();
        this.connection = null;
        super.channelInactive(ctx);
    }
}

