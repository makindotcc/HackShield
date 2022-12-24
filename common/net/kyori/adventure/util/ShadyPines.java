/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.util;

import java.util.Set;
import net.kyori.adventure.util.MonkeyBars;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class ShadyPines {
    private ShadyPines() {
    }

    @Deprecated
    @SafeVarargs
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static <E extends Enum<E>> Set<E> enumSet(Class<E> type, E ... constants) {
        return MonkeyBars.enumSet(type, constants);
    }

    public static boolean equals(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    public static boolean equals(float a, float b) {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b);
    }
}

