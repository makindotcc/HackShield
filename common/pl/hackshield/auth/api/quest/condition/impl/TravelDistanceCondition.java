/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api.quest.condition.impl;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;
import pl.hackshield.auth.api.quest.condition.base.Condition;

public class TravelDistanceCondition
extends Condition {
    @SerializedName(value="fly")
    private Boolean fly;
    @SerializedName(value="ride")
    private List<String> ride;

    public Boolean getFly() {
        return this.fly;
    }

    public List<String> getRide() {
        return this.ride;
    }

    @Override
    public boolean isValid(Condition situation) {
        if (!super.isValid(situation)) {
            return false;
        }
        if (!(situation instanceof TravelDistanceCondition)) {
            return false;
        }
        TravelDistanceCondition s = (TravelDistanceCondition)situation;
        if (this.fly != null && !Objects.equals(this.fly, s.getFly())) {
            return false;
        }
        return this.ride == null || s.getRide() != null && this.getRide().containsAll(s.getRide());
    }
}

