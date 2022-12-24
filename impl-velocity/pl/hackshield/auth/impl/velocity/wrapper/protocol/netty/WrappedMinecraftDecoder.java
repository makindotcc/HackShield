/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.protocol.ProtocolUtils$Direction
 *  com.velocitypowered.proxy.protocol.netty.MinecraftDecoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.velocity.wrapper.protocol.netty;

import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.netty.MinecraftDecoder;
import java.util.Objects;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftDecoder
implements IMinecraftDecoder {
    private static final ReflectionUtil.FieldAccessor<MinecraftDecoder, ProtocolUtils.Direction> DIRECTION_FIELD = ReflectionUtil.findField(MinecraftDecoder.class, (String)"direction");
    private final MinecraftDecoder instance;

    public WrappedMinecraftDecoder(MinecraftDecoder decoder) {
        this.instance = Objects.requireNonNull(decoder, "MinecraftDecoder cannot be null!");
    }

    public PacketFlow getDirection() {
        ProtocolUtils.Direction direction = (ProtocolUtils.Direction)DIRECTION_FIELD.get((Object)this.instance);
        return direction == ProtocolUtils.Direction.SERVERBOUND ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
    }
}

