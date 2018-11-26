package com.hyd.sob.controller;

import com.hyd.sob.Result;
import com.hyd.sob.verifycode.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify-code")
public class VerifyCodeController {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @RequestMapping("/check")
    public Result checkCode(String code, String service) {
        String userId = verifyCodeService.getUser(code, service);
        if (userId != null) {
            return Result.success().put("userId", userId);
        } else {
            return Result.fail("Invalid login code.");
        }
    }
}
