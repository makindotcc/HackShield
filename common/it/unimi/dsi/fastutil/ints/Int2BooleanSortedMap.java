/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Int2BooleanSortedMap
extends Int2BooleanMap,
SortedMap<Integer, Boolean> {
    public Int2BooleanSortedMap subMap(int var1, int var2);

    public Int2BooleanSortedMap headMap(int var1);

    public Int2BooleanSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2BooleanSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2BooleanSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2BooleanSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Boolean>> entrySet() {
        return this.int2BooleanEntrySet();
    }

    public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public BooleanCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2BooleanMap.Entry>,
    Int2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap.Entry var1);
    }
}

