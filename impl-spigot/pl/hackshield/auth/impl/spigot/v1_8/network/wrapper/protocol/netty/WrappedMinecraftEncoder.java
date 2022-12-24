/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  net.minecraft.server.v1_8_R3.EnumProtocolDirection
 *  net.minecraft.server.v1_8_R3.PacketEncoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.spigot.v1_8.network.wrapper.protocol.netty;

import io.netty.channel.ChannelHandler;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.PacketEncoder;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftEncoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftEncoder
implements IMinecraftEncoder {
    private static final ReflectionUtil.FieldAccessor<PacketEncoder, EnumProtocolDirection> DIRECTION_FIELD = ReflectionUtil.findField(PacketEncoder.class, (String)"c");
    private final PacketEncoder instance;

    public WrappedMinecraftEncoder(ChannelHandler decoder) {
        this.instance = Objects.requireNonNull((PacketEncoder)decoder, "MinecraftEncoder cannot be null!");
    }

    public PacketFlow getDirection() {
        EnumProtocolDirection direction = (EnumProtocolDirection)DIRECTION_FIELD.get((Object)this.instance);
        return direction == EnumProtocolDirection.SERVERBOUND ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
    }
}

