/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.Objects;
import net.kyori.adventure.text.AbstractComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.NBTComponentBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractNBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
extends AbstractComponentBuilder<C, B>
implements NBTComponentBuilder<C, B> {
    @Nullable
    protected String nbtPath;
    protected boolean interpret = false;
    @Nullable
    protected Component separator;

    AbstractNBTComponentBuilder() {
    }

    AbstractNBTComponentBuilder(@NotNull C component) {
        super(component);
        this.nbtPath = component.nbtPath();
        this.interpret = component.interpret();
        this.separator = component.separator();
    }

    @Override
    @NotNull
    public B nbtPath(@NotNull String nbtPath) {
        this.nbtPath = Objects.requireNonNull(nbtPath, "nbtPath");
        return (B)this;
    }

    @Override
    @NotNull
    public B interpret(boolean interpret) {
        this.interpret = interpret;
        return (B)this;
    }

    @Override
    @NotNull
    public B separator(@Nullable ComponentLike separator) {
        this.separator = ComponentLike.unbox(separator);
        return (B)this;
    }
}

