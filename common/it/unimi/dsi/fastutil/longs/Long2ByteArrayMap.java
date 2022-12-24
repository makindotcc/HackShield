/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.longs.AbstractLong2ByteMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class Long2ByteArrayMap
extends AbstractLong2ByteMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient byte[] value;
    private int size;
    private transient Long2ByteMap.FastEntrySet entries;
    private transient LongSet keys;
    private transient ByteCollection values;

    public Long2ByteArrayMap(long[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Long2ByteArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = ByteArrays.EMPTY_ARRAY;
    }

    public Long2ByteArrayMap(int capacity) {
        this.key = new long[capacity];
        this.value = new byte[capacity];
    }

    public Long2ByteArrayMap(Long2ByteMap m) {
        this(m.size());
        int i = 0;
        for (Long2ByteMap.Entry e : m.long2ByteEntrySet()) {
            this.key[i] = e.getLongKey();
            this.value[i] = e.getByteValue();
            ++i;
        }
        this.size = i;
    }

    public Long2ByteArrayMap(Map<? extends Long, ? extends Byte> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<? extends Long, ? extends Byte> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Long2ByteArrayMap(long[] key, byte[] value, int size) {
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

    public Long2ByteMap.FastEntrySet long2ByteEntrySet() {
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
    public byte get(long k) {
        long[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return this.value[i];
        }
        return this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean containsKey(long k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(byte v) {
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
    public byte put(long k, byte v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            byte oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];
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
        return this.defRetValue;
    }

    @Override
    public byte remove(long k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        byte oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public LongSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ByteCollection values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Long2ByteArrayMap clone() {
        Long2ByteArrayMap c;
        try {
            c = (Long2ByteArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (long[])this.key.clone();
        c.value = (byte[])this.value.clone();
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
            s.writeByte(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readLong();
            this.value[i] = s.readByte();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Long2ByteMap.Entry>
    implements Long2ByteMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Long2ByteMap.Entry> iterator() {
            return new ObjectIterator<Long2ByteMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Long2ByteArrayMap.this.size;
                }

                @Override
                public Long2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractLong2ByteMap.BasicEntry(Long2ByteArrayMap.this.key[this.curr], Long2ByteArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ByteArrayMap.this.key, this.next + 1, Long2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ByteArrayMap.this.value, this.next + 1, Long2ByteArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Long2ByteMap.Entry> action) {
                    int max = Long2ByteArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractLong2ByteMap.BasicEntry(Long2ByteArrayMap.this.key[this.curr], Long2ByteArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Long2ByteMap.Entry> fastIterator() {
            return new ObjectIterator<Long2ByteMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractLong2ByteMap.BasicEntry entry = new AbstractLong2ByteMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Long2ByteArrayMap.this.size;
                }

                @Override
                public Long2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Long2ByteArrayMap.this.key[this.curr];
                    this.entry.value = Long2ByteArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Long2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Long2ByteArrayMap.this.key, this.next + 1, Long2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Long2ByteArrayMap.this.value, this.next + 1, Long2ByteArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Long2ByteMap.Entry> action) {
                    int max = Long2ByteArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Long2ByteArrayMap.this.key[this.curr];
                        this.entry.value = Long2ByteArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Long2ByteMap.Entry> spliterator() {
            return new EntrySetSpliterator(0, Long2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Long2ByteMap.Entry> action) {
            int max = Long2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractLong2ByteMap.BasicEntry(Long2ByteArrayMap.this.key[i], Long2ByteArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2ByteMap.Entry> action) {
            AbstractLong2ByteMap.BasicEntry entry = new AbstractLong2ByteMap.BasicEntry();
            int max = Long2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Long2ByteArrayMap.this.key[i];
                entry.value = Long2ByteArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Long2ByteArrayMap.this.size;
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            long k = (Long)e.getKey();
            return Long2ByteArrayMap.this.containsKey(k) && Long2ByteArrayMap.this.get(k) == ((Byte)e.getValue()).byteValue();
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            long k = (Long)e.getKey();
            byte v = (Byte)e.getValue();
            int oldPos = Long2ByteArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Long2ByteArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Long2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ByteArrayMap.this.key, oldPos + 1, Long2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ByteArrayMap.this.value, oldPos + 1, Long2ByteArrayMap.this.value, oldPos, tail);
            Long2ByteArrayMap.this.size--;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2ByteMap.Entry>
        implements ObjectSpliterator<Long2ByteMap.Entry> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Long2ByteMap.Entry get(int location) {
                return new AbstractLong2ByteMap.BasicEntry(Long2ByteArrayMap.this.key[location], Long2ByteArrayMap.this.value[location]);
            }

            protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
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
            return Long2ByteArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(long k) {
            int oldPos = Long2ByteArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Long2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ByteArrayMap.this.key, oldPos + 1, Long2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ByteArrayMap.this.value, oldPos + 1, Long2ByteArrayMap.this.value, oldPos, tail);
            Long2ByteArrayMap.this.size--;
            return true;
        }

        @Override
        public LongIterator iterator() {
            return new LongIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Long2ByteArrayMap.this.size;
                }

                @Override
                public long nextLong() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Long2ByteArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Long2ByteArrayMap.this.size - this.pos;
                    System.arraycopy(Long2ByteArrayMap.this.key, this.pos, Long2ByteArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Long2ByteArrayMap.this.value, this.pos, Long2ByteArrayMap.this.value, this.pos - 1, tail);
                    Long2ByteArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(LongConsumer action) {
                    int max = Long2ByteArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Long2ByteArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public LongSpliterator spliterator() {
            return new KeySetSpliterator(0, Long2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(LongConsumer action) {
            int max = Long2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Long2ByteArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Long2ByteArrayMap.this.size;
        }

        @Override
        public void clear() {
            Long2ByteArrayMap.this.clear();
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
                return Long2ByteArrayMap.this.key[location];
            }

            @Override
            protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                int max = Long2ByteArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Long2ByteArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractByteCollection {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(byte v) {
            return Long2ByteArrayMap.this.containsValue(v);
        }

        @Override
        public ByteIterator iterator() {
            return new ByteIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Long2ByteArrayMap.this.size;
                }

                @Override
                public byte nextByte() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Long2ByteArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Long2ByteArrayMap.this.size - this.pos;
                    System.arraycopy(Long2ByteArrayMap.this.key, this.pos, Long2ByteArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Long2ByteArrayMap.this.value, this.pos, Long2ByteArrayMap.this.value, this.pos - 1, tail);
                    Long2ByteArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(ByteConsumer action) {
                    int max = Long2ByteArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Long2ByteArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ByteSpliterator spliterator() {
            return new ValuesSpliterator(0, Long2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(ByteConsumer action) {
            int max = Long2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Long2ByteArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Long2ByteArrayMap.this.size;
        }

        @Override
        public void clear() {
            Long2ByteArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends ByteSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements ByteSpliterator {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16720;
            }

            @Override
            protected final byte get(int location) {
                return Long2ByteArrayMap.this.value[location];
            }

            @Override
            protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
                int max = Long2ByteArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Long2ByteArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

