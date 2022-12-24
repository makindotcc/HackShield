/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.identity;

import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public interface Identified {
    @NotNull
    public Identity identity();
}

