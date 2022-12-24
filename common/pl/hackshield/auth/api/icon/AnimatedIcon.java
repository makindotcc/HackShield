/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.icon;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.icon.IconType;

public class AnimatedIcon
implements Icon {
    private final UUID id;
    private final int cycles;
    private final Map<UUID, Long> frames;

    public AnimatedIcon(UUID id, int cycles, Map<UUID, Long> frames) {
        this.id = id;
        this.cycles = cycles;
        this.frames = frames;
    }

    public AnimatedIcon(UUID id, int cycles) {
        this.id = id;
        this.cycles = cycles;
        this.frames = new HashMap<UUID, Long>();
    }

    public AnimatedIcon addFrame(UUID iconId, long duration) {
        this.frames.put(iconId, duration);
        return this;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public final IconType getType() {
        return IconType.ANIMATED;
    }

    public int getCycles() {
        return this.cycles;
    }

    public Map<UUID, Long> getFrames() {
        return this.frames;
    }
}

