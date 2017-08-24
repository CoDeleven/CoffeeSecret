package com.dhy.coffeesecret.model.line_color;

import com.dhy.coffeesecret.pojo.LinesColor;

import java.util.Map;

/**
 * Created by CoDeleven on 17-8-24.
 */

public interface IColorLineModel {
    void updateColor(String key, int position);
    LinesColor saveAllColors(Map<String, Integer> colors);
}
