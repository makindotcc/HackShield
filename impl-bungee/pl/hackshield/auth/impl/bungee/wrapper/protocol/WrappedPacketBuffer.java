/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  net.md_5.bungee.protocol.DefinedPacket
 *  net.md_5.bungee.protocol.ProtocolConstants
 *  pl.hackshield.auth.common.network.IPacketBuffer
 */
package pl.hackshield.auth.impl.bungee.wrapper.protocol;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import pl.hackshield.auth.common.network.IPacketBuffer;

public final class WrappedPacketBuffer
implements IPacketBuffer {
    private static final GsonComponentSerializer PRE_1_16_SERIALIZER = GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().build();
    private static final GsonComponentSerializer MODERN_SERIALIZER = GsonComponentSerializer.builder().build();
    private final ByteBuf buffer;

    public WrappedPacketBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public void writeByteArray(byte[] bytes) {
        DefinedPacket.writeArray((byte[])bytes, (ByteBuf)this.buffer);
    }

    public byte[] readByteArray() {
        return DefinedPacket.readArray((ByteBuf)this.buffer);
    }

    public void writeDouble(double value) {
        this.buffer.writeDouble(value);
    }

    public void writeEnum(Enum<?> enumObject) {
        this.writeVarInt(enumObject.ordinal());
    }

    public void writeComponent(Component component) {
        this.writeString((String)MODERN_SERIALIZER.serialize(component));
    }

    public Component readComponent() {
        return MODERN_SERIALIZER.deserialize((Object)this.readString());
    }

    public void writeVarInt(int value) {
        DefinedPacket.writeVarInt((int)value, (ByteBuf)this.buffer);
    }

    public int readVarInt() {
        return DefinedPacket.readVarInt((ByteBuf)this.buffer);
    }

    public void writeVarLong(long value) {
        throw new RuntimeException("todo add");
    }

    public long readVarLong() {
        throw new RuntimeException("todo add");
    }

    public void writeUUID(UUID uuid) {
        DefinedPacket.writeUUID((UUID)uuid, (ByteBuf)this.buffer);
    }

    public UUID readUUID() {
        return DefinedPacket.readUUID((ByteBuf)this.buffer);
    }

    public void writeStringArray(String[] array) {
        this.writeVarInt(array.length);
        for (String value : array) {
            this.writeString(value);
        }
    }

    public String[] readStringArray() {
        return this.readStringArray(32767);
    }

    public String[] readStringArray(int maxStringLength) {
        int arrayLength = this.readVarInt();
        String[] array = new String[arrayLength];
        for (int i = 0; i < arrayLength; ++i) {
            array[i] = this.readString(maxStringLength);
        }
        return array;
    }

    public void writeString(String value) {
        DefinedPacket.writeString((String)value, (ByteBuf)this.buffer);
    }

    public String readString() {
        return DefinedPacket.readString((ByteBuf)this.buffer);
    }

    public String readString(int maxLength) {
        return DefinedPacket.readString((ByteBuf)this.buffer, (int)maxLength);
    }

    public ByteBuf getBuffer() {
        return this.buffer;
    }

    private static GsonComponentSerializer getJsonChatSerializer(int version) {
        return ProtocolConstants.isAfterOrEq((int)735, (int)version) ? MODERN_SERIALIZER : PRE_1_16_SERIALIZER;
    }
}

