/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

import pl.hackshield.auth.common.network.packet.clientbound.TransferUserInformationRequestPacket;
import pl.hackshield.auth.common.network.packet.serverbound.GamerInformationPacket;
import pl.hackshield.auth.common.network.packet.serverbound.HandshakePacket;
import pl.hackshield.auth.common.network.packet.serverbound.TransferUserInformationPacket;

public interface PacketHandler {
    public void handlePacket(HandshakePacket var1);

    public void handlePacket(GamerInformationPacket var1);

    public void handlePacket(TransferUserInformationPacket var1);

    public void handlePacket(TransferUserInformationRequestPacket var1);
}

