package com.hyd.sob.bots;

import com.hyd.sob.BotFactory;
import com.hyd.sob.SobConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class Bot {

    @Autowired
    private SobConfiguration configuration;

    @Autowired
    private BotFactory botFactory;

    @PostConstruct
    private void init() {
        botFactory.register(this);
    }

    protected boolean isAdmin(Message message) {
        String userId = message.getFrom().getLocalpartOrNull().asUnescapedString();
        return userId.equals(configuration.getAdminUserName());
    }

    public abstract boolean match(Message message);

    public abstract void handleMessage(Message message, Chat chat) throws Exception;
}
