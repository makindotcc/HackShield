/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  io.netty.buffer.ByteBuf
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.spigotmc.SpigotConfig
 *  pl.hackshield.auth.api.HackShield
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.IImplementation
 *  pl.hackshield.auth.common.network.IPacketBuffer
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 *  pl.hackshield.auth.common.util.TimeUtil
 *  pl.hackshield.auth.loader.HackShieldLoader
 */
package pl.hackshield.auth.spigot.common;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;
import pl.hackshield.auth.api.HackShield;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.IImplementation;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.common.util.TimeUtil;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.spigot.common.APIImplementation;
import pl.hackshield.auth.spigot.common.listener.PlayerJoinListener;
import pl.hackshield.auth.spigot.common.listener.PlayerPreLoginListener;
import pl.hackshield.auth.spigot.common.listener.TestListener;
import pl.hackshield.auth.spigot.common.quest.QuestManager;
import pl.hackshield.auth.spigot.common.wrapper.PlayerConnection;

public class SpigotCommon
implements IImplementation {
    private Plugin plugin;
    private static SpigotCommon instance;
    private HackShieldPlugin hackShieldPlugin;
    private QuestManager questManager;
    private Function<Player, PlayerConnection> playerConnection;
    private final Map<UUID, CommonHackShieldUser> pendingUsers = Maps.newConcurrentMap();
    private boolean slave;

    public void init(HackShieldPlugin hackShieldPlugin, HackShieldLoader pluginInstance) {
        instance = this;
        this.plugin = (Plugin)pluginInstance;
        this.hackShieldPlugin = hackShieldPlugin;
        this.slave = SpigotConfig.bungee;
        this.questManager = new QuestManager(this, hackShieldPlugin.getEndpointManager());
        this.getLogger().info("Slave mode: " + this.slave);
        this.registerListeners();
        HackShield.setApi((HackShieldAPI)new APIImplementation(this));
        hackShieldPlugin.setImplementation((IImplementation)this);
    }

    public void registerListeners() {
        if (!this.isSlave()) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener)new PlayerPreLoginListener(this), this.plugin);
            this.plugin.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(this), this.plugin);
        }
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new TestListener(this), this.plugin);
    }

    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    public IPacketBuffer getByteBufWrapper(ByteBuf buffer) {
        return this.hackShieldPlugin.getByteBufWrapper(buffer);
    }

    public PlayerConnection getPlayerConnection(Player player) {
        return this.playerConnection.apply(player);
    }

    public static SpigotCommon getInstance() {
        return instance;
    }

    public void runSync(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, runnable);
    }

    public void runAsync(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void runRepeatingAsync(Runnable runnable, long initialDelay, long periodDelay, TimeUnit unit) {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, (long)TimeUtil.toTicks((long)initialDelay, (TimeUnit)unit), (long)TimeUtil.toTicks((long)periodDelay, (TimeUnit)unit));
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public HackShieldPlugin getHackShieldPlugin() {
        return this.hackShieldPlugin;
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public void setPlayerConnection(Function<Player, PlayerConnection> playerConnection) {
        this.playerConnection = playerConnection;
    }

    public Map<UUID, CommonHackShieldUser> getPendingUsers() {
        return this.pendingUsers;
    }

    public boolean isSlave() {
        return this.slave;
    }
}

