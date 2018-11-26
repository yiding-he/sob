package com.hyd.sob.commands.user;

import com.hyd.sob.SobConfiguration;
import com.hyd.sob.verifycode.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyCodeCommand extends UserCommand {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Autowired
    private SobConfiguration sobConfiguration;

    @Override
    public String getCommandName() {
        return "login";
    }

    @Override
    public String execute(String userId, String command, String[] params) {

        if (params == null || params.length == 0) {
            return "Invalid command.\nUsage: login [service]";
        }

        String service = params[0];
        String code = verifyCodeService.generateCode(userId, service);
        return "Your login code: " + code + "\nPlease use it within " +
                sobConfiguration.getVerifyCodeExpiryMin() + " minutes.";
    }
}
