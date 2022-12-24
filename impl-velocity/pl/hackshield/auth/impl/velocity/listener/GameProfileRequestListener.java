/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.player.GameProfileRequestEvent
 *  com.velocitypowered.api.util.GameProfile
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 *  pl.hackshield.auth.loader.endpoint.EndpointResponse$Error
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenRequest
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse$Data$JoinRequestInfo
 */
package pl.hackshield.auth.impl.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;
import java.util.Arrays;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.velocity.Implementation;
import pl.hackshield.auth.impl.velocity.connection.client.InitialInboundConnection;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;
import pl.hackshield.auth.impl.velocity.wrapper.client.WrappedLoginInboundConnection;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;
import pl.hackshield.auth.loader.endpoint.endpoints.Servers;

public final class GameProfileRequestListener {
    private final Implementation plugin;

    public GameProfileRequestListener(Implementation plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onGameProfileRequest(GameProfileRequestEvent event) {
        WrappedLoginInboundConnection connection = new WrappedLoginInboundConnection((Object)event.getConnection());
        InitialInboundConnection delegate = connection.getDelegate();
        HackShieldConnection hackShieldConnection = (HackShieldConnection)((Object)delegate.getPipelineHandler("hackshield-handler"));
        CommonHackShieldUser user = hackShieldConnection.getUser();
        if (this.plugin.getHackShieldPlugin().getCfg().passiveVerification.enabled && !user.isHandshakeStateAuthorized()) {
            return;
        }
        GameProfile profile = event.getOriginalProfile();
        HackShieldPlugin hackShieldPlugin = this.plugin.getHackShieldPlugin();
        if (event.isOnlineMode() && !profile.getId().equals(user.getMinecraftID())) {
            delegate.disconnect(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Nie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!", (TextColor)NamedTextColor.RED)));
            this.logSecurityViolation(profile, "UUID inconsistency " + user.getMinecraftID().toString() + " != " + profile.getId().toString() + ", probably malicious user!");
            return;
        }
        try {
            Servers.CheckJoinTokenResponse response = hackShieldPlugin.getEndpointManager().getServers().check(Servers.CheckJoinTokenRequest.from((String)user.getToken()));
            EndpointResponse.Error error = response.getError();
            if (error != null) {
                if (error.getMessage().equals("Slot Limit Reached")) {
                    delegate.disconnect(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Serwer jest pe\u0142en graczy!", (TextColor)NamedTextColor.RED)));
                    return;
                }
                delegate.disconnect(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Nie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!", (TextColor)NamedTextColor.RED)));
                this.plugin.getLogger().warning("Couldn't verify HS token (status=" + error.getCode() + ", message=" + error.getMessage() + ")");
                return;
            }
            Servers.CheckJoinTokenResponse.Data.JoinRequestInfo requestInfo = response.getData().getJoinRequestInfo();
            user.setAccountID(requestInfo.getGamerId());
            user.setCurrentBattlePassPremium(requestInfo.isCurrentBattlePassPremium());
            user.setEncryptionStateAuthorized(true);
        }
        catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't verify HS token!", ex);
            delegate.disconnect(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Wyst\u0105pi\u0142 b\u0142\u0105d podczas weryfikacji Twojej sesji!", (TextColor)NamedTextColor.RED)));
        }
    }

    private void logSecurityViolation(GameProfile profile, String message) {
        String identifier = "[" + profile.getName() + "/" + profile.getId().toString();
        this.plugin.getLogger().warning(identifier + " " + message);
    }
}

