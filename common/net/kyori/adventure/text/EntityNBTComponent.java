/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.stream.Stream;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.NBTComponentBuilder;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface EntityNBTComponent
extends NBTComponent<EntityNBTComponent, Builder>,
ScopedComponent<EntityNBTComponent> {
    @NotNull
    public String selector();

    @Contract(pure=true)
    @NotNull
    public EntityNBTComponent selector(@NotNull String var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("selector", this.selector())), NBTComponent.super.examinableProperties());
    }

    public static interface Builder
    extends NBTComponentBuilder<EntityNBTComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder selector(@NotNull String var1);
    }
}

