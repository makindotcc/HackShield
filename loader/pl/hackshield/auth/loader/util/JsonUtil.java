/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package pl.hackshield.auth.loader.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JsonUtil {
    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static <T> T fromJson(String json, Type type) {
        return (T)GSON.fromJson(json, type);
    }
}

