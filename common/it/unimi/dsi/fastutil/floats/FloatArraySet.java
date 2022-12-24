/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class FloatArraySet
extends AbstractFloatSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient float[] a;
    private int size;

    public FloatArraySet(float[] a) {
        this.a = a;
        this.size = a.length;
    }

    public FloatArraySet() {
        this.a = FloatArrays.EMPTY_ARRAY;
    }

    public FloatArraySet(int capacity) {
        this.a = new float[capacity];
    }

    public FloatArraySet(FloatCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public FloatArraySet(Collection<? extends Float> c) {
        this(c.size());
        this.addAll(c);
    }

    public FloatArraySet(FloatSet c) {
        this(c.size());
        int i = 0;
        FloatIterator floatIterator = c.iterator();
        while (floatIterator.hasNext()) {
            float x;
            this.a[i] = x = ((Float)floatIterator.next()).floatValue();
            ++i;
        }
        this.size = i;
    }

    public FloatArraySet(Set<? extends Float> c) {
        this(c.size());
        int i = 0;
        for (Float f : c) {
            this.a[i] = f.floatValue();
            ++i;
        }
        this.size = i;
    }

    public FloatArraySet(float[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    public static FloatArraySet of() {
        return FloatArraySet.ofUnchecked();
    }

    public static FloatArraySet of(float e) {
        return FloatArraySet.ofUnchecked(e);
    }

    public static FloatArraySet of(float ... a) {
        if (a.length == 2) {
            if (Float.floatToIntBits(a[0]) == Float.floatToIntBits(a[1])) {
                throw new IllegalArgumentException("Duplicate element: " + a[1]);
            }
        } else if (a.length > 2) {
            FloatOpenHashSet.of(a);
        }
        return FloatArraySet.ofUnchecked(a);
    }

    public static FloatArraySet ofUnchecked() {
        return new FloatArraySet();
    }

    public static FloatArraySet ofUnchecked(float ... a) {
        return new FloatArraySet(a);
    }

    private int findKey(float o) {
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(this.a[i]) != Float.floatToIntBits(o)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public FloatIterator iterator() {
        return new FloatIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < FloatArraySet.this.size;
            }

            @Override
            public float nextFloat() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return FloatArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = FloatArraySet.this.size-- - this.next--;
                System.arraycopy(FloatArraySet.this.a, this.next + 1, FloatArraySet.this.a, this.next, tail);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = FloatArraySet.this.size - this.next;
                if (n < remaining) {
                    this.next += n;
                    return n;
                }
                n = remaining;
                this.next = FloatArraySet.this.size;
                return n;
            }
        };
    }

    @Override
    public FloatSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public boolean contains(float k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(float k) {
        int pos = this.findKey(k);
        if (pos == -1) {
            return false;
        }
        int tail = this.size - pos - 1;
        for (int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
        }
        --this.size;
        return true;
    }

    @Override
    public boolean add(float k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            float[] b = new float[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                b[i] = this.a[i];
            }
            this.a = b;
        }
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public float[] toFloatArray() {
        return Arrays.copyOf(this.a, this.size);
    }

    @Override
    public float[] toArray(float[] a) {
        if (a == null || a.length < this.size) {
            a = new float[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    public FloatArraySet clone() {
        FloatArraySet c;
        try {
            c = (FloatArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (float[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readFloat();
        }
    }

    private final class Spliterator
    implements FloatSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, floatArraySet.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : FloatArraySet.this.size;
        }

        @Override
        public int characteristics() {
            return 16721;
        }

        @Override
        public long estimateSize() {
            return this.getWorkingMax() - this.pos;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(FloatArraySet.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(FloatArraySet.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int max = this.getWorkingMax();
            if (this.pos >= max) {
                return 0L;
            }
            int remaining = max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = max;
            return n;
        }

        @Override
        public FloatSpliterator trySplit() {
            int myNewPos;
            int max = this.getWorkingMax();
            int retLen = max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            this.max = max;
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return new Spliterator(oldPos, retMax, true);
        }
    }
}

