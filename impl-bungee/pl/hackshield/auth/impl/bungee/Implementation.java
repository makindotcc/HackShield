/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.protocol.packet.PluginMessage
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.IImplementation
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.network.PayloadPacket
 *  pl.hackshield.auth.common.util.ReflectionWarnings
 *  pl.hackshield.auth.loader.HackShieldLoader
 */
package pl.hackshield.auth.impl.bungee;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.packet.PluginMessage;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.IImplementation;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PayloadPacket;
import pl.hackshield.auth.common.util.ReflectionWarnings;
import pl.hackshield.auth.impl.bungee.APIImplementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.handler.WrappedVarint21LengthFieldPrepender;
import pl.hackshield.auth.impl.bungee.listener.GameProfileRequestListener;
import pl.hackshield.auth.impl.bungee.listener.PlayerConnectListener;
import pl.hackshield.auth.impl.bungee.listener.PlayerHandshakeListener;
import pl.hackshield.auth.impl.bungee.listener.PreLoginListener;
import pl.hackshield.auth.impl.bungee.listener.TestAPIListener;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;
import pl.hackshield.auth.impl.bungee.wrapper.protocol.WrappedPacketBuffer;
import pl.hackshield.auth.loader.HackShieldLoader;

public final class Implementation
implements IImplementation {
    private HackShieldPlugin hackShieldPlugin;
    private Plugin plugin;

    public void init(HackShieldPlugin hackShieldPlugin, HackShieldLoader server) {
        this.hackShieldPlugin = hackShieldPlugin;
        this.plugin = (Plugin)server;
        this.replaceInitializers();
        this.registerListeners();
        this.hackShieldPlugin.setByteBufConsumer(WrappedPacketBuffer::new);
        this.hackShieldPlugin.setPayloadPacketWrapper(this.createPayloadWrapper());
        HackShield.setApi((HackShieldAPI)new APIImplementation(this));
        hackShieldPlugin.setImplementation((IImplementation)this);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerListener(this.plugin, (Listener)new PlayerHandshakeListener(this));
        this.getServer().getPluginManager().registerListener(this.plugin, (Listener)new PreLoginListener(this));
        this.getServer().getPluginManager().registerListener(this.plugin, (Listener)new GameProfileRequestListener(this));
        this.getServer().getPluginManager().registerListener(this.plugin, (Listener)new PlayerConnectListener(this));
        this.getServer().getPluginManager().registerListener(this.plugin, (Listener)new TestAPIListener(this));
    }

    private void replaceInitializers() {
        ReflectionWarnings.disableAccessWarnings();
        ConnectionUtil.replaceFramePrepender(new WrappedVarint21LengthFieldPrepender(this, this.createInjectionChannelConsumer()));
    }

    public BiConsumer<Channel, Boolean> createInjectionChannelConsumer() {
        return (channel, serverInitializer) -> {
            ChannelPipeline pipeline = channel.pipeline();
            HackShieldConnection connection = new HackShieldConnection(this, pipeline, (boolean)serverInitializer);
            connection.inject(pipeline, false);
            pipeline.addBefore("inbound-boss", "hackshield-handler", (ChannelHandler)connection);
        };
    }

    private Function<PayloadPacket, Object> createPayloadWrapper() {
        return packet -> {
            ByteBuf byteBuf = Unpooled.buffer();
            IPacketBuffer buffer = this.hackShieldPlugin.getByteBufWrapper(byteBuf);
            packet.write(buffer);
            return new PluginMessage(packet.getChannel(), byteBuf, false);
        };
    }

    public ProxyServer getServer() {
        return this.plugin.getProxy();
    }

    public HackShieldPlugin getHackShieldPlugin() {
        return this.hackShieldPlugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Logger getLogger() {
        return this.hackShieldPlugin.getLogger();
    }

    public void runAsync(Runnable runnable) {
        this.getServer().getScheduler().runAsync(this.plugin, runnable);
    }

    public void runRepeatingAsync(Runnable runnable, long initialDelay, long periodDelay, TimeUnit unit) {
        this.getServer().getScheduler().schedule(this.plugin, runnable, initialDelay, periodDelay, unit);
    }
}

