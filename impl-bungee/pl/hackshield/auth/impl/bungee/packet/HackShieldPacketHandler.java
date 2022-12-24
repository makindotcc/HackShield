/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  pl.hackshield.auth.common.network.PacketHandler
 *  pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket
 *  pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket
 *  pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket
 *  pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket
 *  pl.hackshield.auth.common.user.CommonHackShieldUser
 */
package pl.hackshield.auth.impl.bungee.packet;

import io.netty.channel.Channel;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket;
import pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket;
import pl.hackshield.auth.common.user.CommonHackShieldUser;
import pl.hackshield.auth.impl.bungee.handler.HackShieldConnection;

public final class HackShieldPacketHandler
implements PacketHandler {
    private final HackShieldConnection connection;

    public HackShieldPacketHandler(HackShieldConnection connection) {
        this.connection = connection;
    }

    public void handlePacket(HandshakePacket packet) {
        if (!packet.getServerAddress().endsWith("\u0000HS")) {
            return;
        }
        this.connection.getUser().setHandshakeStateAuthorized(true);
    }

    public void handlePacket(GamerInformationPacket packet) {
        CommonHackShieldUser user = this.connection.getUser();
        user.setHardwareIds(packet.getHardwareIds());
        user.setMinecraftID(packet.getMinecraftAccountID());
        user.setToken(packet.getAuthToken());
    }

    public void handlePacket(TransferUserInformationPacket packet) {
        throw new UnsupportedOperationException("HackShieldPacketHandler#handlePacket unsupported for TransferUserInformation packet!");
    }

    public void handlePacket(TransferUserInformationRequestPacket packet) {
        Channel channel = this.connection.getConnection().getChannel();
        channel.writeAndFlush((Object)new TransferUserInformationPacket(this.connection.getUser()), channel.voidPromise());
    }
}

