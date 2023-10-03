package com.example.demo.utils;

import java.util.Map;
import java.util.StringJoiner;

public class DemoUtils
{
    public static String parseAuthUrlWithParams(String baseUrl, Map<String, String> params){
        StringJoiner joiner = new StringJoiner("&");
        params.forEach((key, value) -> {
            joiner.add(key.concat("=").concat(value));
        });
        return String.join("?", baseUrl, joiner.toString());
    }

}
