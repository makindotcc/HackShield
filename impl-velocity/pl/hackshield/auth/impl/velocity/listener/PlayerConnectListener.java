/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.event.Subscribe
 *  com.velocitypowered.api.event.player.ServerPostConnectEvent
 *  com.velocitypowered.api.event.player.ServerPreConnectEvent
 *  com.velocitypowered.api.event.player.ServerPreConnectEvent$ServerResult
 *  com.velocitypowered.api.proxy.Player
 *  com.velocitypowered.proxy.connection.client.ConnectedPlayer
 *  net.kyori.adventure.text.Component
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.api.HsPacket
 *  pl.hackshield.auth.api.message.UniqueMessagePacket
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.common.data.Config
 *  pl.hackshield.auth.common.data.PassiveVerification
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.message.UniqueMessagePacket;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.common.data.Config;
import pl.hackshield.auth.common.data.PassiveVerification;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.velocity.Implementation;
import pl.hackshield.auth.impl.velocity.handler.HackShieldConnection;

public class PlayerConnectListener {
    private final Implementation plugin;

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        HackShieldConnection hackShieldConnection = (HackShieldConnection)((ConnectedPlayer)player).getConnection().getChannel().pipeline().get("hackshield-handler");
        CommonHackShieldUser user = hackShieldConnection.getUser();
        Config cfg = this.plugin.getHackShieldPlugin().getCfg();
        PassiveVerification passiveVerification = cfg.passiveVerification;
        if (!user.isAuthorized() && passiveVerification.enabled) {
            if (passiveVerification.servers.contains("*")) {
                return;
            }
            if (!passiveVerification.servers.contains(event.getOriginalServer().getServerInfo().getName().toLowerCase())) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                Component disconnectMessage = cfg.getDisconnectMessageAsComponent();
                if (player.getCurrentServer().isEmpty()) {
                    player.disconnect(disconnectMessage);
                } else {
                    player.sendMessage(disconnectMessage);
                }
            }
        }
        this.plugin.getServer().getScheduler().buildTask((Object)this.plugin.getPlugin(), () -> {
            HackShieldAPI api = HackShield.getApi();
            HackShieldUser hsUser = api.getUser(player);
            if (!hsUser.isAuthorized()) {
                player.sendMessage((Component)Component.text((String)"[VELO] Nie u\u017cywasz klienta HS!"));
                return;
            }
            api.sendPacket(hsUser, (HsPacket)new UniqueMessagePacket(Key.key("hackshield", "velocity_test"),
                    (Component)Component.text((String)"Wiadomo\u015b\u0107 z silnika proxy!")));
        }).delay(10L, TimeUnit.SECONDS).schedule();
        this.plugin.getServer().getScheduler().buildTask((Object)this.plugin.getPlugin(), () -> player.sendMessage((Component)Component.text((String)"szummmmm... brrr"))).delay(11L, TimeUnit.SECONDS).schedule();
        this.plugin.getServer().getScheduler().buildTask((Object)this.plugin.getPlugin(), () -> {
            HackShieldAPI api = HackShield.getApi();
            HackShieldUser hsUser = api.getUser(player);
            if (!hsUser.isAuthorized()) {
                player.sendMessage((Component)Component.text((String)"[VELO] Nie u\u017cywasz klienta HS!"));
                return;
            }
            api.sendPacket(hsUser, (HsPacket)new UniqueMessagePacket(Key.key("hackshield", "velocity_test"),
                    (Component)Component.text((String)"Nadpisana z silnika proxy!")));
        }).delay(12L, TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void onJoin(ServerPostConnectEvent event) {
        if (event.getPreviousServer() != null) {
            return;
        }
        ConnectedPlayer player = (ConnectedPlayer)event.getPlayer();
        HackShieldConnection connection = (HackShieldConnection)player.getConnection().getChannel().pipeline().get("hackshield-handler");
        if (!connection.getUser().isAuthorized()) {
            return;
        }
        this.plugin.runAsync(() -> this.plugin.getHackShieldPlugin().sendJoinPackets(connection.getConnection().getChannel()));
    }

    public PlayerConnectListener(Implementation plugin) {
        this.plugin = plugin;
    }
}

