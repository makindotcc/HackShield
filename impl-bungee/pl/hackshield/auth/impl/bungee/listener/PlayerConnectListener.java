/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.ServerConnectEvent
 *  net.md_5.bungee.api.event.ServerConnectEvent$Reason
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 *  net.md_5.bungee.netty.ChannelWrapper
 *  pl.hackshield.auth.common.data.Config
 *  pl.hackshield.auth.common.data.PassiveVerification
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.bungee.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import pl.hackshield.auth.common.data.Config;
import pl.hackshield.auth.common.data.PassiveVerification;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;
import pl.hackshield.auth.impl.bungee.util.MessageUtil;

public class PlayerConnectListener
implements Listener {
    private final Implementation plugin;

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ChannelWrapper channelWrapper = ConnectionUtil.getUnsafeChannelWrapper(player.getPendingConnection());
        HackShieldConnection connection = (HackShieldConnection)channelWrapper.getHandle().pipeline().get("hackshield-handler");
        CommonHackShieldUser user = connection.getUser();
        Config cfg = this.plugin.getHackShieldPlugin().getCfg();
        PassiveVerification passiveVerification = cfg.passiveVerification;
        if (!user.isAuthorized() && passiveVerification.enabled) {
            if (passiveVerification.servers.contains("*")) {
                return;
            }
            if (!passiveVerification.servers.contains(event.getTarget().getName().toLowerCase())) {
                event.setCancelled(true);
                BaseComponent[] disconnectMessage = MessageUtil.serialize(cfg.getDisconnectMessageAsComponent());
                if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
                    player.disconnect(disconnectMessage);
                } else {
                    player.sendMessage(disconnectMessage);
                }
            }
            return;
        }
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
            this.plugin.runAsync(() -> this.plugin.getHackShieldPlugin().sendJoinPackets(connection.getConnection().getChannel()));
        }
    }

    public PlayerConnectListener(Implementation plugin) {
        this.plugin = plugin;
    }
}

