/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static int toTicks(long time, TimeUnit unit) {
        if (time == 0L) {
            return 0;
        }
        return (int)(unit.toMillis(time) / 50L);
    }
}

