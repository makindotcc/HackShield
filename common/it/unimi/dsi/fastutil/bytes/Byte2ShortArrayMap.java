/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
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

public class Byte2ShortArrayMap
extends AbstractByte2ShortMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient short[] value;
    private int size;
    private transient Byte2ShortMap.FastEntrySet entries;
    private transient ByteSet keys;
    private transient ShortCollection values;

    public Byte2ShortArrayMap(byte[] key, short[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2ShortArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = ShortArrays.EMPTY_ARRAY;
    }

    public Byte2ShortArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new short[capacity];
    }

    public Byte2ShortArrayMap(Byte2ShortMap m) {
        this(m.size());
        int i = 0;
        for (Byte2ShortMap.Entry e : m.byte2ShortEntrySet()) {
            this.key[i] = e.getByteKey();
            this.value[i] = e.getShortValue();
            ++i;
        }
        this.size = i;
    }

    public Byte2ShortArrayMap(Map<? extends Byte, ? extends Short> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<? extends Byte, ? extends Short> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Byte2ShortArrayMap(byte[] key, short[] value, int size) {
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

    public Byte2ShortMap.FastEntrySet byte2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(byte k) {
        byte[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public short get(byte k) {
        byte[] key = this.key;
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
    public boolean containsKey(byte k) {
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
    public short put(byte k, short v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            short oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public short remove(byte k) {
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
    public ByteSet keySet() {
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

    public Byte2ShortArrayMap clone() {
        Byte2ShortArrayMap c;
        try {
            c = (Byte2ShortArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (byte[])this.key.clone();
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
            s.writeByte(this.key[i]);
            s.writeShort(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readByte();
            this.value[i] = s.readShort();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2ShortMap.Entry>
    implements Byte2ShortMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> iterator() {
            return new ObjectIterator<Byte2ShortMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ShortArrayMap.this.size;
                }

                @Override
                public Byte2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2ShortMap.BasicEntry(Byte2ShortArrayMap.this.key[this.curr], Byte2ShortArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.next + 1, Byte2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.next + 1, Byte2ShortArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Byte2ShortMap.Entry> action) {
                    int max = Byte2ShortArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractByte2ShortMap.BasicEntry(Byte2ShortArrayMap.this.key[this.curr], Byte2ShortArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> fastIterator() {
            return new ObjectIterator<Byte2ShortMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2ShortMap.BasicEntry entry = new AbstractByte2ShortMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ShortArrayMap.this.size;
                }

                @Override
                public Byte2ShortMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2ShortArrayMap.this.key[this.curr];
                    this.entry.value = Byte2ShortArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ShortArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.next + 1, Byte2ShortArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.next + 1, Byte2ShortArrayMap.this.value, this.next, tail);
                }

                @Override
                public void forEachRemaining(Consumer<? super Byte2ShortMap.Entry> action) {
                    int max = Byte2ShortArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Byte2ShortArrayMap.this.key[this.curr];
                        this.entry.value = Byte2ShortArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Byte2ShortMap.Entry> spliterator() {
            return new EntrySetSpliterator(0, Byte2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Byte2ShortMap.Entry> action) {
            int max = Byte2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractByte2ShortMap.BasicEntry(Byte2ShortArrayMap.this.key[i], Byte2ShortArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Byte2ShortMap.Entry> action) {
            AbstractByte2ShortMap.BasicEntry entry = new AbstractByte2ShortMap.BasicEntry();
            int max = Byte2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Byte2ShortArrayMap.this.key[i];
                entry.value = Byte2ShortArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Byte2ShortArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            return Byte2ShortArrayMap.this.containsKey(k) && Byte2ShortArrayMap.this.get(k) == ((Short)e.getValue()).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            short v = (Short)e.getValue();
            int oldPos = Byte2ShortArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Byte2ShortArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Byte2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ShortArrayMap.this.key, oldPos + 1, Byte2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ShortArrayMap.this.value, oldPos + 1, Byte2ShortArrayMap.this.value, oldPos, tail);
            Byte2ShortArrayMap.this.size--;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Byte2ShortMap.Entry>
        implements ObjectSpliterator<Byte2ShortMap.Entry> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Byte2ShortMap.Entry get(int location) {
                return new AbstractByte2ShortMap.BasicEntry(Byte2ShortArrayMap.this.key[location], Byte2ShortArrayMap.this.value[location]);
            }

            protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractByteSet {
        private KeySet() {
        }

        @Override
        public boolean contains(byte k) {
            return Byte2ShortArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(byte k) {
            int oldPos = Byte2ShortArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Byte2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ShortArrayMap.this.key, oldPos + 1, Byte2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ShortArrayMap.this.value, oldPos + 1, Byte2ShortArrayMap.this.value, oldPos, tail);
            Byte2ShortArrayMap.this.size--;
            return true;
        }

        @Override
        public ByteIterator iterator() {
            return new ByteIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Byte2ShortArrayMap.this.size;
                }

                @Override
                public byte nextByte() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Byte2ShortArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Byte2ShortArrayMap.this.size - this.pos;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.pos, Byte2ShortArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.pos, Byte2ShortArrayMap.this.value, this.pos - 1, tail);
                    Byte2ShortArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(ByteConsumer action) {
                    int max = Byte2ShortArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Byte2ShortArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ByteSpliterator spliterator() {
            return new KeySetSpliterator(0, Byte2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(ByteConsumer action) {
            int max = Byte2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Byte2ShortArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Byte2ShortArrayMap.this.size;
        }

        @Override
        public void clear() {
            Byte2ShortArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends ByteSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements ByteSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final byte get(int location) {
                return Byte2ShortArrayMap.this.key[location];
            }

            @Override
            protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
                int max = Byte2ShortArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Byte2ShortArrayMap.this.key[this.pos++]);
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
            return Byte2ShortArrayMap.this.containsValue(v);
        }

        @Override
        public ShortIterator iterator() {
            return new ShortIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Byte2ShortArrayMap.this.size;
                }

                @Override
                public short nextShort() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Byte2ShortArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Byte2ShortArrayMap.this.size - this.pos;
                    System.arraycopy(Byte2ShortArrayMap.this.key, this.pos, Byte2ShortArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Byte2ShortArrayMap.this.value, this.pos, Byte2ShortArrayMap.this.value, this.pos - 1, tail);
                    Byte2ShortArrayMap.this.size--;
                    --this.pos;
                }

                @Override
                public void forEachRemaining(ShortConsumer action) {
                    int max = Byte2ShortArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Byte2ShortArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ShortSpliterator spliterator() {
            return new ValuesSpliterator(0, Byte2ShortArrayMap.this.size);
        }

        @Override
        public void forEach(ShortConsumer action) {
            int max = Byte2ShortArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Byte2ShortArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Byte2ShortArrayMap.this.size;
        }

        @Override
        public void clear() {
            Byte2ShortArrayMap.this.clear();
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
                return Byte2ShortArrayMap.this.value[location];
            }

            @Override
            protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(ShortConsumer action) {
                int max = Byte2ShortArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Byte2ShortArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

