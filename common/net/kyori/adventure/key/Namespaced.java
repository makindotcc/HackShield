/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.key;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Namespaced {
    @NotNull
    @Pattern(value="[a-z0-9_\\-.]+")
    public String namespace();
}

