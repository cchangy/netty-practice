package com.cchangy.netty.cases.chat.session;

/**
 * 聊天组会话工厂
 *
 * @author cchangy
 * @date 2024/1/20
 */
public class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}
