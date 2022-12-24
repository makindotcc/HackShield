/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.longs.Long2CharMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Long2CharSortedMap
extends Long2CharMap,
SortedMap<Long, Character> {
    public Long2CharSortedMap subMap(long var1, long var3);

    public Long2CharSortedMap headMap(long var1);

    public Long2CharSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2CharSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2CharSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2CharSortedMap tailMap(Long from) {
        return this.tailMap((long)from);
    }

    @Override
    @Deprecated
    default public Long firstKey() {
        return this.firstLongKey();
    }

    @Override
    @Deprecated
    default public Long lastKey() {
        return this.lastLongKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Long, Character>> entrySet() {
        return this.long2CharEntrySet();
    }

    public ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public CharCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2CharMap.Entry>,
    Long2CharMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2CharMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2CharMap.Entry> fastIterator(Long2CharMap.Entry var1);
    }
}

