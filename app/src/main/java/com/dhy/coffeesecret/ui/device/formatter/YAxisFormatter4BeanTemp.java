package com.dhy.coffeesecret.ui.device.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by CoDeleven on 17-1-17.
 */

public class YAxisFormatter4BeanTemp implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return value + "";
    }
}
