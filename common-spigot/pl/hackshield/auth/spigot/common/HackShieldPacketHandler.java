/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.common.network.PacketHandler
 *  pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket
 *  pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket
 *  pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket
 *  pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.spigot.common;

import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket;
import pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.protocol.HackShieldConnection;

public final class HackShieldPacketHandler
implements PacketHandler {
    private final HackShieldConnection connection;
    private final SpigotCommon plugin;

    public HackShieldPacketHandler(SpigotCommon plugin, HackShieldConnection connection) {
        this.connection = connection;
        this.plugin = plugin;
    }

    public void handlePacket(HandshakePacket packet) {
        if (!packet.getServerAddress().endsWith("\u0000HS")) {
            return;
        }
        if (this.plugin.isSlave()) {
            String serverAddress = packet.getServerAddress();
            packet.setServerAddress(serverAddress.substring(0, serverAddress.length() - "\u0000HS".length()));
        }
        this.connection.getUser().setHandshakeStateAuthorized(true);
    }

    public void handlePacket(GamerInformationPacket packet) {
        CommonHackShieldUser user = this.connection.getUser();
        user.setMinecraftID(packet.getMinecraftAccountID());
        user.setToken(packet.getAuthToken());
        this.plugin.getPendingUsers().put(user.getMinecraftID(), user);
    }

    public void handlePacket(TransferUserInformationPacket packet) {
        CommonHackShieldUser user = packet.getUser();
        if (!user.isAuthorized()) {
            return;
        }
        this.connection.setUser(user);
        this.plugin.getPendingUsers().put(user.getMinecraftID(), user);
        this.plugin.getLogger().info("[" + user.getMinecraftID() + "] Transferred HackShield User!");
    }

    public void handlePacket(TransferUserInformationRequestPacket packet) {
        throw new UnsupportedOperationException("HackShieldPacketHandler#handlePacket unsupported for TransferUserInformationRequestPacket packet!");
    }
}

