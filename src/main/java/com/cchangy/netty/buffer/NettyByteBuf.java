package com.cchangy.netty.buffer;

import com.cchangy.netty.util.ByteBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * netty ByteBuf
 * <p>
 * 1. 创建ByteBuf时如果不指定大小，默认为256
 * 2. 池化直接内存ByteBuf实现：io.netty.buffer.PooledUnsafeDirectByteBuf
 * 3. 池化堆内存ByteBuf实现：io.netty.buffer.PooledUnsafeHeapByteBuf
 *
 * ByteBuf相比原生ByteBuffer的优势：
 *      池化 - 可以重用池中 ByteBuf 实例，更节约内存，减少内存溢出的可能
 *      读写指针分离，不需要像 ByteBuffer 一样切换读写模式
 *      可以自动扩容
 *      支持链式调用，使用更流畅
 *      很多地方体现零拷贝，例如 slice、duplicate、CompositeByteBuf
 *
 * @author cchangy
 * @date 2022/02/12
 */
@Slf4j
public class NettyByteBuf {

    /**
     * 创建ByteBuf
     */
    @Test
    public void testCreateByteBuf() {
        // 1. 创建ByteBuf，默认是池化基于直接内存的ByteBuf
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        log.info("ByteBufAllocator.DEFAULT.buffer() -> {}", buffer.getClass());

        // 2. 创建池化基于直接内存的ByteBuf
        buffer = ByteBufAllocator.DEFAULT.directBuffer();
        log.info("ByteBufAllocator.DEFAULT.directBuffer() -> {}", buffer.getClass());

        // 3. 创建池化基于堆内存的ByteBuf
        buffer = ByteBufAllocator.DEFAULT.heapBuffer();
        log.info("ByteBufAllocator.DEFAULT.heapBuffer() -> {}", buffer.getClass());

        ByteBufferUtil.printByteBuf(buffer);
    }

    /**
     * 释放ByteBuf
     * ByteBuf使用的是引用计数的方式来控制内存的释放，每个ByteBuf都实现了ReferenceCounted接口
     * 引用技术初始值为1，当调用release方法时，会将引用计数减一，如果此时引用计数为0了，则会将内存释放
     * <p>
     * 可以调用retain方法来增加引用计数
     * <p>
     * 起点，对于 NIO 实现来讲，在 io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe#read 方法中首次创建 ByteBuf 放入 pipeline（line 163 pipeline.fireChannelRead(byteBuf)）
     * 入站 ByteBuf 处理原则：
     *      对原始 ByteBuf 不做处理，调用 ctx.fireChannelRead(msg) 向后传递，这时无须 release
     *      将原始 ByteBuf 转换为其它类型的 Java 对象，这时 ByteBuf 就没用了，必须 release
     *      如果不调用 ctx.fireChannelRead(msg) 向后传递，那么也必须 release
     *      注意各种异常，如果 ByteBuf 没有成功传递到下一个 ChannelHandler，必须 release
     *      假设消息一直向后传，那么 TailContext 会负责释放未处理消息（原始的 ByteBuf）
     * 出站 ByteBuf 处理原则：
     *      出站消息最终都会转为 ByteBuf 输出，一直向前传，由 HeadContext flush 后 release
     * 异常处理原则：
     *      有时候不清楚 ByteBuf 被引用了多少次，但又必须彻底释放，可以循环调用 release 直到返回 true
     */
    @Test
    public void testRetainAndRelease() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("hello".getBytes());
        ByteBufferUtil.printByteBuf(buffer);

        log.info("release before: {}", buffer);
        // 增加引用计数
        buffer.retain();
        log.info("retain: {}", buffer);

        buffer.release();
        log.info("release 1 after: {}", buffer);
        buffer.release();
        log.info("release 2 after: {}", buffer);
    }

    /**
     * slice用于对ByteBuf进行切片，切片不会产生内存复制，还是使用的原始ByteBuf的内存
     * 切片后的ByteBuf拥有独立的读写指针，但是只可替换ByteBuf中的值，不可写入新的值
     */
    @Test
    public void testSlice() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("abcdef".getBytes());

        ByteBuf b1 = buffer.slice(0, 3);
        ByteBuf b2 = buffer.slice(3, 3);
        ByteBufferUtil.printByteBuf(b1);
        ByteBufferUtil.printByteBuf(b2);

        b1.setByte(0, 'x');
        log.info("replace b1 index 0 value");
        // 替换后，切片的buffer和原始buffer中值都会改变
        ByteBufferUtil.printByteBuf(b1);
        ByteBufferUtil.printByteBuf(buffer);

        /**
         * 切片后的buffer不可写入新的值，如果操作写入会抛异常
         * java.lang.IndexOutOfBoundsException: writerIndex(3) + minWritableBytes(1) exceeds maxCapacity(3): UnpooledSlicedByteBuf(ridx: 0, widx: 3, cap: 3/3, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 6, cap: 256))
         */
        // b1.writeBytes("l".getBytes());
    }

    /**
     * duplicate是对ByteBuf进行完成的拷贝，但使用的还是原始ByteBuf的内存
     * 拥有独立的读写指针
     * <p>
     * copy是对原始ByteBuf进行了完整的copy，做任何操作都不会影响到原始ByteBuf
     */
    @Test
    public void testDuplicateAndCopy() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("abcdef".getBytes());
        ByteBufferUtil.printByteBuf(buffer);

        ByteBuf duplicate = buffer.duplicate();
        ByteBufferUtil.printByteBuf(duplicate);

        duplicate.setByte(0, 'x');
        ByteBufferUtil.printByteBuf(buffer);

        ByteBuf copy = buffer.copy();
        copy.setByte(0, 'z');
        ByteBufferUtil.printByteBuf(copy);

        ByteBufferUtil.printByteBuf(buffer);
    }

    /**
     * compositeBuffer是将多个ByteBuf组合成一个buffer，避免出现多次内存复制
     */
    @Test
    public void testCompositeBuffer() {
        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer();
        b1.writeBytes("abc".getBytes());

        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer();
        b2.writeBytes("def".getBytes());

        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        // increaseWriterIndex=true 表示增加新的 ByteBuf 自动递增 write index, 否则 write index 会始终为 0
        compositeByteBuf.addComponents(true, b1, b2);

        ByteBufferUtil.printByteBuf(compositeByteBuf);
    }

    /**
     * Unpooled是netty提供的一个工具类，提供了非池化的 ByteBuf 创建、组合、复制等操作
     */
    @Test
    public void testUnpooled() {
        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer();
        b1.writeBytes("abc".getBytes());

        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer();
        b2.writeBytes("def".getBytes());

        // 当包装 ByteBuf 个数超过一个时, 底层使用了 CompositeByteBuf
        ByteBuf byteBuf = Unpooled.wrappedBuffer(b1, b2);
        ByteBufferUtil.printByteBuf(byteBuf);
    }

}
