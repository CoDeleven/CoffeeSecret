package com.dhy.coffeesecret.utils;

import android.content.Context;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoDeleven on 17-3-4.
 */

public class CacheUtils {
    public static final String BAKE_REPORT_PREFIX = "62616b657265706f7274";
    public static final String BEAN_INFO_PREFIX = "6265616e696e666f";
    public static final String CUP_INFO_PREFEX = "637570696e666f";
    private static CacheUtils cacheUtils = null;
    private static Object tempObj = null;
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
            return BAKE_REPORT_PREFIX;
        } else if (clazz == BeanInfo.class) {
            return BEAN_INFO_PREFIX;
        } else {
            return CUP_INFO_PREFEX;
        }
    }

    /**
     * 获取所有该类型的文件
     * @param clazz 该类型的class
     * @param <T> 参数类型，一般是BakeReport、BeanInfo或者CuppingInfo
     * @return 返回该类型的所有对象
     */
    public <T> Map<String, T> getListObjectFromCache(final Class<? extends T> clazz) {
        // 获取该类型文件的前缀
        final String prefix = getFileParent(clazz);
        // 查找目录，根据prefix获取files
        File[] files = context.getCacheDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(prefix);
            }
        });

        Map<String, T> objs = new HashMap<>();
        Gson gson = new Gson();

        // 满足要求的文件
        for (int i = 0; i < files.length; ++i) {
            try {
                // 通过文件获取gson
                T temp = gson.fromJson(new InputStreamReader(new FileInputStream(files[i])), clazz);
                // 获取文件id
                String id = files[i].getName().substring(prefix.length());
                // 放入以前缀+id命名的文件
                objs.put(prefix + id, temp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return objs;
    }


    private <T> T getObjectFromCache(String id, Class<? extends T> clazz) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = context.openFileInput(getFileParent(clazz) + id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        isr = new InputStreamReader(fis);

        Gson gson = new Gson();
        T temp = gson.fromJson(isr, clazz);
        return temp;
    }

    public boolean isExists(final String id, Class<? extends T> clazz) {
        tempObj = getObjectFromCache(id, clazz);
        return tempObj != null;
    }

    public void saveObject(String key, String obj, Class clazz) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getCacheDir(), getFileParent(clazz) + key);
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter os = new OutputStreamWriter(fos);
        BufferedWriter writer = new BufferedWriter(os);
        try {
            writer.write(obj);
            // 立即刷新缓存
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
