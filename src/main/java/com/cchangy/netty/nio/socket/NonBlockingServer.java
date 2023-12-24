package com.cchangy.netty.nio.socket;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 非阻塞模式下的服务端
 * <p>
 * 通过socketChannel.configureBlocking(false)设置为非阻塞模式
 * <p>
 * 非阻塞模式下，线程不会因为没有客户端连接，或者没有数据可以读取而使线程暂停
 * ServerSocketChannel.accept在没有连接建立时，会返回null，程序会继续运行
 * SocketChannel.read在没有可读数据时，会返回0，程序也会继续运行
 * <p>
 * 但非阻塞模式下，即使没有新连接建立，依旧可以读取数据，但由于线程仍然在不断运行，白白浪费了CPU
 *
 * @author cchangy
 * @date 2022/01/03
 */
@Slf4j
public class NonBlockingServer {

    public static void main(String[] args) throws Exception {
        // 1. 创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        // 2. 创建服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3. 将serverSocketChannel设置为非阻塞模式，默认是阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 4. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8899));
        // 5. 定义channel集合，用来存放连接上的客户端通道
        List<SocketChannel> socketChannelList = new ArrayList<>();
        while (true) {
            log.debug("connecting...");
            /**
             * 6. 与客户端建立连接
             * 非阻塞模式下，当没有客户端建立连接时，accept方法会返回空
             */
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                // 7. 将socketChannel置为非阻塞模式，默认是阻塞模式
                socketChannel.configureBlocking(false);
                log.debug("connected... {}", socketChannel);
                socketChannelList.add(socketChannel);
            }

            for (SocketChannel channel : socketChannelList) {
                // 8. 读取客户端发送过来的数据，非阻塞模式下，当没有数据可读时，此处会返回0
                if (channel.read(byteBuffer) == 0) {
                    continue;
                }

                log.debug("read before... {}", channel);
                byteBuffer.flip();
                ByteBufferUtil.debugRead(byteBuffer);
                byteBuffer.clear();
                log.debug("read after... {}", channel);
            }

            TimeUnit.SECONDS.sleep(5);
        }
    }
}
