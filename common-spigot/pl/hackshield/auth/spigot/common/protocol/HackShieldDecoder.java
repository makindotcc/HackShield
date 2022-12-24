/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
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
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder
 */
package pl.hackshield.auth.spigot.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

@ChannelHandler.Sharable
public final class HackShieldDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private final Function<ChannelHandler, IMinecraftDecoder> wrappedDecoder;
    private HackShieldConnection connection;
    private PacketFlow direction;
    private int lastPacketNr;

    HackShieldDecoder(HackShieldConnection connection, Function<ChannelHandler, IMinecraftDecoder> wrappedDecoder) {
        this.connection = connection;
        this.wrappedDecoder = wrappedDecoder;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.direction = Optional.ofNullable(ctx.pipeline().get("decoder")).map(this.wrappedDecoder).map(IMinecraftDecoder::getDirection).orElseThrow(() -> new IOException("Not found MinecraftDecoder handler!"));
        super.handlerAdded(ctx);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        if (this.connection.getUser().isHandshakeStateAuthorized()) {
            IPacketBuffer wrappedByteBuf;
            ConnectionProtocol connectionProtocol = this.connection.getConnectionState();
            if (!this.handlePacketNr(ctx, connectionProtocol, wrappedByteBuf = this.connection.getPlugin().getByteBufWrapper(byteBuf))) {
                return;
            }
            if (!this.handleHackShieldPacket(ctx, connectionProtocol, wrappedByteBuf)) {
                return;
            }
        }
        out.add((Object)byteBuf.retain());
    }

    private boolean handleHackShieldPacket(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, IPacketBuffer byteBuf) {
        HackShieldPacket hackShieldPacket;
        if (connectionProtocol == ConnectionProtocol.UNKNOWN) {
            return true;
        }
        int readerIndex = byteBuf.getBuffer().readerIndex();
        int packetId = byteBuf.readVarInt();
        if (packetId < 0 && (hackShieldPacket = connectionProtocol.createPacket(this.direction, packetId, byteBuf)) != null) {
            ctx.fireChannelRead((Object)hackShieldPacket);
            return false;
        }
        byteBuf.getBuffer().readerIndex(readerIndex);
        return true;
    }

    private boolean handlePacketNr(ChannelHandlerContext ctx, ConnectionProtocol connectionProtocol, IPacketBuffer byteBuf) {
        if (this.connection.getPlugin().isSlave() || !this.connection.getUser().isAuthorized() || connectionProtocol != ConnectionProtocol.PLAY) {
            return true;
        }
        int packetNr = byteBuf.readVarInt();
        if (packetNr != this.lastPacketNr + 1) {
            System.out.println("clumsy");
            this.connection.closeWith(ctx, Component.text((String)"Clumsy?", (TextColor)NamedTextColor.RED));
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

