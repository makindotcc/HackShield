/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes.standard;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.serdes.standard.ObjectToStringTransformer;
import eu.okaeri.configs.serdes.standard.StringToBigDecimalTransformer;
import eu.okaeri.configs.serdes.standard.StringToBigIntegerTransformer;
import eu.okaeri.configs.serdes.standard.StringToBooleanTransformer;
import eu.okaeri.configs.serdes.standard.StringToByteTransformer;
import eu.okaeri.configs.serdes.standard.StringToCharacterTransformer;
import eu.okaeri.configs.serdes.standard.StringToDoubleTransformer;
import eu.okaeri.configs.serdes.standard.StringToFloatTransformer;
import eu.okaeri.configs.serdes.standard.StringToIntegerTransformer;
import eu.okaeri.configs.serdes.standard.StringToLongTransformer;
import eu.okaeri.configs.serdes.standard.StringToShortTransformer;
import eu.okaeri.configs.serdes.standard.StringToStringTransformer;
import eu.okaeri.configs.serdes.standard.StringToUuidTransformer;
import lombok.NonNull;

public class StandardSerdes
implements OkaeriSerdesPack {
    @Override
    public void register(@NonNull SerdesRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("registry is marked non-null but is null");
        }
        registry.register(new ObjectToStringTransformer());
        registry.register(new StringToStringTransformer());
        registry.registerWithReversedToString(new StringToBigDecimalTransformer());
        registry.registerWithReversedToString(new StringToBigIntegerTransformer());
        registry.registerWithReversedToString(new StringToBooleanTransformer());
        registry.registerWithReversedToString(new StringToByteTransformer());
        registry.registerWithReversedToString(new StringToCharacterTransformer());
        registry.registerWithReversedToString(new StringToDoubleTransformer());
        registry.registerWithReversedToString(new StringToFloatTransformer());
        registry.registerWithReversedToString(new StringToIntegerTransformer());
        registry.registerWithReversedToString(new StringToLongTransformer());
        registry.registerWithReversedToString(new StringToShortTransformer());
        registry.registerWithReversedToString(new StringToUuidTransformer());
    }
}

