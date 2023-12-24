package com.cchangy.netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * pipeline 示例
 * <p>
 * 入站时handler执行顺序：head -> h1 -> h2 -> h3 -> tail
 * 出站时handler执行顺序: head h4 <- h5 <- h6 <- tail
 * <p>
 * ctx.fireChannelRead(msg): 调用下一个入站处理器
 * ctx.channel().write(msg): 会从尾部触发出站处理器
 * ctx.write(msg): 从当前节点触发上一个出站处理器
 *
 * @author cchangy
 * @date 2023/12/24
 */
@Slf4j
public class PipelineDemo {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1. 获取pipeline
                        ChannelPipeline pipeline = ch.pipeline();

                        // 2. 入站处理器 head -> h1 -> h2 -> h3 -> tail
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                log.info("h1");

                                // 调用下一个入站处理器
                                ctx.fireChannelRead(msg);
                            }
                        });
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                log.info("h2");
                                ctx.fireChannelRead(msg);
                            }
                        });

                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h2.5");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                log.info("h3");

                                // 会从尾部开始触发后续出站处理器的执行，也就是会执行h6这个出站处理器
                                ctx.channel().writeAndFlush("pong");

                                // 从当前节点触发上一个出站处理器，也就是会触发h2.5这个出站处理器
                                // ctx.writeAndFlush("pong");
                            }
                        });

                        // 3. 出站处理器 head <- h4 <- h5 <- h6 <- tail
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h6");

                                // 从当前节点触发上一个出站处理器的执行
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                });
        serverBootstrap.bind(8899);
    }
}
