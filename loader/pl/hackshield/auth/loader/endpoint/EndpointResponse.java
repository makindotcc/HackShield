/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package pl.hackshield.auth.loader.endpoint;

import com.google.gson.annotations.SerializedName;

public class EndpointResponse {
    @SerializedName(value="id")
    private String id;
    @SerializedName(value="error")
    private Error error;

    public String getId() {
        return this.id;
    }

    public Error getError() {
        return this.error;
    }

    public static final class Error {
        @SerializedName(value="code")
        private int code;
        @SerializedName(value="message")
        private String message;

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

        public String toString() {
            return "Error{code=" + this.code + ", message='" + this.message + '\'' + '}';
        }
    }
}

