package com.dhy.coffeesecret.utils;


import android.content.Context;
import android.util.Log;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class Utils {
    public static final String KG = "kg", G = "g", LB = "lb";

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }

    /**
     * 将中文转换为拼音首字母
     *
     * @param cha 中文
     * @return
     */
    public static String getFirstPinYinLetter(String cha) {
        String pinyin = "";
        for (int i = 0; i < cha.length(); i++) {
            char c = cha.charAt(i);
            if (isChinese(c)) {
                pinyin += PinyinHelper.toHanyuPinyinStringArray(cha.charAt(i))[0].substring(0, 1);
            } else {
                pinyin += c;
            }
        }
        return pinyin.toUpperCase();
    }

    // 判断一个字符是否是中文
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;// 有一个中文字符就返回
        }
        return false;
    }

    public static String getTimeWithFormat(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        return String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds);
    }

    public static String convertUnitChineses2Eng(String unitZh) {
        if ("克".equals(unitZh)) {
            return "g";
        }
        if ("千克".equals(unitZh)) {
            return "kg";
        }
        if ("磅".equals(unitZh)) {
            return "lb";
        }
        return "";
    }

    public static long date2IdWithoutTimestamp(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        long result = -1;
        try {
            result = format1.parse(date).getTime();
        } catch (Exception e) {

        }
        return result;
    }

    public static String data2Timestamp(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

    public static String dateWidthoutTimestamp(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format1.format(format1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long date2IdWithTimestamp(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long result = -1;
        try {
            result = format1.parse(date).getTime();
        } catch (Exception e) {

        }
        return result;
    }

    public static float get2PrecisionFloat(float val) {
        return ((int) (val * 100)) / 100f;
    }

    /**
     * 将文件数值（默认kg）转换成当前设置的单位
     *
     * @param value 默认单位是kg的任何数值均可以转换
     * @return
     */
    public static float getCrspWeightValue(String value) {
        String curUnit = MyApplication.weightUnit;
        float result = Float.parseFloat(value);
        if ("g".equals(curUnit)) {
            return get2PrecisionFloat(result * 1000);
        } else if ("lb".equals(curUnit)) {
            return get2PrecisionFloat(result * 2.20f);
        }
        return get2PrecisionFloat(result);
    }

    /**
     * 将文件数值（默认摄氏度）转换成当前温度设置
     *
     * @param value  默认单位是摄氏度的均可以转换
     * @return
     */
    public static float getCrspTempratureValue(String value) {
        String curUnit = MyApplication.tempratureUnit;
        float result = Float.parseFloat(value);
        if ("℉".equals(curUnit)) {
            return get2PrecisionFloat(result * 1.8f + 32.0f);
        }
        return get2PrecisionFloat(result);
    }

    /**
     * 对输入的自定义单位进行转换
     * @param value
     * @return
     */
    public static float getReversed2DefaultWeight(String value){
        String curUnit = MyApplication.weightUnit;
        float result = Float.parseFloat(value);
        if ("g".equals(curUnit)) {
            return get2PrecisionFloat(result / 1000);
        } else if ("lb".equals(curUnit)) {
            return get2PrecisionFloat(result / 2.20f);
        }
        return get2PrecisionFloat(result);
    }
}
