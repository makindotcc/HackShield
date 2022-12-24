/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.network.ProtocolVersion
 *  com.velocitypowered.proxy.protocol.packet.Handshake
 */
package pl.hackshield.auth.impl.velocity.wrapper.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import java.util.Objects;
import pl.hackshield.auth.impl.velocity.connection.protocol.packet.Handshake;

public final class WrappedHandshake
implements Handshake {
    private final com.velocitypowered.proxy.protocol.packet.Handshake handshake;

    public WrappedHandshake(com.velocitypowered.proxy.protocol.packet.Handshake handshake) {
        this.handshake = Objects.requireNonNull(handshake, "Handshake cannot be null!");
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.handshake.getProtocolVersion();
    }

    @Override
    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.handshake.setProtocolVersion(protocolVersion);
    }

    @Override
    public String getServerAddress() {
        return this.handshake.getServerAddress();
    }

    @Override
    public void setServerAddress(String serverAddress) {
        this.handshake.setServerAddress(serverAddress);
    }

    @Override
    public int getPort() {
        return this.handshake.getPort();
    }

    @Override
    public void setPort(int port) {
        this.handshake.setPort(port);
    }

    @Override
    public int getNextStatus() {
        return this.handshake.getNextStatus();
    }

    @Override
    public void setNextStatus(int nextStatus) {
        this.handshake.setNextStatus(nextStatus);
    }
}

