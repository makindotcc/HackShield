/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.key;

import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyedValue;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class KeyedValueImpl<T>
implements Examinable,
KeyedValue<T> {
    private final Key key;
    private final T value;

    KeyedValueImpl(Key key, T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    @NotNull
    public Key key() {
        return this.key;
    }

    @Override
    @NotNull
    public T value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        KeyedValueImpl that = (KeyedValueImpl)other;
        return this.key.equals(that.key) && this.value.equals(that.value);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("value", this.value));
    }

    public String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }
}

