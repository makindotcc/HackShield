/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.exception;

import eu.okaeri.configs.exception.OkaeriException;

public class ValidationException
extends OkaeriException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}

