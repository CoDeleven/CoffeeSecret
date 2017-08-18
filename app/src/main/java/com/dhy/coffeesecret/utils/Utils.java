package com.dhy.coffeesecret.utils;


import android.graphics.Color;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.views.BakeDegreeCircleSeekBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class Utils {
    public static final String KG = "kg", G = "g", LB = "lb";
    public static float get1PrecisionFloat(double val){
        return (int)(val * 10) / 10.0f;
    }
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
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        String curUnit = MyApplication.temperatureUnit;
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


    public static int getContainerIcon(String icon) {
        int id = -1;
        switch (icon) {
            case "aa":
                id = R.drawable.ic_container_aa;
                break;
            case "ac":
                id = R.drawable.ic_container_aa;
                break;
            case "ae":
                id = R.drawable.ic_container_aa;
                break;
            case "al":
                id = R.drawable.ic_container_aa;
                break;
            default:
                id = R.drawable.ic_container_add_bean;
        }
        return id;
    }

    public static String getContainerIconName(int id) {
        String name;
        switch (id) {
            case R.drawable.ic_container_aa:
                name = "aa";
                break;
            case R.drawable.ic_container_ac:
                name = "ac";
                break;
            case R.drawable.ic_container_ae:
                name = "ae";
                break;
            case R.drawable.ic_container_al:
                name = "al";
                break;
            default:
                name = "default";
        }
        return name;
    }

    public static String getCommaBefore(float temperature){
        // 处理后只剩两位小数,并且转换为当前的温度单位
        float processValue = Utils.getCrspTempratureValue(temperature + "");
        int commaBefore = (int)(processValue * 100) / 100;
        return commaBefore + "";
    }

    public static String getCommaAfter(float temperature){
        // 处理后只剩两位小数,并且转换为当前的温度单位
        float processValue = Utils.getCrspTempratureValue(temperature + "");
        int commaAfter = (int)(processValue * 100) % 100;
        return String.format("%02d", Math.abs(commaAfter));
    }

    /**
     * 获取某个百分比位置的颜色
     * @param radio 取值[0,1]
     * @return
     */
    public static int getColor(float radio) {
        // 反转数组
        int[] colors = ArrayUtil.reverseIntArray(BakeDegreeCircleSeekBar.colors);
        float[] positions = BakeDegreeCircleSeekBar.positions;

        int startColor;
        int endColor;
        if (radio >= 1) {
            return colors[colors.length - 1];
        }
        for (int i = 0; i < positions.length; i++) {
            if (radio <= positions[i]) {
                if (i == 0) {
                    return colors[0];
                }
                startColor = colors[i - 1];
                endColor = colors[i];
                float areaRadio = getAreaRadio(radio,positions[i-1],positions[i]);
                return getColorFrom(startColor,endColor,areaRadio);
            }
        }
        return -1;
    }

    private static float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     *  取两个颜色间的渐变区间 中的某一点的颜色
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    private static int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.rgb(red, greed, blue);
    }
}
