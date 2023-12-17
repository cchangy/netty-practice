package com.cchangy.netty.nio.buffer;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * String与ByteBuffer相互转换
 *
 * @author cchangy
 * @date 2022/01/02
 */
@Slf4j
public class StringByteBufferConvert {

    public static void main(String[] args) {
        // 1. 获取字符串的字节数组
        byte[] bytes = "abc".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        ByteBufferUtil.debugAll(byteBuffer);

        // 2. StandardCharsets(会切换到读模式)
        byteBuffer = StandardCharsets.UTF_8.encode("abc");
        ByteBufferUtil.debugAll(byteBuffer);

        // 3. wrap(会切换到读模式)
        byteBuffer = ByteBuffer.wrap(bytes);
        ByteBufferUtil.debugAll(byteBuffer);

        // 转字符串
        String string = StandardCharsets.UTF_8.decode(byteBuffer).toString();
        log.debug("decode result: {}", string);
    }
}
