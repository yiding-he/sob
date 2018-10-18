package com.hyd.sob.controller;

import com.hyd.sob.BotService;
import com.hyd.sob.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private BotService botService;

    @PostMapping("/post")
    public Result postMessage(HttpServletRequest request) {
        try {
            Map<String, Object> message = parseMessage(request);
            botService.sendMessage(message);
            return Result.success();

        } catch (Exception e) {
            LOG.error("", e);
            return Result.fail(e.toString());
        }
    }

    private Map<String, Object> parseMessage(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        request.getParameterMap().forEach((key, value) -> {
            result.put(key, Arrays.toString(value));
        });

        return result;
    }
}
