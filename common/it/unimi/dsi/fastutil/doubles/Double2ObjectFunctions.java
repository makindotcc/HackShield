/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleFunction;

public final class Double2ObjectFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Double2ObjectFunctions() {
    }

    public static <V> Double2ObjectFunction<V> singleton(double key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Double2ObjectFunction<V> singleton(Double key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Double2ObjectFunction<V> synchronize(Double2ObjectFunction<V> f) {
        return new SynchronizedFunction<V>(f);
    }

    public static <V> Double2ObjectFunction<V> synchronize(Double2ObjectFunction<V> f, Object sync) {
        return new SynchronizedFunction<V>(f, sync);
    }

    public static <V> Double2ObjectFunction<V> unmodifiable(Double2ObjectFunction<? extends V> f) {
        return new UnmodifiableFunction<V>(f);
    }

    public static <V> Double2ObjectFunction<V> primitive(java.util.function.Function<? super Double, ? extends V> f) {
        Objects.requireNonNull(f);
        if (f instanceof Double2ObjectFunction) {
            return (Double2ObjectFunction)f;
        }
        if (f instanceof DoubleFunction) {
            return ((DoubleFunction)((Object)f))::apply;
        }
        return new PrimitiveFunction<V>(f);
    }

    public static class Singleton<V>
    extends AbstractDouble2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final double key;
        protected final V value;

        protected Singleton(double key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(double k) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
        }

        @Override
        public V get(double k) {
            return (V)(Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue);
        }

        @Override
        public V getOrDefault(double k, V defaultValue) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : defaultValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedFunction<V>
    implements Double2ObjectFunction<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ObjectFunction<V> function;
        protected final Object sync;

        protected SynchronizedFunction(Double2ObjectFunction<V> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Double2ObjectFunction<V> f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V apply(double operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.apply(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public V apply(Double key) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.apply(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(V defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean containsKey(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V put(double k, V v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V getOrDefault(double k, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V remove(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.function.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public V put(Double k, V v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public V get(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public V getOrDefault(Object k, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public V remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.function.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.toString();
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

    public static class UnmodifiableFunction<V>
    extends AbstractDouble2ObjectFunction<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ObjectFunction<? extends V> function;

        protected UnmodifiableFunction(Double2ObjectFunction<? extends V> f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
        }

        @Override
        public int size() {
            return this.function.size();
        }

        @Override
        public V defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(V defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(double k) {
            return this.function.containsKey(k);
        }

        @Override
        public V put(double k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(double k) {
            return this.function.get(k);
        }

        @Override
        public V getOrDefault(double k, V defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public V remove(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public V put(Double k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public V get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public V getOrDefault(Object k, V defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public V remove(Object k) {
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            return this.function.hashCode();
        }

        public boolean equals(Object o) {
            return o == this || this.function.equals(o);
        }

        public String toString() {
            return this.function.toString();
        }
    }

    public static class PrimitiveFunction<V>
    implements Double2ObjectFunction<V> {
        protected final java.util.function.Function<? super Double, ? extends V> function;

        protected PrimitiveFunction(java.util.function.Function<? super Double, ? extends V> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(double key) {
            return this.function.apply(key) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Double)key) != null;
        }

        @Override
        public V get(double key) {
            V v = this.function.apply(key);
            if (v == null) {
                return null;
            }
            return v;
        }

        @Override
        public V getOrDefault(double key, V defaultValue) {
            V v = this.function.apply(key);
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public V get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Double)key);
        }

        @Override
        @Deprecated
        public V getOrDefault(Object key, V defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            V v = this.function.apply((Double)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public V put(Double key, V value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<V>
    extends AbstractDouble2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public V get(double k) {
            return null;
        }

        @Override
        public V getOrDefault(double k, V defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(double k) {
            return false;
        }

        @Override
        public V defaultReturnValue() {
            return null;
        }

        @Override
        public void defaultReturnValue(V defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        public Object clone() {
            return EMPTY_FUNCTION;
        }

        public int hashCode() {
            return 0;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Function)) {
                return false;
            }
            return ((Function)o).size() == 0;
        }

        public String toString() {
            return "{}";
        }

        private Object readResolve() {
            return EMPTY_FUNCTION;
        }
    }
}

