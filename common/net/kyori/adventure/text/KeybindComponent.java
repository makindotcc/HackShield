/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface KeybindComponent
extends BuildableComponent<KeybindComponent, Builder>,
ScopedComponent<KeybindComponent> {
    @NotNull
    public String keybind();

    @Override
    @Contract(pure=true)
    @NotNull
    public KeybindComponent keybind(@NotNull String var1);

    @Override
    @Contract(pure=true)
    @NotNull
    default public KeybindComponent keybind(@NotNull KeybindLike keybind) {
        return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("keybind", this.keybind())), BuildableComponent.super.examinableProperties());
    }

    public static interface Builder
    extends ComponentBuilder<KeybindComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder keybind(@NotNull String var1);

        @Contract(pure=true)
        @NotNull
        default public Builder keybind(@NotNull KeybindLike keybind) {
            return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
        }
    }

    public static interface KeybindLike {
        @NotNull
        public String asKeybind();
    }
}

