package com.hyd.sob.commands.admin;

import com.hyd.sob.commands.Command;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdminHelpCommand extends AdminCommand {

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String execute(String userId, String command, String[] params) {

        String commandList = commands.getCommandsForRole(forRole())
                .stream()
                .map(Command::getCommandName)
                .collect(Collectors.joining("\n"));

        return "Available commands:\n" + commandList;
    }
}
