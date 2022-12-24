/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.bossbar;

import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public class BossInfo {
    private final UUID uniqueId;
    private boolean darkenSky;
    private boolean playEndBossMusic;
    private boolean createFog;
    private boolean renderBar;
    private Overlay overlay;
    private float percent;
    private Component name;
    private Color color;

    public BossInfo(UUID uniqueId, Component name, Color color, Overlay overlay) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.color = color;
        this.overlay = overlay;
        this.percent = 1.0f;
    }

    public BossInfo(Component name, Color color, Overlay overlay) {
        this(UUID.randomUUID(), name, color, overlay);
    }

    public boolean hasDisplayName() {
        return Objects.nonNull(this.name);
    }

    public BossInfo setOverlay(Overlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public BossInfo setPercent(float percent) {
        this.percent = Float.max(percent, 0.0f);
        this.percent = Float.min(1.0f, this.percent);
        return this;
    }

    public BossInfo setName(Component name) {
        this.name = name;
        return this;
    }

    public BossInfo setColor(Color color) {
        this.color = color;
        return this;
    }

    public BossInfo setDarkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }

    public BossInfo setPlayEndBossMusic(boolean playEndBossMusic) {
        this.playEndBossMusic = playEndBossMusic;
        return this;
    }

    public BossInfo setCreateFog(boolean createFog) {
        this.createFog = createFog;
        return this;
    }

    public BossInfo setRenderBar(boolean renderBar) {
        this.renderBar = renderBar;
        return this;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public boolean isDarkenSky() {
        return this.darkenSky;
    }

    public boolean isPlayEndBossMusic() {
        return this.playEndBossMusic;
    }

    public boolean isCreateFog() {
        return this.createFog;
    }

    public boolean isRenderBar() {
        return this.renderBar;
    }

    public Overlay getOverlay() {
        return this.overlay;
    }

    public float getPercent() {
        return this.percent;
    }

    public Component getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public static enum Overlay {
        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20;

    }

    public static enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;

    }
}

