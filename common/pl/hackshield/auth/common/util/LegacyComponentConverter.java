/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package pl.hackshield.auth.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.mutable.MutableObject;
import pl.hackshield.auth.common.util.ComponentVisitor;

public final class LegacyComponentConverter {
    public static final Executor CACHE_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Cache Executor").setDaemon(true).build());
    private final int[] plainColorCodes = new int[32];
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final ThreadLocal<List<TextComponent>> temporaryComponents = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<TextDecoration>> temporaryDecorations = ThreadLocal.withInitial(ArrayList::new);

    public LegacyComponentConverter() {
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + j;
            int green = (i >> 1 & 1) * 170 + j;
            int blue = (i & 1) * 170 + j;
            if (i == 6) {
                red += 85;
            }
            this.plainColorCodes[i] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        }
        this.legacyComponentSerializer = LegacyComponentSerializer.legacy('\u00a7');
    }

    public String convertToLegacyText(TextComponent baseComponent) {
        return this.legacyComponentSerializer.serialize(baseComponent);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TextComponent fixNewlines(TextComponent component) {
        TextComponent.Builder builder = Component.text();
        MutableObject mutableComponent = new MutableObject((Object)Component.text(""));
        try {
            ComponentVisitor.visit(component, textComponent -> {
                for (char aChar : textComponent.content().toCharArray()) {
                    if (aChar == '\n') {
                        builder.append((Component)mutableComponent.getValue());
                        builder.append((Component)Component.newline());
                        mutableComponent.setValue((Object)Component.text(""));
                        continue;
                    }
                    mutableComponent.setValue((Object)((TextComponent)((TextComponent)mutableComponent.getValue()).content(((TextComponent)mutableComponent.getValue()).content() + aChar).decorations((Map)textComponent.decorations()).color(textComponent.color()).font(textComponent.font())));
                }
                if (!((TextComponent)mutableComponent.getValue()).content().isEmpty()) {
                    builder.append((Component)mutableComponent.getValue());
                    mutableComponent.setValue((Object)Component.text(""));
                }
            });
            TextComponent textComponent2 = (TextComponent)builder.build();
            return textComponent2;
        }
        finally {
            mutableComponent.setValue(null);
        }
    }
}

