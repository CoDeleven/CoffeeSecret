package com.dhy.coffeesecret.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CoDeleven on 17-8-24.
 */

public class FormatUtils {
    public static String getTimeWithFormat(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        return String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds);
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
}
