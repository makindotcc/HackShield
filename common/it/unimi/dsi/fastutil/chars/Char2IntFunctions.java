/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.chars.AbstractChar2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class Char2IntFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Char2IntFunctions() {
    }

    public static Char2IntFunction singleton(char key, int value) {
        return new Singleton(key, value);
    }

    public static Char2IntFunction singleton(Character key, Integer value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2IntFunction synchronize(Char2IntFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Char2IntFunction synchronize(Char2IntFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Char2IntFunction unmodifiable(Char2IntFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Char2IntFunction primitive(java.util.function.Function<? super Character, ? extends Integer> f) {
        Objects.requireNonNull(f);
        if (f instanceof Char2IntFunction) {
            return (Char2IntFunction)f;
        }
        if (f instanceof IntUnaryOperator) {
            return ((IntUnaryOperator)((Object)f))::applyAsInt;
        }
        return new PrimitiveFunction(f);
    }

    public static class Singleton
    extends AbstractChar2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final char key;
        protected final int value;

        protected Singleton(char key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(char k) {
            return this.key == k;
        }

        @Override
        public int get(char k) {
            return this.key == k ? this.value : this.defRetValue;
        }

        @Override
        public int getOrDefault(char k, int defaultValue) {
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
    implements Char2IntFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2IntFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Char2IntFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Char2IntFunction f) {
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
        public Integer apply(Character key) {
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
        public boolean containsKey(char k) {
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
        public int put(char k, int v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int get(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getOrDefault(char k, int defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getOrDefault(k, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int remove(char k) {
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
        public Integer put(Character k, Integer v) {
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

    public static class UnmodifiableFunction
    extends AbstractChar2IntFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2IntFunction function;

        protected UnmodifiableFunction(Char2IntFunction f) {
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
        public boolean containsKey(char k) {
            return this.function.containsKey(k);
        }

        @Override
        public int put(char k, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int get(char k) {
            return this.function.get(k);
        }

        @Override
        public int getOrDefault(char k, int defaultValue) {
            return this.function.getOrDefault(k, defaultValue);
        }

        @Override
        public int remove(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer put(Character k, Integer v) {
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

    public static class PrimitiveFunction
    implements Char2IntFunction {
        protected final java.util.function.Function<? super Character, ? extends Integer> function;

        protected PrimitiveFunction(java.util.function.Function<? super Character, ? extends Integer> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(char key) {
            return this.function.apply(Character.valueOf(key)) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Character)key) != null;
        }

        @Override
        public int get(char key) {
            Integer v = this.function.apply(Character.valueOf(key));
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        public int getOrDefault(char key, int defaultValue) {
            Integer v = this.function.apply(Character.valueOf(key));
            if (v == null) {
                return defaultValue;
            }
            return v;
        }

        @Override
        @Deprecated
        public Integer get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Character)key);
        }

        @Override
        @Deprecated
        public Integer getOrDefault(Object key, Integer defaultValue) {
            if (key == null) {
                return defaultValue;
            }
            Integer v = this.function.apply((Character)key);
            return v == null ? defaultValue : v;
        }

        @Override
        @Deprecated
        public Integer put(Character key, Integer value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction
    extends AbstractChar2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public int get(char k) {
            return 0;
        }

        @Override
        public int getOrDefault(char k, int defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(char k) {
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

