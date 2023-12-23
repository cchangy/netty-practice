package com.cchangy.netty.nio.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * FileChannel transferTo
 * <p>
 * 单次最大支持2G大小的文件传输，超过2G需要多次处理
 *
 * @author cchangy
 * @date 2022/01/02
 */
@Slf4j
public class FileChannelTransferTo {

    public static void main(String[] args) {
        // 小文件传输
        smallFileTransferTo();

        // 大文件传输
        bigFileTransferTo();
    }

    private static void smallFileTransferTo() {
        try (FileChannel input = new FileInputStream("data.txt").getChannel();
             FileChannel output = new FileOutputStream("out.txt").getChannel()) {
            input.transferTo(0, input.size(), output);
        } catch (IOException e) {
            log.error("error", e);
        }
    }

    private static void bigFileTransferTo() {
        try (FileChannel input = new FileInputStream("data.txt").getChannel();
             FileChannel output = new FileOutputStream("out.txt").getChannel()) {

            long size = input.size();
            // left 变量代表还剩余多少字节
            for (long left = size; left > 0; ) {
                log.info("position: {}, left: {}", (size - left), left);
                left -= input.transferTo((size - left), left, output);
            }
        } catch (IOException e) {
            log.error("error", e);
        }
    }
}
