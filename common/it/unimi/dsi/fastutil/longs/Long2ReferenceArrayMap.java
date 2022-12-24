/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$EntrySet.EntrySetSpliterator
 *  it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$KeySet.KeySetSpliterator
 *  it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$ValuesCollection.ValuesSpliterator
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ReferenceMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class Long2ReferenceArrayMap<V>
extends AbstractLong2ReferenceMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient Object[] value;
    private int size;
    private transient Long2ReferenceMap.FastEntrySet<V> entries;
    private transient LongSet keys;
    private transient ReferenceCollection<V> values;

    public Long2ReferenceArrayMap(long[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2ReferenceArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Long2ReferenceArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new Object[capacity];
    }

    public Long2ReferenceArrayMap(Long2ReferenceMap<V> m) {
        this(m.size());
        int i = 0;
        for (Long2ReferenceMap.Entry entry : m.long2ReferenceEntrySet()) {
            this.key[i] = entry.getLongKey();
            this.value[i] = entry.getValue();
            ++i;
        }
        this.size = i;
    }

    public Long2ReferenceArrayMap(Map<? extends Long, ? extends V> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<Long, V> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Long2ReferenceArrayMap(long[] key, Object[] value, int size) {
        this.key = key;
        this.value = value;
        this.size = size;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
        if (size > key.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
        }
    }

    public Long2ReferenceMap.FastEntrySet<V> long2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(long k) {
        long[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public V get(long k) {
        long[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return (V)this.value[i];
        }
        return (V)this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        int i = this.size;
        while (i-- != 0) {
            this.value[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(long k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(Object v) {
        int i = this.size;
        while (i-- != 0) {
            if (this.value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public V put(long k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                newKey[i] = this.key[i];
                newValue[i] = this.value[i];
            }
            this.key = newKey;
            this.value = newValue;
        }
        this.key[this.size] = k;
        this.value[this.size] = v;
        ++this.size;
        return (V)this.defRetValue;
    }

    @Override
    public V remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return (V)this.defRetValue;
        }
        Object oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.value[this.size] = null;
        return (V)oldValue;
    }

    @Override
    public LongSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Long2ReferenceArrayMap<V> clone() {
        Long2ReferenceArrayMap c;
        try {
            c = (Long2ReferenceArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (Object[])this.value.clone();
        c.entries = null;
        c.keys = null;
        c.values = null;
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int max = this.size;
        for (int i = 0; i < max; ++i) {
            s.writeLong(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2ReferenceMap.Entry<V>>
    implements Long2ReferenceMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
            return new ObjectIterator<Long2ReferenceMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2ReferenceArrayMap.this.size;
                }

                @Override
                public Long2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2ReferenceMap.BasicEntry<Object>(Long2ReferenceArrayMap.this.key[this.curr], Long2ReferenceArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ReferenceArrayMap.this.key, this.next + 1, Long2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ReferenceArrayMap.this.value, this.next + 1, Long2ReferenceArrayMap.this.value, this.next, tail);
                    ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Long2ReferenceMap.Entry<V>> action) {
                    int max = Long2ReferenceArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractLong2ReferenceMap.BasicEntry<Object>(Long2ReferenceArrayMap.this.key[this.curr], Long2ReferenceArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Long2ReferenceMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2ReferenceMap.BasicEntry<V> entry = new AbstractLong2ReferenceMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2ReferenceArrayMap.this.size;
                }

                @Override
                public Long2ReferenceMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2ReferenceArrayMap.this.key[this.curr];
                    this.entry.value = Long2ReferenceArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ReferenceArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ReferenceArrayMap.this.key, this.next + 1, Long2ReferenceArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ReferenceArrayMap.this.value, this.next + 1, Long2ReferenceArrayMap.this.value, this.next, tail);
                    ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Long2ReferenceMap.Entry<V>> action) {
                    int max = Long2ReferenceArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Long2ReferenceArrayMap.this.key[this.curr];
                        this.entry.value = Long2ReferenceArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Long2ReferenceMap.Entry<V>> spliterator() {
            return new EntrySetSpliterator(0, Long2ReferenceArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Long2ReferenceMap.Entry<V>> action) {
            int max = Long2ReferenceArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractLong2ReferenceMap.BasicEntry<Object>(Long2ReferenceArrayMap.this.key[i], Long2ReferenceArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2ReferenceMap.Entry<V>> action) {
            AbstractLong2ReferenceMap.BasicEntry entry = new AbstractLong2ReferenceMap.BasicEntry();
            int max = Long2ReferenceArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Long2ReferenceArrayMap.this.key[i];
                entry.value = Long2ReferenceArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Long2ReferenceArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2ReferenceArrayMap.this.containsKey(k) && Long2ReferenceArrayMap.this.get(k) == e.getValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            long k = (Long)e.getKey();
            Object v = e.getValue();
            int oldPos = Long2ReferenceArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Long2ReferenceArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Long2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ReferenceArrayMap.this.key, oldPos + 1, Long2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ReferenceArrayMap.this.value, oldPos + 1, Long2ReferenceArrayMap.this.value, oldPos, tail);
            Long2ReferenceArrayMap.this.size--;
            ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2ReferenceMap.Entry<V>>
        implements ObjectSpliterator<Long2ReferenceMap.Entry<V>> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Long2ReferenceMap.Entry<V> get(int location) {
                return new AbstractLong2ReferenceMap.BasicEntry<Object>(Long2ReferenceArrayMap.this.key[location], Long2ReferenceArrayMap.this.value[location]);
            }

            protected final it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractLongSet {
        private KeySet() {
        }

        @Override
        public boolean contains(long k) {
            return Long2ReferenceArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(long k) {
            int oldPos = Long2ReferenceArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Long2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ReferenceArrayMap.this.key, oldPos + 1, Long2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ReferenceArrayMap.this.value, oldPos + 1, Long2ReferenceArrayMap.this.value, oldPos, tail);
            Long2ReferenceArrayMap.this.size--;
            ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
            return true;
        }

        @Override
        public LongIterator iterator() {
            return new LongIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Long2ReferenceArrayMap.this.size;
                }

                @Override
                public long nextLong() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Long2ReferenceArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Long2ReferenceArrayMap.this.size - this.pos;
                    System.arraycopy(Long2ReferenceArrayMap.this.key, this.pos, Long2ReferenceArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Long2ReferenceArrayMap.this.value, this.pos, Long2ReferenceArrayMap.this.value, this.pos - 1, tail);
                    Long2ReferenceArrayMap.this.size--;
                    --this.pos;
                    ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
                }

                @Override
                public void forEachRemaining(LongConsumer action) {
                    int max = Long2ReferenceArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Long2ReferenceArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public LongSpliterator spliterator() {
            return new KeySetSpliterator(0, Long2ReferenceArrayMap.this.size);
        }

        @Override
        public void forEach(LongConsumer action) {
            int max = Long2ReferenceArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Long2ReferenceArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Long2ReferenceArrayMap.this.size;
        }

        @Override
        public void clear() {
            Long2ReferenceArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends LongSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements LongSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final long get(int location) {
                return Long2ReferenceArrayMap.this.key[location];
            }

            protected final it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                int max = Long2ReferenceArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Long2ReferenceArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractReferenceCollection<V> {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(Object v) {
            return Long2ReferenceArrayMap.this.containsValue(v);
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Long2ReferenceArrayMap.this.size;
                }

                @Override
                public V next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Long2ReferenceArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Long2ReferenceArrayMap.this.size - this.pos;
                    System.arraycopy(Long2ReferenceArrayMap.this.key, this.pos, Long2ReferenceArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Long2ReferenceArrayMap.this.value, this.pos, Long2ReferenceArrayMap.this.value, this.pos - 1, tail);
                    Long2ReferenceArrayMap.this.size--;
                    --this.pos;
                    ((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).value[((Long2ReferenceArrayMap)Long2ReferenceArrayMap.this).size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super V> action) {
                    int max = Long2ReferenceArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Long2ReferenceArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<V> spliterator() {
            return new ValuesSpliterator(0, Long2ReferenceArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            int max = Long2ReferenceArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Long2ReferenceArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Long2ReferenceArrayMap.this.size;
        }

        @Override
        public void clear() {
            Long2ReferenceArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<V>
        implements ObjectSpliterator<V> {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16464;
            }

            @Override
            protected final V get(int location) {
                return Long2ReferenceArrayMap.this.value[location];
            }

            protected final it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap$ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
                int max = Long2ReferenceArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Long2ReferenceArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}
