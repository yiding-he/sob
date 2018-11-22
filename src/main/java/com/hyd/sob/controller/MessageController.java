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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private BotService botService;

    /**
     * Send everything in request parameters with XMPP bot
     *
     * @param request request object
     *
     * @return operation result
     */
    @PostMapping("/post")
    public Result postMessage(HttpServletRequest request) {
        try {
            Map<String, Object> message = parseMessage(request);
            String group = request.getParameter("group");
            botService.sendMessage(message, group);
            return Result.success();

        } catch (Exception e) {
            LOG.error("", e);
            return Result.fail(e.toString());
        }
    }

    // Wrap request parameters to a Map object
    private Map<String, Object> parseMessage(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (!key.equals("group")) {
                result.put(key, String.join(",", value));
            }
        });
        return result;
    }
}
