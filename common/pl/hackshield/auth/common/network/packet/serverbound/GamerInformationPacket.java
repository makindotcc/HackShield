/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.serverbound;

import java.util.UUID;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class GamerInformationPacket
implements HackShieldPacket {
    private final UUID minecraftAccountID;
    private final String authToken;

    public GamerInformationPacket(IPacketBuffer packetBuffer) {
        this.minecraftAccountID = packetBuffer.readUUID();
        this.authToken = packetBuffer.readString(1024);
        if (packetBuffer.getBuffer().isReadable()) {
            packetBuffer.getBuffer().skipBytes(packetBuffer.getBuffer().readableBytes());
        }
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#write unsupported for serverbound packet!");
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        packetHandler.handlePacket(this);
    }

    public UUID getMinecraftAccountID() {
        return this.minecraftAccountID;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}

