/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Optional;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.abovehead.IconAboveHead;
import pl.hackshield.auth.api.abovehead.IconAboveHeadPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class WrappedIconAboveHeadPacket
implements HackShieldPacket {
    private final IconAboveHeadPacket packet;

    public WrappedIconAboveHeadPacket(HsPacket packet) {
        this.packet = Optional.ofNullable(packet).map(p -> (IconAboveHeadPacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to WaypointPacket"));
    }

    public WrappedIconAboveHeadPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        IconAboveHead info = this.packet.getData();
        packetBuffer.writeEnum(this.packet.getOperation());
        packetBuffer.writeVarInt(info.getEntityId());
        if (this.packet.getOperation() == IconAboveHeadPacket.Operation.START) {
            packetBuffer.writeUUID(info.getIconId());
            packetBuffer.writeVarInt(info.getVisibleWidth());
            packetBuffer.writeVarInt(info.getVisibleHeight());
        }
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

