/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.event;

import java.util.function.UnaryOperator;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HoverEventSource<V> {
    @Nullable
    public static <V> HoverEvent<V> unbox(@Nullable HoverEventSource<V> source) {
        return source != null ? source.asHoverEvent() : null;
    }

    @NotNull
    default public HoverEvent<V> asHoverEvent() {
        return this.asHoverEvent(UnaryOperator.identity());
    }

    @NotNull
    public HoverEvent<V> asHoverEvent(@NotNull UnaryOperator<V> var1);
}

