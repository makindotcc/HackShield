/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongBigListIterators;
import it.unimi.dsi.fastutil.longs.LongBigSpliterators;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.LongConsumer;

public abstract class AbstractLongBigList
extends AbstractLongCollection
implements LongBigList,
LongStack {
    protected AbstractLongBigList() {
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
    public void add(long index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(long k) {
        this.add(this.size64(), k);
        return true;
    }

    @Override
    public long removeLong(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long set(long index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long index, Collection<? extends Long> c) {
        this.ensureIndex(index);
        Iterator<? extends Long> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public LongBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public LongBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public LongBigListIterator listIterator(long index) {
        this.ensureIndex(index);
        return new LongBigListIterators.AbstractIndexBasedBigListIterator(0L, index){

            @Override
            protected final long get(long i) {
                return AbstractLongBigList.this.getLong(i);
            }

            @Override
            protected final void add(long i, long k) {
                AbstractLongBigList.this.add(i, k);
            }

            @Override
            protected final void set(long i, long k) {
                AbstractLongBigList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                AbstractLongBigList.this.removeLong(i);
            }

            @Override
            protected final long getMaxPos() {
                return AbstractLongBigList.this.size64();
            }
        };
    }

    @Override
    public boolean contains(long k) {
        return this.indexOf(k) >= 0L;
    }

    @Override
    public long indexOf(long k) {
        LongBigListIterator i = this.listIterator();
        while (i.hasNext()) {
            long e = i.nextLong();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(long k) {
        LongBigListIterator i = this.listIterator(this.size64());
        while (i.hasPrevious()) {
            long e = i.previousLong();
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
                this.add(0L);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public LongBigList subList(long from, long to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new LongRandomAccessSubList(this, from, to) : new LongSubList(this, from, to);
    }

    @Override
    public void forEach(LongConsumer action) {
        if (this instanceof RandomAccess) {
            long max = this.size64();
            for (long i = 0L; i < max; ++i) {
                action.accept(this.getLong(i));
            }
        } else {
            super.forEach(action);
        }
    }

    @Override
    public void removeElements(long from, long to) {
        this.ensureIndex(to);
        LongBigListIterator i = this.listIterator(from);
        long n = to - from;
        if (n < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0L) {
            i.nextLong();
            i.remove();
        }
    }

    @Override
    public void addElements(long index, long[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0L) {
                this.add(index++, BigArrays.get(a, offset++));
            }
        } else {
            LongBigListIterator iter = this.listIterator(index);
            while (length-- != 0L) {
                iter.add(BigArrays.get(a, offset++));
            }
        }
    }

    @Override
    public void addElements(long index, long[][] a) {
        this.addElements(index, a, 0L, BigArrays.length(a));
    }

    @Override
    public void getElements(long from, long[][] a, long offset, long length) {
        this.ensureIndex(from);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        if (this instanceof RandomAccess) {
            long current = from;
            while (length-- != 0L) {
                BigArrays.set(a, offset++, this.getLong(current++));
            }
        } else {
            LongBigListIterator i = this.listIterator(from);
            while (length-- != 0L) {
                BigArrays.set(a, offset++, i.nextLong());
            }
        }
    }

    @Override
    public void setElements(long index, long[][] a, long offset, long length) {
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
            LongBigListIterator iter = this.listIterator(index);
            long i = 0L;
            while (i < length) {
                iter.nextLong();
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
        LongBigListIterator i = this.iterator();
        int h = 1;
        long s = this.size64();
        while (s-- != 0L) {
            long k = i.nextLong();
            h = 31 * h + HashCommon.long2int(k);
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
        if (l instanceof LongBigList) {
            LongBigListIterator i1 = this.listIterator();
            LongBigListIterator i2 = ((LongBigList)l).listIterator();
            while (s-- != 0L) {
                if (i1.nextLong() == i2.nextLong()) continue;
                return false;
            }
            return true;
        }
        LongBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l.listIterator();
        while (s-- != 0L) {
            if (Objects.equals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Long> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof LongBigList) {
            LongBigListIterator i1 = this.listIterator();
            LongBigListIterator i2 = ((LongBigList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                long e2;
                long e1 = i1.nextLong();
                int r = Long.compare(e1, e2 = i2.nextLong());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        LongBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Long> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(long o) {
        this.add(o);
    }

    @Override
    public long popLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeLong(this.size64() - 1L);
    }

    @Override
    public long topLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getLong(this.size64() - 1L);
    }

    @Override
    public long peekLong(int i) {
        return this.getLong(this.size64() - 1L - (long)i);
    }

    @Override
    public boolean rem(long k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeLong(index);
        return true;
    }

    @Override
    public boolean addAll(long index, LongCollection c) {
        return this.addAll(index, (Collection<? extends Long>)c);
    }

    @Override
    public boolean addAll(LongCollection c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    @Deprecated
    public void add(long index, Long ok) {
        this.add(index, (long)ok);
    }

    @Override
    @Deprecated
    public Long set(long index, Long ok) {
        return this.set(index, (long)ok);
    }

    @Override
    @Deprecated
    public Long get(long index) {
        return this.getLong(index);
    }

    @Override
    @Deprecated
    public long indexOf(Object ok) {
        return this.indexOf((Long)ok);
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok) {
        return this.lastIndexOf((Long)ok);
    }

    @Override
    @Deprecated
    public Long remove(long index) {
        return this.removeLong(index);
    }

    @Override
    @Deprecated
    public void push(Long o) {
        this.push((long)o);
    }

    @Override
    @Deprecated
    public Long pop() {
        return this.popLong();
    }

    @Override
    @Deprecated
    public Long top() {
        return this.topLong();
    }

    @Override
    @Deprecated
    public Long peek(int i) {
        return this.peekLong(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongBigListIterator i = this.iterator();
        long n = this.size64();
        boolean first = true;
        s.append("[");
        while (n-- != 0L) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class LongRandomAccessSubList
    extends LongSubList
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public LongRandomAccessSubList(LongBigList l, long from, long to) {
            super(l, from, to);
        }

        @Override
        public LongBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new LongRandomAccessSubList(this, from, to);
        }
    }

    public static class LongSubList
    extends AbstractLongBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongBigList l;
        protected final long from;
        protected long to;

        public LongSubList(LongBigList l, long from, long to) {
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
        public boolean add(long k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(long index, long k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Long> c) {
            this.ensureIndex(index);
            this.to += (long)c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public long getLong(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getLong(this.from + index);
        }

        @Override
        public long removeLong(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeLong(this.from + index);
        }

        @Override
        public long set(long index, long k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, long[][] a, long offset, long length) {
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
        public void addElements(long index, long[][] a, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public LongBigListIterator listIterator(long index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public LongSpliterator spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public LongBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new LongSubList(this, from, to);
        }

        @Override
        public boolean rem(long k) {
            long index = this.indexOf(k);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeLong(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(long index, LongCollection c) {
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(long index, LongBigList l) {
            return super.addAll(index, l);
        }

        private final class RandomAccessIter
        extends LongBigListIterators.AbstractIndexBasedBigListIterator {
            RandomAccessIter(long pos) {
                super(0L, pos);
            }

            @Override
            protected final long get(long i) {
                return LongSubList.this.l.getLong(LongSubList.this.from + i);
            }

            @Override
            protected final void add(long i, long k) {
                LongSubList.this.add(i, k);
            }

            @Override
            protected final void set(long i, long k) {
                LongSubList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                LongSubList.this.removeLong(i);
            }

            @Override
            protected final long getMaxPos() {
                return LongSubList.this.to - LongSubList.this.from;
            }

            @Override
            public void add(long k) {
                super.add(k);
                assert (LongSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (LongSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements LongBigListIterator {
            private LongBigListIterator parent;

            ParentWrappingIter(LongBigListIterator parent) {
                this.parent = parent;
            }

            @Override
            public long nextIndex() {
                return this.parent.nextIndex() - LongSubList.this.from;
            }

            @Override
            public long previousIndex() {
                return this.parent.previousIndex() - LongSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < LongSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= LongSubList.this.from;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.nextLong();
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previousLong();
            }

            @Override
            public void add(long k) {
                this.parent.add(k);
            }

            @Override
            public void set(long k) {
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
                if (parentNewPos < LongSubList.this.from - 1L) {
                    parentNewPos = LongSubList.this.from - 1L;
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
                if (parentNewPos > LongSubList.this.to) {
                    parentNewPos = LongSubList.this.to;
                }
                long toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator
    extends LongBigSpliterators.LateBindingSizeIndexBasedSpliterator {
        final LongBigList l;

        IndexBasedSpliterator(LongBigList l, long pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(LongBigList l, long pos, long maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final long getMaxPosFromBackingStore() {
            return this.l.size64();
        }

        @Override
        protected final long get(long i) {
            return this.l.getLong(i);
        }

        @Override
        protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
            return new IndexBasedSpliterator(this.l, pos, maxPos);
        }
    }
}

