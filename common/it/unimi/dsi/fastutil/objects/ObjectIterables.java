/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

public final class ObjectIterables {
    private ObjectIterables() {
    }

    public static <K> long size(Iterable<K> iterable) {
        long c = 0L;
        for (K dummy : iterable) {
            ++c;
        }
        return c;
    }
}

