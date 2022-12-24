/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import java.util.List;
import pl.hackshield.auth.common.data.Mod;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PayloadPacket;

public class ModListPacket
extends PayloadPacket {
    private final List<Mod> modList;

    public ModListPacket(List<Mod> modList) {
        super("hs:mod_list");
        this.modList = modList;
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.modList.size());
        this.modList.forEach(mod -> {
            packetBuffer.writeString(mod.getName());
            packetBuffer.getBuffer().writeBoolean(mod.isEnabled());
        });
    }
}

