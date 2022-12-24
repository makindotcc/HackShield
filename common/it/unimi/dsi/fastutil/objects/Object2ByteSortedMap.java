/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public interface Object2ByteSortedMap<K>
extends Object2ByteMap<K>,
SortedMap<K, Byte> {
    public Object2ByteSortedMap<K> subMap(K var1, K var2);

    public Object2ByteSortedMap<K> headMap(K var1);

    public Object2ByteSortedMap<K> tailMap(K var1);

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
        return this.object2ByteEntrySet();
    }

    @Override
    public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet();

    @Override
    public ObjectSortedSet<K> keySet();

    @Override
    public ByteCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Object2ByteMap.Entry<K>>,
    Object2ByteMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> var1);
    }
}

