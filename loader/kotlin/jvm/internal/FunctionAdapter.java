/*
 * Decompiled with CFR 0.150.
 */
package kotlin.jvm.internal;

import kotlin.Function;
import kotlin.SinceKotlin;

@SinceKotlin(version="1.4")
public interface FunctionAdapter {
    public Function<?> getFunctionDelegate();
}

