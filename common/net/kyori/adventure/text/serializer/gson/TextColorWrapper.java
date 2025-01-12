/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.TextColorSerializer;
import org.jetbrains.annotations.Nullable;

final class TextColorWrapper {
    @Nullable
    final TextColor color;
    @Nullable
    final TextDecoration decoration;
    final boolean reset;

    TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
        this.color = color;
        this.decoration = decoration;
        this.reset = reset;
    }

    static final class Serializer
    extends TypeAdapter<TextColorWrapper> {
        static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public void write(JsonWriter out, TextColorWrapper value) {
            throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
        }

        @Override
        public TextColorWrapper read(JsonReader in) throws IOException {
            boolean reset;
            String input = in.nextString();
            TextColor color = TextColorSerializer.fromString(input);
            TextDecoration decoration = TextDecoration.NAMES.value(input);
            boolean bl = reset = decoration == null && input.equals("reset");
            if (color == null && decoration == null && !reset) {
                throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
            }
            return new TextColorWrapper(color, decoration, reset);
        }
    }
}

