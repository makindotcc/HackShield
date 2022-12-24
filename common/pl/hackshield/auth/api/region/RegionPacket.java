/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.region;

import java.util.Collection;
import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.region.RegionInfo;

public final class RegionPacket
implements HsPacket {
    private Operation operation;
    private Collection<RegionInfo> data;

    public RegionPacket(Operation operation, Collection<RegionInfo> regionInfos) {
        this.operation = operation;
        this.data = regionInfos;
    }

    public RegionPacket() {
        this.operation = Operation.REMOVE_ALL;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public Collection<RegionInfo> getData() {
        return this.data;
    }

    public void setData(Collection<RegionInfo> data) {
        this.data = data;
    }

    public static enum Operation {
        ADD,
        REMOVE,
        REMOVE_ALL,
        UPDATE_POS,
        UPDATE_FLAGS,
        UPDATE_MSG,
        UPDATE_OPTIONS,
        UPDATE_IGNORED_BLOCKS;

    }
}

