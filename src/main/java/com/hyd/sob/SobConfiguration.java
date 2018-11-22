package com.hyd.sob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

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

    private Map<String, List<String>> users;
}
