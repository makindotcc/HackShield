/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.data.serdes;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.jetbrains.annotations.NotNull;
import pl.hackshield.auth.common.data.serializer.ModDataSerializer;

public final class ModSerdesPack
implements OkaeriSerdesPack {
    @Override
    public void register(@NotNull SerdesRegistry registry) {
        registry.register(new ModDataSerializer());
    }
}

