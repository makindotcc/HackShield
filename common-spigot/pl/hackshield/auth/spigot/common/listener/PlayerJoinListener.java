/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package pl.hackshield.auth.spigot.common.listener;

import io.netty.channel.Channel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public final class PlayerJoinListener
implements Listener {
    private final SpigotCommon plugin;

    public PlayerJoinListener(SpigotCommon plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Channel channel = this.plugin.getPlayerConnection(event.getPlayer()).getChannel();
        HackShieldConnection connection = (HackShieldConnection)channel.pipeline().get("hackshield-handler");
        if (!connection.getUser().isAuthorized()) {
            return;
        }
        this.plugin.runAsync(() -> this.plugin.getHackShieldPlugin().sendJoinPackets(connection.getChannel()));
    }
}

