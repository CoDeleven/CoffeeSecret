package com.dhy.coffeesecret.utils;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-3-6.
 */

public class HttpParser {
    public static Map<String, BakeReport> getBakeReports(String temp){
        Type type = new TypeToken<Map<String, BakeReport>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(temp, type);
    }

    public static Map<? extends String,? extends BeanInfo> getBeanInfos(String temp) {
        Type type = new TypeToken<Map<String, BeanInfo>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(temp, type);
    }

    public static Map<? extends String,? extends CuppingInfo> getCuppingInfos(String temp) {
        Type type = new TypeToken<Map<String, CuppingInfo>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(temp, type);
    }
}
