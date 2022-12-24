/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;

public interface HackShieldPacket {
    public void write(IPacketBuffer var1);

    public void handlePacket(PacketHandler var1);
}

