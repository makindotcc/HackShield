/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponentImpl;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentCompaction {
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();

    private ComponentCompaction() {
    }

    static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
        Component child;
        int i;
        TextComponent textComponent;
        int childrenSize;
        List<Component> children = self.children();
        Component optimized = self.children(Collections.emptyList());
        if (parentStyle != null) {
            optimized = optimized.style(ComponentCompaction.simplifyStyle(self.style(), parentStyle));
        }
        if ((childrenSize = children.size()) == 0) {
            if (ComponentCompaction.isBlank(optimized)) {
                optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style()));
            }
            return optimized;
        }
        if (childrenSize == 1 && optimized instanceof TextComponent && (textComponent = (TextComponent)optimized).content().isEmpty()) {
            Component child2 = children.get(0);
            return child2.style(child2.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
        }
        Style childParentStyle = optimized.style();
        if (parentStyle != null) {
            childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        }
        ArrayList<Component> childrenToAppend = new ArrayList<Component>(children.size());
        for (i = 0; i < children.size(); ++i) {
            TextComponent textComponent2;
            child = children.get(i);
            if ((child = ComponentCompaction.compact(child, childParentStyle)).children().isEmpty() && child instanceof TextComponent && (textComponent2 = (TextComponent)child).content().isEmpty()) continue;
            childrenToAppend.add(child);
        }
        if (optimized instanceof TextComponent) {
            while (!childrenToAppend.isEmpty()) {
                Component child3 = (Component)childrenToAppend.get(0);
                Style childStyle = child3.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                if (!(child3 instanceof TextComponent) || !Objects.equals(childStyle, childParentStyle)) break;
                optimized = ComponentCompaction.joinText((TextComponent)optimized, (TextComponent)child3);
                childrenToAppend.remove(0);
                childrenToAppend.addAll(0, child3.children());
            }
        }
        i = 0;
        while (i + 1 < childrenToAppend.size()) {
            Style neighborStyle;
            Style childStyle;
            child = (Component)childrenToAppend.get(i);
            Component neighbor = (Component)childrenToAppend.get(i + 1);
            if (child.children().isEmpty() && child instanceof TextComponent && neighbor instanceof TextComponent && (childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).equals(neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET))) {
                TextComponent combined = ComponentCompaction.joinText((TextComponent)child, (TextComponent)neighbor);
                childrenToAppend.set(i, combined);
                childrenToAppend.remove(i + 1);
                continue;
            }
            ++i;
        }
        if (childrenToAppend.isEmpty() && ComponentCompaction.isBlank(optimized)) {
            optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style()));
        }
        return optimized.children(childrenToAppend);
    }

    @NotNull
    private static Style simplifyStyle(@NotNull Style style, @NotNull Style parentStyle) {
        if (style.isEmpty()) {
            return style;
        }
        Style.Builder builder = style.toBuilder();
        if (Objects.equals(style.font(), parentStyle.font())) {
            builder.font(null);
        }
        if (Objects.equals(style.color(), parentStyle.color())) {
            builder.color(null);
        }
        for (TextDecoration decoration : DECORATIONS) {
            if (style.decoration(decoration) != parentStyle.decoration(decoration)) continue;
            builder.decoration(decoration, TextDecoration.State.NOT_SET);
        }
        if (Objects.equals(style.clickEvent(), parentStyle.clickEvent())) {
            builder.clickEvent(null);
        }
        if (Objects.equals(style.hoverEvent(), parentStyle.hoverEvent())) {
            builder.hoverEvent((HoverEventSource)null);
        }
        if (Objects.equals(style.insertion(), parentStyle.insertion())) {
            builder.insertion(null);
        }
        return builder.build();
    }

    private static boolean isBlank(Component component) {
        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent)component;
            String content = textComponent.content();
            for (int i = 0; i < content.length(); ++i) {
                char c = content.charAt(i);
                if (c == ' ') continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @NotNull
    private static Style simplifyStyleForBlank(@NotNull Style style) {
        Style.Builder builder = style.toBuilder();
        builder.color(null);
        builder.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
        builder.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET);
        return builder.build();
    }

    private static TextComponent joinText(TextComponent one, TextComponent two) {
        return TextComponentImpl.create(two.children(), one.style(), one.content() + two.content());
    }
}

