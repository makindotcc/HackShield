/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.flattener;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FlattenerListener {
    default public void pushStyle(@NotNull Style style) {
    }

    public void component(@NotNull String var1);

    default public void popStyle(@NotNull Style style) {
    }
}

