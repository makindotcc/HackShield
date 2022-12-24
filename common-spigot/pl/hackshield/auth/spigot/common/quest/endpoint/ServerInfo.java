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
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;

public final class ServerInfo {
    private final EndpointManager endpointManager;

    public ServerInfo(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public ServerInfoResponse serverInfo() throws IOException {
        return (ServerInfoResponse)this.endpointManager.sendRequest(this.endpointManager.newRequest("/v1/servers/me"), ServerInfoResponse.class);
    }

    public static final class ServerInfoResponse
    extends EndpointResponse {
        @SerializedName(value="data")
        private Data data;

        public Data getData() {
            return this.data;
        }

        public static final class Data {
            @SerializedName(value="server")
            private Server server;

            public Server getServer() {
                return this.server;
            }

            public static final class Server {
                @SerializedName(value="isQuestEnabled")
                private boolean isQuestEnabled;

                public boolean isQuestEnabled() {
                    return this.isQuestEnabled;
                }
            }
        }
    }
}

