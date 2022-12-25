/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Optional;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.bossbar.BossBarPacket;
import pl.hackshield.auth.api.bossbar.BossInfo;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.util.PacketUtil;

public final class WrappedBossBarPacket
implements HackShieldPacket {
    private final BossBarPacket packet;

    public WrappedBossBarPacket(HsPacket packet) {
        this.packet = Optional.ofNullable(packet).map(p -> (BossBarPacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to BossBarPacket"));
    }

    public WrappedBossBarPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        BossInfo info = this.packet.getData();
        packetBuffer.writeUUID(info.getUniqueId());
        packetBuffer.writeEnum(this.packet.getOperation());
        switch (this.packet.getOperation()) {
            case ADD: {
                packetBuffer.writeComponent(info.getName());
                packetBuffer.getBuffer().writeFloat(info.getPercent());
                packetBuffer.writeEnum(info.getColor());
                packetBuffer.writeEnum(info.getOverlay());
                packetBuffer.writeVarInt(PacketUtil.getFlags(info));
                break;
            }
            case UPDATE_PCT: {
                packetBuffer.getBuffer().writeFloat(info.getPercent());
                break;
            }
            case UPDATE_NAME: {
                packetBuffer.writeComponent(info.getName());
                break;
            }
            case UPDATE_STYLE: {
                packetBuffer.writeEnum(info.getColor());
                packetBuffer.writeEnum(info.getOverlay());
                break;
            }
            case UPDATE_PROPERTIES: {
                packetBuffer.writeVarInt(PacketUtil.getFlags(info));
                break;
            }
        }
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

