package com.cchangy.netty.cases.chat.session;

import io.netty.channel.Channel;

/**
 * 会话接口
 *
 * @author cchangy
 * @date 2024/1/20
 */
public interface Session {

    /**
     * 绑定会话
     *
     * @param channel  哪个 channel 要绑定会话
     * @param username 会话绑定用户
     */
    void bind(Channel channel, String username);

    /**
     * 解绑会话
     *
     * @param channel 哪个 channel 要解绑会话
     */
    void unbind(Channel channel);

    /**
     * 获取属性
     *
     * @param channel 哪个 channel
     * @param name    属性名
     * @return 属性值
     */
    Object getAttribute(Channel channel, String name);

    /**
     * 设置属性
     *
     * @param channel 哪个 channel
     * @param name    属性名
     * @param value   属性值
     */
    void setAttribute(Channel channel, String name, Object value);

    /**
     * 根据用户名获取 channel
     *
     * @param username 用户名
     * @return channel
     */
    Channel getChannel(String username);
}
