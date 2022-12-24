/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;

public final class Float2ByteFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Float2ByteFunctions() {
    }

    public static Float2ByteFunction singleton(float key, byte value) {
        return new Singleton(key, value);
    }

    public static Float2ByteFunction singleton(Float key, Byte value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2ByteFunction synchronize(Float2ByteFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Float2ByteFunction synchronize(Float2ByteFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Float2ByteFunction unmodifiable(Float2ByteFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Float2ByteFunction primitive(java.util.function.Function<? super Float, ? extends Byte> f) {
        Objects.requireNonNull(f);
        if (f instanceof Float2ByteFunction) {
            return (Float2ByteFunction)f;
        }
        if (f instanceof DoubleToIntFunction) {
            return key -> SafeMath.safeIntToByte(((DoubleToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class Singleton
    extends AbstractFloat2ByteFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final float key;
        protected final byte value;

        protected Singleton(float key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
        }

        @Override
        public byte get(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
        }

        @Override
        public byte getOrDefault(float k, byte defaultValue) {
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

    public static class SynchronizedFunction
    implements Float2ByteFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ByteFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Float2ByteFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Float2ByteFunction f) {
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
        public int applyAsInt(double operand) {
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
        public Byte apply(Float key) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.function.apply(key);
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
        public byte defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(byte defRetValue) {
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
        public byte put(float k, byte v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte get(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getOrDefault(float k, byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte remove(float k) {
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
        public Byte put(Float k, Byte v) {
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
        public Byte get(Object k) {
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
        public Byte getOrDefault(Object k, Byte defaultValue) {
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
        public Byte remove(Object k) {
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
    extends AbstractFloat2ByteFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ByteFunction function;

        protected UnmodifiableFunction(Float2ByteFunction f) {
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
        public byte defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(float k) {
            return this.function.containsKey(k);
        }

        @Override
        public byte put(float k, byte v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte get(float k) {
            return this.function.get(k);
        }

        @Override
        public byte getOrDefault(float k, byte defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public byte remove(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte put(Float k, Byte v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Byte getOrDefault(Object k, Byte defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Byte remove(Object k) {
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
    implements Float2ByteFunction {
        protected final java.util.function.Function<? super Float, ? extends Byte> function;

        protected PrimitiveFunction(java.util.function.Function<? super Float, ? extends Byte> function) {
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
        public byte get(float key) {
            Byte v = this.function.apply(Float.valueOf(key));
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public byte getOrDefault(float key, byte defaultValue) {
            Byte v = this.function.apply(Float.valueOf(key));
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Byte get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Float)key);
        }

        @Override
        @Deprecated
        public Byte getOrDefault(Object key, Byte defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            Byte v = this.function.apply((Float)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Byte put(Float key, Byte value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction
    extends AbstractFloat2ByteFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public byte get(float k) {
            return 0;
        }

        @Override
        public byte getOrDefault(float k, byte defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(float k) {
            return false;
        }

        @Override
        public byte defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
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

