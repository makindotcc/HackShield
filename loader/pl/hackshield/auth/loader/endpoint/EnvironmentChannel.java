/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.endpoint;

public final class EnvironmentChannel
extends Enum<EnvironmentChannel> {
    public static final /* enum */ EnvironmentChannel DEV = new EnvironmentChannel("dev");
    public static final /* enum */ EnvironmentChannel TEST = new EnvironmentChannel("test");
    public static final /* enum */ EnvironmentChannel PROD = new EnvironmentChannel("prod");
    private final String urlPartName;
    private static final /* synthetic */ EnvironmentChannel[] $VALUES;

    public static EnvironmentChannel[] values() {
        return (EnvironmentChannel[])$VALUES.clone();
    }

    public static EnvironmentChannel valueOf(String name) {
        return Enum.valueOf(EnvironmentChannel.class, name);
    }

    private EnvironmentChannel(String urlPartName) {
        this.urlPartName = urlPartName;
    }

    public String getUrlPartName() {
        return this.urlPartName;
    }

    private static /* synthetic */ EnvironmentChannel[] $values() {
        return new EnvironmentChannel[]{DEV, TEST, PROD};
    }

    static {
        $VALUES = EnvironmentChannel.$values();
    }
}

