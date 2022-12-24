/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2IntMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2IntSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Comparator;

public abstract class AbstractReference2IntSortedMap<K>
extends AbstractReference2IntMap<K>
implements Reference2IntSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2IntSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public IntCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2IntSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2IntSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2IntSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2IntSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2IntSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2IntSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2IntSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2IntSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2IntSortedMap.this.reference2IntEntrySet().iterator(new AbstractReference2IntMap.BasicEntry(from, 0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2IntSortedMaps.fastIterator(AbstractReference2IntSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractIntCollection {
        protected ValuesCollection() {
        }

        @Override
        public IntIterator iterator() {
            return new ValuesIterator(Reference2IntSortedMaps.fastIterator(AbstractReference2IntSortedMap.this));
        }

        @Override
        public boolean contains(int k) {
            return AbstractReference2IntSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2IntSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2IntSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements IntIterator {
        protected final ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public int nextInt() {
            return ((Reference2IntMap.Entry)this.i.next()).getIntValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Reference2IntMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Reference2IntMap.Entry)this.i.previous()).getKey();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }
    }
}

