/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.loader.util.JsonUtil
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import pl.hackshield.auth.common.data.ServerSettings;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.loader.util.JsonUtil;

public final class ServerSettingsPacket
implements HackShieldPacket {
    private final ServerSettings serverSettings;

    public ServerSettingsPacket(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    public ServerSettingsPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeString(JsonUtil.toJson((Object)this.serverSettings));
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

