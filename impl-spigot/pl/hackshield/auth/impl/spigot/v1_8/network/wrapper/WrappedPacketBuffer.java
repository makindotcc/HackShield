/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  net.minecraft.server.v1_8_R3.PacketDataSerializer
 *  pl.hackshield.auth.common.network.IPacketBuffer
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import pl.hackshield.auth.common.network.IPacketBuffer;

public class WrappedPacketBuffer
implements IPacketBuffer {
    private static final GsonComponentSerializer MODERN_SERIALIZER = GsonComponentSerializer.builder().build();
    private final PacketDataSerializer serializer;

    public WrappedPacketBuffer(ByteBuf packetDataSerializer) {
        this.serializer = new PacketDataSerializer(packetDataSerializer);
    }

    public PacketDataSerializer getSerializer() {
        return this.serializer;
    }

    public void writeByteArray(byte[] bytes) {
        this.serializer.a(bytes);
    }

    public byte[] readByteArray() {
        return this.serializer.readByteArray(this.serializer.readableBytes());
    }

    public void writeDouble(double value) {
        this.serializer.writeDouble(value);
    }

    public void writeEnum(Enum<?> enumObject) {
        this.serializer.a(enumObject);
    }

    public void writeComponent(Component component) {
        this.writeString((String)MODERN_SERIALIZER.serialize(component));
    }

    public Component readComponent() {
        return MODERN_SERIALIZER.deserialize((Object)this.readString());
    }

    public void writeStringArray(String[] array) {
        this.writeVarInt(array.length);
        for (String value : array) {
            this.writeString(value);
        }
    }

    public String[] readStringArray() {
        String[] array = new String[this.readVarInt()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.readString(65536);
        }
        return array;
    }

    public String[] readStringArray(int maxStringLength) {
        int arrayLength = this.readVarInt();
        String[] array = new String[arrayLength];
        for (int i = 0; i < arrayLength; ++i) {
            array[i] = this.readString(maxStringLength);
        }
        return array;
    }

    public void writeVarInt(int value) {
        this.serializer.b(value);
    }

    public int readVarInt() {
        return this.serializer.e();
    }

    public void writeVarLong(long value) {
        this.serializer.b(value);
    }

    public long readVarLong() {
        return this.serializer.f();
    }

    public void writeUUID(UUID uuid) {
        this.serializer.a(uuid);
    }

    public UUID readUUID() {
        return this.serializer.g();
    }

    public void writeString(String value) {
        this.serializer.a(value);
    }

    public String readString() {
        return this.readString(32767);
    }

    public String readString(int maxLength) {
        return this.serializer.c(maxLength);
    }

    public ByteBuf getBuffer() {
        return this.serializer;
    }
}

