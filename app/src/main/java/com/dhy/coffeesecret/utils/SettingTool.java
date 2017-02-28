package com.dhy.coffeesecret.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.SharedPreferencesCompat;

import com.dhy.coffeesecret.pojo.UniversalConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class SettingTool {
    private static UniversalConfiguration mConfig;
    private static SharedPreferences sharedPreferences;

    public static UniversalConfiguration getConfig(Context context) {
        if (mConfig == null) {
            setupConfig(context);
        }
        return mConfig;
    }

    private static void setupConfig(Context context) {
        sharedPreferences = context.getSharedPreferences("secret", Context.MODE_PRIVATE);
        mConfig = new UniversalConfiguration();
        mConfig.setWeightUnit(sharedPreferences.getString("weightUnit", "kg"));
        mConfig.setTempratureUnit(sharedPreferences.getString("tempratureUnit", "℃"));
        mConfig.setReferDegree(sharedPreferences.getString("referDegree", "℃"));
        mConfig.setQuickStart(sharedPreferences.getBoolean("quickStart", false));
        mConfig.setDoubleClick(sharedPreferences.getBoolean("doubleClick", true));
        mConfig.setMarkByCircle(sharedPreferences.getBoolean("markByCircle", false));
        mConfig.setMaxX(sharedPreferences.getInt("maxX", 20));
        mConfig.setMaxLeftY(sharedPreferences.getInt("maxLeftY", 300));
        mConfig.setMaxRightY(sharedPreferences.getInt("maxRightY", 30));
        mConfig.setTempratureSmooth(sharedPreferences.getInt("tempratureSmooth", 3));
        mConfig.setTempratureAccSmooth(sharedPreferences.getInt("tempratureAccSmooth", 10));
        mConfig.setCheckBeanTemp(sharedPreferences.getInt("checkBeanTemp", 0));
        mConfig.setCheckInwindTemp(sharedPreferences.getInt("checkInwindTemp", 0));
        mConfig.setCheckOutwindTemp(sharedPreferences.getInt("checkOutwindTemp", 0));
        mConfig.setCheckEvnTemp(sharedPreferences.getInt("checkEvnTemp", 0));
        mConfig.setBeanColor(sharedPreferences.getInt("beanColor", Color.parseColor("#FF0000")));
        mConfig.setInwindColor(sharedPreferences.getInt("inwindColor", Color.parseColor("#00FF00")));
        mConfig.setOutwindColor(sharedPreferences.getInt("outwindColor", Color.parseColor("#0000FF")));
        mConfig.setEnvColor(sharedPreferences.getInt("envColor", Color.BLACK));
        mConfig.setAccBeanColor(sharedPreferences.getInt("accBeanColor", Color.parseColor("#FFFF00")));
        mConfig.setAccInwindColor(sharedPreferences.getInt("accInwindColor", Color.parseColor("#00FFFF")));
        mConfig.setAccOutwindColor(sharedPreferences.getInt("accOutwindColor", Color.parseColor("#FF00FF")));
        mConfig.setQuickEvents(sharedPreferences.getStringSet("quickEvent", new HashSet<String>()));
    }

    public static void saveConfig(UniversalConfiguration config) {
        mConfig = config;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("weightUnit", config.getWeightUnit());
        editor.putString("tempratureUnit", config.getTempratureUnit());
        editor.putString("referDegree", config.getReferDegree());
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
        editor.putStringSet("quickEvent", config.getQuickEvents());
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
