/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class Short2ShortFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Short2ShortFunctions() {
    }

    public static Short2ShortFunction singleton(short key, short value) {
        return new Singleton(key, value);
    }

    public static Short2ShortFunction singleton(Short key, Short value) {
        return new Singleton(key, value);
    }

    public static Short2ShortFunction synchronize(Short2ShortFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Short2ShortFunction synchronize(Short2ShortFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Short2ShortFunction unmodifiable(Short2ShortFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Short2ShortFunction primitive(java.util.function.Function<? super Short, ? extends Short> f) {
        Objects.requireNonNull(f);
        if (f instanceof Short2ShortFunction) {
            return (Short2ShortFunction)f;
        }
        if (f instanceof IntUnaryOperator) {
            return key -> SafeMath.safeIntToShort(((IntUnaryOperator)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class Singleton
    extends AbstractShort2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final short key;
        protected final short value;

        protected Singleton(short key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(short k) {
            return this.key == k;
        }

        @Override
        public short get(short k) {
            return this.key == k ? this.value : this.defRetValue;
        }

        @Override
        public short getOrDefault(short k, short defaultValue) {
            return this.key == k ? this.value : defaultValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedFunction
    implements Short2ShortFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ShortFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Short2ShortFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Short2ShortFunction f) {
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
        public int applyAsInt(int operand) {
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
        public Short apply(Short key) {
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
        public boolean containsKey(short k) {
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
        public short put(short k, short v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short get(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short getOrDefault(short k, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short remove(short k) {
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
        public Short put(Short k, Short v) {
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

    public static class UnmodifiableFunction
    extends AbstractShort2ShortFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ShortFunction function;

        protected UnmodifiableFunction(Short2ShortFunction f) {
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
        public boolean containsKey(short k) {
            return this.function.containsKey(k);
        }

        @Override
        public short put(short k, short v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short get(short k) {
            return this.function.get(k);
        }

        @Override
        public short getOrDefault(short k, short defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public short remove(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Short put(Short k, Short v) {
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

    public static class PrimitiveFunction
    implements Short2ShortFunction {
        protected final java.util.function.Function<? super Short, ? extends Short> function;

        protected PrimitiveFunction(java.util.function.Function<? super Short, ? extends Short> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(short key) {
            return this.function.apply((Short)key) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Short)key) != null;
        }

        @Override
        public short get(short key) {
            Short v = this.function.apply((Short)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public short getOrDefault(short key, short defaultValue) {
            Short v = this.function.apply((Short)key);
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Short get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Short)key);
        }

        @Override
        @Deprecated
        public Short getOrDefault(Object key, Short defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            Short v = this.function.apply((Short)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Short put(Short key, Short value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction
    extends AbstractShort2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public short get(short k) {
            return 0;
        }

        @Override
        public short getOrDefault(short k, short defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(short k) {
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

