/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class JacksonYAMLParseException
extends JsonParseException {
    private static final long serialVersionUID = 1L;

    public JacksonYAMLParseException(JsonParser p, String msg, Exception e) {
        super(p, msg, (Throwable)e);
    }
}

