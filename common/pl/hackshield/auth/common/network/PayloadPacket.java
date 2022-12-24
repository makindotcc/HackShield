/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

import pl.hackshield.auth.common.network.IPacketBuffer;

public abstract class PayloadPacket {
    private final String channel;

    public PayloadPacket(String channel) {
        this.channel = channel;
    }

    public abstract void write(IPacketBuffer var1);

    public String getChannel() {
        return this.channel;
    }
}

