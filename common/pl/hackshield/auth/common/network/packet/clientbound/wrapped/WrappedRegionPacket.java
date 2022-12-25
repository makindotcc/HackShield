/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.loader.util.JsonUtil
 */
package pl.hackshield.auth.common.network.packet.clientbound.wrapped;

import java.util.Collection;
import java.util.Optional;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.region.RegionInfo;
import pl.hackshield.auth.api.region.RegionPacket;
import pl.hackshield.auth.common.network.HackShieldPacket;
import pl.hackshield.auth.common.network.IPacketBuffer;
import pl.hackshield.auth.common.network.PacketHandler;
import pl.hackshield.auth.common.util.PacketUtil;
import pl.hackshield.auth.loader.util.JsonUtil;

public final class WrappedRegionPacket
implements HackShieldPacket {
    private final RegionPacket packet;

    public WrappedRegionPacket(HsPacket packet) {
        this.packet = Optional.ofNullable(packet).map(p -> (RegionPacket)p).orElseThrow(() -> new IllegalArgumentException("Cannot wrap packet to RegionPacket"));
    }

    public WrappedRegionPacket(IPacketBuffer packetBuffer) {
        throw new UnsupportedOperationException("HackShieldPacket#read unsupported for clientbound packet!");
    }

    @Override
    public void write(IPacketBuffer packetBuffer) {
        RegionPacket.Operation operation = this.packet.getOperation();
        packetBuffer.writeEnum(operation);
        if (operation == RegionPacket.Operation.REMOVE_ALL) {
            return;
        }
        Collection<RegionInfo> regions = this.packet.getData();
        packetBuffer.writeVarInt(regions.size());
        regions.forEach(region -> {
            if (operation != RegionPacket.Operation.ADD) {
                packetBuffer.writeUUID(region.getId());
            }
            switch (operation) {
                case ADD: {
                    packetBuffer.writeString(JsonUtil.toJson((Object)region));
                    break;
                }
                case UPDATE_MSG: {
                    packetBuffer.writeComponent(region.getCancelBreakMessage());
                    packetBuffer.writeComponent(region.getCancelBuildMessage());
                    break;
                }
                case UPDATE_POS: {
                    packetBuffer.writeVarInt(region.getMinX());
                    packetBuffer.writeVarInt(region.getMinY());
                    packetBuffer.writeVarInt(region.getMinZ());
                    packetBuffer.writeVarInt(region.getMaxX());
                    packetBuffer.writeVarInt(region.getMaxY());
                    packetBuffer.writeVarInt(region.getMaxZ());
                    break;
                }
                case UPDATE_FLAGS: {
                    packetBuffer.getBuffer().writeByte(PacketUtil.getFlags(region));
                    break;
                }
                case UPDATE_OPTIONS: {
                    packetBuffer.writeVarInt(region.getRegionBlocksColor());
                    break;
                }
                case UPDATE_IGNORED_BLOCKS: {
                    packetBuffer.writeVarInt(region.getIgnoredBlockLocations().size());
                    region.getIgnoredBlockLocations().forEach(packetBuffer::writeVarLong);
                }
            }
        });
    }

    @Override
    public void handlePacket(PacketHandler packetHandler) {
        throw new UnsupportedOperationException("HackShieldPacket#handle unsupported for clientbound packet!");
    }
}

