/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Optional;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.tablist.ForceTablistPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class WrappedForceTablistPacket
implements HackShieldPacket {
    private final ForceTablistPacket packet;

    public WrappedForceTablistPacket(HsPacket packet) {
        this.packet = Optional.ofNullable(packet).map(p -> (ForceTablistPacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to ForceTablistPacket"));
    }

    public WrappedForceTablistPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.getBuffer().writeBoolean(this.packet.isShow());
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

