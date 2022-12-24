/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 *  pl.hackshield.auth.loader.endpoint.EndpointResponse$Error
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenRequest
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse
 *  pl.hackshield.auth.loader.endpoint.endpoints.Servers$CheckJoinTokenResponse$Data$JoinRequestInfo
 */
package pl.hackshield.auth.spigot.common.listener;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;
import pl.hackshield.auth.loader.endpoint.endpoints.Servers;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.utils.ChatUtil;

public final class PlayerPreLoginListener
implements Listener {
    private final SpigotCommon plugin;

    public PlayerPreLoginListener(SpigotCommon plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onGameProfileRequest(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        CommonHackShieldUser user = this.plugin.getPendingUsers().get(uuid);
        if (user == null || !user.isHandshakeStateAuthorized()) {
            if (this.plugin.getHackShieldPlugin().getCfg().passiveVerification.enabled) {
                return;
            }
            this.kick(event, String.join((CharSequence)"\n", this.plugin.getHackShieldPlugin().getCfg().disconnectMessage));
            return;
        }
        if (!uuid.equals(user.getMinecraftID())) {
            this.kick(event, "&cNie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!");
            this.logSecurityViolation(uuid, event.getName(), user.getHardwareIds(), "UUID inconsistency " + user.getMinecraftID().toString() + " != " + uuid + ", probably malicious user!");
            return;
        }
        try {
            Servers.CheckJoinTokenResponse response = HackShieldPlugin.getInstance().getEndpointManager().getServers().check(Servers.CheckJoinTokenRequest.from((String)user.getToken()));
            EndpointResponse.Error error = response.getError();
            if (error != null) {
                if (error.getMessage().equals("Slot Limit Reached")) {
                    this.kick(event, "&cSerwer jest pe\u0142en graczy!");
                    return;
                }
                this.kick(event, "&cNie uda\u0142o si\u0119 zweryfikowa\u0107 Twojej sesji!");
                this.plugin.getLogger().warning("Couldn't verify HS token (status=" + error.getCode() + ", message=" + error.getMessage() + ")");
                return;
            }
            Servers.CheckJoinTokenResponse.Data.JoinRequestInfo requestInfo = response.getData().getJoinRequestInfo();
            user.setAccountID(requestInfo.getGamerId());
            user.setCurrentBattlePassPremium(requestInfo.isCurrentBattlePassPremium());
            user.setEncryptionStateAuthorized(true);
        }
        catch (Exception ex) {
            this.kick(event, "&cWyst\u0105pi\u0142 b\u0142\u0105d podczas weryfikacji Twojej sesji!");
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't verify HS token!", ex);
        }
    }

    private void kick(AsyncPlayerPreLoginEvent event, String message) {
        event.setKickMessage(ChatUtil.fixColor(message));
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
    }

    private void logSecurityViolation(UUID uuid, String username, String[] hardwareIdParts, String message) {
        String identifier = "[" + username + "/" + uuid + "/" + Arrays.toString(hardwareIdParts).substring(1);
        this.plugin.getLogger().warning(identifier + " " + message);
    }
}

