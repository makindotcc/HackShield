/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.message;

import net.kyori.adventure.text.Component;
import pl.hackshield.auth.api.HsPacket;

public final class UniqueMessagePacket
implements HsPacket {
    private final int id;
    private final Component message;

    public UniqueMessagePacket(int id, Component message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return this.id;
    }

    public Component getMessage() {
        return this.message;
    }
}

