/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleArraySet;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public final class DoubleSets {
    static final int ARRAY_SET_CUTOFF = 4;
    public static final EmptySet EMPTY_SET = new EmptySet();
    static final DoubleSet UNMODIFIABLE_EMPTY_SET = DoubleSets.unmodifiable(new DoubleArraySet(DoubleArrays.EMPTY_ARRAY));

    private DoubleSets() {
    }

    public static DoubleSet emptySet() {
        return EMPTY_SET;
    }

    public static DoubleSet singleton(double element) {
        return new Singleton(element);
    }

    public static DoubleSet singleton(Double element) {
        return new Singleton(element);
    }

    public static DoubleSet synchronize(DoubleSet s) {
        return new SynchronizedSet(s);
    }

    public static DoubleSet synchronize(DoubleSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static DoubleSet unmodifiable(DoubleSet s) {
        return new UnmodifiableSet(s);
    }

    public static class EmptySet
    extends DoubleCollections.EmptyCollection
    implements DoubleSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(double ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Override
        @Deprecated
        public boolean rem(double k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }

    public static class Singleton
    extends AbstractDoubleSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final double element;

        protected Singleton(double element) {
            this.element = element;
        }

        @Override
        public boolean contains(double k) {
            return Double.doubleToLongBits(k) == Double.doubleToLongBits(this.element);
        }

        @Override
        public boolean remove(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoubleListIterator iterator() {
            return DoubleIterators.singleton(this.element);
        }

        @Override
        public DoubleSpliterator spliterator() {
            return DoubleSpliterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public double[] toDoubleArray() {
            return new double[]{this.element};
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Double> action) {
            action.accept((Double)this.element);
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
        public void forEach(DoubleConsumer action) {
            action.accept(this.element);
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

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedSet
    extends DoubleCollections.SynchronizedCollection
    implements DoubleSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(DoubleSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(DoubleSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Override
        @Deprecated
        public boolean rem(double k) {
            return super.rem(k);
        }
    }

    public static class UnmodifiableSet
    extends DoubleCollections.UnmodifiableCollection
    implements DoubleSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(DoubleSet s) {
            super(s);
        }

        @Override
        public boolean remove(double k) {
            throw new UnsupportedOperationException();
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
        @Deprecated
        public boolean rem(double k) {
            return super.rem(k);
        }
    }
}

