/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Map;
import java.util.SortedMap;

public interface Int2ReferenceSortedMap<V>
extends Int2ReferenceMap<V>,
SortedMap<Integer, V> {
    public Int2ReferenceSortedMap<V> subMap(int var1, int var2);

    public Int2ReferenceSortedMap<V> headMap(int var1);

    public Int2ReferenceSortedMap<V> tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2ReferenceSortedMap<V> subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2ReferenceSortedMap<V> headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2ReferenceSortedMap<V> tailMap(Integer from) {
        return this.tailMap((int)from);
    }

    @Override
    @Deprecated
    default public Integer firstKey() {
        return this.firstIntKey();
    }

    @Override
    @Deprecated
    default public Integer lastKey() {
        return this.lastIntKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Integer, V>> entrySet() {
        return this.int2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Int2ReferenceMap.Entry<V>>,
    Int2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator(Int2ReferenceMap.Entry<V> var1);
    }
}

