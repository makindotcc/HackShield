/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.IndexedSerializer;

final class TextDecorationSerializer {
    static final TypeAdapter<TextDecoration> INSTANCE = IndexedSerializer.of("text decoration", TextDecoration.NAMES);

    private TextDecorationSerializer() {
    }
}

