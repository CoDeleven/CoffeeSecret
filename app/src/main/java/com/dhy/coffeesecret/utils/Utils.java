package com.dhy.coffeesecret.utils;


import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.List;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class Utils {
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
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;// 有一个中文字符就返回
        }
        return false;
    }
    public static String getTimeWithFormat(float time){
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        return String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds);
    }

    public static String convertUnitChineses2Eng(String unitZh){
        if("克".equals(unitZh)){
            return "g";
        }
        if("千克".equals(unitZh)){
            return "kg";
        }
        if("磅".equals(unitZh)){
            return "lb";
        }
        return "";
    }
}
