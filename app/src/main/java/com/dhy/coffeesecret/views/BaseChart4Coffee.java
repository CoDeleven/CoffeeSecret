package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.dhy.coffeesecret.model.UniversalConfiguration;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class BaseChart4Coffee extends LineChart {
    public final static int BEANLINE = 1, ACCBEANLINE = -1, INWINDLINE = 2, ACCINWINDLINE = -2, OUTWINDLINE = 3, ACCOUTWINDLINE = -3;
    private UniversalConfiguration mConfig;
    private Map<Integer, String> labels = new HashMap<>();
    private Map<Integer, ILineDataSet> lines = new HashMap<>();

    {
        labels.put(BEANLINE, "豆温");
        labels.put(ACCBEANLINE, "豆升温");
        labels.put(INWINDLINE, "进风温");
        labels.put(ACCINWINDLINE, "进风升温");
        labels.put(OUTWINDLINE, "出风温");
        labels.put(ACCOUTWINDLINE, "出风升温");

    }

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

    private void initConfig() {
        // 启用触屏手势
        setTouchEnabled(true);
        // 设置没有描述
        getDescription().setEnabled(false);
        // 设置摩擦系数？
        setDragDecelerationFrictionCoef(0.9f);
        // 启用放大缩小
        setDragEnabled(true);
        setScaleEnabled(true);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(true);
        setBackgroundColor(Color.WHITE);

        Legend l = getLegend();


        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        // x轴部分
        XAxis xAxis = getXAxis();

        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.parseColor("#868d9b"));
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.rgb(232, 238, 249));
        xAxis.setGridLineWidth(2f);
        xAxis.setDrawAxisLine(false);
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

    public void addLine(int lineIndex) {
        LineDataSet set = new LineDataSet(new ArrayList<Entry>(Arrays.asList(new Entry(0, 0))), labels.get(lineIndex));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set.setDrawCircleHole(false);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        switch (lineIndex) {
            case BEANLINE:
                set.setColor(mConfig.getBeanColor());
                break;
            case ACCBEANLINE:
                set.setColor(mConfig.getBeanColor());
                break;
            case INWINDLINE:
                set.setColor(mConfig.getInwindColor());
                break;
            case ACCINWINDLINE:
                set.setColor(mConfig.getInwindColor());
                break;
            case OUTWINDLINE:
                set.setColor(mConfig.getOutwindColor());
                break;
            case ACCOUTWINDLINE:
                set.setColor(mConfig.getOutwindColor());
                break;
        }

        lines.put(lineIndex, set);
        setData(new LineData(new ArrayList<ILineDataSet>(lines.values())));
    }

    public void removeLine(int lineIndex) throws Exception {
        if (lines.containsKey(lineIndex)) {
            lines.remove(lineIndex);
        } else {
            throw new Exception("No " + lineIndex + " Line");
        }
    }


}
