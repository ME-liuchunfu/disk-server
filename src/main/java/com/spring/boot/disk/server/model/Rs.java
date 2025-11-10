package com.spring.boot.disk.server.model;

import java.util.LinkedHashMap;


public class Rs extends LinkedHashMap<String, Object> {

    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";

    public static Rs ok(String msg) {
        return new Rs().flatPut(CODE, 200).flatPut(MSG, msg);
    }

    public static Rs ok() {
        return ok("ok");
    }

    public static Rs fail(String msg) {
        return new Rs().flatPut(CODE, 500).flatPut(MSG, msg);
    }

    public static Rs fail() {
        return fail("fail");
    }

    public Rs flatPut(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public Rs data(Object data) {
        this.put(DATA, data);
        return this;
    }

    public Rs code(int code) {
        if (code > 0) {
            this.put(CODE, code);
        }
        return this;
    }

    public Rs msg(String msg) {
        this.put(MSG, msg);
        return this;
    }

}
