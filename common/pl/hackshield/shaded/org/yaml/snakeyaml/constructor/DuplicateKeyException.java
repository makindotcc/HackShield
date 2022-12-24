/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.constructor;

import pl.hackshield.shaded.org.yaml.snakeyaml.constructor.ConstructorException;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;

public class DuplicateKeyException
extends ConstructorException {
    protected DuplicateKeyException(Mark contextMark, Object key, Mark problemMark) {
        super("while constructing a mapping", contextMark, "found duplicate key " + String.valueOf(key), problemMark);
    }
}

