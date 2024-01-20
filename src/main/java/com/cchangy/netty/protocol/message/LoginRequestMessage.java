package com.cchangy.netty.protocol.message;

import lombok.Data;

/**
 * 登录请求消息
 *
 * @author cchangy
 * @date 2022/02/17
 */
@Data
public class LoginRequestMessage extends Message {

    private static final long serialVersionUID = -1520056645674558353L;

    private String username;
    private String password;

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return LOGIN_REQUEST_MESSAGE;
    }
}
