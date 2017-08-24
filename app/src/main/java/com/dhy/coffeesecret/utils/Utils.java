package com.dhy.coffeesecret.utils;


import android.graphics.Color;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.views.BakeDegreeCircleSeekBar;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class Utils {
    public static final String KG = "kg", G = "g", LB = "lb";
    public static float get1PrecisionFloat(double val){
        return (int)(val * 10) / 10.0f;
    }

    // private static byte charToByte(char c) {
    //     return (byte) "0123456789ABCDEF".indexOf(c);
    // }

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

    public static float get2PrecisionFloat(float val) {
        return ((int) (val * 100)) / 100f;
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
        float processValue = ConvertUtils.getCrspTemperatureValue(temperature + "");
        int commaBefore = (int)(processValue * 100) / 100;
        return commaBefore + "";
    }

    public static String getCommaAfter(float temperature){
        // 处理后只剩两位小数,并且转换为当前的温度单位
        float processValue = ConvertUtils.getCrspTemperatureValue(temperature + "");
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
