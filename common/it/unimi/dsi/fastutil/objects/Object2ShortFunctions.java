/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObject2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class Object2ShortFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2ShortFunctions() {
    }

    public static <K> Object2ShortFunction<K> singleton(K key, short value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2ShortFunction<K> singleton(K key, Short value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2ShortFunction<K> synchronize(Object2ShortFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Object2ShortFunction<K> synchronize(Object2ShortFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Object2ShortFunction<K> unmodifiable(Object2ShortFunction<? extends K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Object2ShortFunction<K> primitive(java.util.function.Function<? super K, ? extends Short> f) {
        Objects.requireNonNull(f);
        if (f instanceof Object2ShortFunction) {
            return (Object2ShortFunction)f;
        }
        if (f instanceof ToIntFunction) {
            return key -> SafeMath.safeIntToShort(((ToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class Singleton<K>
    extends AbstractObject2ShortFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final short value;

        protected Singleton(K key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return Objects.equals(this.key, k);
        }

        @Override
        public short getShort(Object k) {
            return Objects.equals(this.key, k) ? this.value : this.defRetValue;
        }

        @Override
        public short getOrDefault(Object k, short defaultValue) {
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
    implements Object2ShortFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ShortFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Object2ShortFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Object2ShortFunction<K> f) {
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
        public Short apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Short)this.function.apply(key);
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
        public short defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(short defRetValue) {
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
        public short put(K k, short v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short getShort(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getShort(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short getOrDefault(Object k, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short removeShort(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeShort(k);
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
        public Short put(K k, Short v) {
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
        public Short get(Object k) {
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
        public Short getOrDefault(Object k, Short defaultValue) {
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
        public Short remove(Object k) {
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
    extends AbstractObject2ShortFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ShortFunction<? extends K> function;

        protected UnmodifiableFunction(Object2ShortFunction<? extends K> f) {
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
        public short defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(short defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public short put(K k, short v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(Object k) {
            return this.function.getShort(k);
        }

        @Override
        public short getOrDefault(Object k, short defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public short removeShort(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Short put(K k, Short v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Short get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Short getOrDefault(Object k, Short defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Short remove(Object k) {
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
    implements Object2ShortFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Short> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Short> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public short getShort(Object key) {
            Short v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public short getOrDefault(Object key, short defaultValue) {
            Short v = this.function.apply(key);
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Short get(Object key) {
            return this.function.apply(key);
        }

        @Override
        @Deprecated
        public Short getOrDefault(Object key, Short defaultValue) {
            Short v = this.function.apply(key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Short put(K key, Short value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<K>
    extends AbstractObject2ShortFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public short getShort(Object k) {
            return 0;
        }

        @Override
        public short getOrDefault(Object k, short defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public short defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(short defRetValue) {
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

