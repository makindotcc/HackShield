/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.serverbound;

import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public final class HandshakePacket
implements HackShieldPacket {
    private final int protocolVersion;
    private String serverAddress;
    private final int port;
    private final int nextStatus;

    public HandshakePacket(int protocolVersion, String serverAddress, int port, int nextStatus) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.port = port;
        this.nextStatus = nextStatus;
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#write unsupported for serverbound packet!");
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getPort() {
        return this.port;
    }

    public int getNextStatus() {
        return this.nextStatus;
    }
}

