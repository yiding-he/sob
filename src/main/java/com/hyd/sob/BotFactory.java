package com.hyd.sob;

import com.hyd.sob.bots.Bot;
import org.jivesoftware.smack.packet.Message;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BotFactory {

    private Set<Bot> bots = new HashSet<>();

    public void register(Bot bot) {
        bots.add(bot);
    }

    public Bot getBot(Message message) {
        return bots.stream()
                .filter(bot -> bot.match(message))
                .findFirst().orElse(null);
    }
}
