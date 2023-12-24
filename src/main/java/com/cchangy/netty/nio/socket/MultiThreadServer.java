package com.cchangy.netty.nio.socket;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程服务端
 *
 * @author cchangy
 * @date 2022/01/03
 */
@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8899));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);

        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(i);
        }

        AtomicInteger index = new AtomicInteger();
        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                try {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        log.debug("connected... {}", socketChannel);
                        log.debug("register before... {}",socketChannel);
                        workers[index.incrementAndGet() % workers.length].register(socketChannel);
                        log.debug("register after... {}",socketChannel);
                    }

                    iterator.remove();
                } catch (Exception e) {
                    log.error("error: ", e);
                }
            }
        }
    }

    static class Worker implements Runnable {
        private Selector selector;
        private int index;
        private volatile boolean isStart = false;

        public Worker(int index) {
            this.index = index;
        }

        public void register(SocketChannel channel) throws Exception {
            if (isStart) {
                return;
            }

            selector = Selector.open();
            new Thread(this, "worker-" + index).start();
            isStart = true;

            selector.wakeup();
            channel.register(selector, SelectionKey.OP_READ, null);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            socketChannel.read(byteBuffer);
                            byteBuffer.flip();
                            ByteBufferUtil.debugRead(byteBuffer);
                        }

                        iterator.remove();
                    }
                } catch (IOException e) {
                    log.error("error: ", e);
                }
            }
        }
    }
}
