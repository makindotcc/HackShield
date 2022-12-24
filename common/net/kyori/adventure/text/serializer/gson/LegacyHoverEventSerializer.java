/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

public interface LegacyHoverEventSerializer {
    public @NotNull HoverEvent.ShowItem deserializeShowItem(@NotNull Component var1) throws IOException;

    public @NotNull HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component var1, Codec.Decoder<Component, String, ? extends RuntimeException> var2) throws IOException;

    @NotNull
    public Component serializeShowItem(@NotNull HoverEvent.ShowItem var1) throws IOException;

    @NotNull
    public Component serializeShowEntity(@NotNull HoverEvent.ShowEntity var1, Codec.Encoder<Component, String, ? extends RuntimeException> var2) throws IOException;
}

