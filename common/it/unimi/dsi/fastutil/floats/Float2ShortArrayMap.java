/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2ShortMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ShortMap;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Float2ShortArrayMap
extends AbstractFloat2ShortMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient float[] key;
    private transient short[] value;
    private int size;
    private transient Float2ShortMap.FastEntrySet entries;
    private transient FloatSet keys;
    private transient ShortCollection values;

    public Float2ShortArrayMap(float[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Float2ShortArrayMap() {
        this.key = FloatArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Float2ShortArrayMap(int capacity) {
        this.key = new float[capacity];
        this.value = new short[capacity];
    }

    public Float2ShortArrayMap(Float2ShortMap m) {
        this(m.size());
        int i = 0;
        for (Float2ShortMap.Entry e : m.float2ShortEntrySet()) {
            this.key[i] = e.getFloatKey();
            this.value[i] = e.getShortValue();
            ++i;
        }
        this.size = i;
    }

    public Float2ShortArrayMap(Map<? extends Float, ? extends Short> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<? extends Float, ? extends Short> e : m.entrySet()) {
            this.key[i] = e.getKey().floatValue();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Float2ShortArrayMap(float[] key, short[] value, int size) {
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

    public Float2ShortMap.FastEntrySet float2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public short get(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
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
    public boolean containsKey(float k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(short v) {
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
    public short put(float k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            short[] newValue = new short[this.size == 0 ? 2 : this.size * 2];
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
    public short remove(float k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        short oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public FloatSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ShortCollection values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Float2ShortArrayMap clone() {
        Float2ShortArrayMap c;
        try {
            c = (Float2ShortArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.value = (short[])this.value.clone();
        c.entries = null;
        c.keys = null;
        c.values = null;
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int max = this.size;
        for (int i = 0; i < max; ++i) {
            s.writeFloat(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new float[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readFloat();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Float2ShortMap.Entry>
    implements Float2ShortMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Float2ShortMap.Entry> iterator() {
            return new ObjectIterator<Float2ShortMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Float2ShortArrayMap.this.size;
                }

                @Override
                public Float2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractFloat2ShortMap.BasicEntry(Float2ShortArrayMap.this.key[this.curr], Float2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ShortArrayMap.this.key, this.next + 1, Float2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ShortArrayMap.this.value, this.next + 1, Float2ShortArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Float2ShortMap.Entry> action) {
                    int max = Float2ShortArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractFloat2ShortMap.BasicEntry(Float2ShortArrayMap.this.key[this.curr], Float2ShortArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Float2ShortMap.Entry> fastIterator() {
            return new ObjectIterator<Float2ShortMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractFloat2ShortMap.BasicEntry entry = new AbstractFloat2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Float2ShortArrayMap.this.size;
                }

                @Override
                public Float2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Float2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Float2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ShortArrayMap.this.key, this.next + 1, Float2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ShortArrayMap.this.value, this.next + 1, Float2ShortArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Float2ShortMap.Entry> action) {
                    int max = Float2ShortArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Float2ShortArrayMap.this.key[this.curr];
                        this.entry.value = Float2ShortArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Float2ShortMap.Entry> spliterator() {
            return new EntrySetSpliterator(0, Float2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Float2ShortMap.Entry> action) {
            int max = Float2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractFloat2ShortMap.BasicEntry(Float2ShortArrayMap.this.key[i], Float2ShortArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Float2ShortMap.Entry> action) {
            AbstractFloat2ShortMap.BasicEntry entry = new AbstractFloat2ShortMap.BasicEntry();
            int max = Float2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Float2ShortArrayMap.this.key[i];
                entry.value = Float2ShortArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Float2ShortArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            return Float2ShortArrayMap.this.containsKey(k) && Float2ShortArrayMap.this.get(k) == ((Short)e.getValue()).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            short v = (Short)e.getValue();
            int oldPos = Float2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Float2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Float2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ShortArrayMap.this.key, oldPos + 1, Float2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ShortArrayMap.this.value, oldPos + 1, Float2ShortArrayMap.this.value, oldPos, tail);
            Float2ShortArrayMap.this.size--;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2ShortMap.Entry>
        implements ObjectSpliterator<Float2ShortMap.Entry> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Float2ShortMap.Entry get(int location) {
                return new AbstractFloat2ShortMap.BasicEntry(Float2ShortArrayMap.this.key[location], Float2ShortArrayMap.this.value[location]);
            }

            protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractFloatSet {
        private KeySet() {
        }

        @Override
        public boolean contains(float k) {
            return Float2ShortArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(float k) {
            int oldPos = Float2ShortArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Float2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ShortArrayMap.this.key, oldPos + 1, Float2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ShortArrayMap.this.value, oldPos + 1, Float2ShortArrayMap.this.value, oldPos, tail);
            Float2ShortArrayMap.this.size--;
            return true;
        }

        @Override
        public FloatIterator iterator() {
            return new FloatIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Float2ShortArrayMap.this.size;
                }

                @Override
                public float nextFloat() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Float2ShortArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Float2ShortArrayMap.this.size - this.pos;
                    System.arraycopy(Float2ShortArrayMap.this.key, this.pos, Float2ShortArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Float2ShortArrayMap.this.value, this.pos, Float2ShortArrayMap.this.value, this.pos - 1, tail);
                    Float2ShortArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(FloatConsumer action) {
                    int max = Float2ShortArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Float2ShortArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public FloatSpliterator spliterator() {
            return new KeySetSpliterator(0, Float2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(FloatConsumer action) {
            int max = Float2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Float2ShortArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Float2ShortArrayMap.this.size;
        }

        @Override
        public void clear() {
            Float2ShortArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends FloatSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements FloatSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final float get(int location) {
                return Float2ShortArrayMap.this.key[location];
            }

            @Override
            protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
                int max = Float2ShortArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Float2ShortArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractShortCollection {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(short v) {
            return Float2ShortArrayMap.this.containsValue(v);
        }

        @Override
        public ShortIterator iterator() {
            return new ShortIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Float2ShortArrayMap.this.size;
                }

                @Override
                public short nextShort() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Float2ShortArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Float2ShortArrayMap.this.size - this.pos;
                    System.arraycopy(Float2ShortArrayMap.this.key, this.pos, Float2ShortArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Float2ShortArrayMap.this.value, this.pos, Float2ShortArrayMap.this.value, this.pos - 1, tail);
                    Float2ShortArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(ShortConsumer action) {
                    int max = Float2ShortArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Float2ShortArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ShortSpliterator spliterator() {
            return new ValuesSpliterator(0, Float2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(ShortConsumer action) {
            int max = Float2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Float2ShortArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Float2ShortArrayMap.this.size;
        }

        @Override
        public void clear() {
            Float2ShortArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends ShortSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements ShortSpliterator {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16720;
            }

            @Override
            protected final short get(int location) {
                return Float2ShortArrayMap.this.value[location];
            }

            @Override
            protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(ShortConsumer action) {
                int max = Float2ShortArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Float2ShortArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

