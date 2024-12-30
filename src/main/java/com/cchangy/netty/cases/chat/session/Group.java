package com.cchangy.netty.cases.chat.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * 聊天组实体
 *
 * @author cchangy
 * @date 2024/1/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    // 聊天室名称
    private String name;
    // 聊天室成员
    private Set<String> members;

    public static final Group EMPTY_GROUP = new Group("empty", Collections.emptySet());
}
