/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.shaded.org.yaml.snakeyaml.parser;

import pl.hackshield.shaded.org.yaml.snakeyaml.events.Event;

public interface Parser {
    public boolean checkEvent(Event.ID var1);

    public Event peekEvent();

    public Event getEvent();
}

