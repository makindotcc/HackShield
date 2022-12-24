/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml.snakeyaml.error;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException;

@Deprecated
public class YAMLException
extends JacksonYAMLParseException {
    private static final long serialVersionUID = 1L;

    public YAMLException(JsonParser p, pl.hackshield.shaded.org.yaml.snakeyaml.error.YAMLException src) {
        super(p, src.getMessage(), src);
    }

    public static YAMLException from(JsonParser p, pl.hackshield.shaded.org.yaml.snakeyaml.error.YAMLException src) {
        return new YAMLException(p, src);
    }
}

