/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package pl.hackshield.auth.spigot.common.quest;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class QuestBatch {
    @SerializedName(value="gamerId")
    private UUID gamerId;
    @SerializedName(value="questId")
    private UUID questId;
    @SerializedName(value="incrementBy")
    private int incrementBy;

    public QuestBatch(UUID gamerId, UUID questId, int incrementBy) {
        this.gamerId = gamerId;
        this.questId = questId;
        this.incrementBy = incrementBy;
    }

    public UUID getGamerId() {
        return this.gamerId;
    }

    public UUID getQuestId() {
        return this.questId;
    }

    public int getIncrementBy() {
        return this.incrementBy;
    }

    public void setGamerId(UUID gamerId) {
        this.gamerId = gamerId;
    }

    public void setQuestId(UUID questId) {
        this.questId = questId;
    }

    public void setIncrementBy(int incrementBy) {
        this.incrementBy = incrementBy;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QuestBatch)) {
            return false;
        }
        QuestBatch other = (QuestBatch)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getIncrementBy() != other.getIncrementBy()) {
            return false;
        }
        UUID this$gamerId = this.getGamerId();
        UUID other$gamerId = other.getGamerId();
        if (this$gamerId == null ? other$gamerId != null : !((Object)this$gamerId).equals(other$gamerId)) {
            return false;
        }
        UUID this$questId = this.getQuestId();
        UUID other$questId = other.getQuestId();
        return !(this$questId == null ? other$questId != null : !((Object)this$questId).equals(other$questId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QuestBatch;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getIncrementBy();
        UUID $gamerId = this.getGamerId();
        result = result * 59 + ($gamerId == null ? 43 : ((Object)$gamerId).hashCode());
        UUID $questId = this.getQuestId();
        result = result * 59 + ($questId == null ? 43 : ((Object)$questId).hashCode());
        return result;
    }

    public String toString() {
        return "QuestBatch(gamerId=" + this.getGamerId() + ", questId=" + this.getQuestId() + ", incrementBy=" + this.getIncrementBy() + ")";
    }
}

