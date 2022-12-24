/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.annotations.SerializedName
 *  pl.hackshield.auth.api.quest.Quest
 *  pl.hackshield.auth.loader.endpoint.EndpointManager
 *  pl.hackshield.auth.loader.endpoint.EndpointResponse
 */
package pl.hackshield.auth.spigot.common.quest.endpoint;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;
import pl.hackshield.auth.api.quest.Quest;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;

public final class Quests {
    private final EndpointManager endpointManager;

    public Quests(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public QuestsResponse quests(Gson gson) throws IOException {
        return (QuestsResponse)this.endpointManager.sendRequestWithCustomGson(gson, this.endpointManager.newRequest("/v1/servers/me/quests"), QuestsResponse.class);
    }

    public static final class QuestsResponse
    extends EndpointResponse {
        @SerializedName(value="data")
        private Data data;

        public Data getData() {
            return this.data;
        }

        public static final class Data {
            @SerializedName(value="questSerieses")
            private List<Quest> questSerieses;

            public List<Quest> getQuestSerieses() {
                return this.questSerieses;
            }
        }
    }
}

