/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public final class DoubleCollections {
    private DoubleCollections() {
    }

    public static DoubleCollection synchronize(DoubleCollection c) {
        return new SynchronizedCollection(c);
    }

    public static DoubleCollection synchronize(DoubleCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static DoubleCollection unmodifiable(DoubleCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static DoubleCollection asCollection(DoubleIterable iterable) {
        if (iterable instanceof DoubleCollection) {
            return (DoubleCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    static class SynchronizedCollection
    implements DoubleCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(DoubleCollection c, Object sync) {
            this.collection = Objects.requireNonNull(c);
            this.sync = sync;
        }

        protected SynchronizedCollection(DoubleCollection c) {
            this.collection = Objects.requireNonNull(c);
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double[] toDoubleArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toDoubleArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object[] toArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray();
            }
        }

        @Override
        @Deprecated
        public double[] toDoubleArray(double[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double[] toArray(double[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(DoubleCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(DoubleCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(DoubleCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(DoubleCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean add(Double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean contains(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(k);
            }
        }

        @Override
        public DoubleIterator doubleIterator() {
            return this.collection.doubleIterator();
        }

        @Override
        public DoubleSpliterator doubleSpliterator() {
            return this.collection.doubleSpliterator();
        }

        @Override
        public DoubleStream doubleStream() {
            return this.collection.doubleStream();
        }

        @Override
        public DoubleStream doubleParallelStream() {
            return this.collection.doubleParallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        @Override
        public DoubleIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        public DoubleSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Double> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Double> parallelStream() {
            return this.collection.parallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(DoubleConsumer action) {
            Object object = this.sync;
            synchronized (object) {
                this.collection.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Double> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(DoublePredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.collection.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toString();
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
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    static class UnmodifiableCollection
    implements DoubleCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleCollection collection;

        protected UnmodifiableCollection(DoubleCollection c) {
            this.collection = Objects.requireNonNull(c);
        }

        @Override
        public boolean add(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return this.collection.size();
        }

        @Override
        public boolean isEmpty() {
            return this.collection.isEmpty();
        }

        @Override
        public boolean contains(double o) {
            return this.collection.contains(o);
        }

        @Override
        public DoubleIterator iterator() {
            return DoubleIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public DoubleSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Double> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Double> parallelStream() {
            return this.collection.parallelStream();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return this.collection.toArray();
        }

        @Override
        public void forEach(DoubleConsumer action) {
            this.collection.forEach(action);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
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
        public boolean removeIf(DoublePredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean contains(Object k) {
            return this.collection.contains(k);
        }

        @Override
        @Deprecated
        public boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double[] toDoubleArray() {
            return this.collection.toDoubleArray();
        }

        @Override
        @Deprecated
        public double[] toDoubleArray(double[] a) {
            return this.toArray(a);
        }

        @Override
        public double[] toArray(double[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(DoubleCollection c) {
            return this.collection.containsAll(c);
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
        public DoubleIterator doubleIterator() {
            return this.collection.doubleIterator();
        }

        @Override
        public DoubleSpliterator doubleSpliterator() {
            return this.collection.doubleSpliterator();
        }

        @Override
        public DoubleStream doubleStream() {
            return this.collection.doubleStream();
        }

        @Override
        public DoubleStream doubleParallelStream() {
            return this.collection.doubleParallelStream();
        }

        public String toString() {
            return this.collection.toString();
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }
    }

    public static class IterableCollection
    extends AbstractDoubleCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleIterable iterable;

        protected IterableCollection(DoubleIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            DoubleIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextDouble();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public DoubleIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public DoubleSpliterator spliterator() {
            return this.iterable.spliterator();
        }

        @Override
        public DoubleIterator doubleIterator() {
            return this.iterable.doubleIterator();
        }

        @Override
        public DoubleSpliterator doubleSpliterator() {
            return this.iterable.doubleSpliterator();
        }
    }

    static class SizeDecreasingSupplier<C extends DoubleCollection>
    implements Supplier<C> {
        static final int RECOMMENDED_MIN_SIZE = 8;
        final AtomicInteger suppliedCount = new AtomicInteger(0);
        final int expectedFinalSize;
        final IntFunction<C> builder;

        SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
            this.expectedFinalSize = expectedFinalSize;
            this.builder = builder;
        }

        @Override
        public C get() {
            int expectedNeededNextSize = 1 + (this.expectedFinalSize - 1) / this.suppliedCount.incrementAndGet();
            if (expectedNeededNextSize < 0) {
                expectedNeededNextSize = 8;
            }
            return (C)((DoubleCollection)this.builder.apply(expectedNeededNextSize));
        }
    }

    public static abstract class EmptyCollection
    extends AbstractDoubleCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(double k) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return DoubleIterators.EMPTY_ITERATOR;
        }

        @Override
        public DoubleSpliterator spliterator() {
            return DoubleSpliterators.EMPTY_SPLITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Collection)) {
                return false;
            }
            return ((Collection)o).isEmpty();
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Double> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public double[] toDoubleArray() {
            return DoubleArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public double[] toDoubleArray(double[] a) {
            return a;
        }

        @Override
        public void forEach(DoubleConsumer action) {
        }

        @Override
        public boolean containsAll(DoubleCollection c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }
    }
}

