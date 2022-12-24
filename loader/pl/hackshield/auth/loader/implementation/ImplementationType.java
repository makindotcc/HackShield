/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.implementation;

public final class ImplementationType
extends Enum<ImplementationType> {
    public static final /* enum */ ImplementationType SPIGOT = new ImplementationType();
    public static final /* enum */ ImplementationType VELOCITY = new ImplementationType();
    public static final /* enum */ ImplementationType BUNGEE = new ImplementationType();
    private static final /* synthetic */ ImplementationType[] $VALUES;

    public static ImplementationType[] values() {
        return (ImplementationType[])$VALUES.clone();
    }

    public static ImplementationType valueOf(String name) {
        return Enum.valueOf(ImplementationType.class, name);
    }

    private static /* synthetic */ ImplementationType[] $values() {
        return new ImplementationType[]{SPIGOT, VELOCITY, BUNGEE};
    }

    static {
        $VALUES = ImplementationType.$values();
    }
}

