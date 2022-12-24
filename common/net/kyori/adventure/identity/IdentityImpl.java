/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.identity;

import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.internal.Internals;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class IdentityImpl
implements Examinable,
Identity {
    private final UUID uuid;

    IdentityImpl(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    @NotNull
    public UUID uuid() {
        return this.uuid;
    }

    public String toString() {
        return Internals.toString(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Identity)) {
            return false;
        }
        Identity that = (Identity)other;
        return this.uuid.equals(that.uuid());
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }
}

