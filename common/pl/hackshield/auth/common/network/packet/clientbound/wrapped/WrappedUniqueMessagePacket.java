/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Optional;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.message.UniqueMessagePacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class WrappedUniqueMessagePacket
implements HackShieldPacket {
    private final UniqueMessagePacket packet;

    public WrappedUniqueMessagePacket(HsPacket packet) {
        this.packet = Optional.of(packet).map(p -> (UniqueMessagePacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to UniqueMessagePacket"));
    }

    public WrappedUniqueMessagePacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.packet.getId());
        packetBuffer.writeComponent(this.packet.getMessage());
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

