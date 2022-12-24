/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.JsonMappingException;

public class RuntimeJsonMappingException
extends RuntimeException {
    public RuntimeJsonMappingException(JsonMappingException cause) {
        super(cause);
    }

    public RuntimeJsonMappingException(String message) {
        super(message);
    }

    public RuntimeJsonMappingException(String message, JsonMappingException cause) {
        super(message, cause);
    }
}

