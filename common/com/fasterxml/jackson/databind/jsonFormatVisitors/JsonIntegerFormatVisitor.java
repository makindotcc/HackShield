/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormatVisitor;

public interface JsonIntegerFormatVisitor
extends JsonValueFormatVisitor {
    public void numberType(JsonParser.NumberType var1);

    public static class Base
    extends JsonValueFormatVisitor.Base
    implements JsonIntegerFormatVisitor {
        @Override
        public void numberType(JsonParser.NumberType type) {
        }
    }
}

