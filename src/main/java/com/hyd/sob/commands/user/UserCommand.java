package com.hyd.sob.commands.user;

import com.hyd.sob.Role;
import com.hyd.sob.commands.Command;

public abstract class UserCommand extends Command {

    @Override
    public Role forRole() {
        return Role.User;
    }
}
