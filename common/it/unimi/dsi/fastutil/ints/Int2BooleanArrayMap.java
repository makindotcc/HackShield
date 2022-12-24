/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.ints.AbstractInt2BooleanMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
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
import java.util.function.IntConsumer;

public class Int2BooleanArrayMap
extends AbstractInt2BooleanMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient int[] key;
    private transient boolean[] value;
    private int size;
    private transient Int2BooleanMap.FastEntrySet entries;
    private transient IntSet keys;
    private transient BooleanCollection values;

    public Int2BooleanArrayMap(int[] key, boolean[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Int2BooleanArrayMap() {
        this.key = IntArrays.EMPTY_ARRAY;
        this.value = BooleanArrays.EMPTY_ARRAY;
    }

    public Int2BooleanArrayMap(int capacity) {
        this.key = new int[capacity];
        this.value = new boolean[capacity];
    }

    public Int2BooleanArrayMap(Int2BooleanMap m) {
        this(m.size());
        int i = 0;
        for (Int2BooleanMap.Entry e : m.int2BooleanEntrySet()) {
            this.key[i] = e.getIntKey();
            this.value[i] = e.getBooleanValue();
            ++i;
        }
        this.size = i;
    }

    public Int2BooleanArrayMap(Map<? extends Integer, ? extends Boolean> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<? extends Integer, ? extends Boolean> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Int2BooleanArrayMap(int[] key, boolean[] value, int size) {
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

    public Int2BooleanMap.FastEntrySet int2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(int k) {
        int[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public boolean get(int k) {
        int[] key = this.key;
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
    public boolean containsKey(int k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(boolean v) {
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
    public boolean put(int k, boolean v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            boolean oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            int[] newKey = new int[this.size == 0 ? 2 : this.size * 2];
            boolean[] newValue = new boolean[this.size == 0 ? 2 : this.size * 2];
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
    public boolean remove(int k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public IntSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Int2BooleanArrayMap clone() {
        Int2BooleanArrayMap c;
        try {
            c = (Int2BooleanArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (int[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        c.entries = null;
        c.keys = null;
        c.values = null;
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int max = this.size;
        for (int i = 0; i < max; ++i) {
            s.writeInt(this.key[i]);
            s.writeBoolean(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new int[this.size];
        this.value = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readInt();
            this.value[i] = s.readBoolean();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Int2BooleanMap.Entry>
    implements Int2BooleanMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Int2BooleanMap.Entry> iterator() {
            return new ObjectIterator<Int2BooleanMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Int2BooleanArrayMap.this.size;
                }

                @Override
                public Int2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractInt2BooleanMap.BasicEntry(Int2BooleanArrayMap.this.key[this.curr], Int2BooleanArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.next + 1, Int2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.next + 1, Int2BooleanArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Int2BooleanMap.Entry> action) {
                    int max = Int2BooleanArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractInt2BooleanMap.BasicEntry(Int2BooleanArrayMap.this.key[this.curr], Int2BooleanArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Int2BooleanMap.Entry> fastIterator() {
            return new ObjectIterator<Int2BooleanMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractInt2BooleanMap.BasicEntry entry = new AbstractInt2BooleanMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Int2BooleanArrayMap.this.size;
                }

                @Override
                public Int2BooleanMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Int2BooleanArrayMap.this.key[this.curr];
                    this.entry.value = Int2BooleanArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Int2BooleanArrayMap.this.size-- - this.next--;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.next + 1, Int2BooleanArrayMap.this.key, this.next, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.next + 1, Int2BooleanArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Int2BooleanMap.Entry> action) {
                    int max = Int2BooleanArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Int2BooleanArrayMap.this.key[this.curr];
                        this.entry.value = Int2BooleanArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Int2BooleanMap.Entry> spliterator() {
            return new EntrySetSpliterator(0, Int2BooleanArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Int2BooleanMap.Entry> action) {
            int max = Int2BooleanArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractInt2BooleanMap.BasicEntry(Int2BooleanArrayMap.this.key[i], Int2BooleanArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Int2BooleanMap.Entry> action) {
            AbstractInt2BooleanMap.BasicEntry entry = new AbstractInt2BooleanMap.BasicEntry();
            int max = Int2BooleanArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Int2BooleanArrayMap.this.key[i];
                entry.value = Int2BooleanArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Int2BooleanArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            int k = (Integer)e.getKey();
            return Int2BooleanArrayMap.this.containsKey(k) && Int2BooleanArrayMap.this.get(k) == ((Boolean)e.getValue()).booleanValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            int k = (Integer)e.getKey();
            boolean v = (Boolean)e.getValue();
            int oldPos = Int2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Int2BooleanArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Int2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2BooleanArrayMap.this.key, oldPos + 1, Int2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2BooleanArrayMap.this.value, oldPos + 1, Int2BooleanArrayMap.this.value, oldPos, tail);
            Int2BooleanArrayMap.this.size--;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Int2BooleanMap.Entry>
        implements ObjectSpliterator<Int2BooleanMap.Entry> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Int2BooleanMap.Entry get(int location) {
                return new AbstractInt2BooleanMap.BasicEntry(Int2BooleanArrayMap.this.key[location], Int2BooleanArrayMap.this.value[location]);
            }

            protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractIntSet {
        private KeySet() {
        }

        @Override
        public boolean contains(int k) {
            return Int2BooleanArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(int k) {
            int oldPos = Int2BooleanArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Int2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2BooleanArrayMap.this.key, oldPos + 1, Int2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2BooleanArrayMap.this.value, oldPos + 1, Int2BooleanArrayMap.this.value, oldPos, tail);
            Int2BooleanArrayMap.this.size--;
            return true;
        }

        @Override
        public IntIterator iterator() {
            return new IntIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Int2BooleanArrayMap.this.size;
                }

                @Override
                public int nextInt() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Int2BooleanArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Int2BooleanArrayMap.this.size - this.pos;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.pos, Int2BooleanArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.pos, Int2BooleanArrayMap.this.value, this.pos - 1, tail);
                    Int2BooleanArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(IntConsumer action) {
                    int max = Int2BooleanArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Int2BooleanArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public IntSpliterator spliterator() {
            return new KeySetSpliterator(0, Int2BooleanArrayMap.this.size);
        }

        @Override
        public void forEach(IntConsumer action) {
            int max = Int2BooleanArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Int2BooleanArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Int2BooleanArrayMap.this.size;
        }

        @Override
        public void clear() {
            Int2BooleanArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends IntSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements IntSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final int get(int location) {
                return Int2BooleanArrayMap.this.key[location];
            }

            @Override
            protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(IntConsumer action) {
                int max = Int2BooleanArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Int2BooleanArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractBooleanCollection {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(boolean v) {
            return Int2BooleanArrayMap.this.containsValue(v);
        }

        @Override
        public BooleanIterator iterator() {
            return new BooleanIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Int2BooleanArrayMap.this.size;
                }

                @Override
                public boolean nextBoolean() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Int2BooleanArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Int2BooleanArrayMap.this.size - this.pos;
                    System.arraycopy(Int2BooleanArrayMap.this.key, this.pos, Int2BooleanArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Int2BooleanArrayMap.this.value, this.pos, Int2BooleanArrayMap.this.value, this.pos - 1, tail);
                    Int2BooleanArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(BooleanConsumer action) {
                    int max = Int2BooleanArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Int2BooleanArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public BooleanSpliterator spliterator() {
            return new ValuesSpliterator(0, Int2BooleanArrayMap.this.size);
        }

        @Override
        public void forEach(BooleanConsumer action) {
            int max = Int2BooleanArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Int2BooleanArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Int2BooleanArrayMap.this.size;
        }

        @Override
        public void clear() {
            Int2BooleanArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends BooleanSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements BooleanSpliterator {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16720;
            }

            @Override
            protected final boolean get(int location) {
                return Int2BooleanArrayMap.this.value[location];
            }

            @Override
            protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(BooleanConsumer action) {
                int max = Int2BooleanArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Int2BooleanArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

