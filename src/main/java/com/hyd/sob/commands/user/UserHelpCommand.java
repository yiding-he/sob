package com.hyd.sob.commands.user;

import com.hyd.sob.Role;
import com.hyd.sob.commands.admin.AdminHelpCommand;
import org.springframework.stereotype.Component;

@Component("userHelpCommand")
public class UserHelpCommand extends AdminHelpCommand {

    @Override
    public Role forRole() {
        return Role.User;
    }
}
