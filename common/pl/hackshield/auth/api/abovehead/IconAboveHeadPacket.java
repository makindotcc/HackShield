/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.abovehead;

import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.abovehead.IconAboveHead;

public class IconAboveHeadPacket
implements HsPacket {
    private Operation operation;
    private IconAboveHead data;

    public IconAboveHeadPacket(Operation operation, IconAboveHead iconAboveHead) {
        this.operation = operation;
        this.data = iconAboveHead;
    }

    public IconAboveHeadPacket(int entityId) {
        this.operation = Operation.STOP;
        this.data = new IconAboveHead(entityId);
    }

    public Operation getOperation() {
        return this.operation;
    }

    public IconAboveHead getData() {
        return this.data;
    }

    public static enum Operation {
        START,
        STOP;

    }
}

