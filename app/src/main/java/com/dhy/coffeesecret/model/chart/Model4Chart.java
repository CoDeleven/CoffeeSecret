package com.dhy.coffeesecret.model.chart;

import com.dhy.coffeesecret.model.base.BaseModel;
import com.github.mikephil.charting.data.Entry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by CoDeleven on 17-8-14.
 */

public class Model4Chart extends BaseModel implements IChartModel {
    public final static int BEANLINE = 1;
    public final static int ACCBEANLINE = 4;
    public final static int INWINDLINE = 2;
    public final static int ACCINWINDLINE = 5;
    public final static int OUTWINDLINE = 3;
    public final static int ACCOUTWINDLINE = 6;
    public final static int REFERLINE = 7;
    // 这里的温度队列只需要保存4个，以便追溯
    private static final int DEFAULT_QUEUE_SIZE = 4;
    private static final int DEFAULT_ACC_QUEUE_SIZE = 15;
    // 自定义的queuesize
    private int queueSize = -1;
    // 自定义的accQueueSize
    private int accQueueSize = -1;
    private Map<Integer, LinkedList<Entry>> weightedObservedPointsMap = new HashMap<>();
    // private Map<Integer, PolynomialCurveFitter> fitters = new HashMap<>();

    {
        for (int i = 1; i < 8; ++i) {
            weightedObservedPointsMap.put(i, new LinkedList<Entry>());
        }
    }

    public Model4Chart(int temperatureSmooth, int temperatureAccSmooth) {
        // fitters.put(BEANLINE, PolynomialCurveFitter.create(temperatureSmooth));
        // fitters.put(ACCBEANLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
        // fitters.put(INWINDLINE, PolynomialCurveFitter.create(temperatureSmooth));
        // fitters.put(ACCINWINDLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
        // fitters.put(OUTWINDLINE, PolynomialCurveFitter.create(temperatureSmooth));
        // fitters.put(ACCOUTWINDLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
    }

    public double getMockData(Entry nextData, int lineIndex) {
        Queue<Entry> queue = weightedObservedPointsMap.get(lineIndex);
        // 维持queue为 DEFAULT_QUEUE_SIZE 的大小
        if (queue.size() >= (queueSize == -1 ? DEFAULT_QUEUE_SIZE : queueSize)) {
            queue.poll();
        }
        queue.offer(nextData);

        return nextData.getY();
    }

    public double getMockAccData(Entry nextData, int lineIndex) {
        LinkedList<Entry> queue = weightedObservedPointsMap.get(lineIndex);
        // 获取前n秒的问题,因为温度一定先于升温速率加入观察队列，所以到这一步，已经添加了该秒的温度，所以要比实际 + 1
        double firstSecondTemperature = getFirstNSecondsTemperature(true, lineIndex);
        double firstNSecondsTemperature = getFirstNSecondsTemperature(false, lineIndex);
        // （当前所处秒 - 3s前的问题） * 20
        double curAccTemperature = (firstNSecondsTemperature - firstSecondTemperature) * 20;

        double firstNSumAccTemperature = getFirstNSecondsSumAccTemperature(lineIndex);

        // 平均两次
        for (int i = 0; i < 2; ++i) {
            curAccTemperature = getAvg((curAccTemperature + firstNSumAccTemperature), DEFAULT_ACC_QUEUE_SIZE + 1);
        }

        int curQueueSize = (accQueueSize == -1 ? DEFAULT_ACC_QUEUE_SIZE : accQueueSize);
        if (queue.size() >= curQueueSize) {
            queue.poll();
        }

        queue.offer(new Entry(nextData.getX(), (float) curAccTemperature));

        return curAccTemperature;
    }

    private double getFirstNSecondsTemperature(boolean isHead, int accTemperatureIndex) {
        LinkedList<Entry> queue = weightedObservedPointsMap.get(accTemperatureIndex - 3);

        Entry element;
        if(isHead){
            if(queue.size() < DEFAULT_QUEUE_SIZE){
                element = null;
            }else{
                element = queue.getFirst();
            }

        }else{
            element = queue.getLast();
        }

        if(element != null){
            return element.getY();
        }

        return 0;
    }

    private double getFirstNSecondsSumAccTemperature(int accTemperatureIndex) {
        LinkedList<Entry> queue = weightedObservedPointsMap.get(accTemperatureIndex);
        double sum = 0;
        for (Entry entry : queue) {
            sum += entry.getY();
        }
        return sum;
    }

    private double getAvg(double sum, double n) {
        return sum / n;
    }
}
