/*
 * Decompiled with CFR 0.150.
 */
package kotlin.jvm.internal;

import kotlin.Metadata;
import kotlin.SinceKotlin;

@Metadata(mv={1, 4, 0}, bv={1, 0, 3}, k=1, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b\u00c0\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\u00020\u00048\u0006X\u0087T\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\u0002R\u0016\u0010\b\u001a\u00020\u00048\u0006X\u0087T\u00a2\u0006\b\n\u0000\u0012\u0004\b\t\u0010\u0002\u00a8\u0006\n"}, d2={"Lkotlin/jvm/internal/IntCompanionObject;", "", "()V", "MAX_VALUE", "", "MIN_VALUE", "SIZE_BITS", "getSIZE_BITS$annotations", "SIZE_BYTES", "getSIZE_BYTES$annotations", "kotlin-stdlib"})
public final class IntCompanionObject {
    public static final int MIN_VALUE = Integer.MIN_VALUE;
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int SIZE_BYTES = 4;
    public static final int SIZE_BITS = 32;
    public static final IntCompanionObject INSTANCE;

    @SinceKotlin(version="1.3")
    public static /* synthetic */ void getSIZE_BYTES$annotations() {
    }

    @SinceKotlin(version="1.3")
    public static /* synthetic */ void getSIZE_BITS$annotations() {
    }

    private IntCompanionObject() {
    }

    static {
        IntCompanionObject intCompanionObject;
        INSTANCE = intCompanionObject = new IntCompanionObject();
    }
}

