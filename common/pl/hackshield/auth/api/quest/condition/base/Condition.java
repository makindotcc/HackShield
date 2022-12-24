/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.quest.condition.base;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public abstract class Condition {
    @SerializedName(value="event")
    private String event;
    @SerializedName(value="world")
    private String targetWorld;

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return this.event;
    }

    public boolean isValid(Condition event) {
        return this.targetWorld == null || Objects.equals(this.targetWorld, event.targetWorld);
    }
}

