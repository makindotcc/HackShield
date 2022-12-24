/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.channel.ChannelInitializer
 *  org.apache.commons.lang3.reflect.FieldUtils
 */
package pl.hackshield.auth.spigot.common.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.apache.commons.lang3.reflect.FieldUtils;
import pl.hackshield.auth.spigot.common.SpigotCommon;
import pl.hackshield.auth.spigot.common.utils.Reflections;

public class ChannelInjector {
    private final Object networkManagers;
    private final ChannelInboundHandlerAdapter adapter;
    private final Consumer<Channel> injectChannelSupplier;
    private final SpigotCommon plugin;

    public ChannelInjector(SpigotCommon plugin, String minecraftServerClassName, String serverConnectionFieldName, String networkManagerFieldName, String listeningChannelsFieldName, Consumer<Channel> injectChannelSupplier) throws IllegalAccessException {
        this.plugin = plugin;
        this.injectChannelSupplier = injectChannelSupplier;
        Class<?> minecraftServerClass = Reflections.getClass(minecraftServerClassName);
        Field serverConnectionField = Reflections.getJavaField(minecraftServerClass, serverConnectionFieldName);
        Reflections.MethodInvoker getServerMethod = Reflections.getMethod(minecraftServerClass, "getServer", false, new Class[0]);
        Object serverConnectionInstance = serverConnectionField.get(getServerMethod.invoke(null, new Object[0]));
        this.networkManagers = FieldUtils.readDeclaredField((Object)serverConnectionInstance, (String)networkManagerFieldName, (boolean)true);
        this.adapter = this.createServerChannelHandler();
        List listeningChannels = (List)FieldUtils.readDeclaredField((Object)serverConnectionInstance, (String)listeningChannelsFieldName, (boolean)true);
        listeningChannels.forEach(channelFuture -> channelFuture.channel().pipeline().addFirst(new ChannelHandler[]{this.adapter}));
    }

    private ChannelInboundHandlerAdapter createServerChannelHandler() {
        ChannelInitializer<Channel> endInitProtocol = new ChannelInitializer<Channel>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            protected void initChannel(Channel channel) throws Exception {
                try {
                    Object object = ChannelInjector.this.networkManagers;
                    synchronized (object) {
                        channel.eventLoop().submit(() -> ChannelInjector.this.injectChannelSupplier.accept(channel));
                    }
                }
                catch (Exception e) {
                    ChannelInjector.this.plugin.getLogger().log(Level.SEVERE, "Error while injecting channel!", e);
                }
            }
        };
        ChannelInitializer<Channel> beginInitProtocol = new ChannelInitializer<Channel>((ChannelInitializer)endInitProtocol){
            final /* synthetic */ ChannelInitializer val$endInitProtocol;
            {
                this.val$endInitProtocol = channelInitializer;
            }

            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(new ChannelHandler[]{this.val$endInitProtocol});
            }
        };
        return new ChannelInboundHandlerAdapter((ChannelInitializer)beginInitProtocol){
            final /* synthetic */ ChannelInitializer val$beginInitProtocol;
            {
                this.val$beginInitProtocol = channelInitializer;
            }

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Channel channel = (Channel)msg;
                channel.pipeline().addFirst(new ChannelHandler[]{this.val$beginInitProtocol});
                ctx.fireChannelRead(msg);
            }

            public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                throwable.printStackTrace();
                super.exceptionCaught(channelHandlerContext, throwable);
            }
        };
    }
}

