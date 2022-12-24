/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public interface Object2DoubleSortedMap<K>
extends Object2DoubleMap<K>,
SortedMap<K, Double> {
    public Object2DoubleSortedMap<K> subMap(K var1, K var2);

    public Object2DoubleSortedMap<K> headMap(K var1);

    public Object2DoubleSortedMap<K> tailMap(K var1);

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<K, Double>> entrySet() {
        return this.object2DoubleEntrySet();
    }

    @Override
    public ObjectSortedSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet();

    @Override
    public ObjectSortedSet<K> keySet();

    @Override
    public DoubleCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Object2DoubleMap.Entry<K>>,
    Object2DoubleMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> fastIterator(Object2DoubleMap.Entry<K> var1);
    }
}

