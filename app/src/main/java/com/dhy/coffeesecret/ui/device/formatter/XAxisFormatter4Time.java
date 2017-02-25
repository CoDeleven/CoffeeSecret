package com.dhy.coffeesecret.ui.device.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by CoDeleven on 17-1-17.
 * 将x轴转换为时间
 */

public class XAxisFormatter4Time implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return formatString2Time(value);
    }
    public static String formatString2Time(float value){
        int time = (int) value;
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds);
    }
}
