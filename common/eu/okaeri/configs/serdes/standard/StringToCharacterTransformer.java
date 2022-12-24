/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes.standard;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.ObjectTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import lombok.NonNull;

public class StringToCharacterTransformer
extends ObjectTransformer<String, Character> {
    @Override
    public GenericsPair<String, Character> getPair() {
        return this.genericsPair(String.class, Character.class);
    }

    @Override
    public Character transform(@NonNull String data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        if (data.length() > 1) {
            throw new IllegalArgumentException("char '" + data + "' too long: " + data.length() + ">1");
        }
        return Character.valueOf(data.charAt(0));
    }
}

