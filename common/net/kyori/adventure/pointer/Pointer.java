/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.pointer;

import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.PointerImpl;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

public interface Pointer<V>
extends Examinable {
    @NotNull
    public static <V> Pointer<V> pointer(@NotNull Class<V> type, @NotNull Key key) {
        return new PointerImpl<V>(type, key);
    }

    @NotNull
    public Class<V> type();

    @NotNull
    public Key key();

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("type", this.type()), ExaminableProperty.of("key", this.key()));
    }
}

