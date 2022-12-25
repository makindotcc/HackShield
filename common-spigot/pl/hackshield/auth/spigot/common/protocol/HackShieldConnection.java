/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelPromise
 *  net.kyori.adventure.text.TextComponent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnknownNullability
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IHackShieldConnection
 *  pl.hackshield.auth.common.network.PacketHandler
 *  pl.hackshield.auth.common.network.connection.MinecraftConnection
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder
 *  pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket
 *  pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 *  pl.hackshield.auth.common.util.LegacyComponentConverter
 */
package pl.hackshield.auth.spigot.common.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IHackShieldConnection;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.network.connection.MinecraftConnection;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder;
import pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.common.util.LegacyComponentConverter;
import pl.hackshield.auth.spigot.common.HackShieldPacketHandler;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldDecoder;
import pl.hackshield.auth.spigot.common.protocol.HackShieldEncoder;
import pl.hackshield.auth.spigot.common.wrapper.IPacketHandshakingInSetProtocol;

public final class HackShieldConnection
extends ChannelDuplexHandler
implements IHackShieldConnection {
    public static final String MINECRAFT_DECODER = "decoder";
    public static final String MINECRAFT_ENCODER = "encoder";
    public static final String PACKET_HANDLER = "packet_handler";
    public static final String HACKSHIELD_ENCODER = "hackshield-encoder";
    public static final String HACKSHIELD_DECODER = "hackshield-decoder";
    public static final String HACKSHIELD_HANDLER = "hackshield-handler";
    private @UnknownNullability PacketHandler packetHandler;
    private @UnknownNullability HackShieldDecoder decoder;
    private @UnknownNullability HackShieldEncoder encoder;
    private @UnknownNullability CommonHackShieldUser user;
    private @UnknownNullability MinecraftConnection connection;
    private final @UnknownNullability Channel channel;
    @Nullable
    private Consumer<TextComponent> disconnector;
    private final SpigotCommon plugin;
    private boolean disconnected = false;
    private final Function<Object, IPacketHandshakingInSetProtocol> handshakeWrapper;

    public HackShieldConnection(SpigotCommon plugin, ChannelPipeline pipeline, Function<ChannelHandler, MinecraftConnection> wrappedNetworkManager, Function<ChannelHandler, IMinecraftDecoder> wrappedDecoder, Function<ChannelHandler, IMinecraftEncoder> wrappedEncoder, Function<Object, IPacketHandshakingInSetProtocol> handshakeWrapper) {
        this.plugin = plugin;
        this.channel = pipeline.channel();
        this.user = new CommonHackShieldUser((IHackShieldConnection)this);
        this.handshakeWrapper = handshakeWrapper;
        this.decoder = new HackShieldDecoder(this, wrappedDecoder);
        this.encoder = new HackShieldEncoder(this, wrappedEncoder);
        this.packetHandler = new HackShieldPacketHandler(plugin, this);
        this.connection = Optional.ofNullable(pipeline.get(PACKET_HANDLER)).map(wrappedNetworkManager).orElseThrow(() -> new IllegalStateException("Not found NetworkManager handler!"));
    }

    public void cleanup() {
        this.user = null;
        this.decoder = null;
        this.encoder = null;
        this.packetHandler = null;
        this.connection = null;
        this.disconnector = null;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HackShieldPacket) {
            ((HackShieldPacket)msg).handlePacket(this.packetHandler);
        } else {
            String className = msg.getClass().getSimpleName();
            if (className.equals("PacketHandshakingInSetProtocol")) {
                IPacketHandshakingInSetProtocol wrapped = Optional.ofNullable(msg).map(this.handshakeWrapper).orElseThrow(() -> new IllegalStateException("Cant wrap Handshake packet!"));
                HandshakePacket handshakePacket = new HandshakePacket(wrapped.getProtocolVersion(), wrapped.getServerAddress(), wrapped.getPort(), wrapped.getNextStatus());
                this.packetHandler.handlePacket(handshakePacket);
                if (!handshakePacket.getServerAddress().equals(wrapped.getServerAddress())) {
                    System.out.println("Applying changes...");
                    wrapped.setServerAddress(handshakePacket.getServerAddress());
                }
            } else if (className.equals("PacketLoginInStart") && this.plugin.isSlave()) {
                System.out.println("UserInformationRequestPacket...");
                Channel channel = ctx.channel();
                channel.writeAndFlush((Object)new TransferUserInformationRequestPacket(), channel.voidPromise());
            }
        }
        super.channelRead(ctx, msg);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String className = msg.getClass().getSimpleName();
        if (className.equals("PacketLoginOutSuccess")) {
            this.inject(ctx.pipeline(), true);
        } else if (className.equals("PacketPlayOutLogin")) {
            this.setDisconnector(component -> this.plugin.runSync(() -> Optional.ofNullable(this.connection).ifPresent(connection -> connection.disconnect(new LegacyComponentConverter().convertToLegacyText(component)))));
        }
        super.write(ctx, msg, promise);
    }

    public void inject(ChannelPipeline pipeline, boolean reload) {
        if (reload) {
            pipeline.remove((ChannelHandler)this.decoder);
            pipeline.remove((ChannelHandler)this.encoder);
        }
        pipeline.addBefore(MINECRAFT_DECODER, HACKSHIELD_DECODER, (ChannelHandler)this.decoder);
        pipeline.addBefore(MINECRAFT_ENCODER, HACKSHIELD_ENCODER, (ChannelHandler)this.encoder);
    }

    public void setDisconnector(@NotNull Consumer<TextComponent> disconnector) {
        this.disconnector = Objects.requireNonNull(disconnector, "Disconnector cannot be null!");
    }

    public void closeWith(ChannelHandlerContext ctx, TextComponent component) {
        if (this.disconnector == null) {
            ctx.close();
            return;
        }
        if (this.disconnected) {
            return;
        }
        this.disconnector.accept(component);
        this.disconnected = true;
    }

    public MinecraftConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public ConnectionProtocol getConnectionState() {
        return this.connection != null ? this.connection.getState() : ConnectionProtocol.UNKNOWN;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setUser(@NotNull CommonHackShieldUser user) {
        this.user = Objects.requireNonNull(user, "CommonHackShieldUser cannot be null!");
        user.setConnection((IHackShieldConnection)this);
    }

    @NotNull
    public CommonHackShieldUser getUser() {
        return Objects.requireNonNull(this.user, "HackShieldUser cannot be null!");
    }

    public SpigotCommon getPlugin() {
        return this.plugin;
    }
}

