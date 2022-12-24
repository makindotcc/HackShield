/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.data.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import pl.hackshield.auth.common.data.Mod;

public final class ModDataSerializer
implements ObjectSerializer<Mod> {
    @Override
    public void serialize(@NotNull Mod object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("name", object.getName());
        data.add("enabled", object.isEnabled());
        if (object.getSettings().size() > 0) {
            data.addAsMap("settings", object.getSettings(), GenericsDeclaration.of(String.class));
        }
    }

    @Override
    public Mod deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Mod mod = new Mod(data.get("name", String.class), data.get("enabled", Boolean.class));
        Map<String, Object> settings = data.getAsMap("settings", String.class, Object.class);
        if (settings != null) {
            mod.getSettings().putAll(settings);
        }
        return mod;
    }

    @Override
    public boolean supports(@NotNull Class<? super Mod> type) {
        return Mod.class.isAssignableFrom(type);
    }
}

