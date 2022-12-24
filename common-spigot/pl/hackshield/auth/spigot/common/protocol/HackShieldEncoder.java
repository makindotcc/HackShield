/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToByteEncoder
 *  net.minecraft.server.v1_8_R3.PacketEncoder
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder
 */
package pl.hackshield.auth.spigot.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.server.v1_8_R3.PacketEncoder;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

@ChannelHandler.Sharable
public final class HackShieldEncoder
extends MessageToByteEncoder<HackShieldPacket> {
    private final Function<ChannelHandler, IMinecraftEncoder> wrappedEncoder;
    private final HackShieldConnection connection;
    private PacketFlow direction;

    HackShieldEncoder(HackShieldConnection connection, Function<ChannelHandler, IMinecraftEncoder> wrappedEncoder) {
        this.wrappedEncoder = wrappedEncoder;
        this.connection = connection;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.direction = Optional.ofNullable(ctx.pipeline().get("encoder")).map(handler -> (PacketEncoder)handler).map(this.wrappedEncoder).map(IMinecraftEncoder::getDirection).orElseThrow(() -> new IOException("Not found MinecraftEncoder handler!"));
        super.handlerAdded(ctx);
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, HackShieldPacket packet, ByteBuf out) {
        OptionalInt packetId = this.connection.getConnectionState().getPacketId(this.direction, packet.getClass());
        if (!packetId.isPresent()) {
            throw new EncoderException("Cannot serialize not registered packet! (" + packet.getClass().getSimpleName() + ")");
        }
        IPacketBuffer wrappedByteBuf = this.connection.getPlugin().getByteBufWrapper(out);
        wrappedByteBuf.writeVarInt(packetId.getAsInt());
        packet.write(wrappedByteBuf);
    }
}

