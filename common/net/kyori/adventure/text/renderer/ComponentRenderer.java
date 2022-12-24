/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.renderer;

import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ComponentRenderer<C> {
    @NotNull
    public Component render(@NotNull Component var1, @NotNull C var2);

    default public <T> ComponentRenderer<T> mapContext(Function<T, C> transformer) {
        return (component, ctx) -> this.render(component, transformer.apply(ctx));
    }
}

