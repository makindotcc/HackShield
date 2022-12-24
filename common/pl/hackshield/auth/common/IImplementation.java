/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common;

import java.util.concurrent.TimeUnit;

public interface IImplementation {
    public void runAsync(Runnable var1);

    public void runRepeatingAsync(Runnable var1, long var2, long var4, TimeUnit var6);
}

