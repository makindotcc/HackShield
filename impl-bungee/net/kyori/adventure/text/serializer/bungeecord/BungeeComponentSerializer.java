/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.stream.JsonWriter
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.ComponentSerializer
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.chat.ComponentSerializer
 *  org.jetbrains.annotations.NotNull
 */
package net.kyori.adventure.text.serializer.bungeecord;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.bungeecord.GsonInjections;
import net.kyori.adventure.text.serializer.bungeecord.SelfSerializable;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public final class BungeeComponentSerializer
implements ComponentSerializer<Component, Component, BaseComponent[]> {
    private static boolean SUPPORTED = true;
    private static final BungeeComponentSerializer MODERN;
    private static final BungeeComponentSerializer PRE_1_16;
    private final GsonComponentSerializer serializer;
    private final LegacyComponentSerializer legacySerializer;

    public static boolean isNative() {
        return SUPPORTED;
    }

    public static BungeeComponentSerializer get() {
        return MODERN;
    }

    public static BungeeComponentSerializer legacy() {
        return PRE_1_16;
    }

    public static BungeeComponentSerializer of(GsonComponentSerializer serializer, LegacyComponentSerializer legacySerializer) {
        if (serializer == null || legacySerializer == null) {
            return null;
        }
        return new BungeeComponentSerializer(serializer, legacySerializer);
    }

    public static boolean inject(Gson existing) {
        boolean result = GsonInjections.injectGson(Objects.requireNonNull(existing, "existing"), builder -> {
            GsonComponentSerializer.gson().populator().apply(builder);
            builder.registerTypeAdapterFactory((TypeAdapterFactory)new SelfSerializable.AdapterFactory());
        });
        SUPPORTED &= result;
        return result;
    }

    private BungeeComponentSerializer(GsonComponentSerializer serializer, LegacyComponentSerializer legacySerializer) {
        this.serializer = serializer;
        this.legacySerializer = legacySerializer;
    }

    private static void bind() {
        try {
            Field gsonField = GsonInjections.field(net.md_5.bungee.chat.ComponentSerializer.class, "gson");
            BungeeComponentSerializer.inject((Gson)gsonField.get(null));
        }
        catch (Throwable error) {
            SUPPORTED = false;
        }
    }

    @NotNull
    public Component deserialize(@NotNull @NotNull BaseComponent @NotNull [] input) {
        Objects.requireNonNull(input, "input");
        if (input.length == 1 && input[0] instanceof AdapterComponent) {
            return ((AdapterComponent)input[0]).component;
        }
        return this.serializer.deserialize((Object)net.md_5.bungee.chat.ComponentSerializer.toString((BaseComponent[])input));
    }

    @NotNull
    public @NotNull BaseComponent @NotNull [] serialize(@NotNull Component component) {
        Objects.requireNonNull(component, "component");
        if (SUPPORTED) {
            return new BaseComponent[]{new AdapterComponent(component)};
        }
        return net.md_5.bungee.chat.ComponentSerializer.parse((String)((String)this.serializer.serialize(component)));
    }

    static {
        BungeeComponentSerializer.bind();
        MODERN = new BungeeComponentSerializer(GsonComponentSerializer.gson(), LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build());
        PRE_1_16 = new BungeeComponentSerializer(GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().build(), LegacyComponentSerializer.legacySection());
    }

    class AdapterComponent
    extends BaseComponent
    implements SelfSerializable {
        private final Component component;
        private volatile String legacy;

        AdapterComponent(Component component) {
            this.component = component;
        }

        public String toLegacyText() {
            if (this.legacy == null) {
                this.legacy = BungeeComponentSerializer.this.legacySerializer.serialize(this.component);
            }
            return this.legacy;
        }

        @NotNull
        public BaseComponent duplicate() {
            return this;
        }

        @Override
        public void write(JsonWriter out) throws IOException {
            BungeeComponentSerializer.this.serializer.serializer().getAdapter(Component.class).write(out, (Object)this.component);
        }
    }
}

