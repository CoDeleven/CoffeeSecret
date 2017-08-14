package com.dhy.coffeesecret.model.chart;

import com.dhy.coffeesecret.model.IBaseView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

/**
 * Created by CoDeleven on 17-8-14.
 */

public interface IChartView extends IBaseView {
    void updateChart(Entry newEntry, int lineIndex, boolean toRefresh);
    void addLine(LineDataSet set, int lineIndex, boolean isAcc);
    void enableReferLine(LineDataSet set);
}
