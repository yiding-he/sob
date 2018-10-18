package com.hyd.sob.bots;

import com.hyd.sob.Role;
import com.hyd.sob.commands.Commands;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserBot extends Bot {

    @Autowired
    private Commands commands;

    @Override
    public boolean match(Message message) {
        return !isAdmin(message);
    }

    @Override
    public void handleMessage(Message message, Chat chat) throws Exception {
        commands.execute(Role.User, message, chat);
    }
}
