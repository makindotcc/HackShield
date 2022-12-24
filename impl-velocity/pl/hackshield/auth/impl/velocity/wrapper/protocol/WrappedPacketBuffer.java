/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.network.ProtocolVersion
 *  com.velocitypowered.proxy.protocol.ProtocolUtils
 *  com.velocitypowered.proxy.protocol.util.VelocityLegacyHoverEventSerializer
 *  io.netty.buffer.ByteBuf
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  pl.hackshield.auth.common.network.IPacketBuffer
 */
package pl.hackshield.auth.impl.velocity.wrapper.protocol;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.util.VelocityLegacyHoverEventSerializer;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import pl.hackshield.auth.common.network.IPacketBuffer;

public final class WrappedPacketBuffer
implements IPacketBuffer {
    private static final GsonComponentSerializer PRE_1_16_SERIALIZER = GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().legacyHoverEventSerializer(VelocityLegacyHoverEventSerializer.INSTANCE).build();
    private static final GsonComponentSerializer MODERN_SERIALIZER = GsonComponentSerializer.builder().legacyHoverEventSerializer(VelocityLegacyHoverEventSerializer.INSTANCE).build();
    private final ByteBuf buffer;

    public WrappedPacketBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public void writeByteArray(byte[] bytes) {
        ProtocolUtils.writeByteArray((ByteBuf)this.buffer, (byte[])bytes);
    }

    public byte[] readByteArray() {
        return ProtocolUtils.readByteArray((ByteBuf)this.buffer);
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
        ProtocolUtils.writeVarInt((ByteBuf)this.buffer, (int)value);
    }

    public int readVarInt() {
        return ProtocolUtils.readVarInt((ByteBuf)this.buffer);
    }

    public void writeVarLong(long value) {
        throw new RuntimeException("todo add");
    }

    public long readVarLong() {
        throw new RuntimeException("todo add");
    }

    public void writeUUID(UUID uuid) {
        ProtocolUtils.writeUuid((ByteBuf)this.buffer, (UUID)uuid);
    }

    public UUID readUUID() {
        return ProtocolUtils.readUuid((ByteBuf)this.buffer);
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
        ProtocolUtils.writeString((ByteBuf)this.buffer, (CharSequence)value);
    }

    public String readString() {
        return ProtocolUtils.readString((ByteBuf)this.buffer);
    }

    public String readString(int maxLength) {
        return ProtocolUtils.readString((ByteBuf)this.buffer, (int)maxLength);
    }

    public ByteBuf getBuffer() {
        return this.buffer;
    }

    private static GsonComponentSerializer getJsonChatSerializer(ProtocolVersion version) {
        return version.compareTo((Enum)ProtocolVersion.MINECRAFT_1_16) >= 0 ? MODERN_SERIALIZER : PRE_1_16_SERIALIZER;
    }
}

