/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PayloadPacket
 *  pl.hackshield.auth.loader.HackShieldLoader
 *  pl.hackshield.auth.spigot.common.SpigotCommon
 *  pl.hackshield.auth.spigot.common.protocol.ChannelInjector
 *  pl.hackshield.auth.spigot.common.protocol.HackShieldConnection
 */
package pl.hackshield.auth.impl.spigot.v1_8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PayloadPacket;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.WrappedMinecraftConnection;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.WrappedPacketBuffer;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.WrappedPlayerConnection;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.packet.WrappedPacketHandshakingInSetProtocol;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.protocol.netty.WrappedMinecraftDecoder;
import pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.protocol.netty.WrappedMinecraftEncoder;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.ChannelInjector;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public final class Implementation {
    private HackShieldPlugin hackShieldPlugin;
    private SpigotCommon spigotCommon;
    private ChannelInjector channelInjector;

    public void init(HackShieldPlugin hackShieldPlugin, HackShieldLoader plugin) {
        this.hackShieldPlugin = hackShieldPlugin;
        this.spigotCommon = SpigotCommon.getInstance();
        this.hackShieldPlugin.setByteBufConsumer(WrappedPacketBuffer::new);
        this.hackShieldPlugin.setPayloadPacketWrapper(this.createPayloadWrapper());
        this.spigotCommon.setPlayerConnection(WrappedPlayerConnection::new);
        try {
            this.channelInjector = new ChannelInjector(this.spigotCommon, "net.minecraft.server.v1_8_R3.MinecraftServer", "q", "h", "g", this.createInjectionChannelSupplier());
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Consumer<Channel> createInjectionChannelSupplier() {
        return channel -> {
            ChannelPipeline pipeline = channel.pipeline();
            HackShieldConnection connection = new HackShieldConnection(this.spigotCommon, pipeline, WrappedMinecraftConnection::new, WrappedMinecraftDecoder::new, WrappedMinecraftEncoder::new, WrappedPacketHandshakingInSetProtocol::new);
            connection.inject(pipeline, false);
            pipeline.addBefore("packet_handler", "hackshield-handler", (ChannelHandler)connection);
        };
    }

    private Function<PayloadPacket, Object> createPayloadWrapper() {
        return packet -> {
            ByteBuf byteBuf = Unpooled.buffer();
            IPacketBuffer buffer = this.hackShieldPlugin.getByteBufWrapper(byteBuf);
            packet.write(buffer);
            return new PacketPlayOutCustomPayload(packet.getChannel(), new WrappedPacketBuffer(byteBuf).getSerializer());
        };
    }

    public Logger getLogger() {
        return this.hackShieldPlugin.getLogger();
    }
}

