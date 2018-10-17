package com.hyd.sob.bots;

import com.hyd.sob.Role;
import com.hyd.sob.SobConfiguration;
import com.hyd.sob.commands.Commands;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminBot extends Bot {

    @Autowired
    private SobConfiguration configuration;

    @Autowired
    private Commands commands;

    @Override
    public boolean match(Message message) {
        String userId = message.getFrom().getLocalpartOrNull().asUnescapedString();
        return userId.equals(configuration.getAdminUserName());
    }

    @Override
    public void handleMessage(Message message, Chat chat) throws Exception {
        commands.execute(Role.Admin, message, chat);
    }
}
