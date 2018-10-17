package com.hyd.sob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sob")
@Data
public class SobConfiguration {

    private String xmppServerHost = "localhost";

    private int xmppServerPort = 5222;

    private boolean useSsl = false;

    private String adminUserName = "admin";

    private String botUserName;

    private String botUserPass;

    private String botResource = "sob-bot";
}
