/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

public final class Object2BooleanFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2BooleanFunctions() {
    }

    public static <K> Object2BooleanFunction<K> singleton(K key, boolean value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2BooleanFunction<K> singleton(K key, Boolean value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2BooleanFunction<K> synchronize(Object2BooleanFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Object2BooleanFunction<K> synchronize(Object2BooleanFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Object2BooleanFunction<K> unmodifiable(Object2BooleanFunction<? extends K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Object2BooleanFunction<K> primitive(java.util.function.Function<? super K, ? extends Boolean> f) {
        Objects.requireNonNull(f);
        if (f instanceof Object2BooleanFunction) {
            return (Object2BooleanFunction)f;
        }
        if (f instanceof Predicate) {
            return key -> ((Predicate)((Object)f)).test(key);
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class Singleton<K>
    extends AbstractObject2BooleanFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final boolean value;

        protected Singleton(K key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return Objects.equals(this.key, k);
        }

        @Override
        public boolean getBoolean(Object k) {
            return Objects.equals(this.key, k) ? this.value : this.defRetValue;
        }

        @Override
        public boolean getOrDefault(Object k, boolean defaultValue) {
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
    implements Object2BooleanFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Object2BooleanFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Object2BooleanFunction<K> f) {
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
        public boolean test(K operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.test(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Boolean apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.function.apply(key);
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
        public boolean defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(boolean defRetValue) {
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
        public boolean put(K k, boolean v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getBoolean(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getBoolean(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getOrDefault(Object k, boolean defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeBoolean(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeBoolean(k);
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
        public Boolean put(K k, Boolean v) {
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
        public Boolean get(Object k) {
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
        public Boolean getOrDefault(Object k, Boolean defaultValue) {
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
        public Boolean remove(Object k) {
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
    extends AbstractObject2BooleanFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanFunction<? extends K> function;

        protected UnmodifiableFunction(Object2BooleanFunction<? extends K> f) {
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
        public boolean defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public boolean put(K k, boolean v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean(Object k) {
            return this.function.getBoolean(k);
        }

        @Override
        public boolean getOrDefault(Object k, boolean defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public boolean removeBoolean(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean put(K k, Boolean v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Boolean getOrDefault(Object k, Boolean defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Boolean remove(Object k) {
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
    implements Object2BooleanFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Boolean> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Boolean> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public boolean getBoolean(Object key) {
            Boolean v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public boolean getOrDefault(Object key, boolean defaultValue) {
            Boolean v = this.function.apply(key);
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Boolean get(Object key) {
            return this.function.apply(key);
        }

        @Override
        @Deprecated
        public Boolean getOrDefault(Object key, Boolean defaultValue) {
            Boolean v = this.function.apply(key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Boolean put(K key, Boolean value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<K>
    extends AbstractObject2BooleanFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public boolean getBoolean(Object k) {
            return false;
        }

        @Override
        public boolean getOrDefault(Object k, boolean defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public boolean defaultReturnValue() {
            return false;
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
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

