/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.chars;

public interface CharHash {

    public static interface Strategy {
        public int hashCode(char var1);

        public boolean equals(char var1, char var2);
    }
}

