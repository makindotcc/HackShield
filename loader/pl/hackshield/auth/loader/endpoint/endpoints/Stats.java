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

public final class Stats {
    private final EndpointManager endpointManager;

    public Stats(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    public ServerStatsResponse update(ServerStatsRequest request) throws IOException {
        return (ServerStatsResponse)this.endpointManager.sendRequest(this.endpointManager.newRequest("PUT", "/v1/servers/me/stats", request), ServerStatsResponse.class);
    }

    public static final class ServerStatsResponse
    extends EndpointResponse {
    }

    public static final class ServerStatsRequest {
        @SerializedName(value="cpus")
        private int cpus;
        @SerializedName(value="totalRam")
        private int totalRam;
        @SerializedName(value="freeRam")
        private int freeRam;
        @SerializedName(value="totalDiskSpace")
        private int totalDiskSpace;
        @SerializedName(value="freeDiskSpace")
        private int freeDiskSpace;
        @SerializedName(value="version")
        private String version;

        private ServerStatsRequest(int cpus, int totalRam, int freeRam, int totalDiskSpace, int freeDiskSpace, String version) {
            this.cpus = cpus;
            this.totalRam = totalRam;
            this.freeRam = freeRam;
            this.totalDiskSpace = totalDiskSpace;
            this.freeDiskSpace = freeDiskSpace;
            this.version = version;
        }

        public static ServerStatsRequest from(int cpus, int totalRam, int freeRam, int totalDiskSpace, int freeDiskSpace, String version) {
            return new ServerStatsRequest(cpus, totalRam, freeRam, totalDiskSpace, freeDiskSpace, version);
        }
    }
}

