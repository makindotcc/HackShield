/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.common.network;

public final class PacketFlow
extends Enum<PacketFlow> {
    public static final /* enum */ PacketFlow CLIENTBOUND = new PacketFlow();
    public static final /* enum */ PacketFlow SERVERBOUND = new PacketFlow();
    private static final /* synthetic */ PacketFlow[] $VALUES;

    public static PacketFlow[] values() {
        return (PacketFlow[])$VALUES.clone();
    }

    public static PacketFlow valueOf(String name) {
        return Enum.valueOf(PacketFlow.class, name);
    }

    private static /* synthetic */ PacketFlow[] $values() {
        return new PacketFlow[]{CLIENTBOUND, SERVERBOUND};
    }

    static {
        $VALUES = PacketFlow.$values();
    }
}

