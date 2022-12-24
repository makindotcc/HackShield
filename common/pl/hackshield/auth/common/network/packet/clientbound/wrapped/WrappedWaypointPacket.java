/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.loader.util.JsonUtil
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.waypoint.Waypoint;
import pl.hackshield.auth.api.waypoint.WaypointPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.loader.util.JsonUtil;

public final class WrappedWaypointPacket
implements HackShieldPacket {
    private final WaypointPacket packet;

    public WrappedWaypointPacket(HsPacket packet) {
        this.packet = Optional.of(packet).map(p -> (WaypointPacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to WaypointPacket"));
    }

    public WrappedWaypointPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        WaypointPacket.Operation operation = this.packet.getOperation();
        packetBuffer.writeEnum(operation);
        if (operation == WaypointPacket.Operation.REMOVE_ALL) {
            return;
        }
        Collection<Waypoint> waypoints = this.packet.getData();
        packetBuffer.writeVarInt(waypoints.size());
        waypoints.forEach(waypoint -> {
            packetBuffer.writeUUID(waypoint.getUniqueId());
            if (operation == WaypointPacket.Operation.ADD) {
                packetBuffer.writeUUID(Objects.isNull(waypoint.getIconId()) ? UUID.randomUUID() : waypoint.getIconId());
                packetBuffer.writeVarInt(waypoint.getColor());
                packetBuffer.writeComponent(waypoint.getName());
                packetBuffer.writeDouble(waypoint.getX());
                packetBuffer.writeDouble(waypoint.getY());
                packetBuffer.writeDouble(waypoint.getZ());
            }
            if (operation == WaypointPacket.Operation.ADD || operation == WaypointPacket.Operation.UPDATE_PROPERTIES) {
                packetBuffer.writeString(JsonUtil.toJson((Object)waypoint.getOptions()));
            }
            if (operation == WaypointPacket.Operation.UPDATE_LOCATION) {
                packetBuffer.writeDouble(waypoint.getX());
                packetBuffer.writeDouble(waypoint.getY());
                packetBuffer.writeDouble(waypoint.getZ());
            }
        });
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

