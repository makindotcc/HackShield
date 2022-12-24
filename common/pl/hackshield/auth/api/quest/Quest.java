/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package pl.hackshield.auth.api.quest;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import pl.hackshield.auth.api.quest.condition.base.Condition;

public class Quest {
    @SerializedName(value="id")
    private UUID id;
    @SerializedName(value="name")
    private String name;
    @SerializedName(value="condition")
    private Condition condition;
    @SerializedName(value="reset-condition")
    private Condition resetCondition;
    @SerializedName(value="increment-by")
    private int incrementBy;
    @SerializedName(value="target")
    private int target;
    @SerializedName(value="period")
    private long period;
    private final transient Map<UUID, AtomicInteger> batch = Maps.newConcurrentMap();

    public int getIncrementBy() {
        return this.incrementBy;
    }

    public Map<UUID, AtomicInteger> getBatch() {
        return this.batch;
    }

    public UUID getId() {
        return this.id;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Condition getResetCondition() {
        return this.resetCondition;
    }
}

