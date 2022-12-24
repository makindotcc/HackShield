/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.loader.util.JsonUtil
 */
package pl.hackshield.auth.common.network.packet.clientbound;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import pl.hackshield.auth.api.waypoint.Waypoint;
import pl.hackshield.auth.api.waypoint.WaypointPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.loader.util.JsonUtil;

public class HackShieldWaypointPacket
implements HackShieldPacket {
    private WaypointPacket data;

    public HackShieldWaypointPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        packetBuffer.writeEnum(this.data.getOperation());
        if (this.data.getOperation() == WaypointPacket.Operation.REMOVE_ALL) {
            return;
        }
        Collection<Waypoint> waypoints = this.data.getData();
        packetBuffer.writeVarInt(waypoints.size());
        waypoints.forEach(waypoint -> {
            packetBuffer.writeUUID(waypoint.getUniqueId());
            if (this.data.getOperation() == WaypointPacket.Operation.ADD) {
                packetBuffer.writeUUID(Objects.isNull(waypoint.getIconId()) ? UUID.randomUUID() : waypoint.getIconId());
                packetBuffer.writeVarInt(waypoint.getColor());
                packetBuffer.writeComponent(waypoint.getName());
                packetBuffer.writeDouble(waypoint.getX());
                packetBuffer.writeDouble(waypoint.getY());
                packetBuffer.writeDouble(waypoint.getZ());
            }
            if (this.data.getOperation() == WaypointPacket.Operation.ADD || this.data.getOperation() == WaypointPacket.Operation.UPDATE_PROPERTIES) {
                packetBuffer.writeString(JsonUtil.toJson((Object)waypoint.getOptions()));
            }
            if (this.data.getOperation() == WaypointPacket.Operation.UPDATE_LOCATION) {
                packetBuffer.writeDouble(waypoint.getX());
                packetBuffer.writeDouble(waypoint.getY());
                packetBuffer.writeDouble(waypoint.getZ());
            }
        });
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
    }

    public HackShieldWaypointPacket(WaypointPacket data) {
        this.data = data;
    }
}

