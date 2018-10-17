package com.hyd.sob.bots;

import com.hyd.sob.SobConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminBot extends Bot {

    @Autowired
    private SobConfiguration configuration;

    @Override
    public boolean match(Message message) {
        String userId = message.getFrom().getLocalpartOrNull().asUnescapedString();
        return userId.equals(configuration.getAdminUserName());
    }

    @Override
    public void handleMessage(Message message, Chat chat) throws Exception {
        chat.send("你好，管理员");
    }
}
