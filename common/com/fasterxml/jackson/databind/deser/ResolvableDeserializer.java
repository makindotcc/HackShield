/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ResolvableDeserializer {
    public void resolve(DeserializationContext var1) throws JsonMappingException;
}

