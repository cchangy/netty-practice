package com.cchangy.netty.nio.socket;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 阻塞模式下的服务端
 *
 * ServerSocketChannel.accept方法会在没有连接建立时让线程阻塞住
 * SocketChannel.read方法会在没有可读数据时让线程阻塞住
 *
 * 结论：单线程下，阻塞模式会无法正常运行，一个连接建立后第一次发送数据能正常读取，
 *      而这个连接再次发送数据时，将不能正常读取，因为阻塞在了accept方法上
 *
 * @author cchangy
 * @date 2022/01/03
 */
@Slf4j
public class BlockingServer {

    public static void main(String[] args)throws Exception {
        // 1. 创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        // 2. 创建服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8899));
        // 4. 定义channel集合，用来存放连接上的客户端通道
        List<SocketChannel> socketChannelList = new ArrayList<>();
        while (true) {
            /**
             * 5. 与客户端建立连接
             * 阻塞模式下，当没有客户端建立连接时，accept方法会阻塞在此处
             */
            log.debug("connecting...");
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.debug("connected... {}", socketChannel);
            socketChannelList.add(socketChannel);

            for (SocketChannel channel : socketChannelList) {
                // 6. 读取客户端发送过来的数据，阻塞模式下，当没有数据可读时，会阻塞在此处
                log.debug("read before... {}", channel);
                channel.read(byteBuffer);
                byteBuffer.flip();
                ByteBufferUtil.debugRead(byteBuffer);
                byteBuffer.clear();
                log.debug("read after... {}", channel);
            }
        }
    }
}
