package com.hyd.sob;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Result {

    public static Result success() {
        return new Result();
    }

    public static Result fail(String message) {
        return fail(-1, message);
    }

    public static Result fail(int code, String message) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    private int code;

    private String message;

    private Map<String, Object> data = new HashMap<>();

    public Result put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
