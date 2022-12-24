/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.connection.PendingConnection
 *  net.md_5.bungee.connection.DownstreamBridge
 *  net.md_5.bungee.connection.InitialHandler
 *  net.md_5.bungee.connection.UpstreamBridge
 *  net.md_5.bungee.netty.ChannelWrapper
 *  net.md_5.bungee.netty.PipelineUtils
 *  net.md_5.bungee.protocol.MinecraftDecoder
 *  net.md_5.bungee.protocol.Varint21LengthFieldPrepender
 *  pl.hackshield.auth.common.HackShieldPlugin
 *  pl.hackshield.auth.common.util.ReflectionUtil
 */
package pl.hackshield.auth.impl.bungee.util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.UpstreamBridge;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;
import pl.hackshield.auth.common.HackShieldPlugin;
import pl.hackshield.auth.common.util.ReflectionUtil;

public final class ConnectionUtil {
    public static Field FRAME_PREPENDER;
    private static Field DOWNSTREAM_BRIDGE_CONNECTION;
    private static Field UPSTREAM_BRIDGE_CONNECTION;
    private static Field INITIAL_HANDLER_CHANNEL;
    public static Field MINECRAFT_DECODER_PROTOCOL;
    public static Field MINECRAFT_DECODER_IS_SERVER;
    private static Field MINECRAFT_DECODER_PROTOCOL_VERSION;

    private ConnectionUtil() {
    }

    public static void replaceFramePrepender(Varint21LengthFieldPrepender handler) {
        try {
            ReflectionUtil.setFinalField(null, (Field)FRAME_PREPENDER, (Object)handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ChannelWrapper getUnsafeChannelWrapper(PendingConnection pendingConnection) {
        return (ChannelWrapper)INITIAL_HANDLER_CHANNEL.get((Object)pendingConnection);
    }

    public static ChannelWrapper getChannelWrapper(PendingConnection pendingConnection) throws IllegalAccessException {
        return (ChannelWrapper)INITIAL_HANDLER_CHANNEL.get((Object)pendingConnection);
    }

    static {
        try {
            INITIAL_HANDLER_CHANNEL = InitialHandler.class.getDeclaredField("ch");
            INITIAL_HANDLER_CHANNEL.setAccessible(true);
            MINECRAFT_DECODER_PROTOCOL = MinecraftDecoder.class.getDeclaredField("protocol");
            MINECRAFT_DECODER_PROTOCOL.setAccessible(true);
            MINECRAFT_DECODER_IS_SERVER = MinecraftDecoder.class.getDeclaredField("server");
            MINECRAFT_DECODER_IS_SERVER.setAccessible(true);
            MINECRAFT_DECODER_PROTOCOL_VERSION = MinecraftDecoder.class.getDeclaredField("protocolVersion");
            MINECRAFT_DECODER_PROTOCOL_VERSION.setAccessible(true);
            FRAME_PREPENDER = PipelineUtils.class.getDeclaredField("framePrepender");
            FRAME_PREPENDER.setAccessible(true);
            DOWNSTREAM_BRIDGE_CONNECTION = DownstreamBridge.class.getDeclaredField("con");
            DOWNSTREAM_BRIDGE_CONNECTION.setAccessible(true);
            UPSTREAM_BRIDGE_CONNECTION = UpstreamBridge.class.getDeclaredField("con");
            UPSTREAM_BRIDGE_CONNECTION.setAccessible(true);
        }
        catch (Exception ex) {
            HackShieldPlugin.getInstance().getLogger().log(Level.SEVERE, "Cannot find field!", ex);
        }
    }
}

