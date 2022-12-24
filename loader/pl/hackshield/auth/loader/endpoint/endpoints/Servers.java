/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package pl.hackshield.auth.loader.endpoint.endpoints;

import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.UUID;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;

public final class Servers {
    public static final String SLOT_LIMIT_REACHED = "Slot Limit Reached";
    public static final String UPDATE_REQUIRED = "Update Required";
    private final EndpointManager endpointManager;

    public Servers(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public CheckJoinTokenResponse check(CheckJoinTokenRequest request) throws IOException {
        return (CheckJoinTokenResponse)this.endpointManager.sendRequest(this.endpointManager.newRequest("POST", "/v1/servers/join-requests/check", request), CheckJoinTokenResponse.class);
    }

    public static final class CheckJoinTokenResponse
    extends EndpointResponse {
        @SerializedName(value="data")
        private Data data;

        public Data getData() {
            return this.data;
        }

        public static final class Data {
            @SerializedName(value="joinRequestInfo")
            private JoinRequestInfo joinRequestInfo;

            public JoinRequestInfo getJoinRequestInfo() {
                return this.joinRequestInfo;
            }

            public static class JoinRequestInfo {
                @SerializedName(value="gamerId")
                private UUID gamerId;
                @SerializedName(value="currentBattlePassIsPremium")
                private boolean currentBattlePassPremium;

                public UUID getGamerId() {
                    return this.gamerId;
                }

                public boolean isCurrentBattlePassPremium() {
                    return this.currentBattlePassPremium;
                }
            }
        }
    }

    public static final class CheckJoinTokenRequest {
        @SerializedName(value="token")
        private String token;

        private CheckJoinTokenRequest(String token) {
            this.token = token;
        }

        public static CheckJoinTokenRequest from(String token) {
            return new CheckJoinTokenRequest(token);
        }
    }
}

