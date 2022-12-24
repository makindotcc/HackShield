/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
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
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2CharFunction<K>
extends Function<K, Character>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getChar(operand);
    }

    @Override
    default public char put(K key, char value) {
        throw new UnsupportedOperationException();
    }

    public char getChar(Object var1);

    @Override
    default public char getOrDefault(Object key, char defaultValue) {
        char v = this.getChar(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char removeChar(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(K key, Character value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        Object k = key;
        char v = this.getChar(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        Object k = key;
        char v = this.getChar(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Character.valueOf(this.removeChar(k)) : null;
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Object2ByteFunction<K> andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Byte2CharFunction composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Short2CharFunction composeShort(Short2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Char2IntFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Int2CharFunction composeInt(Int2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Char2LongFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Long2CharFunction composeLong(Long2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Char2CharFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Char2CharFunction composeChar(Char2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Float2CharFunction composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Double2CharFunction composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getChar(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getChar(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getChar(before.get(k));
    }
}

