package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.pojo.TempratureSet;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.device.formatter.XAxisFormatter4Time;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class BaseChart4Coffee extends LineChart {

    public final static int BEANLINE = 1, ACCBEANLINE = 4, INWINDLINE = 2, ACCINWINDLINE = 5, OUTWINDLINE = 3, ACCOUTWINDLINE = 6, REFERLINE = 7;
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

    private Map<Integer, LinkedList<WeightedObservedPoint>> weightedObservedPointsMap = new HashMap<>();
    private Map<Integer, PolynomialCurveFitter> fitters = new HashMap<>();
    private Map<Integer, List<Double>> params = new HashMap<>();
    private TempratureSet set;
    private List<Entry> referEntries;
    private UniversalConfiguration mConfig;
    private Map<Integer, ILineDataSet> lines = new HashMap<>();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            invalidate();
            return false;
        }
    });
    private int tempSmoothNumber = -1;
    private int accTempSmoothNumber = -1;

    {


        weightedObservedPointsMap.put(BEANLINE, new LinkedList<WeightedObservedPoint>());
        weightedObservedPointsMap.put(ACCBEANLINE, new LinkedList<WeightedObservedPoint>());
        weightedObservedPointsMap.put(INWINDLINE, new LinkedList<WeightedObservedPoint>());
        weightedObservedPointsMap.put(OUTWINDLINE, new LinkedList<WeightedObservedPoint>());
        weightedObservedPointsMap.put(ACCOUTWINDLINE, new LinkedList<WeightedObservedPoint>());
        weightedObservedPointsMap.put(ACCINWINDLINE, new LinkedList<WeightedObservedPoint>());


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

        Log.e("BaseChart4Coffee", mConfig.getMaxLeftY() + "");

        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.unit = mConfig.getTempratureUnit();
        // leftAxis.setLabelCount();
        leftAxis.setLabelCount(7, true);
        // 右边y轴部分
        YAxis rightAxis = getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#868d9b"));
        rightAxis.setAxisMaximum(mConfig.getMaxRightY());
        // 设置最小值
        rightAxis.setAxisMinimum(0f);
        rightAxis.setDrawGridLines(false);
        rightAxis.unit = MyApplication.tempratureUnit;
        rightAxis.setLabelCount(7, true);

        tempSmoothNumber = mConfig.getTempratureSmooth();
        accTempSmoothNumber = mConfig.getTempratureAccSmooth();

        fitters.put(BEANLINE, PolynomialCurveFitter.create(mConfig.getTempratureSmooth()));
        fitters.put(ACCBEANLINE, PolynomialCurveFitter.create(mConfig.getTempratureAccSmooth()));
        fitters.put(INWINDLINE, PolynomialCurveFitter.create(mConfig.getTempratureSmooth()));
        fitters.put(ACCINWINDLINE, PolynomialCurveFitter.create(mConfig.getTempratureAccSmooth()));
        fitters.put(OUTWINDLINE, PolynomialCurveFitter.create(mConfig.getTempratureSmooth()));
        fitters.put(ACCOUTWINDLINE, PolynomialCurveFitter.create(mConfig.getTempratureAccSmooth()));
    }

    /**
     * 设置tempratureSet
     *
     * @param set
     */
    public void setTempratureSet(TempratureSet set) {
        this.set = set;
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
        set.setLineWidth(1f);
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


        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        switch (lineIndex) {
            case BEANLINE:
                set.setColor(mConfig.getBeanColor());
                set.setHighLightColor(Color.rgb(244, 117, 117));
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
        LinkedList<WeightedObservedPoint> queue = weightedObservedPointsMap.get(lineIndex);
        if (queue.size() == 50) {
            queue.pop();
        }
        queue.offer(new WeightedObservedPoint(1d, beanData.getX(), beanData.getY()));
        // 此处本应进行set的设置，但是因为引用外界的tempratureSet，则不进行处理
        addOneDataToLine(beanData, lineIndex, true);
    }

    /**
     * 防止addNewDatas不重复invalidate, 让其手动刷新
     *
     * @param beanData  温度实体
     * @param lineIndex 曲线编号
     * @param toRefresh 是否实时刷新
     */
    public void addOneDataToLine(Entry beanData, int lineIndex, boolean toRefresh) {
        LineDataSet beanLine = (LineDataSet) lines.get(lineIndex);

        // 如果是从烘焙报告里出来的，则进行此方法
        if (set != null && !toRefresh) {
            float temp = getMockData(lineIndex, beanData.getX());
            // System.out.println(temp);
            beanData.setY(temp);
        }

        // 如果是从烘焙过程里出来的，则进行此方法
/*        if (beanData.getX() > 1 && set != null && toRefresh) {
            float temp = getMockDataImm(lineIndex, beanData.getX());
            // System.out.println(temp);
            beanData.setY(temp);
        }*/

        beanLine.addEntry(beanData);
        if (toRefresh) {
            mHandler.sendMessage(new Message());
            if (lineIndex == BEANLINE) {
                float afterScaleX = (getXRange() / getScaleX());
                float curPosition = beanData.getX() - (afterScaleX - 5);
                this.moveViewToX(curPosition);
            }
        }
    }

    /**
     * 增加新的数据给对应编号的曲线
     *
     * @param beanDatas 一组Entry数据
     * @param lineIndex 曲线的编号
     */
    public void addNewDatas(List<Entry> beanDatas, int lineIndex) {
        // 因为是直接添加全部的曲线，那么预先处理拟合的曲线函数
        if (weightedObservedPointsMap.get(lineIndex).size() == 0) {
            // 获取对应lineIndex的点
            LinkedList obs = weightedObservedPointsMap.get(lineIndex);
            List<Float> floats = set.getTempratureByIndex(lineIndex);
            List<Float> timex = set.getTimex();
            for (int i = 0; i < floats.size(); ++i) {
                // 添加观测点
                obs.add(new WeightedObservedPoint(1d, timex.get(i), floats.get(i)));
            }
        }

        for (Entry entry : beanDatas) {
            entry.setY(Utils.getCrspTempratureValue(entry.getY() + ""));
            addOneDataToLine(entry, lineIndex, false);
        }
        // TODO 这里获取的数据也均需要求平均值
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 按照用户设置，初始化曲线,默认显示全部曲线
     */
    public void initLine() {
        this.setData(null);
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
        this.referEntries = entries;
        LineDataSet set = new LineDataSet(entries, "");
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

        setData(new LineData(new ArrayList<>(lines.values())));
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

    @Override
    public void clear() {
        super.clear();
        initLine();
        if (referEntries != null && referEntries.size() > 0) {
            enableReferLine(referEntries);
        }
    }

    public float getMockData(int lineIndex, float x) {
        if (params.get(lineIndex) == null) {
            LinkedList queue = weightedObservedPointsMap.get(lineIndex);
            double[] param = fitters.get(lineIndex).fit(queue.subList(0, queue.size()));
            List<Double> temp = new ArrayList<>();
            for (int i = 0; i < param.length; ++i) {
                temp.add(param[i]);
            }
            params.put(lineIndex, temp);
        }
        return getYByXValue(lineIndex, x);
    }

    public float getMockDataImm(int lineIndex, float x) {
        LinkedList queue = weightedObservedPointsMap.get(lineIndex);
        double[] param = fitters.get(lineIndex).fit(queue.subList(0, queue.size()));
        Log.e("wrong", x + "->" + getYByXValue(param, x));
        return getYByXValue(param, x);
    }

    public float getYByXValue(double[] params, float x) {
        float sum = 0;
        for (int i = 0; i < params.length; ++i) {
            sum += params[i] * Math.pow(x, (double) i);
        }
        return sum;
    }

    public float getYByXValue(int lineIndex, float x) {
        float sum = 0;
        List<Double> temp = params.get(lineIndex);
        for (int i = 0; i < temp.size(); ++i) {
            sum += temp.get(i) * Math.pow(x, (double) i);
        }
        return sum;
    }
}
