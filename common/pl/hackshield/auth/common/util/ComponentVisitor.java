/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package pl.hackshield.auth.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ComponentVisitor {
    public static final ThreadLocal<List<TextComponent>> COMPONENTS_BETWEEN_NEWLINES_THREAD_LOCAL = ThreadLocal.withInitial(ArrayList::new);

    public void visit(TextComponent var1);

    public static int countNewLines(TextComponent baseComponent) {
        MutableInt newLineCount = new MutableInt();
        ComponentVisitor.visit(baseComponent, component -> {
            String text = component.content();
            for (int i = 0; i < text.length(); ++i) {
                char aChar = text.charAt(i);
                if (aChar != '\n') continue;
                newLineCount.increment();
            }
        });
        return newLineCount.intValue();
    }

    public static List<TextComponent> splitLines(@NotNull TextComponent baseComponent) {
        Function<List, TextComponent> mergeComponents = textComponents -> {
            TextComponent.Builder componentBuilder = Component.text();
            componentBuilder.append((Iterable<? extends ComponentLike>)textComponents);
            try {
                TextComponent textComponent = (TextComponent)componentBuilder.build();
                return textComponent;
            }
            finally {
                textComponents.clear();
            }
        };
        List<TextComponent> componentsBetweenNewline = COMPONENTS_BETWEEN_NEWLINES_THREAD_LOCAL.get();
        ArrayList<TextComponent> lines = new ArrayList<TextComponent>();
        ComponentVisitor.visit(baseComponent, component -> {
            String content = component.content();
            if (content.equals("\n")) {
                lines.add((TextComponent)mergeComponents.apply(componentsBetweenNewline));
                return;
            }
            componentsBetweenNewline.add(component);
        });
        if (!componentsBetweenNewline.isEmpty()) {
            lines.add(mergeComponents.apply(componentsBetweenNewline));
        }
        return lines;
    }

    public static void visit(@NotNull TextComponent baseComponent, @NotNull ComponentVisitor visitor) {
        if (baseComponent == Component.empty()) {
            return;
        }
        visitor.visit((TextComponent)baseComponent.children(Collections.emptyList()));
        if (!baseComponent.children().isEmpty()) {
            for (Component child : baseComponent.children()) {
                if (!(child instanceof TextComponent)) continue;
                ComponentVisitor.visit((TextComponent)child, visitor);
            }
        }
    }
}

