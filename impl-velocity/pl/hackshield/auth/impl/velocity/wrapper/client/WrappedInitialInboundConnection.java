/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.proxy.connection.MinecraftConnection
 *  com.velocitypowered.proxy.protocol.packet.Handshake
 *  io.netty.channel.ChannelHandler
 *  net.kyori.adventure.text.Component
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 *  pl.hackshield.auth.common.util.ReflectionUtil$MethodInvoker
 */
package pl.hackshield.auth.impl.velocity.wrapper.client;

import com.velocitypowered.proxy.protocol.packet.Handshake;
import io.netty.channel.ChannelHandler;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.velocity.connection.MinecraftConnection;
import pl.hackshield.auth.impl.velocity.connection.client.InitialInboundConnection;
import pl.hackshield.auth.impl.velocity.wrapper.connection.WrappedMinecraftConnection;
import pl.hackshield.auth.impl.velocity.wrapper.protocol.packet.WrappedHandshake;

public final class WrappedInitialInboundConnection
implements InitialInboundConnection {
    private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS = ReflectionUtil.findClass((String)"com.velocitypowered.proxy.connection.client.InitialInboundConnection");
    private static final ReflectionUtil.FieldAccessor<Object, com.velocitypowered.proxy.connection.MinecraftConnection> MINECRAFT_CONNECTION_FIELD;
    private static final ReflectionUtil.FieldAccessor<Object, Handshake> HANDSHAKE_FIELD;
    private static final ReflectionUtil.MethodInvoker<Object, Void> DISCONNECT_METHOD;
    private final Object instance;

    public WrappedInitialInboundConnection(Object initialInboundConnection) {
        Objects.requireNonNull(initialInboundConnection, "InitialInboundConnection cannot be null!");
        if (!INITIAL_INBOUND_CONNECTION_CLASS.isAssignableFrom(initialInboundConnection.getClass())) {
            throw new RuntimeException(initialInboundConnection.getClass().getName() + " is not InitialInboundConnection!");
        }
        this.instance = initialInboundConnection;
    }

    @Override
    public <T extends ChannelHandler> T getPipelineHandler(String name) {
        return (T)this.getConnection().getChannel().pipeline().get(name);
    }

    @Override
    public MinecraftConnection getConnection() {
        return Optional.ofNullable((com.velocitypowered.proxy.connection.MinecraftConnection)MINECRAFT_CONNECTION_FIELD.get(this.instance)).map(WrappedMinecraftConnection::new).orElse(null);
    }

    @Override
    public pl.hackshield.auth.impl.velocity.connection.protocol.packet.Handshake getHandshake() {
        return Optional.ofNullable((Handshake)HANDSHAKE_FIELD.get(this.instance)).map(WrappedHandshake::new).orElse(null);
    }

    @Override
    public void disconnect(Component reason) {
        DISCONNECT_METHOD.invoke(this.instance, new Object[]{reason});
    }

    static {
        HANDSHAKE_FIELD = ReflectionUtil.findField(INITIAL_INBOUND_CONNECTION_CLASS, (String)"handshake");
        MINECRAFT_CONNECTION_FIELD = ReflectionUtil.findField(INITIAL_INBOUND_CONNECTION_CLASS, (String)"connection");
        DISCONNECT_METHOD = ReflectionUtil.findMethod(INITIAL_INBOUND_CONNECTION_CLASS, (String)"disconnect", (Class[])new Class[]{Component.class});
    }
}

