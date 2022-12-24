/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.protocol.ProtocolUtils
 *  com.velocitypowered.proxy.protocol.netty.MinecraftEncoder
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToByteEncoder
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PacketFlow
 */
package pl.hackshield.auth.impl.velocity.handler;

import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.netty.MinecraftEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.WrappedPacketBuffer;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.netty.WrappedMinecraftEncoder;

@ChannelHandler.Sharable
public final class HackShieldEncoder
extends MessageToByteEncoder<HackShieldPacket> {
    private final HackShieldConnection connection;
    private PacketFlow direction;

    HackShieldEncoder(HackShieldConnection connection) {
        this.connection = connection;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.direction = Optional.ofNullable(ctx.pipeline().get("minecraft-encoder")).map(handler -> (MinecraftEncoder)handler).map(WrappedMinecraftEncoder::new).map(WrappedMinecraftEncoder::getDirection).orElseThrow(() -> new IOException("Not found MinecraftEncoder handler!"));
        super.handlerAdded(ctx);
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, HackShieldPacket packet, ByteBuf out) {
        OptionalInt packetId = this.connection.getConnectionState().getPacketId(this.direction, packet.getClass());
        if (packetId.isEmpty()) {
            throw new EncoderException("Cannot serialize not registered packet! (" + packet.getClass().getSimpleName() + ")");
        }
        ProtocolUtils.writeVarInt((ByteBuf)out, (int)packetId.getAsInt());
        packet.write((IPacketBuffer)new WrappedPacketBuffer(out));
    }
}

