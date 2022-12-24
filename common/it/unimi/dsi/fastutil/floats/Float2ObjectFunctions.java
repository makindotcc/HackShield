/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleFunction;

public final class Float2ObjectFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Float2ObjectFunctions() {
    }

    public static <V> Float2ObjectFunction<V> singleton(float key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Float2ObjectFunction<V> singleton(Float key, V value) {
        return new Singleton<V>(key.floatValue(), value);
    }

    public static <V> Float2ObjectFunction<V> synchronize(Float2ObjectFunction<V> f) {
        return new SynchronizedFunction<V>(f);
    }

    public static <V> Float2ObjectFunction<V> synchronize(Float2ObjectFunction<V> f, Object sync) {
        return new SynchronizedFunction<V>(f, sync);
    }

    public static <V> Float2ObjectFunction<V> unmodifiable(Float2ObjectFunction<? extends V> f) {
        return new UnmodifiableFunction<V>(f);
    }

    public static <V> Float2ObjectFunction<V> primitive(java.util.function.Function<? super Float, ? extends V> f) {
        Objects.requireNonNull(f);
        if (f instanceof Float2ObjectFunction) {
            return (Float2ObjectFunction)f;
        }
        if (f instanceof DoubleFunction) {
            return ((DoubleFunction)((Object)f))::apply;
        }
        return new PrimitiveFunction<V>(f);
    }

    public static class Singleton<V>
    extends AbstractFloat2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final float key;
        protected final V value;

        protected Singleton(float key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
        }

        @Override
        public V get(float k) {
            return (V)(Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue);
        }

        @Override
        public V getOrDefault(float k, V defaultValue) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : defaultValue;
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
    implements Float2ObjectFunction<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ObjectFunction<V> function;
        protected final Object sync;

        protected SynchronizedFunction(Float2ObjectFunction<V> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Float2ObjectFunction<V> f) {
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
        @Deprecated
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
        public V apply(Float key) {
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
        public boolean containsKey(float k) {
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
        public V put(float k, V v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V getOrDefault(float k, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V remove(float k) {
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
        public V put(Float k, V v) {
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
    extends AbstractFloat2ObjectFunction<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ObjectFunction<? extends V> function;

        protected UnmodifiableFunction(Float2ObjectFunction<? extends V> f) {
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
        public boolean containsKey(float k) {
            return this.function.containsKey(k);
        }

        @Override
        public V put(float k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(float k) {
            return this.function.get(k);
        }

        @Override
        public V getOrDefault(float k, V defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public V remove(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public V put(Float k, V v) {
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
    implements Float2ObjectFunction<V> {
        protected final java.util.function.Function<? super Float, ? extends V> function;

        protected PrimitiveFunction(java.util.function.Function<? super Float, ? extends V> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(float key) {
            return this.function.apply(Float.valueOf(key)) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Float)key) != null;
        }

        @Override
        public V get(float key) {
            V v = this.function.apply(Float.valueOf(key));
            if (v == null) {
                return null;
            }
            return v;
        }

        @Override
        public V getOrDefault(float key, V defaultValue) {
            V v = this.function.apply(Float.valueOf(key));
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
            return this.function.apply((Float)key);
        }

        @Override
        @Deprecated
        public V getOrDefault(Object key, V defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            V v = this.function.apply((Float)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public V put(Float key, V value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<V>
    extends AbstractFloat2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public V get(float k) {
            return null;
        }

        @Override
        public V getOrDefault(float k, V defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(float k) {
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

