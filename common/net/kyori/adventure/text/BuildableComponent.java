/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.NotNull;

public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
extends Buildable<C, B>,
Component {
    @Override
    @NotNull
    public B toBuilder();
}

