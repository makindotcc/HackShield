/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  pl.hackshield.auth.loader.HackShieldLoader
 *  pl.hackshield.auth.loader.endpoint.EndpointManager
 *  pl.hackshield.auth.loader.implementation.ImplementationData
 *  pl.hackshield.auth.loader.module.ModuleInfo
 *  pl.hackshield.auth.loader.module.ModuleLoader
 */
package pl.hackshield.auth.common;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MalformedObjectNameException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.abovehead.IconAboveHeadPacket;
import pl.hackshield.auth.api.bossbar.BossBarPacket;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.message.UniqueMessagePacket;
import pl.hackshield.auth.api.region.RegionPacket;
import pl.hackshield.auth.api.tablist.ForceTablistPacket;
import pl.hackshield.auth.api.waypoint.WaypointPacket;
import pl.hackshield.auth.common.IImplementation;
import pl.hackshield.auth.common.data.Config;
import pl.hackshield.auth.common.data.Mod;
import pl.hackshield.auth.common.data.serdes.ModSerdesPack;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.PayloadPacket;
import pl.hackshield.auth.common.network.packet.clientbound.ModListPacket;
import pl.hackshield.auth.common.network.packet.clientbound.ModSettingsPacket;
import pl.hackshield.auth.common.network.packet.clientbound.RegisterIconPacket;
import pl.hackshield.auth.common.network.packet.clientbound.ServerSettingsPacket;
import pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedBossBarPacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedForceTablistPacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedIconAboveHeadPacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedRegionPacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedUniqueMessagePacket;
import pl.hackshield.auth.common.network.packet.clientbound.wrapped.WrappedWaypointPacket;
import pl.hackshield.auth.common.network.packet.serverbound.DisplayIconAboveHeadRequestPacket;
import pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket;
import pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket;
import pl.hackshield.auth.common.task.UpdateStatsTask;
import pl.hackshield.auth.loader.HackShieldLoader;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.implementation.ImplementationData;
import pl.hackshield.auth.loader.module.ModuleInfo;
import pl.hackshield.auth.loader.module.ModuleLoader;

public class HackShieldPlugin {
    private HackShieldLoader hackShieldLoader;
    private static HackShieldPlugin instance;
    private IImplementation implementation;
    private Config configuration;
    private ModuleLoader moduleLoader;
    private EndpointManager endpointManager;
    private HackShieldAPI hackShieldAPI;
    private Function<ByteBuf, IPacketBuffer> byteBufConsumer;
    private Function<PayloadPacket, Object> payloadPacketWrapper;
    private Consumer<UUID> iconAboveHeadRequestConsumer;
    private Map<UUID, Icon> icons;

    public void init(HackShieldLoader hackShieldLoader, EndpointManager endpointManager, ModuleLoader moduleLoader, ImplementationData implementationData) {
        instance = this;
        this.hackShieldLoader = hackShieldLoader;
        this.icons = new HashMap<UUID, Icon>();
        this.configuration = ConfigManager.create(Config.class, initializer -> {
            initializer.withConfigurer(new YamlSnakeYamlConfigurer("\n"));
            initializer.withBindFile(new File(hackShieldLoader.getPluginDirectory(), "config.yml"));
            initializer.withSerdesPack(new ModSerdesPack());
            initializer.saveDefaults();
            initializer.load(true);
        });
        this.getCfg().getModsWithSettings().get(0).getSettings().forEach((key, value) -> System.out.println(key + " " + value));
        this.endpointManager = endpointManager;
        this.moduleLoader = moduleLoader;
        this.registerPackets();
        this.loadImplementation(hackShieldLoader, implementationData);
        this.registerTasks();
    }

