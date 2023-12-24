package com.cchangy.netty.components.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 事件循环组 {@link EventLoopGroup} 示例
 *
 * @author cchangy
 * @date 2022/01/15
 */
@Slf4j
public class EventLoopDemo {

    private EventLoopGroup eventLoopGroup = null;

    /**
     * 创建事件循环组
     */
    @Before
    public void createEventLoop() {
        /**
         * 创建事件循环组，可用于处理io事件、普通事件、定时任务
         * 可指定循环组内的线程数量，默认是CPU核心数对应的线程数（Runtime.getRuntime().availableProcessors()）
         */
        eventLoopGroup = new NioEventLoopGroup(2);
        // 可用于处理普通事件、定时任务的事件循环组
        // eventLoopGroup = new DefaultEventLoop();

        log.info("runtime available processors: {}", Runtime.getRuntime().availableProcessors());
        log.info("netty runtime available processors: {}", NettyRuntime.availableProcessors());
    }

    /**
     * 得到事件循环执行器，只有得到了它才能通过它去执行任务
     * 创建事件循环组的时候根据指定的线程数来创建事件执行器，然后循环利用
     */
    @Test
    public void getEventLoop() {
        for (EventExecutor eventExecutor : eventLoopGroup) {
            log.debug(eventExecutor.toString());
        }
        log.debug("--------------------");
        for (EventExecutor eventExecutor : eventLoopGroup) {
            log.debug(eventExecutor.toString());
        }
    }

    /**
     * 提交普通事件
     */
    @Test
    public void submitNormalEvent() throws Exception {
        eventLoopGroup.next().submit(() -> {
            log.debug("normal event");
        });

        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 提交定时任务事件
     */
    @Test
    public void submitScheduleEvent() throws Exception {
        eventLoopGroup.next().scheduleAtFixedRate(() -> {
            log.debug("schedule event");
        }, 1, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);
    }
}
