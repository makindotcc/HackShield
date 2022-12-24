/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class Object2IntFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2IntFunctions() {
    }

    public static <K> Object2IntFunction<K> singleton(K key, int value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2IntFunction<K> singleton(K key, Integer value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2IntFunction<K> synchronize(Object2IntFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Object2IntFunction<K> synchronize(Object2IntFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Object2IntFunction<K> unmodifiable(Object2IntFunction<? extends K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Object2IntFunction<K> primitive(java.util.function.Function<? super K, ? extends Integer> f) {
        Objects.requireNonNull(f);
        if (f instanceof Object2IntFunction) {
            return (Object2IntFunction)f;
        }
        if (f instanceof ToIntFunction) {
            return key -> ((ToIntFunction)((Object)f)).applyAsInt(key);
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class Singleton<K>
    extends AbstractObject2IntFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final int value;

        protected Singleton(K key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return Objects.equals(this.key, k);
        }

        @Override
        public int getInt(Object k) {
            return Objects.equals(this.key, k) ? this.value : this.defRetValue;
        }

        @Override
        public int getOrDefault(Object k, int defaultValue) {
            return Objects.equals(this.key, k) ? this.value : defaultValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedFunction<K>
    implements Object2IntFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2IntFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Object2IntFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Object2IntFunction<K> f) {
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
        public int applyAsInt(K operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.applyAsInt(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Integer apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Integer)this.function.apply(key);
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
        public int defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(int defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
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
        public int put(K k, int v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getInt(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getInt(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getOrDefault(Object k, int defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int removeInt(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeInt(k);
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
        public Integer put(K k, Integer v) {
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
        public Integer get(Object k) {
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
        public Integer getOrDefault(Object k, Integer defaultValue) {
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
        public Integer remove(Object k) {
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

    public static class UnmodifiableFunction<K>
    extends AbstractObject2IntFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2IntFunction<? extends K> function;

        protected UnmodifiableFunction(Object2IntFunction<? extends K> f) {
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
        public int defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public int put(K k, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(Object k) {
            return this.function.getInt(k);
        }

        @Override
        public int getOrDefault(Object k, int defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public int removeInt(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer put(K k, Integer v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Integer getOrDefault(Object k, Integer defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Integer remove(Object k) {
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

    public static class PrimitiveFunction<K>
    implements Object2IntFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Integer> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Integer> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public int getInt(Object key) {
            Integer v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public int getOrDefault(Object key, int defaultValue) {
            Integer v = this.function.apply(key);
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Integer get(Object key) {
            return this.function.apply(key);
        }

        @Override
        @Deprecated
        public Integer getOrDefault(Object key, Integer defaultValue) {
            Integer v = this.function.apply(key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Integer put(K key, Integer value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<K>
    extends AbstractObject2IntFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public int getInt(Object k) {
            return 0;
        }

        @Override
        public int getOrDefault(Object k, int defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public int defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
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

