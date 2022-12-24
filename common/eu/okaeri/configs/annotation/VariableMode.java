/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.annotation;

public final class VariableMode
extends Enum<VariableMode> {
    public static final /* enum */ VariableMode RUNTIME = new VariableMode();
    public static final /* enum */ VariableMode WRITE = new VariableMode();
    private static final /* synthetic */ VariableMode[] $VALUES;

    public static VariableMode[] values() {
        return (VariableMode[])$VALUES.clone();
    }

    public static VariableMode valueOf(String name) {
        return Enum.valueOf(VariableMode.class, name);
    }

    private static /* synthetic */ VariableMode[] $values() {
        return new VariableMode[]{RUNTIME, WRITE};
    }

    static {
        $VALUES = VariableMode.$values();
    }
}

