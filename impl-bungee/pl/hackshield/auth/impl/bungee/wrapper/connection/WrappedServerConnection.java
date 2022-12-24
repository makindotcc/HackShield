/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.ServerConnector
 *  net.md_5.bungee.UserConnection
 *  net.md_5.bungee.netty.HandlerBoss
 *  net.md_5.bungee.netty.PacketHandler
 *  pl.hackshield.auth.common.util.ReflectionUtil
 *  pl.hackshield.auth.common.util.ReflectionUtil$FieldAccessor
 */
package pl.hackshield.auth.impl.bungee.wrapper.connection;

import java.util.Optional;
import net.md_5.bungee.ServerConnector;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class WrappedServerConnection {
    private static final ReflectionUtil.FieldAccessor<ServerConnector, UserConnection> USER_CONNECTION_FIELD = ReflectionUtil.findField(ServerConnector.class, (String)"user");
    private static final ReflectionUtil.FieldAccessor<HandlerBoss, PacketHandler> PACKET_HANDLER_FIELD = ReflectionUtil.findField(HandlerBoss.class, (String)"handler");
    private final HandlerBoss connection;

    public WrappedServerConnection(HandlerBoss connection) {
        this.connection = connection;
    }

    private ServerConnector getPacketHandler() {
        return Optional.ofNullable((PacketHandler)PACKET_HANDLER_FIELD.get((Object)this.connection)).map(packetHandler -> (ServerConnector)packetHandler).orElseThrow(() -> new IllegalStateException("Not found MinecraftConnection handler!"));
    }

    public UserConnection getUserConnection() {
        return Optional.ofNullable((UserConnection)USER_CONNECTION_FIELD.get((Object)this.getPacketHandler())).orElse(null);
    }
}

