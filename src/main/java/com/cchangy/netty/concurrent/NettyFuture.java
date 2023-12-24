package com.cchangy.netty.concurrent;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * netty future
 *
 * getNow(): 获取任务结果，非阻塞，还未产生结果时返回 null
 * await(): 等待任务结束，如果任务失败，不会抛异常，而是通过 isSuccess 判断
 * sync(): 等待任务结束，如果任务失败，抛出异常
 * isSuccess(): 判断任务是否成功
 * cause(): 获取失败信息，非阻塞，如果没有失败，返回null
 * addLinstener(): 添加回调监听，异步获取结果
 *
 * @author cchangy
 * @date 2022/01/16
 */
@Slf4j
public class NettyFuture {

    public static void main(String[] args) throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = eventLoopGroup.next();
        Future<String> future = eventLoop.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.debug("call running...");
                TimeUnit.SECONDS.sleep(2);
                return "netty future";
            }
        });
        log.debug("get netty future result start");
        // 1. 同步方式：get是阻塞方法，只有得到结果后才会向下执行
        // log.debug("sync get netty future result is: {}", future.get());

        // 2. 异步方式
        future.addListener(new GenericFutureListener<Future<? super String>>() {
            @Override
            public void operationComplete(Future<? super String> future) throws Exception {
                log.debug("async get netty future result is: {}", future.getNow());
            }
        });

        log.debug("get netty future result end");
    }
}
