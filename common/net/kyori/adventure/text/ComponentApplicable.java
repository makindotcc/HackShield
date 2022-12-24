/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ComponentApplicable {
    @NotNull
    public Component componentApply(@NotNull Component var1);
}

