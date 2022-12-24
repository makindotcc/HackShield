/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.connection.MinecraftConnection
 *  com.velocitypowered.proxy.connection.backend.VelocityServerConnection
 *  com.velocitypowered.proxy.connection.client.ConnectedPlayer
 *  com.velocitypowered.proxy.protocol.packet.Handshake
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelPromise
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnknownNullability
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IHackShieldConnection
 *  pl.hackshield.auth.common.network.PacketHandler
 *  pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.velocity.handler;

import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.Handshake;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IHackShieldConnection;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.velocity.Implementation;
import pl.hackshield.auth.impl.velocity.connection.MinecraftConnection;
import pl.hackshield.auth.impl.velocity.handler.HackShieldDecoder;
import pl.hackshield.auth.impl.velocity.handler.HackShieldEncoder;
import pl.hackshield.auth.impl.velocity.packet.HackShieldPacketHandler;
import pl.hackshield.auth.impl.velocity.wrapper.connection.WrappedMinecraftConnection;

public final class HackShieldConnection
extends ChannelDuplexHandler
implements IHackShieldConnection {
    public static final String VELOCITY_DECODER = "minecraft-decoder";
    public static final String VELOCITY_ENCODER = "minecraft-encoder";
    public static final String VELOCITY_HANDLER = "handler";
    public static final String HACKSHIELD_ENCODER = "hackshield-encoder";
    public static final String HACKSHIELD_DECODER = "hackshield-decoder";
    public static final String HACKSHIELD_HANDLER = "hackshield-handler";
    @NotNull
    private final Implementation plugin;
    private final boolean serverInitializer;
    private @UnknownNullability PacketHandler packetHandler;
    private @UnknownNullability HackShieldDecoder decoder;
    private @UnknownNullability HackShieldEncoder encoder;
    private @UnknownNullability CommonHackShieldUser user;
    private @UnknownNullability MinecraftConnection connection;
    @Nullable
    private Consumer<Component> disconnector;

    HackShieldConnection(@NotNull Implementation plugin, ChannelPipeline pipeline, boolean serverInitializer) {
        this.plugin = plugin;
        this.serverInitializer = serverInitializer;
        this.decoder = new HackShieldDecoder(this);
        this.encoder = new HackShieldEncoder(this);
        this.packetHandler = new HackShieldPacketHandler(this);
        if (serverInitializer) {
            this.user = new CommonHackShieldUser((IHackShieldConnection)this);
            this.connection = Optional.ofNullable(pipeline.get(VELOCITY_HANDLER)).map(handler -> (com.velocitypowered.proxy.connection.MinecraftConnection)handler).map(WrappedMinecraftConnection::new).orElseThrow(() -> new IllegalStateException("Not found MinecraftConnection handler!"));
        }
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
        } else if (msg instanceof Handshake && this.serverInitializer) {
            Handshake handshake = (Handshake)msg;
            this.packetHandler.handlePacket(new HandshakePacket(handshake.getProtocolVersion().getProtocol(), handshake.getServerAddress(), handshake.getPort(), handshake.getNextStatus()));
        }
        super.channelRead(ctx, msg);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!this.serverInitializer && msg instanceof Handshake) {
            Handshake handshake = (Handshake)msg;
            handshake.setServerAddress(handshake.getServerAddress() + "\u0000HS");
            this.setConnection(ctx.pipeline());
            ConnectedPlayer player = ((VelocityServerConnection)this.connection.getAssociation()).getPlayer();
            HackShieldConnection hackShieldConnection = (HackShieldConnection)player.getConnection().getChannel().pipeline().get(HACKSHIELD_HANDLER);
            this.user = hackShieldConnection.getUser();
        }
        super.write(ctx, msg, promise);
    }

    void inject(ChannelPipeline pipeline, boolean reload) {
        if (reload) {
            pipeline.remove((ChannelHandler)this.decoder);
            pipeline.remove((ChannelHandler)this.encoder);
        }
        pipeline.addBefore(VELOCITY_DECODER, HACKSHIELD_DECODER, (ChannelHandler)this.decoder);
        pipeline.addBefore(VELOCITY_ENCODER, HACKSHIELD_ENCODER, (ChannelHandler)this.encoder);
    }

    public void setDisconnector(@NotNull Consumer<Component> disconnector) {
        this.disconnector = Objects.requireNonNull(disconnector, "Disconnector cannot be null!");
    }

    public void closeWith(ChannelHandlerContext ctx, TextComponent component) {
        if (this.disconnector == null) {
            ctx.close();
            return;
        }
        this.disconnector.accept((Component)component);
    }

    @Nullable(value="in pre-login state")
    public @Nullable(value="in pre-login state") MinecraftConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public ConnectionProtocol getConnectionState() {
        return this.connection != null ? this.connection.getState() : ConnectionProtocol.UNKNOWN;
    }

    public void setConnection(@NotNull ChannelPipeline pipeline) {
        this.connection = Optional.ofNullable(pipeline.get(VELOCITY_HANDLER)).map(handler -> (com.velocitypowered.proxy.connection.MinecraftConnection)handler).map(WrappedMinecraftConnection::new).orElseThrow(() -> new IllegalStateException("Not found MinecraftConnection handler!"));
    }

    public boolean hasUser() {
        return this.user != null;
    }

    @NotNull
    public CommonHackShieldUser getUser() {
        return Objects.requireNonNull(this.user, "HackShieldUser cannot be null!");
    }

    @NotNull
    public Implementation getPlugin() {
        return this.plugin;
    }

    public boolean isServerInitializer() {
        return this.serverInitializer;
    }

    public Channel getChannel() {
        return this.connection.getChannel();
    }
}

