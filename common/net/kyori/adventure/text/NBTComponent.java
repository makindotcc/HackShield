/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.stream.Stream;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.NBTComponentBuilder;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NBTComponent<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
extends BuildableComponent<C, B> {
    @NotNull
    public String nbtPath();

    @Contract(pure=true)
    @NotNull
    public C nbtPath(@NotNull String var1);

    public boolean interpret();

    @Contract(pure=true)
    @NotNull
    public C interpret(boolean var1);

    @Nullable
    public Component separator();

    @NotNull
    public C separator(@Nullable ComponentLike var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("nbtPath", this.nbtPath()), ExaminableProperty.of("interpret", this.interpret()), ExaminableProperty.of("separator", this.separator())), BuildableComponent.super.examinableProperties());
    }
}

