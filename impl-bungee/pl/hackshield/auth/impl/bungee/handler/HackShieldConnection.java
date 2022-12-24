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
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.netty.HandlerBoss
 *  net.md_5.bungee.protocol.DefinedPacket
 *  net.md_5.bungee.protocol.PacketWrapper
 *  net.md_5.bungee.protocol.packet.Handshake
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnknownNullability
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.network.IHackShieldConnection
 *  pl.hackshield.auth.common.network.PacketHandler
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.bungee.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.Handshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IHackShieldConnection;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.connection.MinecraftConnection;
import pl.hackshield.auth.impl.bungee.handler.HackShieldDecoder;
import pl.hackshield.auth.impl.bungee.handler.HackShieldEncoder;
import pl.hackshield.auth.impl.bungee.packet.HackShieldPacketHandler;
import pl.hackshield.auth.impl.bungee.wrapper.connection.WrappedMinecraftConnection;
import pl.hackshield.auth.impl.bungee.wrapper.connection.WrappedServerConnection;

public final class HackShieldConnection
extends ChannelDuplexHandler
implements IHackShieldConnection {
    public static final String BUNGEE_DECODER = "packet-decoder";
    public static final String BUNGEE_ENCODER = "packet-encoder";
    public static final String BUNGEE_HANDLER = "inbound-boss";
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
    private Consumer<BaseComponent[]> disconnector;

    public HackShieldConnection(@NotNull Implementation plugin, ChannelPipeline pipeline, boolean serverInitializer) {
        this.plugin = plugin;
        this.serverInitializer = serverInitializer;
        this.decoder = new HackShieldDecoder(this);
        this.encoder = new HackShieldEncoder(this);
        this.packetHandler = new HackShieldPacketHandler(this);
        if (serverInitializer) {
            this.user = new CommonHackShieldUser((IHackShieldConnection)this);
            this.connection = Optional.ofNullable(pipeline.get(BUNGEE_HANDLER)).map(handler -> (HandlerBoss)handler).map(WrappedMinecraftConnection::new).orElseThrow(() -> new IllegalStateException("Not found MinecraftConnection handler!"));
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

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefinedPacket packet;
        if (msg instanceof HackShieldPacket) {
            ((HackShieldPacket)msg).handlePacket(this.packetHandler);
            return;
        }
        if (!this.serverInitializer && msg instanceof PacketWrapper && (packet = ((PacketWrapper)msg).packet) != null && packet.getClass().getSimpleName().equals("LoginSuccess")) {
            this.inject(ctx.pipeline(), true);
        }
        super.channelRead(ctx, msg);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String className = msg.getClass().getSimpleName();
        if (this.serverInitializer && className.equals("LoginSuccess")) {
            this.inject(ctx.pipeline(), true);
        } else if (!this.serverInitializer && msg instanceof Handshake) {
            Handshake handshake = (Handshake)msg;
            handshake.setHost(handshake.getHost() + "\u0000HS");
            this.setConnection(ctx.pipeline());
            WrappedServerConnection playerConnection = new WrappedServerConnection((HandlerBoss)ctx.pipeline().get(HandlerBoss.class));
            HackShieldConnection hackShieldConnection = (HackShieldConnection)playerConnection.getUserConnection().getCh().getHandle().pipeline().get(HACKSHIELD_HANDLER);
            this.user = hackShieldConnection.getUser();
        }
        super.write(ctx, msg, promise);
    }

    public void inject(ChannelPipeline pipeline, boolean reload) {
        if (reload) {
            pipeline.remove((ChannelHandler)this.decoder);
            pipeline.remove((ChannelHandler)this.encoder);
        }
        pipeline.addBefore(BUNGEE_DECODER, HACKSHIELD_DECODER, (ChannelHandler)this.decoder);
        pipeline.addBefore(BUNGEE_ENCODER, HACKSHIELD_ENCODER, (ChannelHandler)this.encoder);
    }

    public void setDisconnector(@NotNull Consumer<BaseComponent[]> disconnector) {
        this.disconnector = Objects.requireNonNull(disconnector, "Disconnector cannot be null!");
    }

    public void closeWith(ChannelHandlerContext ctx, BaseComponent[] component) {
        if (this.disconnector == null) {
            ctx.close();
            return;
        }
        this.disconnector.accept(component);
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
        this.connection = Optional.ofNullable(pipeline.get(BUNGEE_HANDLER)).map(handler -> (HandlerBoss)handler).map(WrappedMinecraftConnection::new).orElseThrow(() -> new IllegalStateException("Not found MinecraftConnection handler!"));
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

