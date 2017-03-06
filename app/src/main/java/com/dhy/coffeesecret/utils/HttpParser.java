package com.dhy.coffeesecret.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-3-6.
 */

public class HttpParser {
    public static <T> Map<String, T> getObjects(String temp, Class clazz){
        Type type = new TypeToken<Map<String, T>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(temp, type);
    }
}
