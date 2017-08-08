package com.dhy.coffeesecret.utils;

/**
 * Created by mxf on 2017/2/20.
 */
public class ArrayUtil {
    public static float[] merge(float[] t1, float[] t2) {

        if (t1 != null && t2 != null) {
            float[] f = new float[t1.length + t2.length];
            for (int i = 0; i < t1.length; i++) {
                f[i] = t1[i];
            }

            for (int i = 0; i < t2.length; i++) {
                f[i + t1.length] = t2[i];
            }
            return f;
        } else if (t1 != null) {
            return t1;
        } else if (t2 != null) {
            return t2;
        }
        return null;
    }
    public static int[] reverseIntArray(int[] array){
        int[] new_array = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = array[array.length - i - 1];
        }
        return new_array;
    }
    public static float[] reverseFloatArray(float[] array) {
        float[] new_array = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = array[array.length - i - 1];
        }
        return new_array;
    }
}
