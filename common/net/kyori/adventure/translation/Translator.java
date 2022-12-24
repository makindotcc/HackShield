/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Translator {
    @Nullable
    public static Locale parseLocale(@NotNull String string) {
        String[] segments = string.split("_", 3);
        int length = segments.length;
        if (length == 1) {
            return new Locale(string);
        }
        if (length == 2) {
            return new Locale(segments[0], segments[1]);
        }
        if (length == 3) {
            return new Locale(segments[0], segments[1], segments[2]);
        }
        return null;
    }

    @NotNull
    public Key name();

    @Nullable
    public MessageFormat translate(@NotNull String var1, @NotNull Locale var2);
}

