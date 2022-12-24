/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.spigot.common.quest.listener;

import io.netty.channel.Channel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public class PlayerMoveListener
implements Listener {
    private final SpigotCommon plugin;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Channel channel = this.plugin.getPlayerConnection(player).getChannel();
        HackShieldConnection connection = (HackShieldConnection)channel.pipeline().get("hackshield-handler");
        CommonHackShieldUser user = connection.getUser();
        if (!user.isCurrentBattlePassPremium()) {
            return;
        }
        if (player.isFlying()) {
            return;
        }
    }

    public PlayerMoveListener(SpigotCommon plugin) {
        this.plugin = plugin;
    }
}

