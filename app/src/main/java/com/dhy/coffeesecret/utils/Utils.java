package com.dhy.coffeesecret.utils;


import com.dhy.coffeesecret.pojo.Temprature;

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

    public static Temprature getTempratures(String data) {
        String[] subStr = data.split(",");
        Temprature temp = new Temprature();
        temp.setBeanTemp(Float.parseFloat(subStr[0]));
        temp.setInwindTemp(Float.parseFloat(subStr[1]));
        temp.setOutwindTemp(Float.parseFloat(subStr[2]));

        return temp;
    }

}
