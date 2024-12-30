package com.cchangy.netty.cases.chat.session;

/**
 * 会话工厂
 *
 * @author cchangy
 * @date 2024/1/20
 */
public class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
