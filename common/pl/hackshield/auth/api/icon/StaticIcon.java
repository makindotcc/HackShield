/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.icon;

import java.util.UUID;
import pl.hackshield.auth.api.icon.Icon;
import pl.hackshield.auth.api.icon.IconType;

public class StaticIcon
implements Icon {
    private final UUID id;
    private final byte[] texture;

    public StaticIcon(UUID id, byte[] texture) {
        this.id = id;
        this.texture = texture;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public final IconType getType() {
        return IconType.STATIC;
    }

    public byte[] getTexture() {
        return this.texture;
    }
}

