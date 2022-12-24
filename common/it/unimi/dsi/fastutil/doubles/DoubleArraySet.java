/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.DoubleConsumer;

public class DoubleArraySet
extends AbstractDoubleSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient double[] a;
    private int size;

    public DoubleArraySet(double[] a) {
        this.a = a;
        this.size = a.length;
    }

    public DoubleArraySet() {
        this.a = DoubleArrays.EMPTY_ARRAY;
    }

    public DoubleArraySet(int capacity) {
        this.a = new double[capacity];
    }

    public DoubleArraySet(DoubleCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public DoubleArraySet(Collection<? extends Double> c) {
        this(c.size());
        this.addAll(c);
    }

    public DoubleArraySet(DoubleSet c) {
        this(c.size());
        int i = 0;
        DoubleIterator doubleIterator = c.iterator();
        while (doubleIterator.hasNext()) {
            double x;
            this.a[i] = x = ((Double)doubleIterator.next()).doubleValue();
            ++i;
        }
        this.size = i;
    }

    public DoubleArraySet(Set<? extends Double> c) {
        this(c.size());
        int i = 0;
        for (Double d : c) {
            this.a[i] = d;
            ++i;
        }
        this.size = i;
    }

    public DoubleArraySet(double[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    public static DoubleArraySet of() {
        return DoubleArraySet.ofUnchecked();
    }

    public static DoubleArraySet of(double e) {
        return DoubleArraySet.ofUnchecked(e);
    }

    public static DoubleArraySet of(double ... a) {
        if (a.length == 2) {
            if (Double.doubleToLongBits(a[0]) == Double.doubleToLongBits(a[1])) {
                throw new IllegalArgumentException("Duplicate element: " + a[1]);
            }
        } else if (a.length > 2) {
            DoubleOpenHashSet.of(a);
        }
        return DoubleArraySet.ofUnchecked(a);
    }

    public static DoubleArraySet ofUnchecked() {
        return new DoubleArraySet();
    }

    public static DoubleArraySet ofUnchecked(double ... a) {
        return new DoubleArraySet(a);
    }

    private int findKey(double o) {
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(this.a[i]) != Double.doubleToLongBits(o)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public DoubleIterator iterator() {
        return new DoubleIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < DoubleArraySet.this.size;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return DoubleArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = DoubleArraySet.this.size-- - this.next--;
                System.arraycopy(DoubleArraySet.this.a, this.next + 1, DoubleArraySet.this.a, this.next, tail);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = DoubleArraySet.this.size - this.next;
                if (n < remaining) {
                    this.next += n;
                    return n;
                }
                n = remaining;
                this.next = DoubleArraySet.this.size;
                return n;
            }
        };
    }

    @Override
    public DoubleSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public boolean contains(double k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(double k) {
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
    public boolean add(double k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            double[] b = new double[this.size == 0 ? 2 : this.size * 2];
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
    public double[] toDoubleArray() {
        return Arrays.copyOf(this.a, this.size);
    }

    @Override
    public double[] toArray(double[] a) {
        if (a == null || a.length < this.size) {
            a = new double[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    public DoubleArraySet clone() {
        DoubleArraySet c;
        try {
            c = (DoubleArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (double[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readDouble();
        }
    }

    private final class Spliterator
    implements DoubleSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, doubleArraySet.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : DoubleArraySet.this.size;
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
        public boolean tryAdvance(DoubleConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(DoubleArraySet.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(DoubleArraySet.this.a[this.pos]);
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
        public DoubleSpliterator trySplit() {
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

