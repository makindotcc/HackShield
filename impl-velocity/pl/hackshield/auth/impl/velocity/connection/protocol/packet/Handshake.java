/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.network.ProtocolVersion
 */
package pl.hackshield.auth.impl.velocity.connection.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;

public interface Handshake {
    public ProtocolVersion getProtocolVersion();

    public void setProtocolVersion(ProtocolVersion var1);

    public String getServerAddress();

    public void setServerAddress(String var1);

    public int getPort();

    public void setPort(int var1);

    public int getNextStatus();

    public void setNextStatus(int var1);
}

