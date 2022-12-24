/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  pl.hackshield.auth.api.quest.condition.base.Condition
 *  pl.hackshield.auth.api.quest.condition.impl.BreakBlockCondition
 *  pl.hackshield.auth.api.quest.event.PreQuestCheckEvent
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.spigot.common.quest.listener;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.hackshield.auth.api.quest.condition.base.Condition;
import pl.hackshield.auth.api.quest.condition.impl.BreakBlockCondition;
import pl.hackshield.auth.api.quest.event.PreQuestCheckEvent;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public class PlayerDigListener
implements Listener {
    private final SpigotCommon plugin;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Channel channel = this.plugin.getPlayerConnection(player).getChannel();
        HackShieldConnection connection = (HackShieldConnection)channel.pipeline().get("hackshield-handler");
        CommonHackShieldUser user = connection.getUser();
        if (!user.isCurrentBattlePassPremium()) {
            return;
        }
        BreakBlockCondition data = new BreakBlockCondition();
        data.setItem(player.getItemInHand());
        data.setBlock(event.getBlock());
        PreQuestCheckEvent questEvent = new PreQuestCheckEvent(player, (HackShieldUser)user, (Condition)data);
        Bukkit.getPluginManager().callEvent((Event)questEvent);
        if (questEvent.isCancelled()) {
            return;
        }
        this.plugin.getQuestManager().fireQuestCheck((HackShieldUser)user, (Condition)data);
    }

    public PlayerDigListener(SpigotCommon plugin) {
        this.plugin = plugin;
    }
}

