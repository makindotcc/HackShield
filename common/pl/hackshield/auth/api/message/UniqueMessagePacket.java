/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.message;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import pl.hackshield.auth.api.HsPacket;

public final class UniqueMessagePacket
implements HsPacket {
    private final Key id;
    private final Component message;

    public UniqueMessagePacket(Key id, Component message) {
        this.id = id;
        this.message = message;
    }

    public Key getId() {
        return this.id;
    }

    public Component getMessage() {
        return this.message;
    }
}

