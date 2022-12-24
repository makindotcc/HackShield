/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.TypeAdapter
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 */
package net.kyori.adventure.text.serializer.bungeecord;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

interface SelfSerializable {
    public void write(JsonWriter var1) throws IOException;

    public static class AdapterFactory
    implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!SelfSerializable.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            return new SelfSerializableTypeAdapter<T>(type);
        }

        static {
            SelfSerializableTypeAdapter.class.getName();
        }

        static class SelfSerializableTypeAdapter<T>
        extends TypeAdapter<T> {
            private final TypeToken<T> type;

            SelfSerializableTypeAdapter(TypeToken<T> type) {
                this.type = type;
            }

            public void write(JsonWriter out, T value) throws IOException {
                ((SelfSerializable)value).write(out);
            }

            public T read(JsonReader in) {
                throw new UnsupportedOperationException("Cannot load values of type " + this.type.getType().getTypeName());
            }
        }
    }
}

