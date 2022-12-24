/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.parser;

import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ParserException
extends MarkedYAMLException {
    private static final long serialVersionUID = -2349253802798398038L;

    public ParserException(String context, Mark contextMark, String problem, Mark problemMark) {
        super(context, contextMark, problem, problemMark, null, null);
    }
}

