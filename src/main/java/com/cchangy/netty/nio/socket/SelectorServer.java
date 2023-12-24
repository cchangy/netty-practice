package com.cchangy.netty.nio.socket;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Selector模式下的服务端
 * <p>
 * 使用selector监控多个channel的事件，当有事件发生时才让线程去处理。避免非阻塞模式下频繁占用CPU
 * 步骤：
 * 1. 创建selector：Selector.open()
 * 2. 将需要监控的channel注册到selector：selectKey.register()
 * 3. 绑定事件：selectKey.interestOps()
 * <p>
 * 事件类型：
 * 1. OP_CONNECT：客户端连接成功事件
 * 2. OP_ACCEPT：服务端连接成功事件
 * 3. OP_READ：可读事件
 * 4. OP_WRITE：可写事件
 *
 * @author cchangy
 * @date 2022/01/03
 */
@Slf4j
public class SelectorServer {

    public static void main(String[] args) throws Exception {
        // 1. 创建ByteBuffer
        // ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        // 2. 创建服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3. 将serverSocketChannel设置为非阻塞模式，默认是阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 4. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8899));
        // 5. 创建selector
        Selector selector = Selector.open();
        /**
         * 6. 将serverSocketChannel注册到selector上
         * 可直接通过构造方法指定事件类型，也可以通过interestOps方法来指定
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 当没有事件发生时，线程会阻塞，有事件时线程会恢复运行
            selector.select();
            // 7. 获取selectedKeys并遍历
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            log.debug("有事件发生了,keySize: {}", selectionKeys.size());

            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 8. 根据事件类型来处理事件
                if (selectionKey.isAcceptable()) { // OP_ACCEPT 事件
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    // 9. 将socketChannel置为非阻塞模式，默认是阻塞模式
                    socketChannel.configureBlocking(false);
                    // 10. 注册可读事件
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(4));
                    log.debug("connected... {}", socketChannel);
                } else if (selectionKey.isReadable()) { // OP_READ 可读事件
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    log.debug("read before... {}", channel);
                    // 获取register时设置的attachment
                    ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                    // 11. 读取客户端发送过来的数据
                    while (channel.read(byteBuffer) > 0) {
                        // 代表需要扩容
                        if (byteBuffer.position() == byteBuffer.limit()) {
                            ByteBuffer newByteBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                            // 切换读
                            byteBuffer.flip();
                            newByteBuffer.put(byteBuffer);

                            // 关联新的attachment
                            selectionKey.attach(newByteBuffer);
                            byteBuffer = (ByteBuffer) selectionKey.attachment();
                        }
                    }
                    log.debug("read after... {}", channel);
                    ByteBufferUtil.debugAll(byteBuffer);
                    // 全部读取完后清除掉byteBuffer
                    byteBuffer.clear();
                    // 重置attachment
                    selectionKey.attach(ByteBuffer.allocate(4));
                }
                // 非常重要：每处理完一个事件，需要将此事件从集合中移除
                iterator.remove();
            }
        }
    }
}
