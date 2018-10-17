package com.hyd.sob.commands.admin;

import com.hyd.sob.Role;
import com.hyd.sob.commands.Command;

public abstract class AdminCommand extends Command {

    @Override
    public Role forRole() {
        return Role.Admin;
    }
}
