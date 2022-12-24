/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.api;

import pl.hackshield.auth.api.HackShieldAPI;

public final class HackShield {
    private static HackShieldAPI api;

    public static HackShieldAPI getApi() {
        return api;
    }

    public static void setApi(HackShieldAPI api) {
        HackShield.api = api;
    }
}

