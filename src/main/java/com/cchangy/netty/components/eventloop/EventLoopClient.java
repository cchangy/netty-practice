package com.cchangy.netty.components.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * EventLoop处理io事件客户端
 *
 * @author cchangy
 * @date 2022/01/15
 */
@Slf4j
public class EventLoopClient {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        channel.pipeline().addLast(new StringEncoder());
                    }
                });

        /**
         * 连接服务器
         * connect是一个异步非阻塞的方法，不是当前线程去执行建立连接，真正执行connect操作的是nio的线程
         */
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8899));

        // 同步发消息
        // syncWrite(channelFuture);
        // 异步发消息
        asyncWrite(channelFuture);

        // 同步关闭
        // syncClose(channelFuture);
        // 异步关闭
        asyncClose(channelFuture, group);
    }

    /**
     * 同步发消息
     *
     * @param channelFuture
     * @throws InterruptedException
     */
    private static void syncWrite(ChannelFuture channelFuture) throws InterruptedException {
        // 阻塞住当前线程、等待与服务端连接建立成功
        channelFuture.sync();

        Channel channel = channelFuture.channel();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            channel.writeAndFlush(next);
        }
    }

    /**
     * 异步发消息
     *
     * @param channelFuture
     */
    private static void asyncWrite(ChannelFuture channelFuture) {
        // 添加连接建立成功监听，当连接建立成功后会触发此事件
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().writeAndFlush("connect complete...");
            }
        });
    }

    /**
     * 同步关闭
     *
     * @param channelFuture
     * @throws InterruptedException
     */
    private static void syncClose(ChannelFuture channelFuture) throws InterruptedException {
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String next = scanner.next();
                if ("q".equals(next)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(next);
            }
        }).start();

        ChannelFuture closeFuture = channel.closeFuture();
        log.info("waiting close...");
        // 阻塞住当前线程、等待channel关闭（channel.close()）
        closeFuture.sync();
        log.info("close complete...");
    }

    private static void asyncClose(ChannelFuture channelFuture, NioEventLoopGroup group) {
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String next = scanner.next();
                if ("q".equals(next)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(next);
            }
        }).start();

        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 优雅关闭，关闭后主线程会退出
                group.shutdownGracefully();
                log.info("close complete...");
            }
        });
    }
}
