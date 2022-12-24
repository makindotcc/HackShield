/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWithSerializerProvider {
    public SerializerProvider getProvider();

    public void setProvider(SerializerProvider var1);
}

