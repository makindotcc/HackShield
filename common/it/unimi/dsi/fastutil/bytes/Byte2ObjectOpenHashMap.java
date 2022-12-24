/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteObjectPair;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class Byte2ObjectOpenHashMap<V>
extends AbstractByte2ObjectMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Byte2ObjectMap.FastEntrySet<V> entries;
    protected transient ByteSet keys;
    protected transient ObjectCollection<V> values;

    public Byte2ObjectOpenHashMap(int expected, float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new byte[this.n + 1];
        this.value = new Object[this.n + 1];
    }

    public Byte2ObjectOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Byte2ObjectOpenHashMap() {
        this(16, 0.75f);
    }

    public Byte2ObjectOpenHashMap(Map<? extends Byte, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Byte2ObjectOpenHashMap(Map<? extends Byte, ? extends V> m) {
        this(m, 0.75f);
    }

    public Byte2ObjectOpenHashMap(Byte2ObjectMap<V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Byte2ObjectOpenHashMap(Byte2ObjectMap<V> m) {
        this(m, 0.75f);
    }

    public Byte2ObjectOpenHashMap(byte[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Byte2ObjectOpenHashMap(byte[] k, V[] v) {
        this(k, v, 0.75f);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(byte k) {
        if (k == 0) {
            return this.containsNullKey ? this.n : -(this.n + 1);
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return -(pos + 1);
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return -(pos + 1);
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, byte k, V v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public V put(byte k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(-pos - 1, k, v);
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        byte[] key = this.key;
        while (true) {
            byte curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    this.value[last] = null;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    @Override
    public V remove(byte k) {
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public V get(byte k) {
        if (k == 0) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(byte k) {
        if (k == 0) {
            return this.containsNullKey;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(Object v) {
        V[] value = this.value;
        byte[] key = this.key;
        if (this.containsNullKey && Objects.equals(value[this.n], v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0 || !Objects.equals(value[i], v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V getOrDefault(byte k, V defaultValue) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public V putIfAbsent(byte k, V v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(-pos - 1, k, v);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(byte k, Object v) {
        if (k == 0) {
            if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr || !Objects.equals(v, this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(byte k, V oldValue, V v) {
        int pos = this.find(k);
        if (pos < 0 || !Objects.equals(oldValue, this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public V replace(byte k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(byte k, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        V newValue = mappingFunction.apply(k);
        this.insert(-pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public V computeIfAbsent(byte key, Byte2ObjectFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(key);
        if (pos >= 0) {
            return this.value[pos];
        }
        if (!mappingFunction.containsKey(key)) {
            return (V)this.defRetValue;
        }
        V newValue = mappingFunction.get(key);
        this.insert(-pos - 1, key, newValue);
        return newValue;
    }

    @Override
    public V computeIfPresent(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        if (this.value[pos] == null) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply(k, this.value[pos]);
        if (newValue == null) {
            if (k == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public V compute(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        V newValue = remappingFunction.apply(k, pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == 0) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return (V)this.defRetValue;
        }
        V newVal = newValue;
        if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public V merge(byte k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(v);
        int pos = this.find(k);
        if (pos < 0 || this.value[pos] == null) {
            if (pos < 0) {
                this.insert(-pos - 1, k, v);
            } else {
                this.value[pos] = v;
            }
            return v;
        }
        V newValue = remappingFunction.apply(this.value[pos], v);
        if (newValue == null) {
            if (k == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, (byte)0);
        Arrays.fill(this.value, null);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Byte2ObjectMap.FastEntrySet<V> byte2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ByteSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public ObjectSpliterator<V> spliterator() {
                    return new ValueSpliterator();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Byte2ObjectOpenHashMap.this.containsNullKey) {
                        consumer.accept(Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n]);
                    }
                    int pos = Byte2ObjectOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Byte2ObjectOpenHashMap.this.key[pos] == 0) continue;
                        consumer.accept(Byte2ObjectOpenHashMap.this.value[pos]);
                    }
                }

                @Override
                public int size() {
                    return Byte2ObjectOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Byte2ObjectOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Byte2ObjectOpenHashMap.this.clear();
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        return this.trim(this.size);
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        byte[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {
            }
            int pos = HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Byte2ObjectOpenHashMap<V> clone() {
        Byte2ObjectOpenHashMap c;
        try {
            c = (Byte2ObjectOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (byte[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            t = this.key[i];
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        byte[] key = this.key;
        V[] value = this.value;
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeByte(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            byte k = s.readByte();
            Object v = s.readObject();
            if (k == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                while (key[pos] != 0) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k;
            value[pos] = v;
        }
    }

    private void checkTable() {
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Byte2ObjectMap.Entry<V>>
    implements Byte2ObjectMap.FastEntrySet<V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ObjectMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Byte2ObjectMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectSpliterator<Byte2ObjectMap.Entry<V>> spliterator() {
            return new EntrySpliterator();
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
            byte k = (Byte)e.getKey();
            Object v = e.getValue();
            if (k == 0) {
                return Byte2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n], v);
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Byte2ObjectOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k == curr) {
                return Objects.equals(Byte2ObjectOpenHashMap.this.value[pos], v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2ObjectOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k != curr);
            return Objects.equals(Byte2ObjectOpenHashMap.this.value[pos], v);
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
            byte k = (Byte)e.getKey();
            Object v = e.getValue();
            if (k == 0) {
                if (Byte2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n], v)) {
                    Byte2ObjectOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Byte2ObjectOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k) {
                if (Objects.equals(Byte2ObjectOpenHashMap.this.value[pos], v)) {
                    Byte2ObjectOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2ObjectOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k || !Objects.equals(Byte2ObjectOpenHashMap.this.value[pos], v));
            Byte2ObjectOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Byte2ObjectOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Byte2ObjectOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
            if (Byte2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractByte2ObjectMap.BasicEntry(Byte2ObjectOpenHashMap.this.key[Byte2ObjectOpenHashMap.this.n], Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n]));
            }
            int pos = Byte2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Byte2ObjectOpenHashMap.this.key[pos] == 0) continue;
                consumer.accept(new AbstractByte2ObjectMap.BasicEntry(Byte2ObjectOpenHashMap.this.key[pos], Byte2ObjectOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
            AbstractByte2ObjectMap.BasicEntry entry = new AbstractByte2ObjectMap.BasicEntry();
            if (Byte2ObjectOpenHashMap.this.containsNullKey) {
                entry.key = Byte2ObjectOpenHashMap.this.key[Byte2ObjectOpenHashMap.this.n];
                entry.value = Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Byte2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Byte2ObjectOpenHashMap.this.key[pos] == 0) continue;
                entry.key = Byte2ObjectOpenHashMap.this.key[pos];
                entry.value = Byte2ObjectOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private final class KeySet
    extends AbstractByteSet {
        private KeySet() {
        }

        @Override
        public ByteIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public ByteSpliterator spliterator() {
            return new KeySpliterator();
        }

        @Override
        public void forEach(ByteConsumer consumer) {
            if (Byte2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(Byte2ObjectOpenHashMap.this.key[Byte2ObjectOpenHashMap.this.n]);
            }
            int pos = Byte2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                byte k = Byte2ObjectOpenHashMap.this.key[pos];
                if (k == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Byte2ObjectOpenHashMap.this.size;
        }

        @Override
        public boolean contains(byte k) {
            return Byte2ObjectOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(byte k) {
            int oldSize = Byte2ObjectOpenHashMap.this.size;
            Byte2ObjectOpenHashMap.this.remove(k);
            return Byte2ObjectOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Byte2ObjectOpenHashMap.this.clear();
        }
    }

    private final class EntryIterator
    extends MapIterator<Consumer<? super Byte2ObjectMap.Entry<V>>>
    implements ObjectIterator<Byte2ObjectMap.Entry<V>> {
        private MapEntry entry;

        private EntryIterator() {
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
            this.entry = new MapEntry(index);
            action.accept(this.entry);
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private final class ValueSpliterator
    extends MapSpliterator<Consumer<? super V>, ValueSpliterator>
    implements ObjectSpliterator<V> {
        private static final int POST_SPLIT_CHARACTERISTICS = 0;

        ValueSpliterator() {
        }

        ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            super(pos, max, mustReturnNull, hasSplit);
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 0 : 64;
        }

        @Override
        final void acceptOnIndex(Consumer<? super V> action, int index) {
            action.accept(Byte2ObjectOpenHashMap.this.value[index]);
        }

        @Override
        final ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
            return new ValueSpliterator(pos, max, mustReturnNull, true);
        }
    }

    private final class ValueIterator
    extends MapIterator<Consumer<? super V>>
    implements ObjectIterator<V> {
        @Override
        final void acceptOnIndex(Consumer<? super V> action, int index) {
            action.accept(Byte2ObjectOpenHashMap.this.value[index]);
        }

        @Override
        public V next() {
            return Byte2ObjectOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySpliterator
    extends MapSpliterator<ByteConsumer, KeySpliterator>
    implements ByteSpliterator {
        private static final int POST_SPLIT_CHARACTERISTICS = 257;

        KeySpliterator() {
        }

        KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            super(pos, max, mustReturnNull, hasSplit);
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 257 : 321;
        }

        @Override
        final void acceptOnIndex(ByteConsumer action, int index) {
            action.accept(Byte2ObjectOpenHashMap.this.key[index]);
        }

        @Override
        final KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
            return new KeySpliterator(pos, max, mustReturnNull, true);
        }
    }

    private final class KeyIterator
    extends MapIterator<ByteConsumer>
    implements ByteIterator {
        @Override
        final void acceptOnIndex(ByteConsumer action, int index) {
            action.accept(Byte2ObjectOpenHashMap.this.key[index]);
        }

        @Override
        public byte nextByte() {
            return Byte2ObjectOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class EntrySpliterator
    extends MapSpliterator<Consumer<? super Byte2ObjectMap.Entry<V>>, EntrySpliterator>
    implements ObjectSpliterator<Byte2ObjectMap.Entry<V>> {
        private static final int POST_SPLIT_CHARACTERISTICS = 1;

        EntrySpliterator() {
        }

        EntrySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            super(pos, max, mustReturnNull, hasSplit);
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 1 : 65;
        }

        @Override
        final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
            action.accept(new MapEntry(index));
        }

        @Override
        final EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
            return new EntrySpliterator(pos, max, mustReturnNull, true);
        }
    }

    private abstract class MapSpliterator<ConsumerType, SplitType extends MapSpliterator<ConsumerType, SplitType>> {
        int pos = 0;
        int max;
        int c;
        boolean mustReturnNull;
        boolean hasSplit;

        MapSpliterator() {
            this.max = Byte2ObjectOpenHashMap.this.n;
            this.c = 0;
            this.mustReturnNull = Byte2ObjectOpenHashMap.this.containsNullKey;
            this.hasSplit = false;
        }

        MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            this.max = Byte2ObjectOpenHashMap.this.n;
            this.c = 0;
            this.mustReturnNull = Byte2ObjectOpenHashMap.this.containsNullKey;
            this.hasSplit = false;
            this.pos = pos;
            this.max = max;
            this.mustReturnNull = mustReturnNull;
            this.hasSplit = hasSplit;
        }

        abstract void acceptOnIndex(ConsumerType var1, int var2);

        abstract SplitType makeForSplit(int var1, int var2, boolean var3);

        public boolean tryAdvance(ConsumerType action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++this.c;
                this.acceptOnIndex(action, Byte2ObjectOpenHashMap.this.n);
                return true;
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    ++this.c;
                    this.acceptOnIndex(action, this.pos++);
                    return true;
                }
                ++this.pos;
            }
            return false;
        }

        public void forEachRemaining(ConsumerType action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++this.c;
                this.acceptOnIndex(action, Byte2ObjectOpenHashMap.this.n);
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    this.acceptOnIndex(action, this.pos);
                    ++this.c;
                }
                ++this.pos;
            }
        }

        public long estimateSize() {
            if (!this.hasSplit) {
                return Byte2ObjectOpenHashMap.this.size - this.c;
            }
            return Math.min((long)(Byte2ObjectOpenHashMap.this.size - this.c), (long)((double)Byte2ObjectOpenHashMap.this.realSize() / (double)Byte2ObjectOpenHashMap.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
        }

        public SplitType trySplit() {
            if (this.pos >= this.max - 1) {
                return null;
            }
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            int myNewPos = this.pos + retLen;
            int retPos = this.pos;
            int retMax = myNewPos;
            SplitType split = this.makeForSplit(retPos, retMax, this.mustReturnNull);
            this.pos = myNewPos;
            this.mustReturnNull = false;
            this.hasSplit = true;
            return split;
        }

        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0L) {
                return 0L;
            }
            long skipped = 0L;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++skipped;
                --n;
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            while (this.pos < this.max && n > 0L) {
                if (key[this.pos++] == 0) continue;
                ++skipped;
                --n;
            }
            return skipped;
        }
    }

    private final class FastEntryIterator
    extends MapIterator<Consumer<? super Byte2ObjectMap.Entry<V>>>
    implements ObjectIterator<Byte2ObjectMap.Entry<V>> {
        private final MapEntry entry;

        private FastEntryIterator() {
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
            this.entry.index = index;
            action.accept(this.entry);
        }
    }

    private abstract class MapIterator<ConsumerType> {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ByteArrayList wrapped;

        private MapIterator() {
            this.pos = Byte2ObjectOpenHashMap.this.n;
            this.last = -1;
            this.c = Byte2ObjectOpenHashMap.this.size;
            this.mustReturnNullKey = Byte2ObjectOpenHashMap.this.containsNullKey;
        }

        abstract void acceptOnIndex(ConsumerType var1, int var2);

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Byte2ObjectOpenHashMap.this.n;
                return this.last;
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                byte k = this.wrapped.getByte(-this.pos - 1);
                int p = HashCommon.mix(k) & Byte2ObjectOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Byte2ObjectOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return this.last;
        }

        public void forEachRemaining(ConsumerType action) {
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Byte2ObjectOpenHashMap.this.n;
                this.acceptOnIndex(action, this.last);
                --this.c;
            }
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            while (this.c != 0) {
                if (--this.pos < 0) {
                    this.last = Integer.MIN_VALUE;
                    byte k = this.wrapped.getByte(-this.pos - 1);
                    int p = HashCommon.mix(k) & Byte2ObjectOpenHashMap.this.mask;
                    while (k != key[p]) {
                        p = p + 1 & Byte2ObjectOpenHashMap.this.mask;
                    }
                    this.acceptOnIndex(action, p);
                    --this.c;
                    continue;
                }
                if (key[this.pos] == 0) continue;
                this.last = this.pos;
                this.acceptOnIndex(action, this.last);
                --this.c;
            }
        }

        private void shiftKeys(int pos) {
            byte[] key = Byte2ObjectOpenHashMap.this.key;
            while (true) {
                byte curr;
                int last = pos;
                pos = last + 1 & Byte2ObjectOpenHashMap.this.mask;
                while (true) {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        Byte2ObjectOpenHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & Byte2ObjectOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Byte2ObjectOpenHashMap.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Byte2ObjectOpenHashMap.this.value[last] = Byte2ObjectOpenHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Byte2ObjectOpenHashMap.this.n) {
                Byte2ObjectOpenHashMap.this.containsNullKey = false;
                Byte2ObjectOpenHashMap.this.value[Byte2ObjectOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Byte2ObjectOpenHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                this.last = -1;
                return;
            }
            --Byte2ObjectOpenHashMap.this.size;
            this.last = -1;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
    }

    final class MapEntry
    implements Byte2ObjectMap.Entry<V>,
    Map.Entry<Byte, V>,
    ByteObjectPair<V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public byte getByteKey() {
            return Byte2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public byte leftByte() {
            return Byte2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Byte2ObjectOpenHashMap.this.value[this.index];
        }

        @Override
        public V right() {
            return Byte2ObjectOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Byte2ObjectOpenHashMap.this.value[this.index];
            Byte2ObjectOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        public ByteObjectPair<V> right(V v) {
            Byte2ObjectOpenHashMap.this.value[this.index] = v;
            return this;
        }

        @Override
        @Deprecated
        public Byte getKey() {
            return Byte2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Byte2ObjectOpenHashMap.this.key[this.index] == (Byte)e.getKey() && Objects.equals(Byte2ObjectOpenHashMap.this.value[this.index], e.getValue());
        }

        @Override
        public int hashCode() {
            return Byte2ObjectOpenHashMap.this.key[this.index] ^ (Byte2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Byte2ObjectOpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return Byte2ObjectOpenHashMap.this.key[this.index] + "=>" + Byte2ObjectOpenHashMap.this.value[this.index];
        }
    }
}

