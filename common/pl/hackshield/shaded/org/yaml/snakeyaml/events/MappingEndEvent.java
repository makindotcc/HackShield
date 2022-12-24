/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.events;

import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.CollectionEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.Event;

public final class MappingEndEvent
extends CollectionEndEvent {
    public MappingEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.MappingEnd;
    }
}

