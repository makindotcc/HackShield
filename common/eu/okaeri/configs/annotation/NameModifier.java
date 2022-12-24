/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

public final class NameModifier
extends Enum<NameModifier> {
    public static final /* enum */ NameModifier NONE = new NameModifier();
    public static final /* enum */ NameModifier TO_UPPER_CASE = new NameModifier();
    public static final /* enum */ NameModifier TO_LOWER_CASE = new NameModifier();
    private static final /* synthetic */ NameModifier[] $VALUES;

    public static NameModifier[] values() {
        return (NameModifier[])$VALUES.clone();
    }

    public static NameModifier valueOf(String name) {
        return Enum.valueOf(NameModifier.class, name);
    }

    private static /* synthetic */ NameModifier[] $values() {
        return new NameModifier[]{NONE, TO_UPPER_CASE, TO_LOWER_CASE};
    }

    static {
        $VALUES = NameModifier.$values();
    }
}

