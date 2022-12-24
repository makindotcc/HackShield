/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.key;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

final class KeyImpl
implements Key {
    static final Comparator<? super Key> COMPARATOR = Comparator.comparing(Key::value).thenComparing(Key::namespace);
    static final String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
    static final String VALUE_PATTERN = "[a-z0-9_\\-./]+";
    private final String namespace;
    private final String value;

    KeyImpl(@NotNull String namespace, @NotNull String value) {
        if (!KeyImpl.namespaceValid(namespace)) {
            throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9_.-] character in namespace of Key[%s]", KeyImpl.asString(namespace, value)));
        }
        if (!KeyImpl.valueValid(value)) {
            throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9/._-] character in value of Key[%s]", KeyImpl.asString(namespace, value)));
        }
        this.namespace = Objects.requireNonNull(namespace, "namespace");
        this.value = Objects.requireNonNull(value, "value");
    }

    @VisibleForTesting
    static boolean namespaceValid(@NotNull String namespace) {
        int length = namespace.length();
        for (int i = 0; i < length; ++i) {
            if (KeyImpl.validNamespaceChar(namespace.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @VisibleForTesting
    static boolean valueValid(@NotNull String value) {
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            if (KeyImpl.validValueChar(value.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean validNamespaceChar(int value) {
        return value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 46;
    }

    private static boolean validValueChar(int value) {
        return value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 47 || value == 46;
    }

    @Override
    @NotNull
    public String namespace() {
        return this.namespace;
    }

    @Override
    @NotNull
    public String value() {
        return this.value;
    }

    @Override
    @NotNull
    public String asString() {
        return KeyImpl.asString(this.namespace, this.value);
    }

    @NotNull
    private static String asString(@NotNull String namespace, @NotNull String value) {
        return namespace + ':' + value;
    }

    @NotNull
    public String toString() {
        return this.asString();
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("namespace", this.namespace), ExaminableProperty.of("value", this.value));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Key)) {
            return false;
        }
        Key that = (Key)other;
        return Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value());
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NotNull Key that) {
        return Key.super.compareTo(that);
    }
}

