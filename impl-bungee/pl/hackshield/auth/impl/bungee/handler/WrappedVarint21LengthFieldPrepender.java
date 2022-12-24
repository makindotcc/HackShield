/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelPipeline
 *  net.md_5.bungee.protocol.Varint21LengthFieldPrepender
 */
package pl.hackshield.auth.impl.bungee.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;
import pl.hackshield.auth.impl.bungee.Implementation;
import pl.hackshield.auth.impl.bungee.util.ConnectionUtil;

@ChannelHandler.Sharable
public class WrappedVarint21LengthFieldPrepender
extends Varint21LengthFieldPrepender {
    private final Implementation plugin;
    private final BiConsumer<Channel, Boolean> injectChannelSupplier;

    public WrappedVarint21LengthFieldPrepender(Implementation plugin, BiConsumer<Channel, Boolean> injectChannelSupplier) {
        this.plugin = plugin;
        this.injectChannelSupplier = injectChannelSupplier;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.tryInjectChannelInitializers(ctx.pipeline());
        super.handlerAdded(ctx);
    }

    private void tryInjectChannelInitializers(ChannelPipeline pipeline) {
        ChannelHandler handler = pipeline.get((String)pipeline.names().get(0));
        if (!handler.getClass().getName().startsWith("net.md_5.bungee.UserConnection")) {
            return;
        }
        ChannelInitializer<Channel> endInitProtocol = new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) {
                channel.eventLoop().submit(() -> {
                    try {
                        ChannelHandler packetDecoder = channel.pipeline().get("packet-decoder");
                        boolean server = (Boolean)ConnectionUtil.MINECRAFT_DECODER_IS_SERVER.get((Object)packetDecoder);
                        if (server) {
                            WrappedVarint21LengthFieldPrepender.this.plugin.getLogger().info("Server channel, cancelling...");
                            return;
                        }
                        WrappedVarint21LengthFieldPrepender.this.injectChannelSupplier.accept(channel, false);
                    }
                    catch (Exception e) {
                        WrappedVarint21LengthFieldPrepender.this.plugin.getLogger().log(Level.SEVERE, "Error while injecting channel!", e);
                    }
                });
            }
        };
        ChannelInitializer<Channel> beginInitProtocol = new ChannelInitializer<Channel>((ChannelInitializer)endInitProtocol){
            final /* synthetic */ ChannelInitializer val$endInitProtocol;
            {
                this.val$endInitProtocol = channelInitializer;
            }

            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(new ChannelHandler[]{this.val$endInitProtocol});
            }
        };
        pipeline.addLast(new ChannelHandler[]{beginInitProtocol});
    }
}

