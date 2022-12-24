/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.PacketHandshakingInSetProtocol
 *  pl.hackshield.auth.spigot.common.wrapper.IPacketHandshakingInSetProtocol
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.packet;

import java.util.Objects;
import net.minecraft.server.v1_8_R3.PacketHandshakingInSetProtocol;
import pl.hackshield.auth.spigot.common.wrapper.IPacketHandshakingInSetProtocol;

public class WrappedPacketHandshakingInSetProtocol
implements IPacketHandshakingInSetProtocol {
    private final PacketHandshakingInSetProtocol instance;

    public WrappedPacketHandshakingInSetProtocol(Object handler) {
        this.instance = Objects.requireNonNull((PacketHandshakingInSetProtocol)handler, "MinecraftDecoder cannot be null!");
    }

    public int getNextStatus() {
        return this.instance.a().a();
    }

    public int getProtocolVersion() {
        return this.instance.b();
    }

    public String getServerAddress() {
        return this.instance.hostname;
    }

    public void setServerAddress(String serverAddress) {
        this.instance.hostname = serverAddress;
    }

    public int getPort() {
        return this.instance.port;
    }
}

