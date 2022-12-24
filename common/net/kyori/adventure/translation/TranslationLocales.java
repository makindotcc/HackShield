/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.translation;

import java.util.Locale;
import java.util.function.Supplier;
import net.kyori.adventure.internal.properties.AdventureProperties;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

final class TranslationLocales {
    private static final Supplier<Locale> GLOBAL;

    private TranslationLocales() {
    }

    static Locale global() {
        return GLOBAL.get();
    }

    static {
        @Nullable String property = AdventureProperties.DEFAULT_TRANSLATION_LOCALE.value();
        if (property == null || property.isEmpty()) {
            GLOBAL = () -> Locale.US;
        } else if (property.equals("system")) {
            GLOBAL = Locale::getDefault;
        } else {
            Locale locale = Translator.parseLocale(property);
            GLOBAL = () -> locale;
        }
    }
}

