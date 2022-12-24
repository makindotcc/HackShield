/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.md_5.bungee.api.connection.PendingConnection
 *  net.md_5.bungee.api.event.PreLoginEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 *  net.md_5.bungee.netty.ChannelWrapper
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.bungee.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;
import pl.hackshield.auth.impl.bungee.util.MessageUtil;

public final class PreLoginListener
implements Listener {
    private final Implementation plugin;

    @EventHandler
    public void onPreLoginEvent(PreLoginEvent event) {
        if (this.plugin.getHackShieldPlugin().getCfg().passiveVerification.enabled) {
            return;
        }
        ChannelWrapper channelWrapper = ConnectionUtil.getUnsafeChannelWrapper(event.getConnection());
        HackShieldConnection hackShieldConnection = (HackShieldConnection)channelWrapper.getHandle().pipeline().get("hackshield-handler");
        CommonHackShieldUser user = hackShieldConnection.getUser();
        if (!user.isHandshakeStateAuthorized()) {
            event.setCancelReason(MessageUtil.serialize(this.plugin.getHackShieldPlugin().getCfg().getDisconnectMessageAsComponent()));
            event.setCancelled(true);
            return;
        }
        if (user.getMinecraftID() == null) {
            event.setCancelReason(MessageUtil.serialize((Component)Component.text((String)"Wyst\u0105pi\u0142 b\u0142\u0105d podczas autoryzacji HackShield!", (TextColor)NamedTextColor.RED)));
            event.setCancelled(true);
            return;
        }
        hackShieldConnection.setDisconnector(((PendingConnection)event.getConnection())::disconnect);
    }

    public PreLoginListener(Implementation plugin) {
        this.plugin = plugin;
    }
}

