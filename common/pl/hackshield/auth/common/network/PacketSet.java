/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;

public final class PacketSet {
    private final Function<IPacketBuffer, ? extends HackShieldPacket> nullPacketFunction = packetBuffer -> null;
    private final Int2ObjectMap<Function<IPacketBuffer, ? extends HackShieldPacket>> packets = new Int2ObjectOpenHashMap<Function<IPacketBuffer, ? extends HackShieldPacket>>();
    private final Object2IntMap<Class<? extends HackShieldPacket>> classToId = new Object2IntOpenHashMap<Class<? extends HackShieldPacket>>();

    public PacketSet() {
        this.classToId.defaultReturnValue(-1);
    }

    public OptionalInt getId(Class<?> packetClass) {
        Integer packetId = this.classToId.get(packetClass);
        return packetId != null ? OptionalInt.of(packetId) : OptionalInt.empty();
    }

    public HackShieldPacket createPacket(int packetId, IPacketBuffer packetBuffer) {
        return Optional.ofNullable((Function)this.packets.get(packetId)).orElse(this.nullPacketFunction).apply(packetBuffer);
    }

    public boolean registeredPacket(int packetId) {
        return this.packets.containsKey(packetId);
    }

    void registerPacket(Class<? extends HackShieldPacket> packetClass, Function<IPacketBuffer, HackShieldPacket> createFunction, int packetId) {
        boolean wasRegistered;
        if (packetId > 0) {
            packetId = -packetId;
        }
        if (wasRegistered = this.classToId.containsValue(--packetId)) {
            throw new IllegalArgumentException("Packet id " + packetId + " is already assigned to packet!");
        }
        this.classToId.put(packetClass, packetId);
        this.packets.put(packetId, (Function<IPacketBuffer, ? extends HackShieldPacket>)createFunction);
    }
}

