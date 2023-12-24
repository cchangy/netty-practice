package com.cchangy.netty.quickstart;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * netty客户端
 *
 * @author cchangy
 * @date 2022/01/15
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        // 1. netty客户端启动器
        Bootstrap bootstrap = new Bootstrap()
                // 2. 定义事件循环组
                .group(new NioEventLoopGroup())
                // 3. 设置channel通道，客户端是NioSocketChannel，服务端是NioServerSocketChannel
                .channel(NioSocketChannel.class)
                // 4. 设置channel初始化器，只会执行一次
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        // 5. 添加字符编码器，将字符串转换为ByteBuf
                        pipeline.addLast(new StringEncoder());
                    }
                });

        // 5. 连接服务端，是异步非阻塞的，由nio线程去执行与服务端连接的操作
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8899));

        /**使用addListener方法异步处理结果*/
        channelFuture.addListener(new ChannelFutureListener() {
            // 在nio线程与服务端建立连接之后，会调用此方法
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("addListener channel: {}", channelFuture.channel());
                channelFuture.channel().writeAndFlush("netty addListener hello world");
            }
        });

        /**使用sync方法来同步获取结果*/
        // 6. 阻塞当前线程，直到nio线程与服务器端建立连接完毕
        channelFuture.sync();
        // 7. 向服务端发送数据
        channelFuture.channel().writeAndFlush("netty sync hello world");
        log.debug("sync channel: {}", channelFuture.channel());

    }
}
