/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.PacketSet;

public final class ConnectionProtocol
extends Enum<ConnectionProtocol> {
    public static final /* enum */ ConnectionProtocol UNKNOWN = new ConnectionProtocol(-100);
    public static final /* enum */ ConnectionProtocol HANDSHAKING = new ConnectionProtocol(-1);
    public static final /* enum */ ConnectionProtocol PLAY = new ConnectionProtocol(0);
    public static final /* enum */ ConnectionProtocol STATUS = new ConnectionProtocol(1);
    public static final /* enum */ ConnectionProtocol LOGIN = new ConnectionProtocol(2);
    private static final Map<Class<? extends HackShieldPacket>, ConnectionProtocol> PROTOCOL_BY_PACKET;
    private static final ConnectionProtocol[] LOOKUP;
    private static final Map<Class<? extends HsPacket>, Function<HsPacket, ? extends HackShieldPacket>> PACKET_WRAPPERS;
    private final int id;
    private final EnumMap<PacketFlow, PacketSet> flows;
    private static final /* synthetic */ ConnectionProtocol[] $VALUES;

    public static ConnectionProtocol[] values() {
        return (ConnectionProtocol[])$VALUES.clone();
    }

    public static ConnectionProtocol valueOf(String name) {
        return Enum.valueOf(ConnectionProtocol.class, name);
    }

    private ConnectionProtocol(int id) {
        this.id = id;
        this.flows = new EnumMap(PacketFlow.class);
        Stream.of(PacketFlow.values()).forEach(packetFlow -> this.flows.put((PacketFlow)((Object)packetFlow), new PacketSet()));
    }

    public int getId() {
        return this.id;
    }

    public OptionalInt getPacketId(PacketFlow packetFlow, Class<? extends HackShieldPacket> packetClass) {
        return this.flows.get((Object)packetFlow).getId(packetClass);
    }

    public void registerPacket(PacketFlow flow, Class<? extends HackShieldPacket> packetClass, Function<IPacketBuffer, HackShieldPacket> createFunction, int packetId) {
        if (this == UNKNOWN) {
            throw new IllegalArgumentException("Registering packets in UNKNOWN state is disallowed!");
        }
        this.flows.get((Object)flow).registerPacket(packetClass, createFunction, packetId);
        PROTOCOL_BY_PACKET.put(packetClass, this);
    }

    public void registerPacket(PacketFlow flow, Class<? extends HsPacket> apiPacket, Class<? extends HackShieldPacket> packetClass, Function<HsPacket, HackShieldPacket> wrapFunction, Function<IPacketBuffer, HackShieldPacket> createFunction, int packetId) {
        this.registerPacketWrapper(apiPacket, wrapFunction);
        this.registerPacket(flow, packetClass, createFunction, packetId);
    }

    void registerPacketWrapper(Class<? extends HsPacket> apiClass, Function<HsPacket, HackShieldPacket> wrapFunction) {
        PACKET_WRAPPERS.put(apiClass, wrapFunction);
    }

    public static HackShieldPacket warpPacket(HsPacket packet) {
        return PACKET_WRAPPERS.get(packet.getClass()).apply(packet);
    }

    @Nullable
    public HackShieldPacket createPacket(PacketFlow packetFlow, int id, IPacketBuffer packetBuffer) {
        return this.flows.get((Object)packetFlow).createPacket(id, packetBuffer);
    }

    public boolean registeredPacket(PacketFlow packetFlow, int id) {
        return this.flows.get((Object)packetFlow).registeredPacket(id);
    }

    @Nullable
    public static ConnectionProtocol getById(int id) {
        return id >= -1 && id <= 2 ? LOOKUP[id + 1] : UNKNOWN;
    }

    public static ConnectionProtocol getProtocolForPacket(HackShieldPacket packet) {
        return PROTOCOL_BY_PACKET.get(packet.getClass());
    }

    private static /* synthetic */ ConnectionProtocol[] $values() {
        return new ConnectionProtocol[]{UNKNOWN, HANDSHAKING, PLAY, STATUS, LOGIN};
    }

    static {
        $VALUES = ConnectionProtocol.$values();
        PROTOCOL_BY_PACKET = new HashMap<Class<? extends HackShieldPacket>, ConnectionProtocol>();
        LOOKUP = new ConnectionProtocol[4];
        PACKET_WRAPPERS = new HashMap<Class<? extends HsPacket>, Function<HsPacket, ? extends HackShieldPacket>>();
        Stream.of(ConnectionProtocol.values()).filter(connectionProtocol -> connectionProtocol != UNKNOWN).forEach(connectionProtocol -> {
            int id = connectionProtocol.getId();
            if (id < -1 || id > 2) {
                throw new Error("Invalid protocol ID " + id);
            }
            ConnectionProtocol.LOOKUP[id + 1] = connectionProtocol;
        });
    }
}

