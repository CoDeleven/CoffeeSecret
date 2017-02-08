package com.dhy.coffeesecret.utils;

import android.content.res.Resources;

/**
 * Created by CoDeleven on 17-2-2.
 */

public class UnitConvert {
    public static int dp2px(Resources res, float dp) {
        float density = res.getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int sp2px(Resources res, float sp) {
        float density = res.getDisplayMetrics().scaledDensity;
        return (int) (sp * density + 0.5f);

    }
}
