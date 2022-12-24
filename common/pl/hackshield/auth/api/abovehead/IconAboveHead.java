/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.abovehead;

import java.util.UUID;

public class IconAboveHead {
    private final int entityId;
    private UUID iconId;
    private int visibleWidth;
    private int visibleHeight;

    public IconAboveHead(UUID iconId, int entityId, int visibleWidth, int visibleHeight) {
        this.entityId = entityId;
        this.iconId = iconId;
        this.visibleWidth = Math.abs(visibleWidth);
        this.visibleHeight = Math.abs(visibleHeight);
    }

    public IconAboveHead(int entityId) {
        this.entityId = entityId;
    }

    public IconAboveHead setIconId(UUID iconId) {
        this.iconId = iconId;
        return this;
    }

    public IconAboveHead setVisibleWidth(int visibleWidth) {
        this.visibleWidth = visibleWidth;
        return this;
    }

    public IconAboveHead setVisibleHeight(int visibleHeight) {
        this.visibleHeight = visibleHeight;
        return this;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getIconId() {
        return this.iconId;
    }

    public int getVisibleWidth() {
        return this.visibleWidth;
    }

    public int getVisibleHeight() {
        return this.visibleHeight;
    }
}

