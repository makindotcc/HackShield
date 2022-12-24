/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.exception;

import eu.okaeri.configs.exception.OkaeriException;

public class InitializationException
extends OkaeriException {
    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }
}

