/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.stream.Stream;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TextComponent
extends BuildableComponent<TextComponent, Builder>,
ScopedComponent<TextComponent> {
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static TextComponent ofChildren(ComponentLike ... components) {
        return Component.textOfChildren(components);
    }

    @NotNull
    public String content();

    @Contract(pure=true)
    @NotNull
    public TextComponent content(@NotNull String var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("content", this.content())), BuildableComponent.super.examinableProperties());
    }

    public static interface Builder
    extends ComponentBuilder<TextComponent, Builder> {
        @NotNull
        public String content();

        @Contract(value="_ -> this")
        @NotNull
        public Builder content(@NotNull String var1);
    }
}
