/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.proxy.ProxyServer
 *  com.velocitypowered.proxy.protocol.packet.PluginMessage
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.IImplementation
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PayloadPacket
 *  pl.hackshield.auth.loader.HackShieldLoader
 *  pl.hackshield.auth.loader.VelocityLoader
 */
package pl.hackshield.auth.impl.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.PluginMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.IImplementation;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PayloadPacket;
import pl.hackshield.auth.impl.velocity.APIImplementation;
import pl.hackshield.auth.impl.velocity.connection.network.ChannelInitializerHolder;
import pl.hackshield.auth.impl.velocity.handler.HackShieldBackendChannelInitializer;
import pl.hackshield.auth.impl.velocity.handler.HackShieldChannelInitializer;
import pl.hackshield.auth.impl.velocity.listener.GameProfileRequestListener;
import pl.hackshield.auth.impl.velocity.listener.PermissionsSetupListener;
import pl.hackshield.auth.impl.velocity.listener.PlayerConnectListener;
import pl.hackshield.auth.impl.velocity.listener.PreLoginListener;
import pl.hackshield.auth.impl.velocity.wrapper.network.WrappedConnectionManager;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.WrappedPacketBuffer;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.VelocityLoader;

public final class Implementation
implements IImplementation {
    private HackShieldPlugin hackShieldPlugin;
    private VelocityLoader plugin;
    private ProxyServer server;

    public void init(HackShieldPlugin hackShieldPlugin, HackShieldLoader pluginInstance) {
        this.hackShieldPlugin = hackShieldPlugin;
        this.server = ((VelocityLoader)pluginInstance).getServer();
        this.plugin = VelocityLoader.getInstance();
        this.onProxyInitialization();
    }

    public void onProxyInitialization() {
        this.replaceInitializers();
        this.registerListeners();
        this.hackShieldPlugin.setByteBufConsumer(WrappedPacketBuffer::new);
        this.hackShieldPlugin.setPayloadPacketWrapper(this.createPayloadWrapper());
        HackShield.setApi((HackShieldAPI)new APIImplementation(this));
        this.hackShieldPlugin.setImplementation((IImplementation)this);
    }

    private void registerListeners() {
        this.server.getEventManager().register((Object)this.plugin, (Object)new PreLoginListener(this));
        this.server.getEventManager().register((Object)this.plugin, (Object)new PermissionsSetupListener());
        this.server.getEventManager().register((Object)this.plugin, (Object)new GameProfileRequestListener(this));
        this.server.getEventManager().register((Object)this.plugin, (Object)new PlayerConnectListener(this));
    }

    private void replaceInitializers() {
        WrappedConnectionManager connectionManager = new WrappedConnectionManager(this.server);
        this.replaceInitializer(connectionManager.getServerChannelInitializer(), HackShieldChannelInitializer::new);
        this.replaceInitializer(connectionManager.getBackendChannelInitializer(), HackShieldBackendChannelInitializer::new);
    }

    private void replaceInitializer(ChannelInitializerHolder holder, BiFunction<Implementation, ChannelInitializer<Channel>, ChannelInitializer<Channel>> channelInitializerFunction) {
        holder.set(channelInitializerFunction.apply(this, (ChannelInitializer<Channel>)((ChannelInitializer)holder.get())));
    }

    private Function<PayloadPacket, Object> createPayloadWrapper() {
        return packet -> {
            ByteBuf byteBuf = Unpooled.buffer();
            IPacketBuffer buffer = this.hackShieldPlugin.getByteBufWrapper(byteBuf);
            packet.write(buffer);
            return new PluginMessage(packet.getChannel(), byteBuf);
        };
    }

    public ProxyServer getServer() {
        return this.server;
    }

    public HackShieldPlugin getHackShieldPlugin() {
        return this.hackShieldPlugin;
    }

    public VelocityLoader getPlugin() {
        return this.plugin;
    }

    public Logger getLogger() {
        return this.hackShieldPlugin.getLogger();
    }

    public void runAsync(Runnable runnable) {
        this.getServer().getScheduler().buildTask((Object)this.plugin, runnable).schedule();
    }

    public void runRepeatingAsync(Runnable runnable, long initialDelay, long periodDelay, TimeUnit unit) {
        this.getServer().getScheduler().buildTask((Object)this.plugin, runnable).delay(initialDelay, unit).repeat(periodDelay, unit).schedule();
    }
}

