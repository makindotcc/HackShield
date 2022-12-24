/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.region;

import java.awt.Color;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public final class RegionInfo {
    private final UUID id;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private boolean allowBuilding;
    private boolean allowDestroying;
    private boolean renderRegionBlocks;
    private int regionBlocksColor = 11393254;
    private Component cancelBuildMessage;
    private Component cancelBreakMessage;
    private Set<Long> ignoredBlockLocations;

    public RegionInfo(UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean allowBuilding, boolean allowDestroying, boolean renderRegionBlocks, Set<Long> ignoredBlockLocations) {
        this.id = id;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.allowBuilding = allowBuilding;
        this.allowDestroying = allowDestroying;
        this.renderRegionBlocks = renderRegionBlocks;
        this.ignoredBlockLocations = ignoredBlockLocations;
    }

    public RegionInfo(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean allowBuilding, boolean allowDestroying, boolean renderRegionBlocks, Set<Long> ignoredBlockLocations) {
        this(UUID.randomUUID(), minX, minY, minZ, maxX, maxY, maxZ, allowBuilding, allowDestroying, renderRegionBlocks, ignoredBlockLocations);
    }

    public UUID getId() {
        return this.id;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getMaxZ() {
        return this.maxZ;
    }

    public boolean shouldRenderRegionBlocks() {
        return this.renderRegionBlocks;
    }

    public boolean isBuildingAllowed() {
        return this.allowBuilding;
    }

    public boolean isDestroyingAllowed() {
        return this.allowDestroying;
    }

    public int getRegionBlocksColor() {
        return this.regionBlocksColor;
    }

    public Set<Long> getIgnoredBlockLocations() {
        return this.ignoredBlockLocations;
    }

    public RegionInfo setRegionBlocksColor(int regionBlocksColor) {
        this.regionBlocksColor = regionBlocksColor;
        return this;
    }

    public RegionInfo setRegionBlocksColor(Color regionBlocksColor) {
        this.regionBlocksColor = regionBlocksColor.getRGB();
        return this;
    }

    public RegionInfo setShouldRenderRegionBlocks(boolean renderRegionBlocks) {
        this.renderRegionBlocks = renderRegionBlocks;
        return this;
    }

    public RegionInfo setBuildingAllowed(boolean buildingAllowed) {
        this.allowBuilding = buildingAllowed;
        return this;
    }

    public RegionInfo setDestroyingAllowed(boolean destroyingAllowed) {
        this.allowDestroying = destroyingAllowed;
        return this;
    }

    public boolean hasCancelBreakMessage() {
        return Objects.nonNull(this.cancelBreakMessage);
    }

    public boolean hasCancelBuildMessage() {
        return Objects.nonNull(this.cancelBuildMessage);
    }

    public Component getCancelBreakMessage() {
        return this.cancelBreakMessage;
    }

    public Component getCancelBuildMessage() {
        return this.cancelBuildMessage;
    }

    public RegionInfo setCancelBreakMessage(Component cancelBreakMessage) {
        this.cancelBreakMessage = cancelBreakMessage;
        return this;
    }

    public RegionInfo setCancelBuildMessage(Component cancelBuildMessage) {
        this.cancelBuildMessage = cancelBuildMessage;
        return this;
    }

    public RegionInfo setMinX(int minX) {
        this.minX = minX;
        return this;
    }

    public RegionInfo setMinY(int minY) {
        this.minY = minY;
        return this;
    }

    public RegionInfo setMinZ(int minZ) {
        this.minZ = minZ;
        return this;
    }

    public RegionInfo setMin(int minX, int minY, int minZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        return this;
    }

    public RegionInfo setMaxX(int maxX) {
        this.maxX = maxX;
        return this;
    }

    public RegionInfo setMaxY(int maxY) {
        this.maxY = maxY;
        return this;
    }

    public RegionInfo setMaxZ(int maxZ) {
        this.maxZ = maxZ;
        return this;
    }

    public RegionInfo setMax(int maxX, int maxY, int maxZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }

    public RegionInfo setIgnoredBlockLocations(Set<Long> ignoredBlockLocations) {
        this.ignoredBlockLocations = ignoredBlockLocations;
        return this;
    }
}

