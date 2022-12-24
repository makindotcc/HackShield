/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class Object2CharFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2CharFunctions() {
    }

    public static <K> Object2CharFunction<K> singleton(K key, char value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2CharFunction<K> singleton(K key, Character value) {
        return new Singleton<K>(key, value.charValue());
    }

    public static <K> Object2CharFunction<K> synchronize(Object2CharFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Object2CharFunction<K> synchronize(Object2CharFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Object2CharFunction<K> unmodifiable(Object2CharFunction<? extends K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Object2CharFunction<K> primitive(java.util.function.Function<? super K, ? extends Character> f) {
        Objects.requireNonNull(f);
        if (f instanceof Object2CharFunction) {
            return (Object2CharFunction)f;
        }
        if (f instanceof ToIntFunction) {
            return key -> SafeMath.safeIntToChar(((ToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class Singleton<K>
    extends AbstractObject2CharFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final char value;

        protected Singleton(K key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return Objects.equals(this.key, k);
        }

        @Override
        public char getChar(Object k) {
            return Objects.equals(this.key, k) ? this.value : this.defRetValue;
        }

        @Override
        public char getOrDefault(Object k, char defaultValue) {
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
    implements Object2CharFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2CharFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Object2CharFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Object2CharFunction<K> f) {
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
        public Character apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.function.apply(key);
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
        public char defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(char defRetValue) {
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
        public char put(K k, char v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char getChar(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getChar(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char getOrDefault(Object k, char defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char removeChar(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeChar(k);
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
        public Character put(K k, Character v) {
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
        public Character get(Object k) {
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
        public Character getOrDefault(Object k, Character defaultValue) {
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
        public Character remove(Object k) {
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
    extends AbstractObject2CharFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2CharFunction<? extends K> function;

        protected UnmodifiableFunction(Object2CharFunction<? extends K> f) {
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
        public char defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public char put(K k, char v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char getChar(Object k) {
            return this.function.getChar(k);
        }

        @Override
        public char getOrDefault(Object k, char defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public char removeChar(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character put(K k, Character v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Character getOrDefault(Object k, Character defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Character remove(Object k) {
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
    implements Object2CharFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Character> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Character> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public char getChar(Object key) {
            Character v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v.charValue();
        }

        @Override
        public char getOrDefault(Object key, char defaultValue) {
            Character v = this.function.apply(key);
            if (v == null) {
                return defaultValue;
            }
            return v.charValue();
        }

        @Override
        @Deprecated
        public Character get(Object key) {
            return this.function.apply(key);
        }

        @Override
        @Deprecated
        public Character getOrDefault(Object key, Character defaultValue) {
            Character v = this.function.apply(key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Character put(K key, Character value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction<K>
    extends AbstractObject2CharFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public char getChar(Object k) {
            return '\u0000';
        }

        @Override
        public char getOrDefault(Object k, char defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public char defaultReturnValue() {
            return '\u0000';
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
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

