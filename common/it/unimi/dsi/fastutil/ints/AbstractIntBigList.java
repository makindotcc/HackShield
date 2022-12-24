/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntBigListIterators;
import it.unimi.dsi.fastutil.ints.IntBigSpliterators;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntConsumer;

public abstract class AbstractIntBigList
extends AbstractIntCollection
implements IntBigList,
IntStack {
    protected AbstractIntBigList() {
    }

    protected void ensureIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
    }

    protected void ensureRestrictedIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
        }
    }

    @Override
    public void add(long index, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(int k) {
        this.add(this.size64(), k);
        return true;
    }

    @Override
    public int removeInt(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int set(long index, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long index, Collection<? extends Integer> c) {
        this.ensureIndex(index);
        Iterator<? extends Integer> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public IntBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public IntBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public IntBigListIterator listIterator(long index) {
        this.ensureIndex(index);
        return new IntBigListIterators.AbstractIndexBasedBigListIterator(0L, index){

            @Override
            protected final int get(long i) {
                return AbstractIntBigList.this.getInt(i);
            }

            @Override
            protected final void add(long i, int k) {
                AbstractIntBigList.this.add(i, k);
            }

            @Override
            protected final void set(long i, int k) {
                AbstractIntBigList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                AbstractIntBigList.this.removeInt(i);
            }

            @Override
            protected final long getMaxPos() {
                return AbstractIntBigList.this.size64();
            }
        };
    }

    @Override
    public boolean contains(int k) {
        return this.indexOf(k) >= 0L;
    }

    @Override
    public long indexOf(int k) {
        IntBigListIterator i = this.listIterator();
        while (i.hasNext()) {
            int e = i.nextInt();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(int k) {
        IntBigListIterator i = this.listIterator(this.size64());
        while (i.hasPrevious()) {
            int e = i.previousInt();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i = this.size64();
        if (size > i) {
            while (i++ < size) {
                this.add(0);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public IntBigList subList(long from, long to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new IntRandomAccessSubList(this, from, to) : new IntSubList(this, from, to);
    }

    @Override
    public void forEach(IntConsumer action) {
        if (this instanceof RandomAccess) {
            long max = this.size64();
            for (long i = 0L; i < max; ++i) {
                action.accept(this.getInt(i));
            }
        } else {
            super.forEach(action);
        }
    }

    @Override
    public void removeElements(long from, long to) {
        this.ensureIndex(to);
        IntBigListIterator i = this.listIterator(from);
        long n = to - from;
        if (n < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0L) {
            i.nextInt();
            i.remove();
        }
    }

    @Override
    public void addElements(long index, int[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0L) {
                this.add(index++, BigArrays.get(a, offset++));
            }
        } else {
            IntBigListIterator iter = this.listIterator(index);
            while (length-- != 0L) {
                iter.add(BigArrays.get(a, offset++));
            }
        }
    }

    @Override
    public void addElements(long index, int[][] a) {
        this.addElements(index, a, 0L, BigArrays.length(a));
    }

    @Override
    public void getElements(long from, int[][] a, long offset, long length) {
        this.ensureIndex(from);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        if (this instanceof RandomAccess) {
            long current = from;
            while (length-- != 0L) {
                BigArrays.set(a, offset++, this.getInt(current++));
            }
        } else {
            IntBigListIterator i = this.listIterator(from);
            while (length-- != 0L) {
                BigArrays.set(a, offset++, i.nextInt());
            }
        }
    }

    @Override
    public void setElements(long index, int[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
        }
        if (this instanceof RandomAccess) {
            for (long i = 0L; i < length; ++i) {
                this.set(i + index, BigArrays.get(a, i + offset));
            }
        } else {
            IntBigListIterator iter = this.listIterator(index);
            long i = 0L;
            while (i < length) {
                iter.nextInt();
                iter.set(BigArrays.get(a, offset + i++));
            }
        }
    }

    @Override
    public void clear() {
        this.removeElements(0L, this.size64());
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    @Override
    public int hashCode() {
        IntBigListIterator i = this.iterator();
        int h = 1;
        long s = this.size64();
        while (s-- != 0L) {
            int k = i.nextInt();
            h = 31 * h + k;
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BigList)) {
            return false;
        }
        BigList l = (BigList)o;
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        if (l instanceof IntBigList) {
            IntBigListIterator i1 = this.listIterator();
            IntBigListIterator i2 = ((IntBigList)l).listIterator();
            while (s-- != 0L) {
                if (i1.nextInt() == i2.nextInt()) continue;
                return false;
            }
            return true;
        }
        IntBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l.listIterator();
        while (s-- != 0L) {
            if (Objects.equals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Integer> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof IntBigList) {
            IntBigListIterator i1 = this.listIterator();
            IntBigListIterator i2 = ((IntBigList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                int e2;
                int e1 = i1.nextInt();
                int r = Integer.compare(e1, e2 = i2.nextInt());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        IntBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Integer> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(int o) {
        this.add(o);
    }

    @Override
    public int popInt() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeInt(this.size64() - 1L);
    }

    @Override
    public int topInt() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getInt(this.size64() - 1L);
    }

    @Override
    public int peekInt(int i) {
        return this.getInt(this.size64() - 1L - (long)i);
    }

    @Override
    public boolean rem(int k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeInt(index);
        return true;
    }

    @Override
    public boolean addAll(long index, IntCollection c) {
        return this.addAll(index, (Collection<? extends Integer>)c);
    }

    @Override
    public boolean addAll(IntCollection c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    @Deprecated
    public void add(long index, Integer ok) {
        this.add(index, (int)ok);
    }

    @Override
    @Deprecated
    public Integer set(long index, Integer ok) {
        return this.set(index, (int)ok);
    }

    @Override
    @Deprecated
    public Integer get(long index) {
        return this.getInt(index);
    }

    @Override
    @Deprecated
    public long indexOf(Object ok) {
        return this.indexOf((Integer)ok);
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok) {
        return this.lastIndexOf((Integer)ok);
    }

    @Override
    @Deprecated
    public Integer remove(long index) {
        return this.removeInt(index);
    }

    @Override
    @Deprecated
    public void push(Integer o) {
        this.push((int)o);
    }

    @Override
    @Deprecated
    public Integer pop() {
        return this.popInt();
    }

    @Override
    @Deprecated
    public Integer top() {
        return this.topInt();
    }

    @Override
    @Deprecated
    public Integer peek(int i) {
        return this.peekInt(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        IntBigListIterator i = this.iterator();
        long n = this.size64();
        boolean first = true;
        s.append("[");
        while (n-- != 0L) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            int k = i.nextInt();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class IntRandomAccessSubList
    extends IntSubList
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public IntRandomAccessSubList(IntBigList l, long from, long to) {
            super(l, from, to);
        }

        @Override
        public IntBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new IntRandomAccessSubList(this, from, to);
        }
    }

    public static class IntSubList
    extends AbstractIntBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntBigList l;
        protected final long from;
        protected long to;

        public IntSubList(IntBigList l, long from, long to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size64());
            assert (this.to <= this.l.size64());
            assert (this.to >= this.from);
            return true;
        }

        @Override
        public boolean add(int k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(long index, int k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Integer> c) {
            this.ensureIndex(index);
            this.to += (long)c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public int getInt(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getInt(this.from + index);
        }

        @Override
        public int removeInt(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeInt(this.from + index);
        }

        @Override
        public int set(long index, int k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, int[][] a, long offset, long length) {
            this.ensureIndex(from);
            if (from + length > this.size64()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(long index, int[][] a, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public IntBigListIterator listIterator(long index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public IntSpliterator spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public IntBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new IntSubList(this, from, to);
        }

        @Override
        public boolean rem(int k) {
            long index = this.indexOf(k);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeInt(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(long index, IntCollection c) {
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(long index, IntBigList l) {
            return super.addAll(index, l);
        }

        private final class RandomAccessIter
        extends IntBigListIterators.AbstractIndexBasedBigListIterator {
            RandomAccessIter(long pos) {
                super(0L, pos);
            }

            @Override
            protected final int get(long i) {
                return IntSubList.this.l.getInt(IntSubList.this.from + i);
            }

            @Override
            protected final void add(long i, int k) {
                IntSubList.this.add(i, k);
            }

            @Override
            protected final void set(long i, int k) {
                IntSubList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                IntSubList.this.removeInt(i);
            }

            @Override
            protected final long getMaxPos() {
                return IntSubList.this.to - IntSubList.this.from;
            }

            @Override
            public void add(int k) {
                super.add(k);
                assert (IntSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (IntSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements IntBigListIterator {
            private IntBigListIterator parent;

            ParentWrappingIter(IntBigListIterator parent) {
                this.parent = parent;
            }

            @Override
            public long nextIndex() {
                return this.parent.nextIndex() - IntSubList.this.from;
            }

            @Override
            public long previousIndex() {
                return this.parent.previousIndex() - IntSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < IntSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= IntSubList.this.from;
            }

            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.nextInt();
            }

            @Override
            public int previousInt() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previousInt();
            }

            @Override
            public void add(int k) {
                this.parent.add(k);
            }

            @Override
            public void set(int k) {
                this.parent.set(k);
            }

            @Override
            public void remove() {
                this.parent.remove();
            }

            @Override
            public long back(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long currentPos = this.parent.previousIndex();
                long parentNewPos = currentPos - n;
                if (parentNewPos < IntSubList.this.from - 1L) {
                    parentNewPos = IntSubList.this.from - 1L;
                }
                long toSkip = parentNewPos - currentPos;
                return this.parent.back(toSkip);
            }

            @Override
            public long skip(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long currentPos = this.parent.nextIndex();
                long parentNewPos = currentPos + n;
                if (parentNewPos > IntSubList.this.to) {
                    parentNewPos = IntSubList.this.to;
                }
                long toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator
    extends IntBigSpliterators.LateBindingSizeIndexBasedSpliterator {
        final IntBigList l;

        IndexBasedSpliterator(IntBigList l, long pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(IntBigList l, long pos, long maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final long getMaxPosFromBackingStore() {
            return this.l.size64();
        }

        @Override
        protected final int get(long i) {
            return this.l.getInt(i);
        }

        @Override
        protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
            return new IndexBasedSpliterator(this.l, pos, maxPos);
        }
    }
}

