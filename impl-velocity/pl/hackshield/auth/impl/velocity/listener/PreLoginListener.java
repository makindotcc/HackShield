/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.connection.PreLoginEvent
 *  com.velocitypowered.api.event.connection.PreLoginEvent$PreLoginComponentResult
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.velocity.Implementation;
import pl.hackshield.auth.impl.velocity.connection.client.InitialInboundConnection;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;
import pl.hackshield.auth.impl.velocity.wrapper.client.WrappedLoginInboundConnection;

public final class PreLoginListener {
    private final Implementation plugin;

    @Subscribe
    public void onPreLoginEvent(PreLoginEvent event) {
        if (this.plugin.getHackShieldPlugin().getCfg().passiveVerification.enabled) {
            return;
        }
        InitialInboundConnection inboundConnection = new WrappedLoginInboundConnection((Object)event.getConnection()).getDelegate();
        HackShieldConnection hackShieldConnection = (HackShieldConnection)((Object)inboundConnection.getPipelineHandler("hackshield-handler"));
        CommonHackShieldUser user = hackShieldConnection.getUser();
        if (!user.isHandshakeStateAuthorized()) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied((Component)this.plugin.getHackShieldPlugin().getCfg().getDisconnectMessageAsComponent()));
            return;
        }
        if (user.getMinecraftID() == null) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied((Component)Component.text((String)"Wyst\u0105pi\u0142 b\u0142\u0105d podczas autoryzacji HackShield!", (TextColor)NamedTextColor.RED)));
            return;
        }
        hackShieldConnection.setDisconnector(inboundConnection::disconnect);
    }

    public PreLoginListener(Implementation plugin) {
        this.plugin = plugin;
    }
}

