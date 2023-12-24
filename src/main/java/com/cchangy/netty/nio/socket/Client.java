package com.cchangy.netty.nio.socket;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * NIO客户端
 *
 * @author cchangy
 * @date 2022/01/03
 */
@Slf4j
public class Client {

    public static void main(String[] args)throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8899));
        socketChannel.write(Charset.defaultCharset().encode("0123456789abc"));
        System.in.read();
    }
}
