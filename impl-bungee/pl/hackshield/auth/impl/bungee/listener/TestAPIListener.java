/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.ServerConnectEvent
 *  net.md_5.bungee.api.event.ServerConnectEvent$Reason
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.api.HsPacket
 *  pl.hackshield.auth.api.message.UniqueMessagePacket
 *  pl.hackshield.auth.api.user.HackShieldUser
 */
package pl.hackshield.auth.impl.bungee.listener;

import java.util.concurrent.TimeUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.message.UniqueMessagePacket;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;

public class TestAPIListener
implements Listener {
    private final Implementation plugin;

    @EventHandler(priority=1)
    public void onConnect(ServerConnectEvent event) {
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) {
            return;
        }
        ProxiedPlayer player = event.getPlayer();
        this.plugin.getServer().getScheduler().schedule(this.plugin.getPlugin(), () -> {
            HackShieldAPI api = HackShield.getApi();
            HackShieldUser hsUser = api.getUser(player);
            if (!hsUser.isAuthorized()) {
                player.sendMessage("[VELO] Nie u\u017cywasz klienta HS!");
                return;
            }
            api.sendPacket(hsUser, (HsPacket)new UniqueMessagePacket(Key.key("hackshield", "bungee_test_api"),
                    (Component)Component.text((String)"Wiadomo\u015b\u0107 z silnika proxy!")));
        }, 10L, TimeUnit.SECONDS);
        this.plugin.getServer().getScheduler().schedule(this.plugin.getPlugin(), () -> player.sendMessage("szummmmm... brrr"), 11L, TimeUnit.SECONDS);
        this.plugin.getServer().getScheduler().schedule(this.plugin.getPlugin(), () -> {
            HackShieldAPI api = HackShield.getApi();
            HackShieldUser hsUser = api.getUser(player);
            if (!hsUser.isAuthorized()) {
                player.sendMessage("[VELO] Nie u\u017cywasz klienta HS!");
                return;
            }
            api.sendPacket(hsUser, (HsPacket)new UniqueMessagePacket(Key.key("hackshield", "bungee_test_api"),
                    (Component)Component.text((String)"Nadpisana z silnika proxy!")));
        }, 12L, TimeUnit.SECONDS);
    }

    public TestAPIListener(Implementation plugin) {
        this.plugin = plugin;
    }
}

