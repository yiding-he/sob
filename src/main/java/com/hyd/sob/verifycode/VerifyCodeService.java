package com.hyd.sob.verifycode;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyd.sob.SobConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyCodeService {

    @Autowired
    private SobConfiguration sobConfiguration;

    private Cache<String, String> cache;

    private Random random = new SecureRandom();

    @PostConstruct
    private void init() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(sobConfiguration.getVerifyCodeExpiryMin(), TimeUnit.MINUTES)
                .build();
    }

    public String generateCode(String userId, String service) {

        String code = randomCode();
        while (cache.getIfPresent(code + ":" + service) != null) {
            code = randomCode();
        }

        cache.put(code + ":" + service, userId);
        return code;
    }

    private String randomCode() {
        return String.format("%08d", random.nextInt(100000000));
    }

    public String getUser(String code, String service) {
        return cache.getIfPresent(code + ":" + service);
    }
}
