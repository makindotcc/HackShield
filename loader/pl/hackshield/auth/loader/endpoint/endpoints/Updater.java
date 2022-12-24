/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package pl.hackshield.auth.loader.endpoint.endpoints;

import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import pl.hackshield.auth.loader.endpoint.EndpointManager;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;

public final class Updater {
    private final EndpointManager endpointManager;

    public Updater(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public IndexMirrorsResponse indexMirrors() throws IOException {
        return (IndexMirrorsResponse)this.endpointManager.sendRequest(this.endpointManager.newUnauthenticatedRequest("/v1/updater/mirrors"), IndexMirrorsResponse.class);
    }

    public static final class IndexMirrorsResponse
    extends EndpointResponse {
        @SerializedName(value="data")
        private Data data;

        public Data getData() {
            return this.data;
        }

        public static final class Data {
            @SerializedName(value="mirrorUrls")
            private String[] mirrorUrls;

            public String[] getMirrorUrls() {
                return this.mirrorUrls;
            }
        }
    }
}

