/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

public interface LongHash {

    public static interface Strategy {
        public int hashCode(long var1);

        public boolean equals(long var1, long var3);
    }
}

