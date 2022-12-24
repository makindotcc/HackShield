/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2CharFunction
extends Function<Long, Character>,
LongToIntFunction {
    @Override
    default public int applyAsInt(long operand) {
        return this.get(operand);
    }

    @Override
    default public char put(long key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(long var1);

    default public char getOrDefault(long key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(Long key, Character value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        long k = (Long)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Character> compose(java.util.function.Function<? super T, ? extends Long> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Long, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Long2ByteFunction andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2CharFunction composeByte(Byte2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2ShortFunction andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2CharFunction composeShort(Short2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2IntFunction andThenInt(Char2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2CharFunction composeInt(Int2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2LongFunction andThenLong(Char2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2CharFunction composeLong(Long2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2CharFunction andThenChar(Char2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2CharFunction composeChar(Char2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2FloatFunction andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2CharFunction composeFloat(Float2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2DoubleFunction andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2CharFunction composeDouble(Double2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Long2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }

    default public <T> Long2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }
}
