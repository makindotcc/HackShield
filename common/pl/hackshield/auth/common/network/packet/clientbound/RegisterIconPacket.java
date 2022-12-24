/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import java.util.Map;
import java.util.UUID;
import pl.hackshield.auth.api.icon.AnimatedIcon;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.icon.StaticIcon;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class RegisterIconPacket
implements HackShieldPacket {
    private final Map<UUID, Icon> data;

    public RegisterIconPacket(Map<UUID, Icon> icons) {
        this.data = icons;
    }

    public RegisterIconPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.data.size());
        this.data.forEach((uuid, icon) -> {
            packetBuffer.writeEnum(icon.getType());
            packetBuffer.writeUUID(icon.getId());
            switch (icon.getType()) {
                case STATIC: {
                    packetBuffer.writeByteArray(((StaticIcon)icon).getTexture());
                    break;
                }
                case ANIMATED: {
                    AnimatedIcon animatedIcon = (AnimatedIcon)icon;
                    Map<UUID, Long> frames = animatedIcon.getFrames();
                    packetBuffer.writeVarInt(frames.size());
                    packetBuffer.writeVarInt(animatedIcon.getCycles());
                    frames.forEach((frameIconId, duration) -> {
                        packetBuffer.writeUUID((UUID)frameIconId);
                        packetBuffer.writeVarLong((long)duration);
                    });
                }
            }
        });
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

