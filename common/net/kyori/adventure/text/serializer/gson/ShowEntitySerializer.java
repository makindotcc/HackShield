/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.SerializerFactory;
import org.jetbrains.annotations.Nullable;

final class ShowEntitySerializer
extends TypeAdapter<HoverEvent.ShowEntity> {
    static final String TYPE = "type";
    static final String ID = "id";
    static final String NAME = "name";
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson) {
        return new ShowEntitySerializer(gson).nullSafe();
    }

    private ShowEntitySerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public HoverEvent.ShowEntity read(JsonReader in) throws IOException {
        in.beginObject();
        Key type = null;
        UUID id = null;
        Component name = null;
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals(TYPE)) {
                type = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals(ID)) {
                id = UUID.fromString(in.nextString());
                continue;
            }
            if (fieldName.equals(NAME)) {
                name = (Component)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
                continue;
            }
            in.skipValue();
        }
        if (type == null || id == null) {
            throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
        }
        in.endObject();
        return HoverEvent.ShowEntity.of(type, id, name);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowEntity value) throws IOException {
        out.beginObject();
        out.name(TYPE);
        this.gson.toJson((Object)value.type(), SerializerFactory.KEY_TYPE, out);
        out.name(ID);
        out.value(value.id().toString());
        @Nullable Component name = value.name();
        if (name != null) {
            out.name(NAME);
            this.gson.toJson((Object)name, SerializerFactory.COMPONENT_TYPE, out);
        }
        out.endObject();
    }
}

