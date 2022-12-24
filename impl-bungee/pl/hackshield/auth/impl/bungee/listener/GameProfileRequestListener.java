/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.PostLoginEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 *  net.md_5.bungee.netty.ChannelWrapper
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 *  pl.hackshield.auth.loader.endpoint.EndpointResponse$Error
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenRequest
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse$Data$JoinRequestInfo
 */
package pl.hackshield.auth.impl.bungee.listener;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;
import pl.hackshield.auth.impl.bungee.util.MessageUtil;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;
import pl.hackshield.auth.loader.endpoint.endpoints.Servers;

public final class GameProfileRequestListener
implements Listener {
    private final Implementation plugin;

    public GameProfileRequestListener(Implementation plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGameProfileRequest(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ChannelWrapper channelWrapper = ConnectionUtil.getUnsafeChannelWrapper(player.getPendingConnection());
        HackShieldConnection connection = (HackShieldConnection)channelWrapper.getHandle().pipeline().get("hackshield-handler");
        CommonHackShieldUser user = connection.getUser();
        if (this.plugin.getHackShieldPlugin().getCfg().passiveVerification.enabled && !user.isHandshakeStateAuthorized()) {
            return;
        }
        HackShieldPlugin hackShieldPlugin = this.plugin.getHackShieldPlugin();
        UUID playerUUID = player.getUniqueId();
        if (player.getPendingConnection().isOnlineMode() && !playerUUID.equals(user.getMinecraftID())) {
            player.disconnect(MessageUtil.serialize(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Nie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!", (TextColor)NamedTextColor.RED))));
            this.logSecurityViolation(player.getName(), playerUUID, user.getHardwareIds(), "UUID inconsistency " + user.getMinecraftID().toString() + " != " + playerUUID + ", probably malicious user!");
            return;
        }
        try {
            Servers.CheckJoinTokenResponse response = hackShieldPlugin.getEndpointManager().getServers().check(Servers.CheckJoinTokenRequest.from((String)user.getToken()));
            EndpointResponse.Error error = response.getError();
            if (error != null) {
                if (error.getMessage().equals("Slot Limit Reached")) {
                    player.disconnect(MessageUtil.serialize(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Serwer jest pe\u0142en graczy!", (TextColor)NamedTextColor.RED))));
                    return;
                }
                player.disconnect(MessageUtil.serialize(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Nie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!", (TextColor)NamedTextColor.RED))));
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
            player.disconnect(MessageUtil.serialize(hackShieldPlugin.getPrefix().append((Component)Component.text((String)"Wyst\u0105pi\u0142 b\u0142\u0105d podczas weryfikacji Twojej sesji!", (TextColor)NamedTextColor.RED))));
        }
    }

    private void logSecurityViolation(String nickname, UUID uuid, String[] hardwareIdParts, String message) {
        String identifier = "[" + nickname + "/" + uuid.toString() + "/" + Arrays.toString(hardwareIdParts).substring(1);
        this.plugin.getLogger().warning(identifier + " " + message);
    }
}

