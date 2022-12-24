/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.protocol.ProtocolUtils$Direction
 *  com.velocitypowered.proxy.protocol.netty.MinecraftEncoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.velocity.wrapper.protocol.netty;

import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.netty.MinecraftEncoder;
import java.util.Objects;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftEncoder
implements IMinecraftEncoder {
    private static final ReflectionUtil.FieldAccessor<MinecraftEncoder, ProtocolUtils.Direction> DIRECTION_FIELD = ReflectionUtil.findField(MinecraftEncoder.class, (String)"direction");
    private final MinecraftEncoder instance;

    public WrappedMinecraftEncoder(MinecraftEncoder decoder) {
        this.instance = Objects.requireNonNull(decoder, "MinecraftEncoder cannot be null!");
    }

    public PacketFlow getDirection() {
        ProtocolUtils.Direction direction = (ProtocolUtils.Direction)DIRECTION_FIELD.get((Object)this.instance);
        return direction == ProtocolUtils.Direction.SERVERBOUND ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
    }
}

