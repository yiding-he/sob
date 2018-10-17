package com.hyd.sob.commands;

import com.hyd.sob.Role;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Commands {

    private Set<Command> commands = new HashSet<>();

    public void registerCommand(Command command) {
        this.commands.add(command);
    }

    public Set<Command> getCommandsForRole(Role role) {
        return commands.stream()
                .filter(command -> {
                    Role forRole = command.forRole();
                    return forRole == null || forRole == role;
                })
                .collect(Collectors.toSet());
    }

    public Command getCommand(Role role, String name) {
        return commands.stream()
                .filter(command -> command.forRole() == role)
                .filter(command -> command.getCommandName().equals(name))
                .findFirst().orElse(null);
    }

    public void execute(Role role, Message message, Chat chat) throws Exception {

        String userId = message.getFrom().getLocalpartOrNull().asUnescapedString();
        String body = message.getBody().trim();
        String response;

        String[] split = body.split("\\s+");
        String commandName = split[0];
        Command command = getCommand(role, commandName);

        if (command == null) {
            command = getCommand(role, "help");
        }

        if (split.length == 1) {
             response = command.execute(userId, commandName, new String[0]);
        } else {
            String[] params = Arrays.copyOfRange(split, 1, split.length);
            response = command.execute(userId, commandName, params);
        }

        chat.send(response);
    }
}
