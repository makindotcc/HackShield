/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;

public abstract class KeyDeserializer {
    public abstract Object deserializeKey(String var1, DeserializationContext var2) throws IOException;

    public static abstract class None
    extends KeyDeserializer {
    }
}

