/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.protocol.MinecraftDecoder
 *  pl.hackshield.auth.common.network.PacketFlow
 *  pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.bungee.wrapper.protocol.netty;

import java.util.Objects;
import net.md_5.bungee.protocol.MinecraftDecoder;
import pl.hackshield.auth.common.network.PacketFlow;
import pl.hackshield.auth.common.network.connection.protocol.netty.IMinecraftDecoder;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedMinecraftDecoder
implements IMinecraftDecoder {
    private static final ReflectionUtil.FieldAccessor<MinecraftDecoder, Boolean> SERVER = ReflectionUtil.findField(MinecraftDecoder.class, (String)"server");
    private final MinecraftDecoder instance;

    public WrappedMinecraftDecoder(MinecraftDecoder decoder) {
        this.instance = Objects.requireNonNull(decoder, "MinecraftDecoder cannot be null!");
    }

    public PacketFlow getDirection() {
        return (Boolean)SERVER.get((Object)this.instance) != false ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
    }
}

