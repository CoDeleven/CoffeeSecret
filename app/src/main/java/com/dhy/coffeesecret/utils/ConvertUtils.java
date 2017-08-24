package com.dhy.coffeesecret.utils;

import android.content.res.Resources;
import android.util.Log;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.clj.fastble.utils.HexUtil.charToByte;

/**
 * Created by CoDeleven on 17-8-24.
 */

public class ConvertUtils {
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

    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }

    public static String convertUnitCn2Eng(String unitZh) {
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
            return Utils.get2PrecisionFloat(result * 1000);
        } else if ("lb".equals(curUnit)) {
            return Utils.get2PrecisionFloat(result * 2.20f);
        }
        return Utils.get2PrecisionFloat(result);
    }

    /**
     * 将文件数值（默认摄氏度）转换成当前温度设置
     *
     * @param value  默认单位是摄氏度的均可以转换
     * @return
     */
    public static float getCrspTemperatureValue(String value) {
        String curUnit = MyApplication.temperatureUnit;
        float result = Float.parseFloat(value);
        if ("℉".equals(curUnit)) {
            return Utils.get2PrecisionFloat(result * 1.8f + 32.0f);
        }
        return Utils.get2PrecisionFloat(result);
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
            return Utils.get2PrecisionFloat(result / 1000);
        } else if ("lb".equals(curUnit)) {
            return Utils.get2PrecisionFloat(result / 2.20f);
        }
        return Utils.get2PrecisionFloat(result);
    }

    public static int spToPx(Resources res, float sp) {
        float density = res.getDisplayMetrics().scaledDensity;
        return (int) (sp * density + 0.5f);
    }

    public static String bakereport2Json(BakeReport imm) {
        String jsonStr = new Gson().toJson(imm);
        return jsonStr;
    }

    private static void sendJsonData(final String jsonStr) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8080/CoffeeSecret/bake/add";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
                Log.e("codelevex", "wwwwww");
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.e("codelevex", "成功获取");
                    } else {
                        Log.e("codelevex", "失败啦！！！:" + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
