/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.events;

import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.CollectionStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.Event;

public final class SequenceStartEvent
extends CollectionStartEvent {
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Deprecated
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle) {
        this(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(flowStyle));
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.SequenceStart;
    }
}

