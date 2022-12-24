/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.IndexedSerializer;

final class HoverEventActionSerializer {
    static final TypeAdapter<HoverEvent.Action<?>> INSTANCE = IndexedSerializer.of("hover action", HoverEvent.Action.NAMES);

    private HoverEventActionSerializer() {
    }
}

