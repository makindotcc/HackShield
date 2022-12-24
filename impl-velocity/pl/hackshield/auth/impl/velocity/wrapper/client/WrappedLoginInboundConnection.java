/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.velocity.wrapper.client;

import java.util.Objects;
import java.util.Optional;
import pl.hackshield.auth.common.util.ReflectionUtil;
import pl.hackshield.auth.impl.velocity.connection.client.InitialInboundConnection;
import pl.hackshield.auth.impl.velocity.connection.client.LoginInboundConnection;
import pl.hackshield.auth.impl.velocity.wrapper.client.WrappedInitialInboundConnection;

public final class WrappedLoginInboundConnection
implements LoginInboundConnection {
    private static final Class<?> LOGIN_INBOUND_CONNECTION_CLASS = ReflectionUtil.findClass((String)"com.velocitypowered.proxy.connection.client.LoginInboundConnection");
    private static final ReflectionUtil.FieldAccessor<Object, Object> DELEGATE_FIELD = ReflectionUtil.findField(LOGIN_INBOUND_CONNECTION_CLASS, (String)"delegate");
    private final Object instance;

    public WrappedLoginInboundConnection(Object loginInboundConnection) {
        Objects.requireNonNull(loginInboundConnection, "LoginInboundConnection cannot be null!");
        if (!LOGIN_INBOUND_CONNECTION_CLASS.isAssignableFrom(loginInboundConnection.getClass())) {
            throw new RuntimeException(loginInboundConnection.getClass().getName() + " is not LoginInboundConnection!");
        }
        this.instance = loginInboundConnection;
    }

    @Override
    public InitialInboundConnection getDelegate() {
        return Optional.ofNullable(DELEGATE_FIELD.get(this.instance)).map(WrappedInitialInboundConnection::new).orElse(null);
    }
}

