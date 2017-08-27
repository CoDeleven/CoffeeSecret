package com.dhy.coffeesecret.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class SettingTool {
    private static UniversalConfiguration mConfig;
    private static SharedPreferences sharedPreferences;

    public static UniversalConfiguration getConfig() {
        if (mConfig == null) {
            throw new RuntimeException("SettingTool 没有在MyApplication正常初始化");
        }
        return mConfig;
    }

    public static boolean isInitSetting(){
        return mConfig != null;
    }

    public static void init(Context context){
        setupConfig(context);
    }

    private static void setupConfig(Context context) {
        sharedPreferences = context.getSharedPreferences("secret", Context.MODE_PRIVATE);
        mConfig = new UniversalConfiguration();
        mConfig.setWeightUnit(sharedPreferences.getString("weightUnit", "kg"));
        mConfig.setTempratureUnit(sharedPreferences.getString("temperatureUnit", "℃"));
        mConfig.setReferDegree(sharedPreferences.getString("referDegree", "Agtron"));
        mConfig.setQuickStart(sharedPreferences.getBoolean("quickStart", false));
        mConfig.setDoubleClick(sharedPreferences.getBoolean("doubleClick", true));
        mConfig.setMarkByCircle(sharedPreferences.getBoolean("markByCircle", false));
        mConfig.setMaxX(sharedPreferences.getInt("maxX", 20));
        mConfig.setMaxLeftY(sharedPreferences.getInt("maxLeftY", 300));
        mConfig.setMaxRightY(sharedPreferences.getInt("maxRightY", 50));
        mConfig.setTempratureSmooth(sharedPreferences.getInt("tempratureSmooth", 3));
        mConfig.setTempratureAccSmooth(sharedPreferences.getInt("tempratureAccSmooth", 10));
        mConfig.setCheckBeanTemp(sharedPreferences.getInt("checkBeanTemp", 0));
        mConfig.setCheckInwindTemp(sharedPreferences.getInt("checkInwindTemp", 0));
        mConfig.setCheckOutwindTemp(sharedPreferences.getInt("checkOutwindTemp", 0));
        mConfig.setCheckEvnTemp(sharedPreferences.getInt("checkEvnTemp", 0));
        mConfig.setColorPackageName(sharedPreferences.getString("colorPackageName", "海洋"));
        mConfig.setBeanColor(sharedPreferences.getInt("beanColor", Color.parseColor("#FF0000")));
        mConfig.setInwindColor(sharedPreferences.getInt("inwindColor", Color.parseColor("#00FF00")));
        mConfig.setOutwindColor(sharedPreferences.getInt("outwindColor", Color.parseColor("#0000FF")));
        mConfig.setEnvColor(sharedPreferences.getInt("envColor", Color.BLACK));
        mConfig.setAccBeanColor(sharedPreferences.getInt("accBeanColor", Color.parseColor("#FFFF00")));
        mConfig.setAccInwindColor(sharedPreferences.getInt("accInwindColor", Color.parseColor("#00FFFF")));
        mConfig.setAccOutwindColor(sharedPreferences.getInt("accOutwindColor", Color.parseColor("#FF00FF")));
        mConfig.setQuickEvents(sharedPreferences.getString("quickEvent", TestData.quickEvents));
        mConfig.setAddress(sharedPreferences.getString("address", ""));
    }

    public static void saveConfig(UniversalConfiguration config) {
        mConfig = config;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("weightUnit", config.getWeightUnit());
        editor.putString("temperatureUnit", config.getTempratureUnit());
        editor.putString("referDegree", config.getReferDegree());
        editor.putString("quickEvent", config.getQuickEvents());
        editor.putString("colorPackageName", config.getColorPackageName());
        editor.putBoolean("quickStart", config.isQuickStart());
        editor.putBoolean("doubleClick", config.isDoubleClick());
        editor.putBoolean("markByCircle", config.isQuickStart());
        editor.putInt("maxX", config.getMaxX());
        editor.putInt("maxLeftY", config.getMaxLeftY());
        editor.putInt("maxRightY", config.getMaxRightY());
        editor.putInt("tempratureSmooth", config.getTempratureSmooth());
        editor.putInt("tempratureAccSmooth", config.getTempratureAccSmooth());
        editor.putInt("beanColor", config.getBeanColor());
        editor.putInt("inwindColor", config.getInwindColor());
        editor.putInt("outwindColor", config.getOutwindColor());
        editor.putInt("envColor", config.getEnvColor());
        editor.putInt("accBeanColor", config.getAccBeanColor());
        editor.putInt("accInwindColor", config.getAccInwindColor());
        editor.putInt("accOutwindColor", config.getAccOutwindColor());
        SharedPreferencesCompat.apply(editor);
    }

    public static void saveTemp(UniversalConfiguration config) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("checkBeanTemp", config.getCheckBeanTemp());
        editor.putInt("checkInwindTemp", config.getCheckInwindTemp());
        editor.putInt("checkOutwindTemp", config.getCheckOutwindTemp());
        editor.putInt("checkEvnTemp", config.getCheckEvnTemp());

        SharedPreferencesCompat.apply(editor);
    }

    public static void saveAddress(String address){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        SharedPreferencesCompat.apply(editor);
    }

    public static ArrayList<String> parse2List(String json) {

        Gson gson = new Gson();
        ArrayList<String> list = gson.fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());

        return list != null ? list : new ArrayList<String>();
    }

    public static String parse2String(ArrayList<String> list) {

        Gson gson = new Gson();

        return list != null ? gson.toJson(list) : "";
    }

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }
}
