/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  pl.hackshield.auth.loader.endpoint.EndpointManager
 *  pl.hackshield.auth.loader.endpoint.EndpointResponse
 */
package pl.hackshield.auth.spigot.common.quest.endpoint;

import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;
import pl.hackshield.auth.spigot.common.quest.QuestBatch;

public final class QuestsProgress {
    private final EndpointManager endpointManager;

    public QuestsProgress(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public QuestsProgressResponse progress(QuestsProgressRequest request) throws IOException {
        return (QuestsProgressResponse)this.endpointManager.sendRequest(this.endpointManager.newRequest("PATCH", "/v1/quests/any/progress", (Object)request), QuestsProgressResponse.class);
    }

    public static final class QuestsProgressResponse
    extends EndpointResponse {
        @SerializedName(value="data")
        private Data data;

        public Data getData() {
            return this.data;
        }

        public static final class Data {
        }
    }

    public static final class QuestsProgressRequest {
        @SerializedName(value="questUpdateParams")
        private List<QuestBatch> questUpdateParams;

        private QuestsProgressRequest(List<QuestBatch> questUpdateParams) {
            this.questUpdateParams = questUpdateParams;
        }

        public static QuestsProgressRequest from(List<QuestBatch> questUpdateParams) {
            return new QuestsProgressRequest(questUpdateParams);
        }
    }
}

