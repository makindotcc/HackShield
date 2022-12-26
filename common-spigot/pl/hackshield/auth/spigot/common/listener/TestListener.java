/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.scheduler.BukkitRunnable
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.api.HsPacket
 *  pl.hackshield.auth.api.bossbar.BossBarPacket
 *  pl.hackshield.auth.api.bossbar.BossBarPacket$Operation
 *  pl.hackshield.auth.api.bossbar.BossInfo
 *  pl.hackshield.auth.api.bossbar.BossInfo$Color
 *  pl.hackshield.auth.api.bossbar.BossInfo$Overlay
 *  pl.hackshield.auth.api.message.UniqueMessagePacket
 *  pl.hackshield.auth.api.region.RegionInfo
 *  pl.hackshield.auth.api.region.RegionPacket
 *  pl.hackshield.auth.api.region.RegionPacket$Operation
 *  pl.hackshield.auth.api.tablist.ForceTablistPacket
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.api.waypoint.Waypoint
 *  pl.hackshield.auth.api.waypoint.WaypointOptions
 *  pl.hackshield.auth.api.waypoint.WaypointPacket
 *  pl.hackshield.auth.api.waypoint.WaypointPacket$Operation
 */
package pl.hackshield.auth.spigot.common.listener;

import java.awt.Color;
import java.util.Collections;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.bossbar.BossBarPacket;
import pl.hackshield.auth.api.bossbar.BossInfo;
import pl.hackshield.auth.api.message.UniqueMessagePacket;
import pl.hackshield.auth.api.region.RegionInfo;
import pl.hackshield.auth.api.region.RegionPacket;
import pl.hackshield.auth.api.tablist.ForceTablistPacket;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.api.waypoint.Waypoint;
import pl.hackshield.auth.api.waypoint.WaypointOptions;
import pl.hackshield.auth.api.waypoint.WaypointPacket;
import pl.hackshield.auth.spigot.common.SpigotCommon;

