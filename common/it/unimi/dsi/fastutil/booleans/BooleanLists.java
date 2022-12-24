/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanPredicate;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.booleans.BooleanUnaryOperator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class BooleanLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private BooleanLists() {
    }

    public static BooleanList shuffle(BooleanList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            boolean t = l.getBoolean(i);
            l.set(i, l.getBoolean(p));
            l.set(p, t);
        }
        return l;
    }

    public static BooleanList emptyList() {
        return EMPTY_LIST;
    }

    public static BooleanList singleton(boolean element) {
        return new Singleton(element);
    }

    public static BooleanList singleton(Object element) {
        return new Singleton((Boolean)element);
    }

    public static BooleanList synchronize(BooleanList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static BooleanList synchronize(BooleanList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static BooleanList unmodifiable(BooleanList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends BooleanCollections.EmptyCollection
    implements BooleanList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public boolean getBoolean(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean set(int index, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(boolean k) {
            return -1;
        }

        @Override
        public int lastIndexOf(boolean k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Boolean> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(BooleanUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean set(int index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean remove(int k) {
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
        public void sort(BooleanComparator comparator) {
        }

        @Override
        public void unstableSort(BooleanComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Boolean> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Boolean> comparator) {
        }

        @Override
        public BooleanListIterator listIterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanListIterator iterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanListIterator listIterator(int i) {
            if (i == 0) {
                return BooleanIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public BooleanList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, boolean[] a, int offset, int length) {
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
        public void addElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Boolean> o) {
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
    extends AbstractBooleanList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final boolean element;

        protected Singleton(boolean element) {
            this.element = element;
        }

        @Override
        public boolean getBoolean(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(boolean k) {
            return k == this.element;
        }

        @Override
        public int indexOf(boolean k) {
            return k == this.element ? 0 : -1;
        }

        @Override
        public boolean[] toBooleanArray() {
            return new boolean[]{this.element};
        }

        @Override
        public BooleanListIterator listIterator() {
            return BooleanIterators.singleton(this.element);
        }

        @Override
        public BooleanListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanSpliterator spliterator() {
            return BooleanSpliterators.singleton(this.element);
        }

        @Override
        public BooleanListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            BooleanListIterator l = this.listIterator();
            if (i == 1) {
                l.nextBoolean();
            }
            return l;
        }

        @Override
        public BooleanList subList(int from, int to) {
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
        public void forEach(Consumer<? super Boolean> action) {
            action.accept((Boolean)this.element);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c) {
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
        public boolean removeIf(Predicate<? super Boolean> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Boolean> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(BooleanUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(BooleanConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public void sort(BooleanComparator comparator) {
        }

        @Override
        public void unstableSort(BooleanComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Boolean> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Boolean> comparator) {
        }

        @Override
        public void getElements(int from, boolean[] a, int offset, int length) {
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
        public void addElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a, int offset, int length) {
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

        protected SynchronizedRandomAccessList(BooleanList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(BooleanList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BooleanList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends BooleanCollections.SynchronizedCollection
    implements BooleanList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList list;

        protected SynchronizedList(BooleanList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(BooleanList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getBoolean(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getBoolean(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean set(int i, boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, boolean k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeBoolean(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeBoolean(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(BooleanPredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BooleanUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, boolean[] a, int offset, int length) {
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
        public void addElements(int index, boolean[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, boolean[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(boolean[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, boolean[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, boolean[] a, int offset, int length) {
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
        public BooleanListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BooleanList subList(int from, int to) {
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
        public int compareTo(List<? extends Boolean> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, BooleanCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, BooleanList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanList l) {
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
        public Boolean get(int i) {
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
        public void add(int i, Boolean k) {
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
        public Boolean set(int index, Boolean k) {
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
        public Boolean remove(int i) {
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
        public void sort(BooleanComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(BooleanComparator comparator) {
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
        public void sort(Comparator<? super Boolean> comparator) {
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
        public void unstableSort(Comparator<? super Boolean> comparator) {
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

        protected UnmodifiableRandomAccessList(BooleanList l) {
            super(l);
        }

        @Override
        public BooleanList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends BooleanCollections.UnmodifiableCollection
    implements BooleanList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList list;

        protected UnmodifiableList(BooleanList l) {
            super(l);
            this.list = l;
        }

        @Override
        public boolean getBoolean(int i) {
            return this.list.getBoolean(i);
        }

        @Override
        public boolean set(int i, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(boolean k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(boolean k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Boolean> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, boolean[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public BooleanListIterator listIterator() {
            return BooleanIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public BooleanListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanListIterator listIterator(int i) {
            return BooleanIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public BooleanList subList(int from, int to) {
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
        public int compareTo(List<? extends Boolean> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, BooleanList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean set(int index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean remove(int i) {
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
        public void sort(BooleanComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(BooleanComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Boolean> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Boolean> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractBooleanList
    implements BooleanList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeBoolean(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(boolean k) {
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
        public final boolean removeIf(Predicate<? super Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(BooleanPredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Boolean> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Boolean remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Boolean set(int index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, BooleanList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean set(int index, boolean k) {
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
        public final void addElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, boolean[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(BooleanComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(BooleanComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Boolean> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Boolean> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

