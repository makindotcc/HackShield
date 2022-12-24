/*
 * Decompiled with CFR 0.150.
 */
package okhttp3.internal.connection;

import java.net.Proxy;
import kotlin.Metadata;

@Metadata(mv={1, 4, 0}, bv={1, 0, 3}, k=3)
public final class RealConnection$WhenMappings {
    public static final /* synthetic */ int[] $EnumSwitchMapping$0;

    static /* synthetic */ {
        $EnumSwitchMapping$0 = new int[Proxy.Type.values().length];
        RealConnection$WhenMappings.$EnumSwitchMapping$0[Proxy.Type.DIRECT.ordinal()] = 1;
        RealConnection$WhenMappings.$EnumSwitchMapping$0[Proxy.Type.HTTP.ordinal()] = 2;
    }
}

