package com.spring.boot.sinple;

import cn.hutool.core.codec.Base64;

public class DiskUrlMain {
    public static void main(String[] args) {
        long expireTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        String encode = Base64.encodeUrlSafe(String.valueOf(expireTime));
        System.out.println(encode);
        String url = String.format("%s%s%s/", "http://localhost:1080/files/", "", encode);
        System.out.println(url);
    }

}
