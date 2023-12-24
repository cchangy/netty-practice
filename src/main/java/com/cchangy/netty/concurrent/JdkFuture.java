package com.cchangy.netty.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * jdk future
 *
 * cancel(): 取消任务
 * isDone(): 任务是否完成，不区分成功或失败
 * get(): 同步阻塞获取结果
 *
 * @author cchangy
 * @date 2022/01/16
 */
@Slf4j
public class JdkFuture {

    public static void main(String[] args) throws Exception {
        // 1. 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 2. 向线程池中提交任务
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.debug("call running...");
                TimeUnit.SECONDS.sleep(2);
                return "jdk future";
            }
        });

        log.debug("get jdk future result start");
        // 3. get阻塞等待获取结果
        String result = future.get();
        log.debug("get jdk future result is: {}", result);
        log.debug("get jdk future result end");
    }
}
