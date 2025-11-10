package com.spring.boot.utils;


public class CountStr {

    public static String concat(Class<?> clazz, String method) {
        return clazz.getName() + "@" + method;
    }

}