    protected void registerPackets() {
        ConnectionProtocol.LOGIN.registerPacket(PacketFlow.CLIENTBOUND, TransferUserInformationRequestPacket.class, TransferUserInformationRequestPacket::new, 1000);
        ConnectionProtocol.LOGIN.registerPacket(PacketFlow.SERVERBOUND, TransferUserInformationPacket.class, TransferUserInformationPacket::new, 1001);
        ConnectionProtocol.LOGIN.registerPacket(PacketFlow.SERVERBOUND, GamerInformationPacket.class, GamerInformationPacket::new, 0);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.SERVERBOUND, DisplayIconAboveHeadRequestPacket.class, DisplayIconAboveHeadRequestPacket::new, 1);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, WaypointPacket.class, WrappedWaypointPacket.class, WrappedWaypointPacket::new, WrappedWaypointPacket::new, 2);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, BossBarPacket.class, WrappedBossBarPacket.class, WrappedBossBarPacket::new, WrappedBossBarPacket::new, 3);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, ForceTablistPacket.class, WrappedForceTablistPacket.class, WrappedForceTablistPacket::new, WrappedForceTablistPacket::new, 4);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, UniqueMessagePacket.class, WrappedUniqueMessagePacket.class, WrappedUniqueMessagePacket::new, WrappedUniqueMessagePacket::new, 5);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, ServerSettingsPacket.class, ServerSettingsPacket::new, 6);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, RegisterIconPacket.class, RegisterIconPacket::new, 7);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, RegionPacket.class, WrappedRegionPacket.class, WrappedRegionPacket::new, WrappedRegionPacket::new, 8);
        ConnectionProtocol.PLAY.registerPacket(PacketFlow.CLIENTBOUND, IconAboveHeadPacket.class, WrappedIconAboveHeadPacket.class, WrappedIconAboveHeadPacket::new, WrappedIconAboveHeadPacket::new, 9);
    }

    public Logger getLogger() {
        return this.hackShieldLoader.getLogger();
    }

    public void registerTasks() {
        try {
            this.implementation.runRepeatingAsync(new UpdateStatsTask(this), 1L, 1L, TimeUnit.SECONDS);
        }
        catch (MalformedObjectNameException e) {
            this.getLogger().log(Level.SEVERE, "Cannot register UpdateStats Task!", e);
        }
    }

    public IPacketBuffer getByteBufWrapper(ByteBuf buffer) {
        return this.byteBufConsumer.apply(buffer);
    }

    public Object wrapPayloadPacket(PayloadPacket packet) {
        return this.payloadPacketWrapper.apply(packet);
    }

    public Function<PayloadPacket, Object> getPayloadPacketWrapper() {
        return this.payloadPacketWrapper;
    }

    private ImplementationData loadImplementation(HackShieldLoader pluginInstance, ImplementationData implementation) {
        if (implementation == null) {
            this.getLogger().warning("Not found implementation!");
            return null;
        }
        this.getLogger().info("Founded implementation: " + implementation.getFileName());
        ModuleInfo implModule = new ModuleInfo("impl-" + implementation.getFileName() + ".jar", "pl.hackshield.auth.impl." + implementation.getPackageName() + ".Implementation", "init", new Class[]{HackShieldPlugin.class, HackShieldLoader.class}, new Object[]{this, pluginInstance});
        if (implementation.getPackageName().startsWith("spigot")) {
            ModuleInfo commonModule = new ModuleInfo("common-spigot.jar", "pl.hackshield.auth.spigot.common.SpigotCommon", "init", new Class[]{HackShieldPlugin.class, HackShieldLoader.class}, new Object[]{this, pluginInstance});
            this.moduleLoader.loadModule(this.getClass().getClassLoader(), new ModuleInfo[]{commonModule, implModule});
        } else {
            this.moduleLoader.loadModule(this.getClass().getClassLoader(), new ModuleInfo[]{implModule});
        }
        return implementation;
    }

    public Component getPrefix() {
        return ((TextComponent)((TextComponent)Component.text('[', (TextColor)NamedTextColor.GRAY).append(Component.text("HackShield", (TextColor)NamedTextColor.DARK_PURPLE))).append(Component.text(']', (TextColor)NamedTextColor.GRAY))).append(Component.newline());
    }

    public Config getCfg() {
        return this.configuration;
    }

    public void sendJoinPackets(Channel channel) {
        Function<PayloadPacket, Object> payloadPacketWrapper = this.getPayloadPacketWrapper();
        channel.writeAndFlush(payloadPacketWrapper.apply(new ModListPacket(this.getCfg().mods)), channel.voidPromise());
        this.getCfg().getModsWithSettings().forEach(mod -> channel.write(payloadPacketWrapper.apply(new ModSettingsPacket((Mod)mod))));
        channel.write((Object)new ServerSettingsPacket(this.getCfg().settings));
        channel.write((Object)new RegisterIconPacket(this.getIcons()));
        channel.flush();
    }

    public static HackShieldPlugin getInstance() {
        return instance;
    }

    public void setImplementation(IImplementation implementation) {
        this.implementation = implementation;
    }

    public ModuleLoader getModuleLoader() {
        return this.moduleLoader;
    }

    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    public HackShieldAPI getHackShieldAPI() {
        return this.hackShieldAPI;
    }

    public void setHackShieldAPI(HackShieldAPI hackShieldAPI) {
        this.hackShieldAPI = hackShieldAPI;
    }

    public void setByteBufConsumer(Function<ByteBuf, IPacketBuffer> byteBufConsumer) {
        this.byteBufConsumer = byteBufConsumer;
    }

    public void setPayloadPacketWrapper(Function<PayloadPacket, Object> payloadPacketWrapper) {
        this.payloadPacketWrapper = payloadPacketWrapper;
    }

    public void setIconAboveHeadRequestConsumer(Consumer<UUID> iconAboveHeadRequestConsumer) {
        this.iconAboveHeadRequestConsumer = iconAboveHeadRequestConsumer;
    }

    public Consumer<UUID> getIconAboveHeadRequestConsumer() {
        return this.iconAboveHeadRequestConsumer;
    }

    public Map<UUID, Icon> getIcons() {
        return this.icons;
    }
}

