package com.dhy.coffeesecret;

import android.app.Application;
import android.util.Log;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.utils.CacheUtils;
import com.dhy.coffeesecret.utils.HttpParser;
import com.dhy.coffeesecret.utils.URLs;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.dhy.coffeesecret.utils.HttpUtils.getStringFromServer;

/**
 * Created by CoDeleven on 17-3-6.
 */

public class MyApplication extends Application {
    private static Map<String, Object> objs = new HashMap<>();
    private static String url = "-1";
    private static CacheUtils cacheUtils;

    public MyApplication() {
        super();
    }

    public <T> T getObjectById(String id, Class<T> clazz) {
        if (cacheUtils == null) {
            cacheUtils = CacheUtils.getCacheUtils(this);
        }
        T temp = null;
        // 获取id前缀
        String prefix = getPrefix(clazz);
        // 修改为内部可识别id
        id = prefix + id;
        // 获取object
        temp = (T) objs.get(id);
        // 如果内存中没有该对象，则从缓存中查找
        if (temp == null) {
            initMap(clazz);
            temp = (T) objs.get(id);
        }
        // 如果缓存中也没有该数据，则从服务器中获取
        if (temp == null) {
            initMapFromServer(clazz);
            Log.e("codelevex", "从服务器获取");
            temp = getObjectById(id, clazz);
            if(temp == null){
                return null;
            }
        }

        return temp;
    }

    /**
     * 需要的文件没加载，整体获取
     */
    private void initMap(Class clazz) {
        objs.putAll(cacheUtils.getListObjectFromCache(clazz));
    }

    private void initMapFromServer(final Class clazz) {

        //启动一个新线程将数据存储到本地
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String temp = null;
                try {
                    temp = getStringFromServer(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Map<String, Object> maps = HttpParser.getObjects(temp, clazz);
                objs.putAll(maps);

                Gson gson = new Gson();
                for (String key : maps.keySet()) {
                    cacheUtils.saveObject(key, gson.toJson(maps.get(key)), clazz);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取该类的前缀
     *
     * @param clazz
     * @return
     */
    private String getPrefix(Class clazz) {
        if (clazz == BakeReport.class) {
            url = URLs.GET_ALL_BAKE_REPORT;
            return CacheUtils.BAKE_REPORT_PREFIX;
        } else if (clazz == BeanInfo.class) {
            url = URLs.GET_ALL_BEAN_INFO;
            return CacheUtils.BEAN_INFO_PREFIX;
        } else if (clazz == CuppingInfo.class) {
            url = URLs.GET_ALL_CUPPING;
            return CacheUtils.CUP_INFO_PREFEX;
        }
        return null;
    }
}
