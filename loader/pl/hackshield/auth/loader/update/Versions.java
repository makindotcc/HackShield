/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package pl.hackshield.auth.loader.update;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Map;

public final class Versions
implements Serializable {
    @SerializedName(value="modules")
    private Map<String, FileInfo> modules;
    @SerializedName(value="libraries")
    private Map<String, FileInfo> libraries;

    public Map<String, FileInfo> getModules() {
        return this.modules;
    }

    public Map<String, FileInfo> getLibraries() {
        return this.libraries;
    }

    public static final class FileInfo {
        @SerializedName(value="fileName")
        private String fileName;
        @SerializedName(value="hash")
        private String hash;
        @SerializedName(value="date")
        private long date;

        public String getFileName() {
            return this.fileName;
        }

        public String getHash() {
            return this.hash;
        }

        public long getDate() {
            return this.date;
        }
    }
}

