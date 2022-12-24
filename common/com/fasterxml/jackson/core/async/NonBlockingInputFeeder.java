/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.core.async;

public interface NonBlockingInputFeeder {
    public boolean needMoreInput();

    public void endOfInput();
}

