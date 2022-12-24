/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteList;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.BytePredicate;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.bytes.ByteUnaryOperator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class ByteLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private ByteLists() {
    }

    public static ByteList shuffle(ByteList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            byte t = l.getByte(i);
            l.set(i, l.getByte(p));
            l.set(p, t);
        }
        return l;
    }

    public static ByteList emptyList() {
        return EMPTY_LIST;
    }

    public static ByteList singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteList singleton(Object element) {
        return new Singleton((Byte)element);
    }

    public static ByteList synchronize(ByteList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static ByteList synchronize(ByteList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static ByteList unmodifiable(ByteList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends ByteCollections.EmptyCollection
    implements ByteList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public byte getByte(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte set(int index, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(byte k) {
            return -1;
        }

        @Override
        public int lastIndexOf(byte k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Byte> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(ByteUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte set(int index, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object k) {
            return -1;
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object k) {
            return -1;
        }

        @Override
        public void sort(ByteComparator comparator) {
        }

        @Override
        public void unstableSort(ByteComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Byte> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Byte> comparator) {
        }

        @Override
        public ByteListIterator listIterator() {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteListIterator iterator() {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteListIterator listIterator(int i) {
            if (i == 0) {
                return ByteIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public ByteList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, byte[] a, int offset, int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Byte> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return EMPTY_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof List && ((List)o).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }

        private Object readResolve() {
            return EMPTY_LIST;
        }
    }

    public static class Singleton
    extends AbstractByteList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final byte element;

        protected Singleton(byte element) {
            this.element = element;
        }

        @Override
        public byte getByte(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(byte k) {
            return k == this.element;
        }

        @Override
        public int indexOf(byte k) {
            return k == this.element ? 0 : -1;
        }

        @Override
        public byte[] toByteArray() {
            return new byte[]{this.element};
        }

        @Override
        public ByteListIterator listIterator() {
            return ByteIterators.singleton(this.element);
        }

        @Override
        public ByteListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteSpliterator spliterator() {
            return ByteSpliterators.singleton(this.element);
        }

        @Override
        public ByteListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            ByteListIterator l = this.listIterator();
            if (i == 1) {
                l.nextByte();
            }
            return l;
        }

        @Override
        public ByteList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return EMPTY_LIST;
            }
            return this;
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Byte> action) {
            action.accept((Byte)this.element);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean removeIf(Predicate<? super Byte> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Byte> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(ByteUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(ByteConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(BytePredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntIterator intIterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntSpliterator intSpliterator() {
            return IntSpliterators.singleton(this.element);
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public void sort(ByteComparator comparator) {
        }

        @Override
        public void unstableSort(ByteComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Byte> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Byte> comparator) {
        }

        @Override
        public void getElements(int from, byte[] a, int offset, int length) {
            if (offset < 0) {
                throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
            }
            if (offset + length > a.length) {
                throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
            }
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
            }
            if (length <= 0) {
                return;
            }
            a[offset] = this.element;
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedRandomAccessList
    extends SynchronizedList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected SynchronizedRandomAccessList(ByteList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(ByteList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends ByteCollections.SynchronizedCollection
    implements ByteList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteList list;

        protected SynchronizedList(ByteList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(ByteList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getByte(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getByte(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte set(int i, byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, byte k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte removeByte(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeByte(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(BytePredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(ByteUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Byte> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, byte[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, byte[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, byte[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(byte[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, byte[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, byte[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(int size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        @Override
        public ByteListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public ByteListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedList(this.list.subList(from, to), this.sync);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.collection.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(List<? extends Byte> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, ByteCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, ByteList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ByteList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Byte get(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void add(int i, Byte k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Byte set(int index, Byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Byte remove(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int indexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sort(ByteComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(ByteComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void sort(Comparator<? super Byte> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Byte> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(ByteList l) {
            super(l);
        }

        @Override
        public ByteList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends ByteCollections.UnmodifiableCollection
    implements ByteList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteList list;

        protected UnmodifiableList(ByteList l) {
            super(l);
            this.list = l;
        }

        @Override
        public byte getByte(int i) {
            return this.list.getByte(i);
        }

        @Override
        public byte set(int i, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(byte k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(byte k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Byte> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, byte[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public ByteListIterator listIterator() {
            return ByteIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public ByteListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteListIterator listIterator(int i) {
            return ByteIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public ByteList subList(int from, int to) {
            return new UnmodifiableList(this.list.subList(from, to));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public int compareTo(List<? extends Byte> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, ByteList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte set(int index, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte remove(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }

        @Override
        public void sort(ByteComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(ByteComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Byte> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Byte> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractByteList
    implements ByteList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final byte removeByte(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(Predicate<? super Byte> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(BytePredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Byte> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Byte remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Byte set(int index, Byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, ByteList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final byte set(int index, byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void addElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, byte[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(ByteComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(ByteComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Byte> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Byte> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

