/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.md_5.bungee.protocol.DefinedPacket
 *  net.md_5.bungee.protocol.MinecraftDecoder
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PacketFlow
 */
package pl.hackshield.auth.impl.bungee.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.MessageUtil;
import pl.hackshield.auth.impl.bungee.wrapper.protocol.WrappedPacketBuffer;
import pl.hackshield.auth.impl.bungee.wrapper.protocol.netty.WrappedMinecraftDecoder;

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
        this.direction = Optional.ofNullable(ctx.pipeline().get("packet-decoder")).map(handler -> (MinecraftDecoder)handler).map(WrappedMinecraftDecoder::new).map(WrappedMinecraftDecoder::getDirection).orElseThrow(() -> new IOException("Not found MinecraftDecoder handler!"));
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
        } else if (!this.connection.isServerInitializer() && this.connection.hasUser() && (connectionProtocol != ConnectionProtocol.PLAY ? !this.handleHackShieldPacket(ctx, connectionProtocol, byteBuf) : this.forwardHackShieldPacket(connectionProtocol, byteBuf))) {
            return;
        }
        out.add((Object)byteBuf.retain());
    }

    private boolean forwardHackShieldPacket(ConnectionProtocol connectionProtocol, ByteBuf byteBuf) {
        boolean forward;
        if (connectionProtocol == ConnectionProtocol.UNKNOWN) {
            return true;
        }
        int readerIndex = byteBuf.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)byteBuf);
        byteBuf.readerIndex(readerIndex);
        boolean bl = forward = packetId < 0 && connectionProtocol.registeredPacket(this.direction, packetId);
        if (forward) {
            Channel channel = this.connection.getUser().getConnection().getChannel();
            ByteBuf copy = byteBuf.copy();
            channel.writeAndFlush((Object)copy, channel.voidPromise());
        }
        return forward;
    }

    private boolean handleHackShieldPacket(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, ByteBuf byteBuf) {
        if (connectionProtocol == ConnectionProtocol.UNKNOWN) {
            return true;
        }
        int readerIndex = byteBuf.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)byteBuf);
        if (packetId < 0) {
            HackShieldPacket hackShieldPacket = connectionProtocol.createPacket(this.direction, packetId, (IPacketBuffer)new WrappedPacketBuffer(byteBuf));
            HackShieldPlugin.getInstance().getLogger().info("Handle hackshield packet, found? " + (hackShieldPacket != null));
            if (hackShieldPacket != null) {
                ctx.fireChannelRead((Object)hackShieldPacket);
                return false;
            }
        }
        byteBuf.readerIndex(readerIndex);
        return true;
    }

    private boolean handlePacketNr(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, ByteBuf byteBuf) {
        if (!this.connection.getUser().isAuthorized() || connectionProtocol != ConnectionProtocol.PLAY) {
            return true;
        }
        int packetNr = DefinedPacket.readVarInt((ByteBuf)byteBuf);
        if (packetNr != this.lastPacketNr + 1) {
            this.connection.closeWith(ctx, MessageUtil.serialize((Component)Component.text((String)"Clumsy?", (TextColor)NamedTextColor.RED)));
            return false;
        }
        this.lastPacketNr = packetNr;
        return true;
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.connection.cleanup();
        this.connection = null;
        super.channelInactive(ctx);
    }
}

