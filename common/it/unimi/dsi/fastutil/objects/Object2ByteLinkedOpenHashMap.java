/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObject2ByteMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2ByteSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectBytePair;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class Object2ByteLinkedOpenHashMap<K>
extends AbstractObject2ByteSortedMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient byte[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Object2ByteSortedMap.FastSortedEntrySet<K> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient ByteCollection values;

    public Object2ByteLinkedOpenHashMap(int expected, float f) {
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
        this.value = new byte[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Object2ByteLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Object2ByteLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Object2ByteLinkedOpenHashMap(Map<? extends K, ? extends Byte> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2ByteLinkedOpenHashMap(Map<? extends K, ? extends Byte> m) {
        this(m, 0.75f);
    }

    public Object2ByteLinkedOpenHashMap(Object2ByteMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Object2ByteLinkedOpenHashMap(Object2ByteMap<K> m) {
        this(m, 0.75f);
    }

    public Object2ByteLinkedOpenHashMap(K[] k, byte[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2ByteLinkedOpenHashMap(K[] k, byte[] v) {
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

    private byte removeEntry(int pos) {
        byte oldValue = this.value[pos];
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private byte removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        byte oldValue = this.value[this.n];
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Byte> m) {
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

    private void insert(int pos, K k, byte v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.last;
            this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public byte put(K k, byte v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(-pos - 1, k, v);
            return this.defRetValue;
        }
        byte oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private byte addToValue(int pos, byte incr) {
        byte oldValue = this.value[pos];
        this.value[pos] = (byte)(oldValue + incr);
        return oldValue;
    }

    public byte addTo(K k, byte incr) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(k.hashCode()) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = (byte)(this.defRetValue + incr);
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.last;
            this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
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
            this.fixPointers(pos, last);
        }
    }

    @Override
    public byte removeByte(Object k) {
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

    private byte setValue(int pos, byte v) {
        byte oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    public byte removeFirstByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            int n = this.first;
            this.link[n] = this.link[n] | 0xFFFFFFFF00000000L;
        }
        --this.size;
        byte v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    public byte removeLastByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            int n = this.last;
            this.link[n] = this.link[n] | 0xFFFFFFFFL;
        }
        --this.size;
        byte v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }

    private void moveIndexToFirst(int i) {
        if (this.size == 1 || this.first == i) {
            return;
        }
        if (this.last == i) {
            int n = this.last = (int)(this.link[i] >>> 32);
            this.link[n] = this.link[n] | 0xFFFFFFFFL;
        } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n = prev;
            this.link[n] = this.link[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n2 = next;
            this.link[n2] = this.link[n2] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n = this.first;
        this.link[n] = this.link[n] ^ (this.link[this.first] ^ ((long)i & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[i] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
        this.first = i;
    }

    private void moveIndexToLast(int i) {
        if (this.size == 1 || this.last == i) {
            return;
        }
        if (this.first == i) {
            int n = this.first = (int)this.link[i];
            this.link[n] = this.link[n] | 0xFFFFFFFF00000000L;
        } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n = prev;
            this.link[n] = this.link[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n2 = next;
            this.link[n2] = this.link[n2] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n = this.last;
        this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)i & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        this.link[i] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
        this.last = i;
    }

    public byte getAndMoveToFirst(K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
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
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public byte getAndMoveToLast(K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
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
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public byte putAndMoveToFirst(K k, byte v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(k.hashCode()) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.first;
            this.link[n] = this.link[n] ^ (this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[pos] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
            this.first = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }

    public byte putAndMoveToLast(K k, byte v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(k.hashCode()) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.last;
            this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }

    @Override
    public byte getByte(Object k) {
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
    public boolean containsValue(byte v) {
        byte[] value = this.value;
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
    public byte getOrDefault(Object k, byte defaultValue) {
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
    public byte putIfAbsent(K k, byte v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(-pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, byte v) {
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
    public boolean replace(K k, byte oldValue, byte v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public byte replace(K k, byte v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        byte oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public byte computeIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(k));
        this.insert(-pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public byte computeIfAbsent(K key, Object2ByteFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(key);
        if (pos >= 0) {
            return this.value[pos];
        }
        if (!mappingFunction.containsKey(key)) {
            return this.defRetValue;
        }
        byte newValue = mappingFunction.getByte(key);
        this.insert(-pos - 1, key, newValue);
        return newValue;
    }

    @Override
    public byte computeByteIfPresent(K k, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Byte newValue = remappingFunction.apply(k, this.value[pos]);
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
    public byte computeByte(K k, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Byte newValue = remappingFunction.apply(k, pos >= 0 ? Byte.valueOf(this.value[pos]) : null);
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
        byte newVal = newValue;
        if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public byte merge(K k, byte v, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        Byte newValue = remappingFunction.apply((Byte)this.value[pos], (Byte)v);
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
        this.last = -1;
        this.first = -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    protected void fixPointers(int i) {
        if (this.size == 0) {
            this.last = -1;
            this.first = -1;
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
                int n = this.first;
                this.link[n] = this.link[n] | 0xFFFFFFFF00000000L;
            }
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
                int n = this.last;
                this.link[n] = this.link[n] | 0xFFFFFFFFL;
            }
            return;
        }
        long linki = this.link[i];
        int prev = (int)(linki >>> 32);
        int next = (int)linki;
        int n = prev;
        this.link[n] = this.link[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n2 = next;
        this.link[n2] = this.link[n2] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
    }

    protected void fixPointers(int s, int d) {
        if (this.size == 1) {
            this.first = this.last = d;
            this.link[d] = -1L;
            return;
        }
        if (this.first == s) {
            this.first = d;
            int n = (int)this.link[s];
            this.link[n] = this.link[n] ^ (this.link[(int)this.link[s]] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[d] = this.link[s];
            return;
        }
        if (this.last == s) {
            this.last = d;
            int n = (int)(this.link[s] >>> 32);
            this.link[n] = this.link[n] ^ (this.link[(int)(this.link[s] >>> 32)] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[d] = this.link[s];
            return;
        }
        long links = this.link[s];
        int prev = (int)(links >>> 32);
        int next = (int)links;
        int n = prev;
        this.link[n] = this.link[n] ^ (this.link[prev] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n2 = next;
        this.link[n2] = this.link[n2] ^ (this.link[next] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[d] = links;
    }

    @Override
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public Object2ByteSortedMap<K> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ByteSortedMap<K> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ByteSortedMap<K> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public Object2ByteSortedMap.FastSortedEntrySet<K> object2ByteEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ByteCollection values() {
        if (this.values == null) {
            this.values = new AbstractByteCollection(){
                private static final int SPLITERATOR_CHARACTERISTICS = 336;

                @Override
                public ByteIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public ByteSpliterator spliterator() {
                    return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ByteLinkedOpenHashMap.this), 336);
                }

                @Override
                public void forEach(ByteConsumer consumer) {
                    int i = Object2ByteLinkedOpenHashMap.this.size;
                    int next = Object2ByteLinkedOpenHashMap.this.first;
                    while (i-- != 0) {
                        int curr = next;
                        next = (int)Object2ByteLinkedOpenHashMap.this.link[curr];
                        consumer.accept(Object2ByteLinkedOpenHashMap.this.value[curr]);
                    }
                }

                @Override
                public int size() {
                    return Object2ByteLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(byte v) {
                    return Object2ByteLinkedOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2ByteLinkedOpenHashMap.this.clear();
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
        byte[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        byte[] newValue = new byte[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == null) {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i].hashCode()) & mask;
                while (newKey[pos] != null) {
                    pos = pos + 1 & mask;
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
            if (prev != -1) {
                int n = newPrev;
                newLink[n] = newLink[n] ^ (newLink[newPrev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n2 = pos;
                newLink[n2] = newLink[n2] ^ (newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                newPrev = pos;
            } else {
                newPrev = this.first = pos;
                newLink[pos] = -1L;
            }
            int t = i;
            i = (int)link[i];
            prev = t;
        }
        this.link = newLink;
        this.last = newPrev;
        if (newPrev != -1) {
            int n = newPrev;
            newLink[n] = newLink[n] | 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Object2ByteLinkedOpenHashMap<K> clone() {
        Object2ByteLinkedOpenHashMap c;
        try {
            c = (Object2ByteLinkedOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (byte[])this.value.clone();
        c.link = (long[])this.link.clone();
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
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        byte[] value = this.value;
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeByte(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new byte[this.n + 1];
        byte[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            byte v = s.readByte();
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
            if (this.first != -1) {
                int n = prev;
                link[n] = link[n] ^ (link[prev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n2 = pos;
                link[n2] = link[n2] ^ (link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                prev = pos;
                continue;
            }
            prev = this.first = pos;
            int n = pos;
            link[n] = link[n] | 0xFFFFFFFF00000000L;
        }
        this.last = prev;
        if (prev != -1) {
            int n = prev;
            link[n] = link[n] | 0xFFFFFFFFL;
        }
    }

    private void checkTable() {
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Object2ByteMap.Entry<K>>
    implements Object2ByteSortedMap.FastSortedEntrySet<K> {
        private static final int SPLITERATOR_CHARACTERISTICS = 81;

        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectSpliterator<Object2ByteMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ByteLinkedOpenHashMap.this), 81);
        }

        @Override
        public Comparator<? super Object2ByteMap.Entry<K>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> fromElement, Object2ByteMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object2ByteMap.Entry<K> first() {
            if (Object2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2ByteLinkedOpenHashMap.this.first);
        }

        @Override
        public Object2ByteMap.Entry<K> last() {
            if (Object2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Object2ByteLinkedOpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            Object k = e.getKey();
            byte v = (Byte)e.getValue();
            if (k == null) {
                return Object2ByteLinkedOpenHashMap.this.containsNullKey && Object2ByteLinkedOpenHashMap.this.value[Object2ByteLinkedOpenHashMap.this.n] == v;
            }
            K[] key = Object2ByteLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2ByteLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k.equals(curr)) {
                return Object2ByteLinkedOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!k.equals(curr));
            return Object2ByteLinkedOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            Object k = e.getKey();
            byte v = (Byte)e.getValue();
            if (k == null) {
                if (Object2ByteLinkedOpenHashMap.this.containsNullKey && Object2ByteLinkedOpenHashMap.this.value[Object2ByteLinkedOpenHashMap.this.n] == v) {
                    Object2ByteLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2ByteLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k.hashCode()) & Object2ByteLinkedOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr.equals(k)) {
                if (Object2ByteLinkedOpenHashMap.this.value[pos] == v) {
                    Object2ByteLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!curr.equals(k) || Object2ByteLinkedOpenHashMap.this.value[pos] != v);
            Object2ByteLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2ByteLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2ByteLinkedOpenHashMap.this.clear();
        }

        @Override
        public ObjectListIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }

        @Override
        public ObjectListIterator<Object2ByteMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> from) {
            return new FastEntryIterator(from.getKey());
        }

        @Override
        public void forEach(Consumer<? super Object2ByteMap.Entry<K>> consumer) {
            int i = Object2ByteLinkedOpenHashMap.this.size;
            int next = Object2ByteLinkedOpenHashMap.this.first;
            while (i-- != 0) {
                int curr = next;
                next = (int)Object2ByteLinkedOpenHashMap.this.link[curr];
                consumer.accept(new AbstractObject2ByteMap.BasicEntry(Object2ByteLinkedOpenHashMap.this.key[curr], Object2ByteLinkedOpenHashMap.this.value[curr]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2ByteMap.Entry<K>> consumer) {
            AbstractObject2ByteMap.BasicEntry entry = new AbstractObject2ByteMap.BasicEntry();
            int i = Object2ByteLinkedOpenHashMap.this.size;
            int next = Object2ByteLinkedOpenHashMap.this.first;
            while (i-- != 0) {
                int curr = next;
                next = (int)Object2ByteLinkedOpenHashMap.this.link[curr];
                entry.key = Object2ByteLinkedOpenHashMap.this.key[curr];
                entry.value = Object2ByteLinkedOpenHashMap.this.value[curr];
                consumer.accept(entry);
            }
        }
    }

    private final class KeySet
    extends AbstractObjectSortedSet<K> {
        private static final int SPLITERATOR_CHARACTERISTICS = 81;

        private KeySet() {
        }

        @Override
        public ObjectListIterator<K> iterator(K from) {
            return new KeyIterator(from);
        }

        @Override
        public ObjectListIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ByteLinkedOpenHashMap.this), 81);
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            int i = Object2ByteLinkedOpenHashMap.this.size;
            int next = Object2ByteLinkedOpenHashMap.this.first;
            while (i-- != 0) {
                int curr = next;
                next = (int)Object2ByteLinkedOpenHashMap.this.link[curr];
                consumer.accept(Object2ByteLinkedOpenHashMap.this.key[curr]);
            }
        }

        @Override
        public int size() {
            return Object2ByteLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2ByteLinkedOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2ByteLinkedOpenHashMap.this.size;
            Object2ByteLinkedOpenHashMap.this.removeByte(k);
            return Object2ByteLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2ByteLinkedOpenHashMap.this.clear();
        }

        @Override
        public K first() {
            if (Object2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.first];
        }

        @Override
        public K last() {
            if (Object2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.last];
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class EntryIterator
    extends MapIterator<Consumer<? super Object2ByteMap.Entry<K>>>
    implements ObjectListIterator<Object2ByteMap.Entry<K>> {
        private MapEntry entry;

        public EntryIterator() {
        }

        public EntryIterator(K from) {
            super(from);
        }

        @Override
        final void acceptOnIndex(Consumer<? super Object2ByteMap.Entry<K>> action, int index) {
            action.accept(new MapEntry(index));
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry = new MapEntry(this.previousEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private final class ValueIterator
    extends MapIterator<ByteConsumer>
    implements ByteListIterator {
        @Override
        public byte previousByte() {
            return Object2ByteLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        final void acceptOnIndex(ByteConsumer action, int index) {
            action.accept(Object2ByteLinkedOpenHashMap.this.value[index]);
        }

        @Override
        public byte nextByte() {
            return Object2ByteLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeyIterator
    extends MapIterator<Consumer<? super K>>
    implements ObjectListIterator<K> {
        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K previous() {
            return Object2ByteLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
        }

        @Override
        final void acceptOnIndex(Consumer<? super K> action, int index) {
            action.accept(Object2ByteLinkedOpenHashMap.this.key[index]);
        }

        @Override
        public K next() {
            return Object2ByteLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class FastEntryIterator
    extends MapIterator<Consumer<? super Object2ByteMap.Entry<K>>>
    implements ObjectListIterator<Object2ByteMap.Entry<K>> {
        final MapEntry entry;

        public FastEntryIterator() {
            this.entry = new MapEntry();
        }

        public FastEntryIterator(K from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        final void acceptOnIndex(Consumer<? super Object2ByteMap.Entry<K>> action, int index) {
            this.entry.index = index;
            action.accept(this.entry);
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private abstract class MapIterator<ConsumerType> {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        abstract void acceptOnIndex(ConsumerType var1, int var2);

        protected MapIterator() {
            this.next = Object2ByteLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(K from) {
            if (from == null) {
                if (Object2ByteLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Object2ByteLinkedOpenHashMap.this.link[Object2ByteLinkedOpenHashMap.this.n];
                    this.prev = Object2ByteLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Objects.equals(Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.last], from)) {
                this.prev = Object2ByteLinkedOpenHashMap.this.last;
                this.index = Object2ByteLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from.hashCode()) & Object2ByteLinkedOpenHashMap.this.mask;
            while (Object2ByteLinkedOpenHashMap.this.key[pos] != null) {
                if (Object2ByteLinkedOpenHashMap.this.key[pos].equals(from)) {
                    this.next = (int)Object2ByteLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
        }

        public boolean hasNext() {
            return this.next != -1;
        }

        public boolean hasPrevious() {
            return this.prev != -1;
        }

        private final void ensureIndexKnown() {
            if (this.index >= 0) {
                return;
            }
            if (this.prev == -1) {
                this.index = 0;
                return;
            }
            if (this.next == -1) {
                this.index = Object2ByteLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Object2ByteLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Object2ByteLinkedOpenHashMap.this.link[pos];
                ++this.index;
            }
        }

        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }

        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)Object2ByteLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }

        public int previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(Object2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }

        public void forEachRemaining(ConsumerType action) {
            while (this.hasNext()) {
                this.curr = this.next;
                this.next = (int)Object2ByteLinkedOpenHashMap.this.link[this.curr];
                this.prev = this.curr;
                if (this.index >= 0) {
                    ++this.index;
                }
                this.acceptOnIndex(action, this.curr);
            }
        }

        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Object2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Object2ByteLinkedOpenHashMap.this.link[this.curr];
            }
            --Object2ByteLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Object2ByteLinkedOpenHashMap.this.first = this.next;
            } else {
                int n = this.prev;
                Object2ByteLinkedOpenHashMap.this.link[n] = Object2ByteLinkedOpenHashMap.this.link[n] ^ (Object2ByteLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Object2ByteLinkedOpenHashMap.this.last = this.prev;
            } else {
                int n = this.next;
                Object2ByteLinkedOpenHashMap.this.link[n] = Object2ByteLinkedOpenHashMap.this.link[n] ^ (Object2ByteLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Object2ByteLinkedOpenHashMap.this.n) {
                K[] key = Object2ByteLinkedOpenHashMap.this.key;
                while (true) {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & Object2ByteLinkedOpenHashMap.this.mask;
                    while (true) {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(curr.hashCode()) & Object2ByteLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
                    }
                    key[last] = curr;
                    Object2ByteLinkedOpenHashMap.this.value[last] = Object2ByteLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Object2ByteLinkedOpenHashMap.this.fixPointers(pos, last);
                }
            }
            Object2ByteLinkedOpenHashMap.this.containsNullKey = false;
            Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.n] = null;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }

        public int back(int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }

        public void set(Object2ByteMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }

        public void add(Object2ByteMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Object2ByteMap.Entry<K>,
    Map.Entry<K, Byte>,
    ObjectBytePair<K> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2ByteLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public K left() {
            return Object2ByteLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public byte getByteValue() {
            return Object2ByteLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public byte rightByte() {
            return Object2ByteLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public byte setValue(byte v) {
            byte oldValue = Object2ByteLinkedOpenHashMap.this.value[this.index];
            Object2ByteLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public ObjectBytePair<K> right(byte v) {
            Object2ByteLinkedOpenHashMap.this.value[this.index] = v;
            return this;
        }

        @Override
        @Deprecated
        public Byte getValue() {
            return Object2ByteLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        @Deprecated
        public Byte setValue(Byte v) {
            return this.setValue((byte)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Objects.equals(Object2ByteLinkedOpenHashMap.this.key[this.index], e.getKey()) && Object2ByteLinkedOpenHashMap.this.value[this.index] == (Byte)e.getValue();
        }

        @Override
        public int hashCode() {
            return (Object2ByteLinkedOpenHashMap.this.key[this.index] == null ? 0 : Object2ByteLinkedOpenHashMap.this.key[this.index].hashCode()) ^ Object2ByteLinkedOpenHashMap.this.value[this.index];
        }

        public String toString() {
            return Object2ByteLinkedOpenHashMap.this.key[this.index] + "=>" + Object2ByteLinkedOpenHashMap.this.value[this.index];
        }
    }
}

