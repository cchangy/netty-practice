package com.cchangy.netty.concurrent;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * netty promise，在netty future基础上还可以设置结果
 *
 * setSuccess(): 设置成功结果
 * setFailure(): 设置失败结果
 *
 * @author cchangy
 * @date 2022/01/16
 */
@Slf4j
public class NettyPromise {
    public static void main(String[] args) throws Exception {
        // 1. 创建eventLoop
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // 2. 创建promise
        DefaultPromise<String> promise = new DefaultPromise<>(eventLoopGroup.next());
        new Thread(() -> {
            try {
//                 int a = 1 / 0;
                log.debug("thread running...");
                TimeUnit.SECONDS.sleep(2);
                promise.setSuccess("success");
            } catch (InterruptedException e) {
                promise.setFailure(e);
            }

        }).start();

        log.debug("get promise result start");
        log.debug("get promise result is: {}", promise.get());
        log.debug("get promise result end");
    }
}
