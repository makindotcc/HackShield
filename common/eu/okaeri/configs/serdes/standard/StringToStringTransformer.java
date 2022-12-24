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

public class StringToStringTransformer
extends ObjectTransformer<String, String> {
    @Override
    public GenericsPair<String, String> getPair() {
        return this.genericsPair(String.class, String.class);
    }

    @Override
    public String transform(@NonNull String data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return data;
    }
}

