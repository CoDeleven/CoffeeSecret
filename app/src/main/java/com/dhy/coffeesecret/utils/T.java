package com.dhy.coffeesecret.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Dale'
 * On 2016/4/13.
 */
public class T {

    public static boolean isShow = false; // 是否需要显示
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable runnable = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    private T() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showToast(Context mContext, CharSequence text, Integer message, int toastDuration, int duration) {
        mHandler.removeCallbacks(runnable);
        if (mToast != null) {
            if (!TextUtils.isEmpty(text)) {
                mToast.setText(text);
            } else {
                mToast.setText(message);
            }
        } else {
            if (!TextUtils.isEmpty(text)) {
                mToast = Toast.makeText(mContext, text, toastDuration);
            } else {
                mToast = Toast.makeText(mContext, message, toastDuration);
            }
        }
        mHandler.postDelayed(runnable, duration);

        mToast.show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context context
     * @param message 显示信息
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow)
            showToast(context, message, null, Toast.LENGTH_SHORT, 1000);
    }


    /**
     * 短时间显示Toast
     *
     * @param context context
     * @param message 显示信息
     */
    public static void showShort(Context context, int message) {
        if (isShow)
            showToast(context, null, message, Toast.LENGTH_SHORT, 1000);

    }

    /**
     * 长时间显示Toast
     *
     * @param context context
     * @param message 显示信息
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            showToast(context, message, null, Toast.LENGTH_LONG, 1000);

    }

    /**
     * 长时间显示Toast
     *
     * @param context context
     * @param message 显示信息
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            showToast(context, null, message, Toast.LENGTH_LONG, 1000);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  context
     * @param message  显示信息
     * @param duration 自定义时间
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            showToast(context, message, null, duration, duration);

    }

    /**
     * 自定义显示Toast时间
     *
     * @param context  context
     * @param message  显示信息
     * @param duration 自定义时间
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            showToast(context, null, message, duration, duration);

    }

}
