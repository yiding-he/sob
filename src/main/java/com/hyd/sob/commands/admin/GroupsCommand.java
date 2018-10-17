package com.hyd.sob.commands.admin;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupsCommand extends AdminCommand {

    private List<String> groupNames = new ArrayList<>();

    @Override
    public String execute(String userId, String command, String[] params) {
        if (params.length == 0) {
            return "群组列表: \n" + String.join("\n", groupNames);
        } else if (params[0].equals("add")) {
            groupNames.add(params[1]);
            return "群组已添加。";
        } else if (params[0].equals("del")) {
            if (groupNames.remove(params[1])) {
                return "群组已删除。";
            } else {
                return "未找到要删除的群组。";
            }
        } else {
            return "未知操作";
        }
    }
}
