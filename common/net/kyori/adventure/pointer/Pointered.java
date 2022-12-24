/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface Pointered {
    @NotNull
    default public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
        return this.pointers().get(pointer);
    }

    @Contract(value="_, null -> _; _, !null -> !null")
    @Nullable
    default public <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
        return this.pointers().getOrDefault(pointer, defaultValue);
    }

    default public <T> @UnknownNullability T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
        return this.pointers().getOrDefaultFrom(pointer, defaultValue);
    }

    @NotNull
    default public Pointers pointers() {
        return Pointers.empty();
    }
}
