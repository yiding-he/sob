package com.hyd.sob.commands;

import com.hyd.sob.Role;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class Command {

    @Autowired
    protected Commands commands;

    @PostConstruct
    private void init() {
        commands.registerCommand(this);
    }

    public String getCommandName() {
        String simpleName = this.getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Command".length()).toLowerCase();
    }

    public abstract Role forRole();

    public abstract String execute(String userId, String command, String[] params);
}
