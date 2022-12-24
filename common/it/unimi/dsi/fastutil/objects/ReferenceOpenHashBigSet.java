/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectBigArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceOpenHashBigSet<K>
extends AbstractReferenceSet<K>
implements Serializable,
Cloneable,
Hash,
Size64 {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[][] key;
    protected transient long mask;
    protected transient int segmentMask;
    protected transient int baseMask;
    protected transient boolean containsNull;
    protected transient long n;
    protected transient long maxFill;
    protected final transient long minN;
    protected final float f;
    protected long size;
    private static final Collector<Object, ?, ReferenceOpenHashBigSet<Object>> TO_SET_COLLECTOR = Collector.of(ReferenceOpenHashBigSet::new, ReferenceOpenHashBigSet::add, ReferenceOpenHashBigSet::combine, new Collector.Characteristics[0]);

    private void initMasks() {
        this.mask = this.n - 1L;
        this.segmentMask = this.key[0].length - 1;
        this.baseMask = this.key.length - 1;
    }

    public ReferenceOpenHashBigSet(long expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.bigArraySize(expected, f);
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = ObjectBigArrays.newBigArray(this.n);
        this.initMasks();
    }

    public ReferenceOpenHashBigSet(long expected) {
        this(expected, 0.75f);
    }

    public ReferenceOpenHashBigSet() {
        this(16L, 0.75f);
    }

    public ReferenceOpenHashBigSet(Collection<? extends K> c, float f) {
        this(Size64.sizeOf(c), f);
        this.addAll(c);
    }

    public ReferenceOpenHashBigSet(Collection<? extends K> c) {
        this(c, 0.75f);
    }

    public ReferenceOpenHashBigSet(ReferenceCollection<? extends K> c, float f) {
        this(Size64.sizeOf(c), f);
        this.addAll((Collection<? extends K>)c);
    }

    public ReferenceOpenHashBigSet(ReferenceCollection<? extends K> c) {
        this(c, 0.75f);
    }

    public ReferenceOpenHashBigSet(Iterator<? extends K> i, float f) {
        this(16L, f);
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ReferenceOpenHashBigSet(Iterator<? extends K> i) {
        this(i, 0.75f);
    }

    public ReferenceOpenHashBigSet(K[] a, int offset, int length, float f) {
        this(length < 0 ? 0L : (long)length, f);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public ReferenceOpenHashBigSet(K[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public ReferenceOpenHashBigSet(K[] a, float f) {
        this(a, 0, a.length, f);
    }

    public ReferenceOpenHashBigSet(K[] a) {
        this(a, 0.75f);
    }

    private ReferenceOpenHashBigSet<K> combine(ReferenceOpenHashBigSet<? extends K> toAddFrom) {
        this.addAll((Collection<? extends K>)toAddFrom);
        return this;
    }

    public static <K> Collector<K, ?, ReferenceOpenHashBigSet<K>> toBigSet() {
        return TO_SET_COLLECTOR;
    }

    public static <K> Collector<K, ?, ReferenceOpenHashBigSet<K>> toBigSetWithExpectedSize(long expectedSize) {
        return Collector.of(() -> new ReferenceOpenHashBigSet(expectedSize), ReferenceOpenHashBigSet::add, ReferenceOpenHashBigSet::combine, new Collector.Characteristics[0]);
    }

    private long realSize() {
        return this.containsNull ? this.size - 1L : this.size;
    }

    private void ensureCapacity(long capacity) {
        long needed = HashCommon.bigArraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    @Override
    public boolean addAll(Collection<? extends K> c) {
        long size = Size64.sizeOf(c);
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(size);
        } else {
            this.ensureCapacity(this.size64() + size);
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(K k) {
        if (k == null) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            int displ;
            K[][] key = this.key;
            long h = HashCommon.mix((long)System.identityHashCode(k));
            int base = (int)((h & this.mask) >>> 27);
            K curr = key[base][displ = (int)(h & (long)this.segmentMask)];
            if (curr != null) {
                if (curr == k) {
                    return false;
                }
                while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
                    if (curr != k) continue;
                    return false;
                }
            }
            key[base][displ] = k;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(2L * this.n);
        }
        return true;
    }

    protected final void shiftKeys(long pos) {
        K[][] key = this.key;
        while (true) {
            long last = pos;
            pos = last + 1L & this.mask;
            while (true) {
                if (BigArrays.get(key, pos) == null) {
                    BigArrays.set(key, last, null);
                    return;
                }
                long slot = HashCommon.mix((long)System.identityHashCode(BigArrays.get(key, pos))) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1L & this.mask;
            }
            BigArrays.set(key, last, BigArrays.get(key, pos));
        }
    }

    private boolean removeEntry(int base, int displ) {
        --this.size;
        this.shiftKeys((long)base * 0x8000000L + (long)displ);
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    @Override
    public boolean remove(Object k) {
        int displ;
        if (k == null) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        K[][] key = this.key;
        long h = HashCommon.mix((long)System.identityHashCode(k));
        int base = (int)((h & this.mask) >>> 27);
        K curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (curr == null) {
            return false;
        }
        if (curr == k) {
            return this.removeEntry(base, displ);
        }
        do {
            if ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) continue;
            return false;
        } while (curr != k);
        return this.removeEntry(base, displ);
    }

    @Override
    public boolean contains(Object k) {
        int displ;
        if (k == null) {
            return this.containsNull;
        }
        K[][] key = this.key;
        long h = HashCommon.mix((long)System.identityHashCode(k));
        int base = (int)((h & this.mask) >>> 27);
        K curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (curr == null) {
            return false;
        }
        if (curr == k) {
            return true;
        }
        do {
            if ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) continue;
            return false;
        } while (curr != k);
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0L) {
            return;
        }
        this.size = 0L;
        this.containsNull = false;
        BigArrays.fill(this.key, null);
    }

    @Override
    public ObjectIterator<K> iterator() {
        return new SetIterator();
    }

    @Override
    public ObjectSpliterator<K> spliterator() {
        return new SetSpliterator();
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        if (this.containsNull) {
            action.accept(null);
        }
        long pos = 0L;
        long max = this.n;
        K[][] key = this.key;
        while (pos < max) {
            K gotten;
            if ((gotten = BigArrays.get(key, pos++)) == null) continue;
            action.accept(gotten);
        }
    }

    public boolean trim() {
        return this.trim(this.size);
    }

    public boolean trim(long n) {
        long l = HashCommon.bigArraySize(n, this.f);
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

    protected void rehash(long newN) {
        K[][] key = this.key;
        Object[][] newKey = ObjectBigArrays.newBigArray(newN);
        long mask = newN - 1L;
        int newSegmentMask = newKey[0].length - 1;
        int newBaseMask = newKey.length - 1;
        int base = 0;
        int displ = 0;
        long i = this.realSize();
        while (i-- != 0L) {
            int d;
            while (key[base][displ] == null) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            K k = key[base][displ];
            long h = HashCommon.mix((long)System.identityHashCode(k));
            int b = (int)((h & mask) >>> 27);
            if (newKey[b][d = (int)(h & (long)newSegmentMask)] != null) {
                while (newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d] != null) {
                }
            }
            newKey[b][d] = k;
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        this.n = newN;
        this.key = newKey;
        this.initMasks();
        this.maxFill = HashCommon.maxFill(this.n, this.f);
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size);
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public ReferenceOpenHashBigSet<K> clone() {
        ReferenceOpenHashBigSet c;
        try {
            c = (ReferenceOpenHashBigSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = BigArrays.copy(this.key);
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        K[][] key = this.key;
        int h = 0;
        int base = 0;
        int displ = 0;
        long j = this.realSize();
        while (j-- != 0L) {
            while (key[base][displ] == null) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            if (this != key[base][displ]) {
                h += System.identityHashCode(key[base][displ]);
            }
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        Iterator i = this.iterator();
        s.defaultWriteObject();
        long j = this.size;
        while (j-- != 0L) {
            s.writeObject(i.next());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.bigArraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = ObjectBigArrays.newBigArray(this.n);
        Object[][] key = this.key;
        this.initMasks();
        long i = this.size;
        while (i-- != 0L) {
            int displ;
            Object k = s.readObject();
            if (k == null) {
                this.containsNull = true;
                continue;
            }
            long h = HashCommon.mix((long)System.identityHashCode(k));
            int base = (int)((h & this.mask) >>> 27);
            if (key[base][displ = (int)(h & (long)this.segmentMask)] != null) {
                while (key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ] != null) {
                }
            }
            key[base][displ] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements ObjectIterator<K> {
        int base;
        int displ;
        long last;
        long c;
        boolean mustReturnNull;
        ReferenceArrayList<K> wrapped;

        private SetIterator() {
            this.base = ReferenceOpenHashBigSet.this.key.length;
            this.last = -1L;
            this.c = ReferenceOpenHashBigSet.this.size;
            this.mustReturnNull = ReferenceOpenHashBigSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0L;
        }

        @Override
        public K next() {
            Object k;
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = ReferenceOpenHashBigSet.this.n;
                return null;
            }
            K[][] key = ReferenceOpenHashBigSet.this.key;
            do {
                if (this.displ == 0 && this.base <= 0) {
                    this.last = Long.MIN_VALUE;
                    return this.wrapped.get(-(--this.base) - 1);
                }
                if (this.displ-- != 0) continue;
                this.displ = key[--this.base].length - 1;
            } while ((k = key[this.base][this.displ]) == null);
            this.last = (long)this.base * 0x8000000L + (long)this.displ;
            return k;
        }

        private final void shiftKeys(long pos) {
            K[][] key = ReferenceOpenHashBigSet.this.key;
            while (true) {
                Object curr;
                long last = pos;
                pos = last + 1L & ReferenceOpenHashBigSet.this.mask;
                while (true) {
                    if ((curr = BigArrays.get(key, pos)) == null) {
                        BigArrays.set(key, last, null);
                        return;
                    }
                    long slot = HashCommon.mix((long)System.identityHashCode(curr)) & ReferenceOpenHashBigSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1L & ReferenceOpenHashBigSet.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList();
                    }
                    this.wrapped.add(BigArrays.get(key, pos));
                }
                BigArrays.set(key, last, curr);
            }
        }

        @Override
        public void remove() {
            if (this.last == -1L) {
                throw new IllegalStateException();
            }
            if (this.last == ReferenceOpenHashBigSet.this.n) {
                ReferenceOpenHashBigSet.this.containsNull = false;
            } else if (this.base >= 0) {
                this.shiftKeys(this.last);
            } else {
                ReferenceOpenHashBigSet.this.remove(this.wrapped.set(-this.base - 1, (Object)null));
                this.last = -1L;
                return;
            }
            --ReferenceOpenHashBigSet.this.size;
            this.last = -1L;
        }
    }

    private class SetSpliterator
    implements ObjectSpliterator<K> {
        private static final int POST_SPLIT_CHARACTERISTICS = 1;
        long pos = 0L;
        long max;
        long c;
        boolean mustReturnNull;
        boolean hasSplit;

        SetSpliterator() {
            this.max = ReferenceOpenHashBigSet.this.n;
            this.c = 0L;
            this.mustReturnNull = ReferenceOpenHashBigSet.this.containsNull;
            this.hasSplit = false;
        }

        SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
            this.max = ReferenceOpenHashBigSet.this.n;
            this.c = 0L;
            this.mustReturnNull = ReferenceOpenHashBigSet.this.containsNull;
            this.hasSplit = false;
            this.pos = pos;
            this.max = max;
            this.mustReturnNull = mustReturnNull;
            this.hasSplit = hasSplit;
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++this.c;
                action.accept(null);
                return true;
            }
            K[][] key = ReferenceOpenHashBigSet.this.key;
            while (this.pos < this.max) {
                Object gotten = BigArrays.get(key, this.pos);
                if (gotten != null) {
                    ++this.c;
                    ++this.pos;
                    action.accept(gotten);
                    return true;
                }
                ++this.pos;
            }
            return false;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                action.accept(null);
                ++this.c;
            }
            K[][] key = ReferenceOpenHashBigSet.this.key;
            while (this.pos < this.max) {
                Object gotten = BigArrays.get(key, this.pos);
                if (gotten != null) {
                    action.accept(gotten);
                    ++this.c;
                }
                ++this.pos;
            }
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 1 : 65;
        }

        @Override
        public long estimateSize() {
            if (!this.hasSplit) {
                return ReferenceOpenHashBigSet.this.size - this.c;
            }
            return Math.min(ReferenceOpenHashBigSet.this.size - this.c, (long)((double)ReferenceOpenHashBigSet.this.realSize() / (double)ReferenceOpenHashBigSet.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
        }

        @Override
        public SetSpliterator trySplit() {
            if (this.pos >= this.max - 1L) {
                return null;
            }
            long retLen = this.max - this.pos >> 1;
            if (retLen <= 1L) {
                return null;
            }
            long myNewPos = this.pos + retLen;
            myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, this.max - 1L);
            long retPos = this.pos;
            long retMax = myNewPos;
            SetSpliterator split = new SetSpliterator(retPos, retMax, this.mustReturnNull, true);
            this.pos = myNewPos;
            this.mustReturnNull = false;
            this.hasSplit = true;
            return split;
        }

        @Override
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
            K[][] key = ReferenceOpenHashBigSet.this.key;
            while (this.pos < this.max && n > 0L) {
                if (BigArrays.get(key, this.pos++) == null) continue;
                ++skipped;
                --n;
            }
            return skipped;
        }
    }
}

