/*
 * Decompiled with CFR 0.150.
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.IndexedSerializer;

final class ClickEventActionSerializer {
    static final TypeAdapter<ClickEvent.Action> INSTANCE = IndexedSerializer.of("click action", ClickEvent.Action.NAMES);

    private ClickEventActionSerializer() {
    }
}

