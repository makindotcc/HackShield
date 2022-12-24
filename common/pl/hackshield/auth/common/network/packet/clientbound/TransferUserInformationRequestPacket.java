/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class TransferUserInformationRequestPacket
implements HackShieldPacket {
    public TransferUserInformationRequestPacket(IPacketBuffer packetBuffer) {
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        packetHandler.handlePacket(this);
    }

    public TransferUserInformationRequestPacket() {
    }
}

