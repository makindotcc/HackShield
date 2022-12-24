/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.format;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.format.StyleSetter;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface MutableStyleSetter<T extends MutableStyleSetter<?>>
extends StyleSetter<T> {
    @Override
    @Contract(value="_ -> this")
    @NotNull
    default public T decorate(TextDecoration ... decorations) {
        int length = decorations.length;
        for (int i = 0; i < length; ++i) {
            this.decorate(decorations[i]);
        }
        return (T)this;
    }

    @Override
    @Contract(value="_ -> this")
    @NotNull
    default public T decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
        Objects.requireNonNull(decorations, "decorations");
        for (Map.Entry<TextDecoration, TextDecoration.State> entry : decorations.entrySet()) {
            this.decoration(entry.getKey(), entry.getValue());
        }
        return (T)this;
    }

    @Override
    @Contract(value="_, _ -> this")
    @NotNull
    default public T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
        TextDecoration.State state = TextDecoration.State.byBoolean(flag);
        decorations.forEach(decoration -> this.decoration((TextDecoration)decoration, state));
        return (T)this;
    }
}

