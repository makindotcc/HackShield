/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.key;

import java.util.Comparator;
import java.util.stream.Stream;
import net.kyori.adventure.key.KeyImpl;
import net.kyori.adventure.key.Namespaced;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Key
extends Comparable<Key>,
Examinable,
Namespaced {
    public static final String MINECRAFT_NAMESPACE = "minecraft";

    @NotNull
    public static Key key(@NotNull @Pattern(value="([a-z0-9_\\-.]+:)?[a-z0-9_\\-./]+") String string) {
        return Key.key(string, ':');
    }

    @NotNull
    public static Key key(@NotNull String string, char character) {
        int index = string.indexOf(character);
        String namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        String value = index >= 0 ? string.substring(index + 1) : string;
        return Key.key(namespace, value);
    }

    @NotNull
    public static Key key(@NotNull Namespaced namespaced, @NotNull @Pattern(value="[a-z0-9_\\-./]+") String value) {
        return Key.key(namespaced.namespace(), value);
    }

    @NotNull
    public static Key key(@NotNull @Pattern(value="[a-z0-9_\\-.]+") String namespace, @NotNull @Pattern(value="[a-z0-9_\\-./]+") String value) {
        return new KeyImpl(namespace, value);
    }

    @NotNull
    public static Comparator<? super Key> comparator() {
        return KeyImpl.COMPARATOR;
    }

    @Override
    @NotNull
    public String namespace();

    @NotNull
    public String value();

    @NotNull
    public String asString();

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("namespace", this.namespace()), ExaminableProperty.of("value", this.value()));
    }

    @Override
    default public int compareTo(@NotNull Key that) {
        return Key.comparator().compare(this, that);
    }
}

