/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public interface ComponentMessageThrowable {
    @Nullable
    public static Component getMessage(@Nullable Throwable throwable) {
        if (throwable instanceof ComponentMessageThrowable) {
            return ((ComponentMessageThrowable)((Object)throwable)).componentMessage();
        }
        return null;
    }

    @Nullable
    public static Component getOrConvertMessage(@Nullable Throwable throwable) {
        String message;
        if (throwable instanceof ComponentMessageThrowable) {
            return ((ComponentMessageThrowable)((Object)throwable)).componentMessage();
        }
        if (throwable != null && (message = throwable.getMessage()) != null) {
            return Component.text(message);
        }
        return null;
    }

    @Nullable
    public Component componentMessage();
}

