/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.core.async;

import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import java.io.IOException;

public interface ByteArrayFeeder
extends NonBlockingInputFeeder {
    public void feedInput(byte[] var1, int var2, int var3) throws IOException;
}

