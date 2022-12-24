/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.google.gson.Gson
 */
package pl.hackshield.auth.loader.endpoint;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.hackshield.auth.loader.endpoint.EndpointResponse;
import pl.hackshield.auth.loader.endpoint.EnvironmentChannel;
import pl.hackshield.auth.loader.endpoint.endpoints.Servers;
import pl.hackshield.auth.loader.endpoint.endpoints.Stats;
import pl.hackshield.auth.loader.endpoint.endpoints.Updater;
import pl.hackshield.auth.loader.util.JsonUtil;

public final class EndpointManager {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private final ExecutorService executor;
    private final OkHttpClient httpClient;
    private final String apiBaseUrl;
    private final Servers servers;
    private final Stats stats;
    private final Updater updater;
    private String serverSecret;

    public EndpointManager(EnvironmentChannel environmentChannel, String serverSecret, boolean allowAsyncRequests) {
        this.serverSecret = serverSecret;
        this.apiBaseUrl = "https://gamer-api." + environmentChannel.getUrlPartName() + ".hackshield.pl";
        this.executor = allowAsyncRequests ? Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("HackShield API Thread #%d").build()) : null;
        this.httpClient = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(true).build();
        this.servers = new Servers(this);
        this.stats = new Stats(this);
        this.updater = new Updater(this);
    }

    public Request newRequest(String method, String endpoint, Object body) {
        return new Request.Builder().url(this.apiBaseUrl + endpoint).header("Authorization", "Bearer " + this.serverSecret).method(method, RequestBody.create(JsonUtil.toJson(body), JSON_MEDIA_TYPE)).build();
    }

    public Request newRequest(String endpoint) {
        return new Request.Builder().url(this.apiBaseUrl + endpoint).header("Authorization", "Bearer " + this.serverSecret).get().build();
    }

    public Request newUnauthenticatedRequest(String endpoint) {
        return new Request.Builder().url(this.apiBaseUrl + endpoint).get().build();
    }

    public <T extends EndpointResponse> CompletableFuture<T> sendRequestAsync(Request request, Class<? extends EndpointResponse> responseClass) {
        if (this.executor == null) {
            throw new IllegalArgumentException("This HackShieldPlugin doesn't support Async requests!");
        }
        CompletableFuture completableFuture = new CompletableFuture();
        this.executor.execute(() -> {
            try {
                completableFuture.complete(this.sendRequest(request, responseClass));
            }
            catch (IOException ex) {
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public <T extends EndpointResponse> T sendRequest(Request request, Class<? extends EndpointResponse> responseClass) throws IOException {
        try (Response response = this.httpClient.newCall(request).execute();){
            EndpointResponse endpointResponse = (EndpointResponse)JsonUtil.fromJson(response.body().string(), responseClass);
            return (T)endpointResponse;
        }
    }

    public <T extends EndpointResponse> T sendRequestWithCustomGson(Gson gson, Request request, Class<? extends EndpointResponse> responseClass) throws IOException {
        try (Response response = this.httpClient.newCall(request).execute();){
            EndpointResponse endpointResponse = (EndpointResponse)gson.fromJson(response.body().string(), responseClass);
            return (T)endpointResponse;
        }
    }

    public String getServerSecret() {
        return this.serverSecret;
    }

    public Servers getServers() {
        return this.servers;
    }

    public Stats getStats() {
        return this.stats;
    }

    public Updater getUpdater() {
        return this.updater;
    }
}

