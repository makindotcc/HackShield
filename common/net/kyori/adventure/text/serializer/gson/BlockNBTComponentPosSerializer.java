/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.BlockNBTComponent;

final class BlockNBTComponentPosSerializer
extends TypeAdapter<BlockNBTComponent.Pos> {
    static final TypeAdapter<BlockNBTComponent.Pos> INSTANCE = new BlockNBTComponentPosSerializer().nullSafe();

    private BlockNBTComponentPosSerializer() {
    }

    @Override
    public BlockNBTComponent.Pos read(JsonReader in) throws IOException {
        String string = in.nextString();
        try {
            return BlockNBTComponent.Pos.fromString(string);
        }
        catch (IllegalArgumentException ex) {
            throw new JsonParseException("Don't know how to turn " + string + " into a Position");
        }
    }

    @Override
    public void write(JsonWriter out, BlockNBTComponent.Pos value) throws IOException {
        out.value(value.asString());
    }
}

