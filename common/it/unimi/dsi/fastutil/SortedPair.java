/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutableSortedPair;
import java.util.Objects;

public interface SortedPair<K extends Comparable<K>>
extends Pair<K, K> {
    public static <K extends Comparable<K>> SortedPair<K> of(K l, K r) {
        return ObjectObjectImmutableSortedPair.of(l, r);
    }

    default public boolean contains(Object o) {
        return Objects.equals(o, this.left()) || Objects.equals(o, this.right());
    }
}

