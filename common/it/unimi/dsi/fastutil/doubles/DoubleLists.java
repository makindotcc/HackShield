/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class DoubleLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private DoubleLists() {
    }

    public static DoubleList shuffle(DoubleList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            double t = l.getDouble(i);
            l.set(i, l.getDouble(p));
            l.set(p, t);
        }
        return l;
    }

    public static DoubleList emptyList() {
        return EMPTY_LIST;
    }

    public static DoubleList singleton(double element) {
        return new Singleton(element);
    }

    public static DoubleList singleton(Object element) {
        return new Singleton((Double)element);
    }

    public static DoubleList synchronize(DoubleList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static DoubleList synchronize(DoubleList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static DoubleList unmodifiable(DoubleList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends DoubleCollections.EmptyCollection
    implements DoubleList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public double getDouble(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double removeDouble(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double set(int index, double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(double k) {
            return -1;
        }

        @Override
        public int lastIndexOf(double k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Double> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(DoubleUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double set(int index, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double remove(int k) {
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
        public void sort(DoubleComparator comparator) {
        }

        @Override
        public void unstableSort(DoubleComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Double> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Double> comparator) {
        }

        @Override
        public DoubleListIterator listIterator() {
            return DoubleIterators.EMPTY_ITERATOR;
        }

        @Override
        public DoubleListIterator iterator() {
            return DoubleIterators.EMPTY_ITERATOR;
        }

        @Override
        public DoubleListIterator listIterator(int i) {
            if (i == 0) {
                return DoubleIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public DoubleList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, double[] a, int offset, int length) {
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
        public void addElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Double> o) {
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
    extends AbstractDoubleList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final double element;

        protected Singleton(double element) {
            this.element = element;
        }

        @Override
        public double getDouble(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double removeDouble(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(double k) {
            return Double.doubleToLongBits(k) == Double.doubleToLongBits(this.element);
        }

        @Override
        public int indexOf(double k) {
            return Double.doubleToLongBits(k) == Double.doubleToLongBits(this.element) ? 0 : -1;
        }

        @Override
        public double[] toDoubleArray() {
            return new double[]{this.element};
        }

        @Override
        public DoubleListIterator listIterator() {
            return DoubleIterators.singleton(this.element);
        }

        @Override
        public DoubleListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public DoubleSpliterator spliterator() {
            return DoubleSpliterators.singleton(this.element);
        }

        @Override
        public DoubleListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            DoubleListIterator l = this.listIterator();
            if (i == 1) {
                l.nextDouble();
            }
            return l;
        }

        @Override
        public DoubleList subList(int from, int to) {
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
        public void forEach(Consumer<? super Double> action) {
            action.accept((Double)this.element);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Double> c) {
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
        public boolean removeIf(Predicate<? super Double> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Double> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(DoubleUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(DoubleConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(DoublePredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public void sort(DoubleComparator comparator) {
        }

        @Override
        public void unstableSort(DoubleComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Double> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Double> comparator) {
        }

        @Override
        public void getElements(int from, double[] a, int offset, int length) {
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
        public void addElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a, int offset, int length) {
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

        protected SynchronizedRandomAccessList(DoubleList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(DoubleList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DoubleList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends DoubleCollections.SynchronizedCollection
    implements DoubleList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleList list;

        protected SynchronizedList(DoubleList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(DoubleList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double getDouble(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getDouble(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double set(int i, double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, double k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double removeDouble(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeDouble(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(DoublePredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(DoubleUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Double> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, double[] a, int offset, int length) {
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
        public void addElements(int index, double[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, double[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(double[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, double[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, double[] a, int offset, int length) {
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
        public DoubleListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public DoubleListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public DoubleListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DoubleList subList(int from, int to) {
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
        public int compareTo(List<? extends Double> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, DoubleCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, DoubleList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(DoubleList l) {
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
        public Double get(int i) {
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
        public void add(int i, Double k) {
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
        public Double set(int index, Double k) {
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
        public Double remove(int i) {
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
        public void sort(DoubleComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(DoubleComparator comparator) {
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
        public void sort(Comparator<? super Double> comparator) {
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
        public void unstableSort(Comparator<? super Double> comparator) {
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

        protected UnmodifiableRandomAccessList(DoubleList l) {
            super(l);
        }

        @Override
        public DoubleList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends DoubleCollections.UnmodifiableCollection
    implements DoubleList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleList list;

        protected UnmodifiableList(DoubleList l) {
            super(l);
            this.list = l;
        }

        @Override
        public double getDouble(int i) {
            return this.list.getDouble(i);
        }

        @Override
        public double set(int i, double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double removeDouble(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(double k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(double k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Double> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, double[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public DoubleListIterator listIterator() {
            return DoubleIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public DoubleListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public DoubleListIterator listIterator(int i) {
            return DoubleIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public DoubleList subList(int from, int to) {
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
        public int compareTo(List<? extends Double> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(DoubleList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, DoubleList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(DoubleUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double set(int index, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Double remove(int i) {
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
        public void sort(DoubleComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(DoubleComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Double> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Double> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractDoubleList
    implements DoubleList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final double removeDouble(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(double k) {
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
        public final boolean removeIf(Predicate<? super Double> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(DoublePredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Double> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(DoubleUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Double remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Double set(int index, Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, DoubleList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(DoubleCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final double set(int index, double k) {
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
        public final void addElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, double[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(DoubleComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(DoubleComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Double> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Double> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

