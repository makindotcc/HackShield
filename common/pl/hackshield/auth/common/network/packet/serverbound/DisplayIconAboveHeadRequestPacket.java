/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.serverbound;

import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class DisplayIconAboveHeadRequestPacket
implements HackShieldPacket {
    public DisplayIconAboveHeadRequestPacket(IPacketBuffer packetBuffer) {
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#write unsupported for serverbound packet!");
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
    }
}

