/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.NBTComponentBuilder;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface StorageNBTComponent
extends NBTComponent<StorageNBTComponent, Builder>,
ScopedComponent<StorageNBTComponent> {
    @NotNull
    public Key storage();

    @Contract(pure=true)
    @NotNull
    public StorageNBTComponent storage(@NotNull Key var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("storage", this.storage())), NBTComponent.super.examinableProperties());
    }

    public static interface Builder
    extends NBTComponentBuilder<StorageNBTComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder storage(@NotNull Key var1);
    }
}

