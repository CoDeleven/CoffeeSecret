package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.device.formatter.XAxisFormatter4Time;
import com.dhy.coffeesecret.utils.SettingTool;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class BaseChart4Coffee extends LineChart {

    public final static int BEANLINE = 0, ACCBEANLINE = 1, INWINDLINE = 2, ACCINWINDLINE = 3, OUTWINDLINE = 4, ACCOUTWINDLINE = 5, REFERLINE = 6;
    private static Map<Integer, String> labels = new HashMap<>();

    static {
        labels.put(BEANLINE, "豆温");
        labels.put(ACCBEANLINE, "豆升温");
        labels.put(INWINDLINE, "进风温");
        labels.put(ACCINWINDLINE, "进风升温");
        labels.put(OUTWINDLINE, "出风温");
        labels.put(ACCOUTWINDLINE, "出风升温");
        labels.put(REFERLINE, "");
    }

    private UniversalConfiguration mConfig;
    private Map<Integer, ILineDataSet> lines = new HashMap<>();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            invalidate();
            return false;
        }
    });

    public BaseChart4Coffee(Context context) {
        this(context, null);
    }

    public BaseChart4Coffee(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChart4Coffee(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mConfig = SettingTool.getConfig(context);
        initConfig();
    }

    public static String getLableByIndex(int index) {
        return labels.get(index);
    }

    /**
     * 通过获取用户的个性化设置初始化
     */
    private void initConfig() {

        // 启用触屏手势
        setTouchEnabled(true);
        // 设置没有描述
        getDescription().setEnabled(false);
        // 设置摩擦系数？
        setDragDecelerationFrictionCoef(0.9f);
        // 启用放大缩小
        setDragEnabled(true);
        setScaleXEnabled(true);
        setScaleYEnabled(false);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(true);
        setBackgroundColor(Color.WHITE);

        setPinchZoom(true);
        Legend l = getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // x轴部分
        XAxis xAxis = getXAxis();

        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.parseColor("#868d9b"));
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.rgb(232, 238, 249));
        xAxis.setGridLineWidth(2f);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(mConfig.getMaxX() * 60);
        // 设置x轴的位置在下
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置时间格式转换
        xAxis.setValueFormatter(new XAxisFormatter4Time());

        // 左边y轴部分
        YAxis leftAxis = getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#868d9b"));
        // 设置最小值
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(mConfig.getMaxLeftY());
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.unit = mConfig.getTempratureUnit();

        // 右边y轴部分
        YAxis rightAxis = getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#868d9b"));
        rightAxis.setAxisMaximum(mConfig.getMaxRightY());
        // 设置最小值
        rightAxis.setAxisMinimum(0f);
        rightAxis.setDrawGridLines(false);
        rightAxis.unit = mConfig.getTempratureUnit();
        // rightAxis.setDrawZeroLine(false);
        // rightAxis.setGranularityEnabled(false);
    }

    public void addTempratureLine(int lineIndex, boolean isAcc) {
        LineDataSet set = new LineDataSet(new ArrayList<Entry>(), labels.get(lineIndex));
        if (isAcc) {
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        } else {
            set.setAxisDependency(YAxis.AxisDependency.LEFT);

        }
        set.setCircleColor(Color.parseColor("#6774a4"));
        // set.setCircleColorHole(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        // set.setCircleHoleRadius(2f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        if (lineIndex == BEANLINE) {
            set.setDrawCircleHole(true);
            set.setDrawCircles(true);
        }

        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        switch (lineIndex) {
            case BEANLINE:
                set.setColor(mConfig.getBeanColor());
                break;
            case ACCBEANLINE:
                set.setColor(mConfig.getAccBeanColor());
                break;
            case INWINDLINE:
                set.setColor(mConfig.getInwindColor());
                break;
            case ACCINWINDLINE:
                set.setColor(mConfig.getAccInwindColor());
                break;
            case OUTWINDLINE:
                set.setColor(mConfig.getOutwindColor());
                break;
            case ACCOUTWINDLINE:
                set.setColor(mConfig.getAccOutwindColor());
                break;
            case REFERLINE:
                set.setColor(Color.BLACK);
        }
        lines.put(lineIndex, set);
        setData(new LineData(new ArrayList<ILineDataSet>(lines.values())));
    }

    /**
     * 添加一个温度曲线
     *
     * @param lineIndex
     */
    private void addTempratureLine(int lineIndex) {
        addTempratureLine(lineIndex, false);
    }

    /**
     * 显示曲线
     *
     * @param lineIndex
     * @return
     */
    public boolean showLine(int lineIndex) {
        lines.get(lineIndex).setVisible(true);
        mHandler.sendEmptyMessage(0);
        return true;
    }

    /**
     * 隐藏曲线
     *
     * @param lineIndex
     * @return
     */
    public boolean hideLine(int lineIndex) {
        lines.get(lineIndex).setVisible(false);
        mHandler.sendEmptyMessage(0);
        return true;
    }

    /**
     * 给指定的曲线添加一个值
     *
     * @param beanData  Entry值
     * @param lineIndex 曲线编号
     */
    public void addOneDataToLine(Entry beanData, int lineIndex) {
        LineDataSet beanLine = (LineDataSet) lines.get(lineIndex);
        beanLine.addEntry(beanData);
        mHandler.sendMessage(new Message());
    }

    /**
     * 增加新的数据给对应编号的曲线
     *
     * @param beanDatas 一组Entry数据
     * @param lineIndex 曲线的编号
     */
    public void addNewDatas(List<Entry> beanDatas, int lineIndex) {
     /*   ILineDataSet beanLine = new LineDataSet(beanDatas, labels.get(lineIndex));
        lines.put(lineIndex, beanLine);
        LineData lineData = getData();
        if(lineData == null){
            lineData = new LineData();
        }*/

        ((LineDataSet) lines.get(lineIndex)).setValues(beanDatas);

        mHandler.sendEmptyMessage(0);
    }

    /**
     * 按照用户设置，初始化曲线,默认显示全部曲线
     */
    public void initLine() {
        addTempratureLine(BaseChart4Coffee.BEANLINE);
        addTempratureLine(BaseChart4Coffee.INWINDLINE);
        addTempratureLine(BaseChart4Coffee.OUTWINDLINE);
        addTempratureLine(BaseChart4Coffee.ACCBEANLINE, true);
        addTempratureLine(BaseChart4Coffee.ACCINWINDLINE, true);
        addTempratureLine(BaseChart4Coffee.ACCOUTWINDLINE, true);
    }

    /**
     * 启用参考曲线
     *
     * @param entries 曲线数据
     */
    public void enableReferLine(List<Entry> entries) {
        LineDataSet set = new LineDataSet(entries, "参考曲线");
        lines.put(BaseChart4Coffee.REFERLINE, set);

        set.setCircleColor(Color.parseColor("#6774a4"));
        // set.setCircleColorHole(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        // set.setCircleHoleRadius(2f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);

        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setColor(Color.BLACK);

        setData(new LineData(new ArrayList<ILineDataSet>(lines.values())));
    }

    /**
     * 在initLine()之后调用可以更改曲线颜色
     *
     * @param color 带#的颜色字串
     * @param index 曲线的编号
     */
    public void changeColorByIndex(String color, int index) {
        ((LineDataSet) lines.get(index)).setColor(Color.parseColor(color));
        notifyDataSetChanged();
    }

}
