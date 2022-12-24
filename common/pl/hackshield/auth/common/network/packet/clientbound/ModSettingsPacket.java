/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import java.util.List;
import pl.hackshield.auth.common.data.Mod;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PayloadPacket;

public class ModSettingsPacket
extends PayloadPacket {
    private final Mod mod;

    public ModSettingsPacket(Mod mod) {
        super("hs:mod_settings");
        this.mod = mod;
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeString(this.mod.getName());
        packetBuffer.writeVarInt(this.mod.getSettings().size());
        this.mod.getSettings().forEach((name, value) -> {
            packetBuffer.writeString((String)name);
            if (value instanceof Boolean) {
                packetBuffer.getBuffer().writeBoolean(((Boolean)value).booleanValue());
            } else if (value instanceof String) {
                packetBuffer.writeString((String)value);
            } else if (value instanceof Integer) {
                packetBuffer.writeVarInt((Integer)value);
            } else if (value instanceof List) {
                packetBuffer.writeString(String.join((CharSequence)"\u0000", (List)value));
            }
        });
    }
}