public class TestListener
implements Listener {
    private final SpigotCommon plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final HackShieldAPI api = HackShield.getApi();
        final HackShieldUser user = api.getUser(player);
        if (!user.isAuthorized()) {
            player.sendMessage("Nie u\u017cywasz klienta HackShield :(");
            return;
        }
        UniqueMessagePacket messagePacket = new UniqueMessagePacket(Key.key("hackshield", "spigot_test"),
                (Component)Component.text((String)"Wiadomo\u015b\u0107 z silnika spigot."));
        api.sendPacket(user, (HsPacket)messagePacket);
        player.sendMessage("Losowa wiadomo\u015b\u0107...");
        new BukkitRunnable(){

            public void run() {
                UniqueMessagePacket messagePacket = new UniqueMessagePacket(Key.key("hackshield", "spigot_test"), 
                        (Component)Component.text((String)"Nadpisana wiadomo\u015b\u0107 o silniku."));
                api.sendPacket(user, (HsPacket)messagePacket);
            }
        }.runTaskLater(this.plugin.getPlugin(), 100L);
    }

    private void sendBossBar(Player player, HackShieldAPI api, HackShieldUser user) {
        BossInfo bossInfo = new BossInfo((Component)Component.text((String)"I <3 HackShield"), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
        BossBarPacket addBossPacket = new BossBarPacket(BossBarPacket.Operation.ADD, bossInfo);
        api.sendPacket(user, (HsPacket)addBossPacket);
        bossInfo.setName((Component)Component.text((String)"HackShield API"));
        BossBarPacket updateNamePacket = new BossBarPacket(BossBarPacket.Operation.UPDATE_NAME, bossInfo);
        api.sendPacket(user, (HsPacket)updateNamePacket);
        bossInfo.setPercent(0.5f);
        BossBarPacket updatePctPacket = new BossBarPacket(BossBarPacket.Operation.UPDATE_PCT, bossInfo);
        api.sendPacket(user, (HsPacket)updatePctPacket);
        bossInfo.setOverlay(BossInfo.Overlay.NOTCHED_20);
        bossInfo.setColor(BossInfo.Color.YELLOW);
        BossBarPacket updateStylePacket = new BossBarPacket(BossBarPacket.Operation.UPDATE_STYLE, bossInfo);
        api.sendPacket(user, (HsPacket)updateStylePacket);
        bossInfo.setCreateFog(true);
        bossInfo.setDarkenSky(true);
        bossInfo.setPlayEndBossMusic(true);
        BossBarPacket updatePropPacket = new BossBarPacket(BossBarPacket.Operation.UPDATE_PROPERTIES, bossInfo);
        api.sendPacket(user, (HsPacket)updatePropPacket);
    }

    private void sendWaypoint(Player player, HackShieldAPI api, HackShieldUser user) {
        Location location = player.getLocation();
        Waypoint waypoint = new Waypoint((Component)Component.text((String)"Spawn"), Color.BLUE, location.getX(), location.getY(), location.getZ());
        WaypointOptions options = waypoint.getOptions();
        options.setBeamHeight(20);
        options.setBeamWidth(2);
        WaypointPacket waypointPacket = new WaypointPacket(WaypointPacket.Operation.ADD, waypoint);
        api.sendPacket(user, (HsPacket)waypointPacket);
        waypoint.setPos(location.getX(), location.getY(), location.getZ());
        WaypointPacket waypointChangePosPacket = new WaypointPacket(WaypointPacket.Operation.UPDATE_LOCATION, waypoint);
        api.sendPacket(user, (HsPacket)waypointChangePosPacket);
        WaypointOptions waypointOptions = waypoint.getOptions();
        waypointOptions.setShowName(false);
        WaypointPacket waypointUpdatePropPacket = new WaypointPacket(WaypointPacket.Operation.UPDATE_PROPERTIES, waypoint);
        api.sendPacket(user, (HsPacket)waypointUpdatePropPacket);
        WaypointPacket waypointRemovePacket = new WaypointPacket(WaypointPacket.Operation.REMOVE, waypoint);
        api.sendPacket(user, (HsPacket)waypointRemovePacket);
        WaypointPacket waypointRemoveAllPacket = new WaypointPacket();
        api.sendPacket(user, (HsPacket)waypointRemoveAllPacket);
    }

    private void sendForceTablist(Player player, HackShieldAPI api, HackShieldUser user) {
        ForceTablistPacket packet = new ForceTablistPacket(true);
        api.sendPacket(user, (HsPacket)packet);
    }

    private void sendRegion(Player player, HackShieldAPI api, HackShieldUser user) {
        UUID regionId = UUID.randomUUID();
        RegionInfo region = new RegionInfo(regionId, -100, 0, -100, 100, 256, 100, false, false, false, Collections.emptySet());
        region.setCancelBuildMessage((Component)Component.text((String)"Nie mo\u017cesz stawia\u0107 blok\u00f3w na tym cuboidzie!").color(NamedTextColor.RED));
        region.setCancelBreakMessage((Component)Component.text((String)"Nie mo\u017cesz niszczy\u0107 blok\u00f3w na tym cuboidzie!").color(NamedTextColor.RED));
        RegionPacket regionPacket = new RegionPacket(RegionPacket.Operation.ADD, Collections.singleton(region));
        api.sendPacket(user, (HsPacket)regionPacket);
        RegionPacket regionRemoveAllPacket = new RegionPacket();
        api.sendPacket(user, (HsPacket)regionRemoveAllPacket);
        region.setMin(-200, 0, -200);
        region.setMax(200, 256, 200);
        RegionPacket regionChangePosPacket = new RegionPacket(RegionPacket.Operation.UPDATE_POS, Collections.singleton(region));
        api.sendPacket(user, (HsPacket)regionChangePosPacket);
        region.setBuildingAllowed(false);
        region.setDestroyingAllowed(true);
        RegionPacket regionChangeFlagsPacket = new RegionPacket(RegionPacket.Operation.UPDATE_FLAGS, Collections.singleton(region));
        api.sendPacket(user, (HsPacket)regionChangeFlagsPacket);
        region.setCancelBuildMessage((Component)Component.text((String)"Nie mo\u017cesz stawia\u0107 blok\u00f3w na spawnie!").color(NamedTextColor.RED));
        region.setCancelBreakMessage((Component)Component.text((String)"Nie mo\u017cesz niszczy\u0107 blok\u00f3w na spawnie!").color(NamedTextColor.RED));
        RegionPacket regionChangeMsgPacket = new RegionPacket(RegionPacket.Operation.UPDATE_FLAGS, Collections.singleton(region));
        api.sendPacket(user, (HsPacket)regionChangeMsgPacket);
    }

    public TestListener(SpigotCommon plugin) {
        this.plugin = plugin;
    }
}

