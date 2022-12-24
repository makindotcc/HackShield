/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  net.minecraft.server.v1_8_R3.EnumProtocolDirection
 *  net.minecraft.server.v1_8_R3.PacketDecoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.protocol.netty;

import io.netty.channel.ChannelHandler;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.PacketDecoder;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftDecoder
implements IMinecraftDecoder {
    private static final ReflectionUtil.FieldAccessor<PacketDecoder, EnumProtocolDirection> DIRECTION_FIELD = ReflectionUtil.findField(PacketDecoder.class, (String)"c");
    private final PacketDecoder instance;

    public WrappedMinecraftDecoder(ChannelHandler handler) {
        this.instance = Objects.requireNonNull((PacketDecoder)handler, "MinecraftDecoder cannot be null!");
    }

    public PacketFlow getDirection() {
        EnumProtocolDirection direction = (EnumProtocolDirection)DIRECTION_FIELD.get((Object)this.instance);
        return direction == EnumProtocolDirection.SERVERBOUND ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
    }
}

