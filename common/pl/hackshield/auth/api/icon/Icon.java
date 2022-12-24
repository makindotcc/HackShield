/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.icon;

import java.util.UUID;
import pl.hackshield.auth.api.icon.IconType;

public interface Icon {
    public UUID getId();

    public IconType getType();
}

