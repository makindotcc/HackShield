/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
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
import java.util.function.Predicate;

public class Object2BooleanOpenHashMap<K>
extends AbstractObject2BooleanMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient boolean[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Object2BooleanMap.FastEntrySet<K> entries;
    protected transient ObjectSet<K> keys;
    protected transient BooleanCollection values;

    public Object2BooleanOpenHashMap(int expected, float f) {
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
        this.key = new Object[this.n + 1];
        this.value = new boolean[this.n + 1];
    }

    public Object2BooleanOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Object2BooleanOpenHashMap() {
        this(16, 0.75f);
    }

    public Object2BooleanOpenHashMap(Map<? extends K, ? extends Boolean> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2BooleanOpenHashMap(Map<? extends K, ? extends Boolean> m) {
        this(m, 0.75f);
    }

    public Object2BooleanOpenHashMap(Object2BooleanMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2BooleanOpenHashMap(Object2BooleanMap<K> m) {
        this(m, 0.75f);
    }

    public Object2BooleanOpenHashMap(K[] k, boolean[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2BooleanOpenHashMap(K[] k, boolean[] v) {
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

    private boolean removeEntry(int pos) {
        boolean oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private boolean removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        boolean oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Boolean> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(K k) {
        if (k == null) {
            return this.containsNullKey ? this.n : -(this.n + 1);
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return -(pos + 1);
        }
        if (k.equals(curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return -(pos + 1);
        } while (!k.equals(curr));
        return pos;
    }

    private void insert(int pos, K k, boolean v) {
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
    public boolean put(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(-pos - 1, k, v);
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        K[] key = this.key;
        while (true) {
            K curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == null) {
                    key[last] = null;
                    return;
                }
                int slot = HashCommon.mix(curr.hashCode()) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    @Override
    public boolean removeBoolean(Object k) {
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean getBoolean(Object k) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k.equals(curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!k.equals(curr));
        return true;
    }

    @Override
    public boolean containsValue(boolean v) {
        boolean[] value = this.value;
        K[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean getOrDefault(Object k, boolean defaultValue) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return defaultValue;
        }
        if (k.equals(curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return defaultValue;
        } while (!k.equals(curr));
        return this.value[pos];
    }

    @Override
    public boolean putIfAbsent(K k, boolean v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(-pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, boolean v) {
        if (k == null) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(k.hashCode()) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k.equals(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!k.equals(curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, boolean oldValue, boolean v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public boolean replace(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public boolean computeIfAbsent(K k, Predicate<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        boolean newValue = mappingFunction.test(k);
        this.insert(-pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public boolean computeIfAbsent(K key, Object2BooleanFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(key);
        if (pos >= 0) {
            return this.value[pos];
        }
        if (!mappingFunction.containsKey(key)) {
            return this.defRetValue;
        }
        boolean newValue = mappingFunction.getBoolean(key);
        this.insert(-pos - 1, key, newValue);
        return newValue;
    }

    @Override
    public boolean computeBooleanIfPresent(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Boolean newValue = remappingFunction.apply(k, this.value[pos]);
        if (newValue == null) {
            if (k == null) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public boolean computeBoolean(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Boolean newValue = remappingFunction.apply(k, pos >= 0 ? Boolean.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == null) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        boolean newVal = newValue;
        if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public boolean merge(K k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            if (pos < 0) {
                this.insert(-pos - 1, k, v);
            } else {
                this.value[pos] = v;
            }
            return v;
        }
        Boolean newValue = remappingFunction.apply((Boolean)this.value[pos], (Boolean)v);
        if (newValue == null) {
            if (k == null) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
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
        Arrays.fill(this.key, null);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Object2BooleanMap.FastEntrySet<K> object2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ObjectSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public BooleanSpliterator spliterator() {
                    return new ValueSpliterator();
                }

                @Override
                public void forEach(BooleanConsumer consumer) {
                    if (Object2BooleanOpenHashMap.this.containsNullKey) {
                        consumer.accept(Object2BooleanOpenHashMap.this.value[Object2BooleanOpenHashMap.this.n]);
                    }
                    int pos = Object2BooleanOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2BooleanOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2BooleanOpenHashMap.this.value[pos]);
                    }
                }

                @Override
                public int size() {
                    return Object2BooleanOpenHashMap.this.size;
                }

                @Override
                public boolean contains(boolean v) {
                    return Object2BooleanOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2BooleanOpenHashMap.this.clear();
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
        K[] key = this.key;
        boolean[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        boolean[] newValue = new boolean[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == null) {
            }
            int pos = HashCommon.mix(key[i].hashCode()) & mask;
            if (newKey[pos] != null) {
                while (newKey[pos = pos + 1 & mask] != null) {
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

    public Object2BooleanOpenHashMap<K> clone() {
        Object2BooleanOpenHashMap c;
        try {
            c = (Object2BooleanOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (boolean[])this.value.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = this.key[i].hashCode();
            }
            h += (t ^= this.value[i] ? 1231 : 1237);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] ? 1231 : 1237;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        boolean[] value = this.value;
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeBoolean(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new boolean[this.n + 1];
        boolean[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            boolean v = s.readBoolean();
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k.hashCode()) & this.mask;
                while (key[pos] != null) {
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
    extends AbstractObjectSet<Object2BooleanMap.Entry<K>>
    implements Object2BooleanMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectSpliterator<Object2BooleanMap.Entry<K>> spliterator() {
            return new EntrySpliterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (k == null) {
                return Object2BooleanOpenHashMap.this.containsNullKey && Object2BooleanOpenHashMap.this.value[Object2BooleanOpenHashMap.this.n] == v;
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2BooleanOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k.equals(curr)) {
                return Object2BooleanOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!k.equals(curr));
            return Object2BooleanOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (k == null) {
                if (Object2BooleanOpenHashMap.this.containsNullKey && Object2BooleanOpenHashMap.this.value[Object2BooleanOpenHashMap.this.n] == v) {
                    Object2BooleanOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2BooleanOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr.equals(k)) {
                if (Object2BooleanOpenHashMap.this.value[pos] == v) {
                    Object2BooleanOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!curr.equals(k) || Object2BooleanOpenHashMap.this.value[pos] != v);
            Object2BooleanOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2BooleanOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2BooleanOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            if (Object2BooleanOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanOpenHashMap.this.key[Object2BooleanOpenHashMap.this.n], Object2BooleanOpenHashMap.this.value[Object2BooleanOpenHashMap.this.n]));
            }
            int pos = Object2BooleanOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanOpenHashMap.this.key[pos], Object2BooleanOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            AbstractObject2BooleanMap.BasicEntry entry = new AbstractObject2BooleanMap.BasicEntry();
            if (Object2BooleanOpenHashMap.this.containsNullKey) {
                entry.key = Object2BooleanOpenHashMap.this.key[Object2BooleanOpenHashMap.this.n];
                entry.value = Object2BooleanOpenHashMap.this.value[Object2BooleanOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2BooleanOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanOpenHashMap.this.key[pos] == null) continue;
                entry.key = Object2BooleanOpenHashMap.this.key[pos];
                entry.value = Object2BooleanOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private final class KeySet
    extends AbstractObjectSet<K> {
        private KeySet() {
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return new KeySpliterator();
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            if (Object2BooleanOpenHashMap.this.containsNullKey) {
                consumer.accept(Object2BooleanOpenHashMap.this.key[Object2BooleanOpenHashMap.this.n]);
            }
            int pos = Object2BooleanOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2BooleanOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2BooleanOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2BooleanOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2BooleanOpenHashMap.this.size;
            Object2BooleanOpenHashMap.this.removeBoolean(k);
            return Object2BooleanOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2BooleanOpenHashMap.this.clear();
        }
    }

    private final class EntryIterator
    extends MapIterator<Consumer<? super Object2BooleanMap.Entry<K>>>
    implements ObjectIterator<Object2BooleanMap.Entry<K>> {
        private MapEntry entry;

        private EntryIterator() {
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        final void acceptOnIndex(Consumer<? super Object2BooleanMap.Entry<K>> action, int index) {
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
    extends MapSpliterator<BooleanConsumer, ValueSpliterator>
    implements BooleanSpliterator {
        private static final int POST_SPLIT_CHARACTERISTICS = 256;

        ValueSpliterator() {
        }

        ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            super(pos, max, mustReturnNull, hasSplit);
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 256 : 320;
        }

        @Override
        final void acceptOnIndex(BooleanConsumer action, int index) {
            action.accept(Object2BooleanOpenHashMap.this.value[index]);
        }

        @Override
        final ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
            return new ValueSpliterator(pos, max, mustReturnNull, true);
        }
    }

    private final class ValueIterator
    extends MapIterator<BooleanConsumer>
    implements BooleanIterator {
        @Override
        final void acceptOnIndex(BooleanConsumer action, int index) {
            action.accept(Object2BooleanOpenHashMap.this.value[index]);
        }

        @Override
        public boolean nextBoolean() {
            return Object2BooleanOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySpliterator
    extends MapSpliterator<Consumer<? super K>, KeySpliterator>
    implements ObjectSpliterator<K> {
        private static final int POST_SPLIT_CHARACTERISTICS = 1;

        KeySpliterator() {
        }

        KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            super(pos, max, mustReturnNull, hasSplit);
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 1 : 65;
        }

        @Override
        final void acceptOnIndex(Consumer<? super K> action, int index) {
            action.accept(Object2BooleanOpenHashMap.this.key[index]);
        }

        @Override
        final KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
            return new KeySpliterator(pos, max, mustReturnNull, true);
        }
    }

    private final class KeyIterator
    extends MapIterator<Consumer<? super K>>
    implements ObjectIterator<K> {
        @Override
        final void acceptOnIndex(Consumer<? super K> action, int index) {
            action.accept(Object2BooleanOpenHashMap.this.key[index]);
        }

        @Override
        public K next() {
            return Object2BooleanOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class EntrySpliterator
    extends MapSpliterator<Consumer<? super Object2BooleanMap.Entry<K>>, EntrySpliterator>
    implements ObjectSpliterator<Object2BooleanMap.Entry<K>> {
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
        final void acceptOnIndex(Consumer<? super Object2BooleanMap.Entry<K>> action, int index) {
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
            this.max = Object2BooleanOpenHashMap.this.n;
            this.c = 0;
            this.mustReturnNull = Object2BooleanOpenHashMap.this.containsNullKey;
            this.hasSplit = false;
        }

        MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            this.max = Object2BooleanOpenHashMap.this.n;
            this.c = 0;
            this.mustReturnNull = Object2BooleanOpenHashMap.this.containsNullKey;
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
                this.acceptOnIndex(action, Object2BooleanOpenHashMap.this.n);
                return true;
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != null) {
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
                this.acceptOnIndex(action, Object2BooleanOpenHashMap.this.n);
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != null) {
                    this.acceptOnIndex(action, this.pos);
                    ++this.c;
                }
                ++this.pos;
            }
        }

        public long estimateSize() {
            if (!this.hasSplit) {
                return Object2BooleanOpenHashMap.this.size - this.c;
            }
            return Math.min((long)(Object2BooleanOpenHashMap.this.size - this.c), (long)((double)Object2BooleanOpenHashMap.this.realSize() / (double)Object2BooleanOpenHashMap.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
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
            K[] key = Object2BooleanOpenHashMap.this.key;
            while (this.pos < this.max && n > 0L) {
                if (key[this.pos++] == null) continue;
                ++skipped;
                --n;
            }
            return skipped;
        }
    }

    private final class FastEntryIterator
    extends MapIterator<Consumer<? super Object2BooleanMap.Entry<K>>>
    implements ObjectIterator<Object2BooleanMap.Entry<K>> {
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
        final void acceptOnIndex(Consumer<? super Object2BooleanMap.Entry<K>> action, int index) {
            this.entry.index = index;
            action.accept(this.entry);
        }
    }

    private abstract class MapIterator<ConsumerType> {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ObjectArrayList<K> wrapped;

        private MapIterator() {
            this.pos = Object2BooleanOpenHashMap.this.n;
            this.last = -1;
            this.c = Object2BooleanOpenHashMap.this.size;
            this.mustReturnNullKey = Object2BooleanOpenHashMap.this.containsNullKey;
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
                this.last = Object2BooleanOpenHashMap.this.n;
                return this.last;
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                Object k = this.wrapped.get(-this.pos - 1);
                int p = HashCommon.mix(k.hashCode()) & Object2BooleanOpenHashMap.this.mask;
                while (!k.equals(key[p])) {
                    p = p + 1 & Object2BooleanOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        public void forEachRemaining(ConsumerType action) {
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Object2BooleanOpenHashMap.this.n;
                this.acceptOnIndex(action, this.last);
                --this.c;
            }
            K[] key = Object2BooleanOpenHashMap.this.key;
            while (this.c != 0) {
                if (--this.pos < 0) {
                    this.last = Integer.MIN_VALUE;
                    Object k = this.wrapped.get(-this.pos - 1);
                    int p = HashCommon.mix(k.hashCode()) & Object2BooleanOpenHashMap.this.mask;
                    while (!k.equals(key[p])) {
                        p = p + 1 & Object2BooleanOpenHashMap.this.mask;
                    }
                    this.acceptOnIndex(action, p);
                    --this.c;
                    continue;
                }
                if (key[this.pos] == null) continue;
                this.last = this.pos;
                this.acceptOnIndex(action, this.last);
                --this.c;
            }
        }

        private void shiftKeys(int pos) {
            K[] key = Object2BooleanOpenHashMap.this.key;
            while (true) {
                Object curr;
                int last = pos;
                pos = last + 1 & Object2BooleanOpenHashMap.this.mask;
                while (true) {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(curr.hashCode()) & Object2BooleanOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Object2BooleanOpenHashMap.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ObjectArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Object2BooleanOpenHashMap.this.value[last] = Object2BooleanOpenHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Object2BooleanOpenHashMap.this.n) {
                Object2BooleanOpenHashMap.this.containsNullKey = false;
                Object2BooleanOpenHashMap.this.key[Object2BooleanOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Object2BooleanOpenHashMap.this.removeBoolean(this.wrapped.set(-this.pos - 1, (Object)null));
                this.last = -1;
                return;
            }
            --Object2BooleanOpenHashMap.this.size;
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
    implements Object2BooleanMap.Entry<K>,
    Map.Entry<K, Boolean>,
    ObjectBooleanPair<K> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2BooleanOpenHashMap.this.key[this.index];
        }

        @Override
        public K left() {
            return Object2BooleanOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean getBooleanValue() {
            return Object2BooleanOpenHashMap.this.value[this.index];
        }

        @Override
        public boolean rightBoolean() {
            return Object2BooleanOpenHashMap.this.value[this.index];
        }

        @Override
        public boolean setValue(boolean v) {
            boolean oldValue = Object2BooleanOpenHashMap.this.value[this.index];
            Object2BooleanOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public ObjectBooleanPair<K> right(boolean v) {
            Object2BooleanOpenHashMap.this.value[this.index] = v;
            return this;
        }

        @Override
        @Deprecated
        public Boolean getValue() {
            return Object2BooleanOpenHashMap.this.value[this.index];
        }

        @Override
        @Deprecated
        public Boolean setValue(Boolean v) {
            return this.setValue((boolean)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Objects.equals(Object2BooleanOpenHashMap.this.key[this.index], e.getKey()) && Object2BooleanOpenHashMap.this.value[this.index] == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return (Object2BooleanOpenHashMap.this.key[this.index] == null ? 0 : Object2BooleanOpenHashMap.this.key[this.index].hashCode()) ^ (Object2BooleanOpenHashMap.this.value[this.index] ? 1231 : 1237);
        }

        public String toString() {
            return Object2BooleanOpenHashMap.this.key[this.index] + "=>" + Object2BooleanOpenHashMap.this.value[this.index];
        }
    }
}

