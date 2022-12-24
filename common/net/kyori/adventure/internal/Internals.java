/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.internal;

import net.kyori.examination.Examinable;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class Internals {
    private Internals() {
    }

    @NotNull
    public static String toString(@NotNull Examinable examinable) {
        return examinable.examine(StringExaminer.simpleEscaping());
    }
}

