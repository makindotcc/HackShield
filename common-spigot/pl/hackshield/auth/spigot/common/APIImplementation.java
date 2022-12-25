/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  net.kyori.adventure.text.Component
 *  org.bukkit.entity.Player
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.api.HsPacket
 *  pl.hackshield.auth.api.icon.Icon
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.spigot.common;

import io.netty.channel.Channel;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public class APIImplementation
implements HackShieldAPI {
    private final SpigotCommon plugin;

    public HackShieldUser getUser(UUID uuid) {
        return (HackShieldUser)this.plugin.getPendingUsers().get(uuid);
    }

    public HackShieldUser getUser(Player player) {
        Channel channel = this.plugin.getPlayerConnection(player).getChannel();
        HackShieldConnection connection = (HackShieldConnection)channel.pipeline().get("hackshield-handler");
        return connection.getUser();
    }

    public void sendPacket(HackShieldUser hackShieldUser, HsPacket packet) {
        CommonHackShieldUser user = (CommonHackShieldUser)hackShieldUser;
        Channel channel = user.getConnection().getChannel();
        HackShieldPacket wrapped = Optional.ofNullable(ConnectionProtocol.warpPacket((HsPacket)packet)).orElseThrow(() -> new IllegalStateException("Not found wrapper for " + packet.getClass().getSimpleName()));
        channel.writeAndFlush((Object)wrapped, channel.voidPromise());
    }

    public void sendPackets(HackShieldUser hackShieldUser, Collection<HsPacket> collection) {
        CommonHackShieldUser user = (CommonHackShieldUser)hackShieldUser;
        Channel channel = user.getConnection().getChannel();
        collection.forEach(packet -> {
            HackShieldPacket wrapped = Optional.ofNullable(ConnectionProtocol.warpPacket((HsPacket)packet)).orElseThrow(() -> new IllegalStateException("Not found wrapper for " + packet.getClass().getSimpleName()));
            channel.write((Object)wrapped, channel.voidPromise());
        });
        channel.flush();
    }

    public void registerIcon(Icon icon) {
        this.plugin.getHackShieldPlugin().getIcons().put(icon.getId(), icon);
    }

    public void unregisterIcon(UUID uuid) {
        this.plugin.getHackShieldPlugin().getIcons().remove(uuid);
    }

    public Component getDisconnectMessage() {
        return this.plugin.getHackShieldPlugin().getCfg().getDisconnectMessageAsComponent();
    }

    public void setDisplayIconAboveHeadRequestHandler(Consumer<UUID> handler) {
        this.plugin.getHackShieldPlugin().setIconAboveHeadRequestConsumer(handler);
    }

    public APIImplementation(SpigotCommon plugin) {
        this.plugin = plugin;
    }
}

