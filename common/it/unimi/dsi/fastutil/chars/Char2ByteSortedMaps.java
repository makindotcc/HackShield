/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteMaps;
import it.unimi.dsi.fastutil.chars.Char2ByteSortedMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.chars.CharSortedSets;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class Char2ByteSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Char2ByteSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Character, ?>> entryComparator(CharComparator comparator) {
        return (x, y) -> comparator.compare(((Character)x.getKey()).charValue(), ((Character)y.getKey()).charValue());
    }

    public static ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteSortedMap map) {
        ObjectSet entries = map.char2ByteEntrySet();
        return entries instanceof Char2ByteSortedMap.FastSortedEntrySet ? ((Char2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Char2ByteMap.Entry> fastIterable(Char2ByteSortedMap map) {
        ObjectSet entries = map.char2ByteEntrySet();
        return entries instanceof Char2ByteSortedMap.FastSortedEntrySet ? ((Char2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Char2ByteSortedMap singleton(Character key, Byte value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2ByteSortedMap singleton(Character key, Byte value, CharComparator comparator) {
        return new Singleton(key.charValue(), value, comparator);
    }

    public static Char2ByteSortedMap singleton(char key, byte value) {
        return new Singleton(key, value);
    }

    public static Char2ByteSortedMap singleton(char key, byte value, CharComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Char2ByteSortedMap synchronize(Char2ByteSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Char2ByteSortedMap synchronize(Char2ByteSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Char2ByteSortedMap unmodifiable(Char2ByteSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class Singleton
    extends Char2ByteMaps.Singleton
    implements Char2ByteSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharComparator comparator;

        protected Singleton(char key, byte value, CharComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(char key, byte value) {
            this(key, value, null);
        }

        final int compare(char k1, char k2) {
            return this.comparator == null ? Character.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public CharComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractChar2ByteMap.BasicEntry(this.key, this.value), Char2ByteSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        @Deprecated
        public ObjectSortedSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.singleton(this.key, this.comparator);
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2ByteSortedMap subMap(char from, char to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Char2ByteSortedMap headMap(char to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Char2ByteSortedMap tailMap(char from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public char firstCharKey() {
            return this.key;
        }

        @Override
        public char lastCharKey() {
            return this.key;
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap headMap(Character oto) {
            return this.headMap(oto.charValue());
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap tailMap(Character ofrom) {
            return this.tailMap(ofrom.charValue());
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap subMap(Character ofrom, Character oto) {
            return this.subMap(ofrom.charValue(), oto.charValue());
        }

        @Override
        @Deprecated
        public Character firstKey() {
            return Character.valueOf(this.firstCharKey());
        }

        @Override
        @Deprecated
        public Character lastKey() {
            return Character.valueOf(this.lastCharKey());
        }
    }

    public static class SynchronizedSortedMap
    extends Char2ByteMaps.SynchronizedMap
    implements Char2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteSortedMap sortedMap;

        protected SynchronizedSortedMap(Char2ByteSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Char2ByteSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2ByteEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        @Deprecated
        public ObjectSortedSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2ByteSortedMap subMap(char from, char to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Char2ByteSortedMap headMap(char to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Char2ByteSortedMap tailMap(char from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char firstCharKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstCharKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char lastCharKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastCharKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character firstKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap subMap(Character from, Character to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap headMap(Character to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap tailMap(Character from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class UnmodifiableSortedMap
    extends Char2ByteMaps.UnmodifiableMap
    implements Char2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteSortedMap sortedMap;

        protected UnmodifiableSortedMap(Char2ByteSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public CharComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2ByteEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        @Deprecated
        public ObjectSortedSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2ByteSortedMap subMap(char from, char to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Char2ByteSortedMap headMap(char to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Char2ByteSortedMap tailMap(char from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public char firstCharKey() {
            return this.sortedMap.firstCharKey();
        }

        @Override
        public char lastCharKey() {
            return this.sortedMap.lastCharKey();
        }

        @Override
        @Deprecated
        public Character firstKey() {
            return this.sortedMap.firstKey();
        }

        @Override
        @Deprecated
        public Character lastKey() {
            return this.sortedMap.lastKey();
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap subMap(Character from, Character to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap headMap(Character to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap tailMap(Character from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class EmptySortedMap
    extends Char2ByteMaps.EmptyMap
    implements Char2ByteSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public CharComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        @Deprecated
        public ObjectSortedSet<Map.Entry<Character, Byte>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public CharSortedSet keySet() {
            return CharSortedSets.EMPTY_SET;
        }

        @Override
        public Char2ByteSortedMap subMap(char from, char to) {
            return EMPTY_MAP;
        }

        @Override
        public Char2ByteSortedMap headMap(char to) {
            return EMPTY_MAP;
        }

        @Override
        public Char2ByteSortedMap tailMap(char from) {
            return EMPTY_MAP;
        }

        @Override
        public char firstCharKey() {
            throw new NoSuchElementException();
        }

        @Override
        public char lastCharKey() {
            throw new NoSuchElementException();
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap headMap(Character oto) {
            return this.headMap(oto.charValue());
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap tailMap(Character ofrom) {
            return this.tailMap(ofrom.charValue());
        }

        @Override
        @Deprecated
        public Char2ByteSortedMap subMap(Character ofrom, Character oto) {
            return this.subMap(ofrom.charValue(), oto.charValue());
        }

        @Override
        @Deprecated
        public Character firstKey() {
            return Character.valueOf(this.firstCharKey());
        }

        @Override
        @Deprecated
        public Character lastKey() {
            return Character.valueOf(this.lastCharKey());
        }
    }
}

