package com.hyd.sob;

import com.hyd.sob.bots.Bot;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class BotService {

    private static final Logger LOG = LoggerFactory.getLogger(BotService.class);

    @Autowired
    private SobConfiguration configuration;

    @Autowired
    private BotFactory botFactory;

    private XMPPConnection connection;

    @PostConstruct
    private void init() throws Exception {

        XMPPTCPConnection tcpConnection = botLogin();
        LOG.info("SOB bot is ready.");

        this.connection = tcpConnection;
        initChatManager();
    }

    private XMPPTCPConnection botLogin() throws Exception {
        LOG.info("SOB bot is connecting to " + configuration.getXmppServerHost() + "...");

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
                .builder()
                .setUsernameAndPassword(
                        configuration.getBotUserName(),
                        configuration.getBotUserPass()
                )
                .setXmppDomain(configuration.getXmppServerHost())
                .setResource(configuration.getBotResource());

        if (!configuration.isUseSsl()) {
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        }

        XMPPTCPConnectionConfiguration config = builder.build();
        XMPPTCPConnection tcpConnection = new XMPPTCPConnection(config);

        tcpConnection.connect().login();
        return tcpConnection;
    }

    private void initChatManager() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(this::onMessageReceived);
    }

    private void onMessageReceived(EntityBareJid jid, Message message, Chat chat) {
        try {
            Bot bot = botFactory.getBot(message);
            if (bot != null) {
                bot.handleMessage(message, chat);
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    @PreDestroy
    private void fin() {
        if (connection instanceof XMPPTCPConnection) {
            ((XMPPTCPConnection) connection).disconnect();
        }
    }
}
