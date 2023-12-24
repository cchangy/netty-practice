package com.cchangy.netty.quickstart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * netty服务端
 *
 * @author cchangy
 * @date 2022/01/15
 */
@Slf4j
public class NettyServer {
    public static void main(String[] args) {
        // 1. netty服务端启动器，负责组装netty组件
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                // 2. 定义事件循环组
                .group(new NioEventLoopGroup())
                // 3. 设置channel通道，服务端是NioServerSocketChannel，客户端是NioSocketChannel
                .channel(NioServerSocketChannel.class)
                // 4. 添加channel初始化器，只会执行一次
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 5. 添加字符串解码器，将ByteBuf转换为字符串
                        pipeline.addLast(new StringDecoder());
                        // 6. 添加自定义处理器
                        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                log.info(s);
                            }
                        });
                    }
                });

        // 7. 绑定端口号
        serverBootstrap.bind(8899);
    }
}
