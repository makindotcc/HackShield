/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;

public class DoubleImmutableList
extends DoubleLists.ImmutableListBase
implements DoubleList,
RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = 0L;
    static final DoubleImmutableList EMPTY = new DoubleImmutableList(DoubleArrays.EMPTY_ARRAY);
    private final double[] a;

    public DoubleImmutableList(double[] a) {
        this.a = a;
    }

    public DoubleImmutableList(Collection<? extends Double> c) {
        this(c.isEmpty() ? DoubleArrays.EMPTY_ARRAY : DoubleIterators.unwrap(DoubleIterators.asDoubleIterator(c.iterator())));
    }

    public DoubleImmutableList(DoubleCollection c) {
        this(c.isEmpty() ? DoubleArrays.EMPTY_ARRAY : DoubleIterators.unwrap(c.iterator()));
    }

    public DoubleImmutableList(DoubleList l) {
        this(l.isEmpty() ? DoubleArrays.EMPTY_ARRAY : new double[l.size()]);
        l.getElements(0, this.a, 0, l.size());
    }

    public DoubleImmutableList(double[] a, int offset, int length) {
        this(length == 0 ? DoubleArrays.EMPTY_ARRAY : new double[length]);
        System.arraycopy(a, offset, this.a, 0, length);
    }

    public DoubleImmutableList(DoubleIterator i) {
        this(i.hasNext() ? DoubleIterators.unwrap(i) : DoubleArrays.EMPTY_ARRAY);
    }

    public static DoubleImmutableList of() {
        return EMPTY;
    }

    public static DoubleImmutableList of(double ... init) {
        return init.length == 0 ? DoubleImmutableList.of() : new DoubleImmutableList(init);
    }

    private static DoubleImmutableList convertTrustedToImmutableList(DoubleArrayList arrayList) {
        if (arrayList.isEmpty()) {
            return DoubleImmutableList.of();
        }
        double[] backingArray = arrayList.elements();
        if (arrayList.size() != backingArray.length) {
            backingArray = Arrays.copyOf(backingArray, arrayList.size());
        }
        return new DoubleImmutableList(backingArray);
    }

    public static DoubleImmutableList toList(DoubleStream stream) {
        return DoubleImmutableList.convertTrustedToImmutableList(DoubleArrayList.toList(stream));
    }

    public static DoubleImmutableList toListWithExpectedSize(DoubleStream stream, int expectedSize) {
        return DoubleImmutableList.convertTrustedToImmutableList(DoubleArrayList.toListWithExpectedSize(stream, expectedSize));
    }

    @Override
    public double getDouble(int index) {
        if (index >= this.a.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(double k) {
        int size = this.a.length;
        for (int i = 0; i < size; ++i) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(double k) {
        int i = this.a.length;
        while (i-- != 0) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.a.length;
    }

    @Override
    public boolean isEmpty() {
        return this.a.length == 0;
    }

    @Override
    public void getElements(int from, double[] a, int offset, int length) {
        DoubleArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void forEach(DoubleConsumer action) {
        for (int i = 0; i < this.a.length; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public double[] toDoubleArray() {
        return (double[])this.a.clone();
    }

    @Override
    public double[] toArray(double[] a) {
        if (a == null || a.length < this.size()) {
            a = new double[this.a.length];
        }
        System.arraycopy(this.a, 0, a, 0, a.length);
        return a;
    }

    @Override
    public DoubleListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new DoubleListIterator(){
            int pos;
            {
                this.pos = index;
            }

            @Override
            public boolean hasNext() {
                return this.pos < DoubleImmutableList.this.a.length;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return DoubleImmutableList.this.a[this.pos++];
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return DoubleImmutableList.this.a[--this.pos];
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                while (this.pos < DoubleImmutableList.this.a.length) {
                    action.accept(DoubleImmutableList.this.a[this.pos++]);
                }
            }

            @Override
            public void add(double k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(double k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = DoubleImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos -= n;
                } else {
                    n = remaining;
                    this.pos = 0;
                }
                return n;
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = DoubleImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = DoubleImmutableList.this.a.length;
                }
                return n;
            }
        };
    }

    @Override
    public DoubleSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public DoubleList subList(int from, int to) {
        if (from == 0 && to == this.size()) {
            return this;
        }
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from == to) {
            return EMPTY;
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new ImmutableSubList(this, from, to);
    }

    public DoubleImmutableList clone() {
        return this;
    }

    public boolean equals(DoubleImmutableList l) {
        if (l == this) {
            return true;
        }
        if (this.a == l.a) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        double[] a1 = this.a;
        double[] a2 = l.a;
        return Arrays.equals(a1, a2);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof List)) {
            return false;
        }
        if (o instanceof DoubleImmutableList) {
            return this.equals((DoubleImmutableList)o);
        }
        if (o instanceof ImmutableSubList) {
            return ((ImmutableSubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(DoubleImmutableList l) {
        int i;
        if (this.a == l.a) {
            return 0;
        }
        int s1 = this.size();
        int s2 = l.size();
        double[] a1 = this.a;
        double[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            double e1 = a1[i];
            double e2 = a2[i];
            int r = Double.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(List<? extends Double> l) {
        if (l instanceof DoubleImmutableList) {
            return this.compareTo((DoubleImmutableList)l);
        }
        if (l instanceof ImmutableSubList) {
            ImmutableSubList other = (ImmutableSubList)l;
            return -other.compareTo(this);
        }
        return super.compareTo(l);
    }

    private final class Spliterator
    implements DoubleSpliterator {
        int pos;
        int max;

        public Spliterator() {
            this(0, doubleImmutableList.a.length);
        }

        private Spliterator(int pos, int max) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
        }

        @Override
        public int characteristics() {
            return 17744;
        }

        @Override
        public long estimateSize() {
            return this.max - this.pos;
        }

        @Override
        public boolean tryAdvance(DoubleConsumer action) {
            if (this.pos >= this.max) {
                return false;
            }
            action.accept(DoubleImmutableList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            while (this.pos < this.max) {
                action.accept(DoubleImmutableList.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.pos >= this.max) {
                return 0L;
            }
            int remaining = this.max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = this.max;
            return n;
        }

        @Override
        public DoubleSpliterator trySplit() {
            int myNewPos;
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            return new Spliterator(oldPos, retMax);
        }
    }

    private static final class ImmutableSubList
    extends DoubleLists.ImmutableListBase
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 7054639518438982401L;
        final DoubleImmutableList innerList;
        final int from;
        final int to;
        final transient double[] a;

        ImmutableSubList(DoubleImmutableList innerList, int from, int to) {
            this.innerList = innerList;
            this.from = from;
            this.to = to;
            this.a = innerList.a;
        }

        @Override
        public double getDouble(int index) {
            this.ensureRestrictedIndex(index);
            return this.a[index + this.from];
        }

        @Override
        public int indexOf(double k) {
            for (int i = this.from; i < this.to; ++i) {
                if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(double k) {
            int i = this.to;
            while (i-- != this.from) {
                if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public boolean isEmpty() {
            return this.to <= this.from;
        }

        @Override
        public void getElements(int fromSublistIndex, double[] a, int offset, int length) {
            DoubleArrays.ensureOffsetLength(a, offset, length);
            this.ensureRestrictedIndex(fromSublistIndex);
            if (this.from + length > this.to) {
                throw new IndexOutOfBoundsException("Final index " + (this.from + length) + " (startingIndex: " + this.from + " + length: " + length + ") is greater then list length " + this.size());
            }
            System.arraycopy(this.a, fromSublistIndex + this.from, a, offset, length);
        }

        @Override
        public void forEach(DoubleConsumer action) {
            for (int i = this.from; i < this.to; ++i) {
                action.accept(this.a[i]);
            }
        }

        @Override
        public double[] toDoubleArray() {
            return Arrays.copyOfRange(this.a, this.from, this.to);
        }

        @Override
        public double[] toArray(double[] a) {
            if (a == null || a.length < this.size()) {
                a = new double[this.size()];
            }
            System.arraycopy(this.a, this.from, a, 0, this.size());
            return a;
        }

        @Override
        public DoubleListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new DoubleListIterator(){
                int pos;
                {
                    this.pos = index;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < to;
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > from;
                }

                @Override
                public double nextDouble() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return a[this.pos++ + from];
                }

                @Override
                public double previousDouble() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    return a[--this.pos + from];
                }

                @Override
                public int nextIndex() {
                    return this.pos;
                }

                @Override
                public int previousIndex() {
                    return this.pos - 1;
                }

                @Override
                public void forEachRemaining(DoubleConsumer action) {
                    while (this.pos < to) {
                        action.accept(a[this.pos++ + from]);
                    }
                }

                @Override
                public void add(double k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(double k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int back(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos -= n;
                    } else {
                        n = remaining;
                        this.pos = 0;
                    }
                    return n;
                }

                @Override
                public int skip(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos += n;
                    } else {
                        n = remaining;
                        this.pos = to;
                    }
                    return n;
                }
            };
        }

        @Override
        public DoubleSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(double[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (this.a[pos++] == otherA[otherPos++]) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof List)) {
                return false;
            }
            if (o instanceof DoubleImmutableList) {
                DoubleImmutableList other = (DoubleImmutableList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)o;
                return this.contentsEquals(other.a, other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(double[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            int i = this.from;
            int j = otherAFrom;
            while (i < this.to && i < otherATo) {
                double e1 = this.a[i];
                double e2 = otherA[j];
                int r = Double.compare(e1, e2);
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(List<? extends Double> l) {
            if (l instanceof DoubleImmutableList) {
                DoubleImmutableList other = (DoubleImmutableList)l;
                return this.contentsCompareTo(other.a, 0, other.size());
            }
            if (l instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)l;
                return this.contentsCompareTo(other.a, other.from, other.to);
            }
            return super.compareTo(l);
        }

        private Object readResolve() throws ObjectStreamException {
            try {
                return this.innerList.subList(this.from, this.to);
            }
            catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                throw (InvalidObjectException)new InvalidObjectException(ex.getMessage()).initCause(ex);
            }
        }

        @Override
        public DoubleList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from == to) {
                return EMPTY;
            }
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ImmutableSubList(this.innerList, from + this.from, to + this.from);
        }

        private final class SubListSpliterator
        extends DoubleSpliterators.EarlyBindingSizeIndexBasedSpliterator {
            SubListSpliterator() {
                super(ImmutableSubList.this.from, ImmutableSubList.this.to);
            }

            private SubListSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final double get(int i) {
                return ImmutableSubList.this.a[i];
            }

            @Override
            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                if (this.pos >= this.maxPos) {
                    return false;
                }
                action.accept(ImmutableSubList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                int max = this.maxPos;
                while (this.pos < max) {
                    action.accept(ImmutableSubList.this.a[this.pos++]);
                }
            }

            @Override
            public int characteristics() {
                return 17744;
            }
        }
    }
}

