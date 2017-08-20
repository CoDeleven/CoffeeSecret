package com.dhy.coffeesecret.model.chart;

import com.dhy.coffeesecret.model.IBaseView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-8-14.
 */

public class Presenter4Chart {
    private static final String TAG = Presenter4Chart.class.getSimpleName();
    private IChartView mViewOperator;
    private Model4Chart mModelOperator;
    private static Presenter4Chart mPresenter;
    private Map<Integer, ILineDataSet> lines = new HashMap<>();
    private static Map<Integer, String> labels = new HashMap<>();
    // 参考曲线
    private List<Entry> referEntries;

    // 用于记录真实的，需要保存的数据,貌似没用
    // private TemperatureSet temperatureSet;

    /*public void setTemperatureSet(TemperatureSet temperatureSet) {
        this.temperatureSet = temperatureSet;
    }*/

    static {
        labels.put(Model4Chart.BEANLINE, "豆温");
        labels.put(Model4Chart.ACCBEANLINE, "豆升温");
        labels.put(Model4Chart.INWINDLINE, "进风温");
        labels.put(Model4Chart.ACCINWINDLINE, "进风升温");
        labels.put(Model4Chart.OUTWINDLINE, "出风温");
        labels.put(Model4Chart.ACCOUTWINDLINE, "出风升温");
        labels.put(Model4Chart.REFERLINE, "");
    }

    public Presenter4Chart(){
        // FIXME 手动设置 -> 读取配置文件
        mModelOperator = new Model4Chart(4, 4);
    }

    public void setView(IBaseView baseView){
        mViewOperator = (IChartView)baseView;
    }

    public void initLines(){
        // 总共6条曲线
        for(int i = 1; i < 7; ++i){
            LineDataSet set = new LineDataSet(new ArrayList<Entry>(), labels.get(new Integer(i)));
            lines.put(new Integer(i), set);
            mViewOperator.addLine(set, i, i >= 4 && i < 7);
        }
    }

    public void continueLastLines(){
        for(int i = 1; i < 7; ++i){
            LineDataSet set = (LineDataSet)lines.get(new Integer(i));
            mViewOperator.addLine(set, i, i >= 4 && i < 7);
        }
    }

    /**
     * 启用参考曲线
     * @param entries
     */
    public void enableReferLine(List<Entry> entries){
        // 保存参考线的数据
        this.referEntries = entries;
        LineDataSet set = new LineDataSet(entries, "");
        // 添加一个参考曲线
        lines.put(Model4Chart.REFERLINE, set);
        // 通知视图启用参考曲线
        mViewOperator.enableReferLine(set);
    }

    /**
     * 切换线条的可见性
     * @param lineIndex 线条的编号
     */
    public void toggleLineVisible(int lineIndex){
        boolean isVisible = lines.get(lineIndex).isVisible();
        lines.get(lineIndex).setVisible(!isVisible);
        mViewOperator.updateChart();
    }


    /**
     * 动态添加一个数据，并及时刷新图表
     *
     * @param immData
     * @param lineIndex
     */
    public void dynamicAddDataImm(Entry immData, int lineIndex, boolean toRefresh) {
        double mockTemperature = mModelOperator.getMockData(immData, lineIndex);
        immData.setY((float) mockTemperature);
        // 设置新的Entry去line里面
        lines.get(new Integer(lineIndex)).addEntry(immData);
        // 更新图表
        if(toRefresh){
            mViewOperator.zoomXAxis(immData.getX());
            mViewOperator.updateChart();
        }
    }

    /**
     * 添加一系列的数据,内部实现通过dynamicAddData()
     *
     * @param batchData
     * @param lineIndex
     */
    public void addBatchData(List<Entry> batchData, int lineIndex) {
        for (int i = 0; i < batchData.size(); i++) {
            // 如果是最后一个元素，那么在添加结束后刷新
            if (i == batchData.size() - 1) {
                dynamicAddDataImm(batchData.get(i), lineIndex, true);
            }
            dynamicAddDataImm(batchData.get(i), lineIndex, false);
        }
    }

    public void clear(){
        mViewOperator.clear();
        initLines();
        if(referEntries != null && referEntries.size() > 0){
            enableReferLine(referEntries);
        }
    }

    public static Presenter4Chart newInstance() {
        if(mPresenter == null){
            mPresenter = new Presenter4Chart();
        }
        return mPresenter;
    }

    public void refreshChart(){
        mViewOperator.updateChart();
    }
}
