/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.composer;

import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ComposerException
extends MarkedYAMLException {
    private static final long serialVersionUID = 2146314636913113935L;

    protected ComposerException(String context, Mark contextMark, String problem, Mark problemMark) {
        super(context, contextMark, problem, problemMark);
    }
}

