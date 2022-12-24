/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  net.md_5.bungee.api.connection.PendingConnection
 *  net.md_5.bungee.api.event.PlayerHandshakeEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.connection.InitialHandler
 *  net.md_5.bungee.event.EventHandler
 *  net.md_5.bungee.netty.ChannelWrapper
 *  net.md_5.bungee.protocol.packet.Handshake
 *  pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket
 */
package pl.hackshield.auth.impl.bungee.listener;

import io.netty.channel.Channel;
import java.util.logging.Level;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.Handshake;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;

public class PlayerHandshakeListener
implements Listener {
    private final Implementation plugin;

    public PlayerHandshakeListener(Implementation plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        if (!(pendingConnection instanceof InitialHandler)) {
            return;
        }
        try {
            ChannelWrapper channelWrapper = ConnectionUtil.getChannelWrapper(pendingConnection);
            Channel channel = channelWrapper.getHandle();
            this.plugin.createInjectionChannelConsumer().accept(channel, true);
            HackShieldConnection hackShieldConnection = (HackShieldConnection)channel.pipeline().get("hackshield-handler");
            Handshake handshake = event.getHandshake();
            System.out.println(handshake.getHost() + " " + ((InitialHandler)pendingConnection).getExtraDataInHandshake());
            hackShieldConnection.getPacketHandler().handlePacket(new HandshakePacket(handshake.getProtocolVersion(), handshake.getHost() + ((InitialHandler)pendingConnection).getExtraDataInHandshake(), handshake.getPort(), handshake.getRequestedProtocol()));
        }
        catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while injecting HackShield handler into UpstreamBridge!", ex);
        }
    }
}

