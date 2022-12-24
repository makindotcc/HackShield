/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.format;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.StyleImpl;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface Merger {
    public void mergeColor(StyleImpl.BuilderImpl var1, @Nullable TextColor var2);

    public void mergeDecoration(StyleImpl.BuilderImpl var1, @NotNull TextDecoration var2, @NotNull TextDecoration.State var3);

    public void mergeClickEvent(StyleImpl.BuilderImpl var1, @Nullable ClickEvent var2);

    public void mergeHoverEvent(StyleImpl.BuilderImpl var1, @Nullable HoverEvent<?> var2);

    public void mergeInsertion(StyleImpl.BuilderImpl var1, @Nullable String var2);

    public void mergeFont(StyleImpl.BuilderImpl var1, @Nullable Key var2);
}

