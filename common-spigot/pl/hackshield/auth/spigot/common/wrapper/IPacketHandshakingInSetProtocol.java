/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.spigot.common.wrapper;

public interface IPacketHandshakingInSetProtocol {
    public int getProtocolVersion();

    public String getServerAddress();

    public void setServerAddress(String var1);

    public int getPort();

    public int getNextStatus();
}

