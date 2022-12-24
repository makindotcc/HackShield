/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package pl.hackshield.auth.common.network;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public interface IPacketBuffer {
    public void writeByteArray(byte[] var1);

    public byte[] readByteArray();

    public void writeDouble(double var1);

    public void writeEnum(Enum<?> var1);

    public void writeComponent(Component var1);

    public Component readComponent();

    public void writeVarInt(int var1);

    public int readVarInt();

    public void writeVarLong(long var1);

    public long readVarLong();

    public void writeUUID(UUID var1);

    public UUID readUUID();

    public void writeStringArray(String[] var1);

    public String[] readStringArray();

    public String[] readStringArray(int var1);

    public void writeString(String var1);

    public String readString();

    public String readString(int var1);

    public ByteBuf getBuffer();
}

