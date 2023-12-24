package com.cchangy.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 优雅关闭
 *
 * @author cchangy
 * @date 2022/01/15
 */
@Slf4j
public class CloseChannelFuture {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8899))
                .sync()
                .channel();

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                if ("quit".equals(scanner.nextLine())) {
                    channel.close();

                    break;
                }
                channel.writeAndFlush(scanner.nextLine());
            }
        },"input").start();

        // 1. 同步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        /*log.debug("close sync...");
        closeFuture.sync();
        log.debug("closed..");*/

        // 2. 异步处理关闭
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("closed..");
                // 优雅关闭
                group.shutdownGracefully();
            }
        });
    }
}
