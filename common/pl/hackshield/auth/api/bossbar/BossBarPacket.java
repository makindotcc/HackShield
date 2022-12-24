/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.bossbar;

import pl.hackshield.auth.api.HsPacket;
import pl.hackshield.auth.api.bossbar.BossInfo;

public final class BossBarPacket
implements HsPacket {
    private Operation operation;
    private BossInfo data;

    public BossBarPacket(Operation operation, BossInfo data) {
        this.operation = operation;
        this.data = data;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public BossInfo getData() {
        return this.data;
    }

    public static enum Operation {
        ADD,
        REMOVE,
        UPDATE_PCT,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;

    }
}

