/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.shorts;

public interface ShortHash {

    public static interface Strategy {
        public int hashCode(short var1);

        public boolean equals(short var1, short var2);
    }
}

