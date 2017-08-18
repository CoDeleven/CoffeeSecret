package com.dhy.coffeesecret.model.bake;

import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.Temperature;
import com.github.mikephil.charting.data.Entry;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBakeView extends IBaseView{
    void updateChart(Entry entry, int lineIndex);
    void updateTemperatureText(Temperature temperature);

    /**
     * 发展率的状态
     * @param developStatus
     */
    void updateTimer(int developStatus);
    void notifyChartDataChanged();
}
