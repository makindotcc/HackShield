/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializerImpl;
import net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GsonComponentSerializer
extends ComponentSerializer<Component, Component, String>,
Buildable<GsonComponentSerializer, Builder> {
    @NotNull
    public static GsonComponentSerializer gson() {
        return GsonComponentSerializerImpl.Instances.INSTANCE;
    }

    @NotNull
    public static GsonComponentSerializer colorDownsamplingGson() {
        return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
    }

    public static Builder builder() {
        return new GsonComponentSerializerImpl.BuilderImpl();
    }

    @NotNull
    public Gson serializer();

    @NotNull
    public UnaryOperator<GsonBuilder> populator();

    @NotNull
    public Component deserializeFromTree(@NotNull JsonElement var1);

    @NotNull
    public JsonElement serializeToTree(@NotNull Component var1);

    @ApiStatus.Internal
    public static interface Provider {
        @ApiStatus.Internal
        @NotNull
        public GsonComponentSerializer gson();

        @ApiStatus.Internal
        @NotNull
        public GsonComponentSerializer gsonLegacy();

        @ApiStatus.Internal
        @NotNull
        public Consumer<Builder> builder();
    }

    public static interface Builder
    extends AbstractBuilder<GsonComponentSerializer>,
    Buildable.Builder<GsonComponentSerializer> {
        @NotNull
        public Builder downsampleColors();

        @NotNull
        public Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer var1);

        @NotNull
        public Builder emitLegacyHoverEvent();

        @Override
        @NotNull
        public GsonComponentSerializer build();
    }
}

