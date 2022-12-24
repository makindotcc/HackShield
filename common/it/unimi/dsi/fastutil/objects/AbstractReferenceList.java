/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;

public abstract class AbstractReferenceList<K>
extends AbstractReferenceCollection<K>
implements ReferenceList<K>,
Stack<K> {
    protected AbstractReferenceList() {
    }

    protected void ensureIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
    }

    protected void ensureRestrictedIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
        }
    }

    @Override
    public void add(int index, K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(K k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public K remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public K set(int index, K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends K> c) {
        this.ensureIndex(index);
        Iterator<K> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends K> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public ObjectListIterator<K> iterator() {
        return this.listIterator();
    }

    @Override
    public ObjectListIterator<K> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ObjectListIterator<K> listIterator(int index) {
        this.ensureIndex(index);
        return new ObjectIterators.AbstractIndexBasedListIterator<K>(0, index){

            @Override
            protected final K get(int i) {
                return AbstractReferenceList.this.get(i);
            }

            @Override
            protected final void add(int i, K k) {
                AbstractReferenceList.this.add(i, k);
            }

            @Override
            protected final void set(int i, K k) {
                AbstractReferenceList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                AbstractReferenceList.this.remove(i);
            }

            @Override
            protected final int getMaxPos() {
                return AbstractReferenceList.this.size();
            }
        };
    }

    @Override
    public boolean contains(Object k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(Object k) {
        ListIterator i = this.listIterator();
        while (i.hasNext()) {
            Object e = i.next();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object k) {
        ListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            Object e = i.previous();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
        if (size > i) {
            while (i++ < size) {
                this.add((K)null);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public ReferenceList<K> subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new ReferenceRandomAccessSubList(this, from, to) : new ReferenceSubList(this, from, to);
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        if (this instanceof RandomAccess) {
            int max = this.size();
            for (int i = 0; i < max; ++i) {
                action.accept(this.get(i));
            }
        } else {
            ReferenceList.super.forEach(action);
        }
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        ListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.next();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, K[] a, int offset, int length) {
        this.ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0) {
                this.add(index++, a[offset++]);
            }
        } else {
            ListIterator iter = this.listIterator(index);
            while (length-- != 0) {
                iter.add(a[offset++]);
            }
        }
    }

    @Override
    public void addElements(int index, K[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, Object[] a, int offset, int length) {
        this.ensureIndex(from);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            int current = from;
            while (length-- != 0) {
                a[offset++] = this.get(current++);
            }
        } else {
            ListIterator i = this.listIterator(from);
            while (length-- != 0) {
                a[offset++] = i.next();
            }
        }
    }

    @Override
    public void setElements(int index, K[] a, int offset, int length) {
        this.ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            for (int i = 0; i < length; ++i) {
                this.set(i + index, a[i + offset]);
            }
        } else {
            ListIterator iter = this.listIterator(index);
            int i = 0;
            while (i < length) {
                iter.next();
                iter.set(a[offset + i++]);
            }
        }
    }

    @Override
    public void clear() {
        this.removeElements(0, this.size());
    }

    @Override
    public Object[] toArray() {
        int size = this.size();
        Object[] ret = new Object[size];
        this.getElements(0, ret, 0, size);
        return ret;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = this.size();
        if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        this.getElements(0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public int hashCode() {
        ObjectIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            Object k = i.next();
            h = 31 * h + System.identityHashCode(k);
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List l = (List)o;
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        ListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (i1.next() == i2.next()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void push(K o) {
        this.add(o);
    }

    @Override
    public K pop() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.remove(this.size() - 1);
    }

    @Override
    public K top() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return (K)this.get(this.size() - 1);
    }

    @Override
    public K peek(int i) {
        return (K)this.get(this.size() - 1 - i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object k = i.next();
            if (this == k) {
                s.append("(this list)");
                continue;
            }
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class ReferenceRandomAccessSubList<K>
    extends ReferenceSubList<K>
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public ReferenceRandomAccessSubList(ReferenceList<K> l, int from, int to) {
            super(l, from, to);
        }

        @Override
        public ReferenceList<K> subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ReferenceRandomAccessSubList<K>(this, from, to);
        }
    }

    public static class ReferenceSubList<K>
    extends AbstractReferenceList<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ReferenceList<K> l;
        protected final int from;
        protected int to;

        public ReferenceSubList(ReferenceList<K> l, int from, int to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size());
            assert (this.to <= this.l.size());
            assert (this.to >= this.from);
            return true;
        }

        @Override
        public boolean add(K k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, K k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends K> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public K get(int index) {
            this.ensureRestrictedIndex(index);
            return (K)this.l.get(this.from + index);
        }

        @Override
        public K remove(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return (K)this.l.remove(this.from + index);
        }

        @Override
        public K set(int index, K k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, Object[] a, int offset, int length) {
            this.ensureIndex(from);
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(int index, K[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public void setElements(int index, K[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.setElements(this.from + index, a, offset, length);
            assert (this.assertRange());
        }

        @Override
        public ObjectListIterator<K> listIterator(int index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator<K>(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public ReferenceList<K> subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ReferenceSubList<K>(this, from, to);
        }

        private final class RandomAccessIter
        extends ObjectIterators.AbstractIndexBasedListIterator<K> {
            RandomAccessIter(int pos) {
                super(0, pos);
            }

            @Override
            protected final K get(int i) {
                return ReferenceSubList.this.l.get(ReferenceSubList.this.from + i);
            }

            @Override
            protected final void add(int i, K k) {
                ReferenceSubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, K k) {
                ReferenceSubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                ReferenceSubList.this.remove(i);
            }

            @Override
            protected final int getMaxPos() {
                return ReferenceSubList.this.to - ReferenceSubList.this.from;
            }

            @Override
            public void add(K k) {
                super.add(k);
                assert (ReferenceSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (ReferenceSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements ObjectListIterator<K> {
            private ObjectListIterator<K> parent;

            ParentWrappingIter(ObjectListIterator<K> parent) {
                this.parent = parent;
            }

            @Override
            public int nextIndex() {
                return this.parent.nextIndex() - ReferenceSubList.this.from;
            }

            @Override
            public int previousIndex() {
                return this.parent.previousIndex() - ReferenceSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < ReferenceSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= ReferenceSubList.this.from;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.next();
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previous();
            }

            @Override
            public void add(K k) {
                this.parent.add(k);
            }

            @Override
            public void set(K k) {
                this.parent.set(k);
            }

            @Override
            public void remove() {
                this.parent.remove();
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int currentPos = this.parent.previousIndex();
                int parentNewPos = currentPos - n;
                if (parentNewPos < ReferenceSubList.this.from - 1) {
                    parentNewPos = ReferenceSubList.this.from - 1;
                }
                int toSkip = parentNewPos - currentPos;
                return this.parent.back(toSkip);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int currentPos = this.parent.nextIndex();
                int parentNewPos = currentPos + n;
                if (parentNewPos > ReferenceSubList.this.to) {
                    parentNewPos = ReferenceSubList.this.to;
                }
                int toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator<K>
    extends ObjectSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
        final ReferenceList<K> l;

        IndexBasedSpliterator(ReferenceList<K> l, int pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(ReferenceList<K> l, int pos, int maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final int getMaxPosFromBackingStore() {
            return this.l.size();
        }

        @Override
        protected final K get(int i) {
            return (K)this.l.get(i);
        }

        @Override
        protected final IndexBasedSpliterator<K> makeForSplit(int pos, int maxPos) {
            return new IndexBasedSpliterator<K>(this.l, pos, maxPos);
        }
    }
}

