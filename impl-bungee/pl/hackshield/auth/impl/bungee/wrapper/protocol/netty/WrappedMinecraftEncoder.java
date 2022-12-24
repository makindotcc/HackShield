/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.protocol.MinecraftEncoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.bungee.wrapper.protocol.netty;

import java.util.Objects;
import net.md_5.bungee.protocol.MinecraftEncoder;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftEncoder
implements IMinecraftEncoder {
    private static final ReflectionUtil.FieldAccessor<MinecraftEncoder, Boolean> SERVER = ReflectionUtil.findField(MinecraftEncoder.class, (String)"server");
    private final MinecraftEncoder instance;

    public WrappedMinecraftEncoder(MinecraftEncoder decoder) {
        this.instance = Objects.requireNonNull(decoder, "MinecraftEncoder cannot be null!");
    }

    public PacketFlow getDirection() {
        return (Boolean)SERVER.get((Object)this.instance) != false ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND;
    }
}

