/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.io;

import java.io.IOException;

public interface MeasurableStream {
    public long length() throws IOException;

    public long position() throws IOException;
}

