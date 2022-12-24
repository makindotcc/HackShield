/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilderApplicable;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponentImpl;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import org.jetbrains.annotations.NotNull;

public final class LinearComponents {
    private LinearComponents() {
    }

    @NotNull
    public static Component linear(ComponentBuilderApplicable ... applicables) {
        int length = applicables.length;
        if (length == 0) {
            return Component.empty();
        }
        if (length == 1) {
            ComponentBuilderApplicable ap0 = applicables[0];
            if (ap0 instanceof ComponentLike) {
                return ((ComponentLike)((Object)ap0)).asComponent();
            }
            throw LinearComponents.nothingComponentLike();
        }
        TextComponentImpl.BuilderImpl builder = new TextComponentImpl.BuilderImpl();
        Style.Builder style = null;
        for (int i = 0; i < length; ++i) {
            ComponentBuilderApplicable applicable = applicables[i];
            if (applicable instanceof StyleBuilderApplicable) {
                if (style == null) {
                    style = Style.style();
                }
                style.apply((StyleBuilderApplicable)applicable);
                continue;
            }
            if (style != null && applicable instanceof ComponentLike) {
                builder.applicableApply(((ComponentLike)((Object)applicable)).asComponent().style(style));
                continue;
            }
            builder.applicableApply(applicable);
        }
        int size = builder.children.size();
        if (size == 0) {
            throw LinearComponents.nothingComponentLike();
        }
        if (size == 1) {
            return (Component)builder.children.get(0);
        }
        return builder.build();
    }

    private static IllegalStateException nothingComponentLike() {
        return new IllegalStateException("Cannot build component linearly - nothing component-like was given");
    }
}

