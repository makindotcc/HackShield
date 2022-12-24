/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.AbstractInt2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntToDoubleFunction;

public final class Int2FloatFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Int2FloatFunctions() {
    }

    public static Int2FloatFunction singleton(int key, float value) {
        return new Singleton(key, value);
    }

    public static Int2FloatFunction singleton(Integer key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Int2FloatFunction synchronize(Int2FloatFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Int2FloatFunction synchronize(Int2FloatFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Int2FloatFunction unmodifiable(Int2FloatFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Int2FloatFunction primitive(java.util.function.Function<? super Integer, ? extends Float> f) {
        Objects.requireNonNull(f);
        if (f instanceof Int2FloatFunction) {
            return (Int2FloatFunction)f;
        }
        if (f instanceof IntToDoubleFunction) {
            return key -> SafeMath.safeDoubleToFloat(((IntToDoubleFunction)((Object)f)).applyAsDouble(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class Singleton
    extends AbstractInt2FloatFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final int key;
        protected final float value;

        protected Singleton(int key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(int k) {
            return this.key == k;
        }

        @Override
        public float get(int k) {
            return this.key == k ? this.value : this.defRetValue;
        }

        @Override
        public float getOrDefault(int k, float defaultValue) {
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
    implements Int2FloatFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2FloatFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Int2FloatFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Int2FloatFunction f) {
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
        public double applyAsDouble(int operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.applyAsDouble(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Float apply(Integer key) {
            Object object = this.sync;
            synchronized (object) {
                return (Float)this.function.apply(key);
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
        public float defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(float defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(int k) {
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
        public float put(int k, float v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float get(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float getOrDefault(int k, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float remove(int k) {
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
        public Float put(Integer k, Float v) {
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
        public Float get(Object k) {
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
        public Float getOrDefault(Object k, Float defaultValue) {
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
        public Float remove(Object k) {
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
    extends AbstractInt2FloatFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2FloatFunction function;

        protected UnmodifiableFunction(Int2FloatFunction f) {
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
        public float defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(float defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(int k) {
            return this.function.containsKey(k);
        }

        @Override
        public float put(int k, float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float get(int k) {
            return this.function.get(k);
        }

        @Override
        public float getOrDefault(int k, float defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public float remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float put(Integer k, Float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float get(Object k) {
            return this.function.get(k);
        }

        @Override
        @Deprecated
        public Float getOrDefault(Object k, Float defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        @Deprecated
        public Float remove(Object k) {
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
    implements Int2FloatFunction {
        protected final java.util.function.Function<? super Integer, ? extends Float> function;

        protected PrimitiveFunction(java.util.function.Function<? super Integer, ? extends Float> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(int key) {
            return this.function.apply((Integer)key) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Integer)key) != null;
        }

        @Override
        public float get(int key) {
            Float v = this.function.apply((Integer)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v.floatValue();
        }

        @Override
        public float getOrDefault(int key, float defaultValue) {
            Float v = this.function.apply((Integer)key);
            if (v == null) {
                return defaultValue;
            }
            return v.floatValue();
        }

        @Override
        @Deprecated
        public Float get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Integer)key);
        }

        @Override
        @Deprecated
        public Float getOrDefault(Object key, Float defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            Float v = this.function.apply((Integer)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Float put(Integer key, Float value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction
    extends AbstractInt2FloatFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public float get(int k) {
            return 0.0f;
        }

        @Override
        public float getOrDefault(int k, float defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(int k) {
            return false;
        }

        @Override
        public float defaultReturnValue() {
            return 0.0f;
        }

        @Override
        public void defaultReturnValue(float defRetValue) {
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

