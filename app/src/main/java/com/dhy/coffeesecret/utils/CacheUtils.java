package com.dhy.coffeesecret.utils;

import android.content.Context;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

/**
 * Created by CoDeleven on 17-3-4.
 */

public class CacheUtils {
    private static LinkedHashMap<String, Object> cache = new LinkedHashMap<>();
    private static CacheUtils cacheUtils = null;
    private Context context;

    private CacheUtils(Context context) {
        this.context = context;
        cacheUtils = this;
    }

    public synchronized static CacheUtils getCacheUtils() {
        return cacheUtils;
    }

    public synchronized static CacheUtils getCacheUtils(Context context) {
        if (cacheUtils == null) {
            cacheUtils = new CacheUtils(context);
        }
        return cacheUtils;
    }

    private static String getFileParent(Class clazz) {
        if (clazz == BakeReport.class) {
            return "62616b657265706f7274";
        } else if (clazz == BeanInfo.class) {
            return "6265616e696e666f";
        } else {
            return "637570696e666f";
        }
    }

    public <T> T getObjectFromMemory(String id, Class<T> clazz) {
        T temp = (T) cache.get(id);
        if (temp == null) {
            temp = getObjectFromCache(id, clazz);
        }
        return temp;
    }

    public <T> T getObjectFromCache(String id, Class<T> clazz) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = context.openFileInput(getFileParent(clazz) + id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        isr = new InputStreamReader(fis);

        Gson gson = new Gson();
        return gson.fromJson(isr, clazz);
    }
}
