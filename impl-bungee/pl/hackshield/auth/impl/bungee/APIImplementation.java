/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  net.kyori.adventure.text.Component
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.netty.ChannelWrapper
 *  pl.hackshield.auth.api.HackShieldAPI
 *  pl.hackshield.auth.api.HsPacket
 *  pl.hackshield.auth.api.icon.Icon
 *  pl.hackshield.auth.api.user.HackShieldUser
 *  pl.hackshield.auth.common.network.ConnectionProtocol
 *  pl.hackshield.auth.common.network.HackShieldPacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.bungee;

import io.netty.channel.Channel;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.netty.ChannelWrapper;
import pl.hackshield.auth.api.HackShieldAPI;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.user.HackShieldUser;
import pl.hackshield.auth.common.network.ConnectionProtocol;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;

public class APIImplementation
implements HackShieldAPI {
    private final Implementation plugin;

    public HackShieldUser getUser(UUID uuid) {
        ProxiedPlayer player = Optional.of(this.plugin.getServer().getPlayer(uuid)).orElseThrow(() -> new IllegalArgumentException("Not found Player with uuid '" + uuid + "'!"));
        return this.getUser(player);
    }

    public HackShieldUser getUser(ProxiedPlayer player) {
        ChannelWrapper channelWrapper = ConnectionUtil.getUnsafeChannelWrapper(player.getPendingConnection());
        HackShieldConnection connection = (HackShieldConnection)channelWrapper.getHandle().pipeline().get("hackshield-handler");
        return connection.getUser();
    }

    public void sendPacket(HackShieldUser hackShieldUser, HsPacket packet) {
        CommonHackShieldUser user = (CommonHackShieldUser)hackShieldUser;
        Channel channel = user.getConnection().getChannel();
        HackShieldPacket wrapped = Optional.of(ConnectionProtocol.warpPacket((HsPacket)packet)).orElseThrow(() -> new IllegalStateException("Not found wrapper for " + packet.getClass().getSimpleName()));
        channel.writeAndFlush((Object)wrapped, channel.voidPromise());
    }

    public void sendPackets(HackShieldUser hackShieldUser, Collection<HsPacket> collection) {
        CommonHackShieldUser user = (CommonHackShieldUser)hackShieldUser;
        Channel channel = user.getConnection().getChannel();
        collection.forEach(packet -> {
            HackShieldPacket wrapped = Optional.of(ConnectionProtocol.warpPacket((HsPacket)packet)).orElseThrow(() -> new IllegalStateException("Not found wrapper for " + packet.getClass().getSimpleName()));
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

    public boolean addPassiveVerificationServer(String serverName) {
        return this.plugin.getHackShieldPlugin().getCfg().passiveVerification.servers.add(serverName.toLowerCase());
    }

    public boolean removePassiveVerificationServer(String serverName) {
        return this.plugin.getHackShieldPlugin().getCfg().passiveVerification.servers.remove(serverName.toLowerCase());
    }

    public APIImplementation(Implementation plugin) {
        this.plugin = plugin;
    }
}

