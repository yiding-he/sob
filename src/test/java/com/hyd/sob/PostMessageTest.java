package com.hyd.sob;

import org.junit.Test;

public class PostMessageTest {

    @Test
    public void testPostMessage() throws Exception {
        String response = new HttpRequest("http://localhost:8080/message/post")
                .setParameter("消息", "服务器挂了！")
                .setParameter("message", "Server is down!")
                .requestPost();

        System.out.println(response);
    }
}
