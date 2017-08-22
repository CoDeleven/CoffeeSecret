package com.dhy.coffeesecret.model.chart;

import com.dhy.coffeesecret.model.base.BaseModel;
import com.github.mikephil.charting.data.Entry;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

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
    private static final int MAX_QUEUE_SIZE = 10;
    private Map<Integer, LinkedList<WeightedObservedPoint>> weightedObservedPointsMap = new HashMap<>();
    private Map<Integer, PolynomialCurveFitter> fitters = new HashMap<>();

    {
        for (int i = 1; i < 8; ++i) {
            weightedObservedPointsMap.put(i, new LinkedList<WeightedObservedPoint>());
        }
    }

    public Model4Chart(int temperatureSmooth, int temperatureAccSmooth) {
        fitters.put(BEANLINE, PolynomialCurveFitter.create(temperatureSmooth));
        fitters.put(ACCBEANLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
        fitters.put(INWINDLINE, PolynomialCurveFitter.create(temperatureSmooth));
        fitters.put(ACCINWINDLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
        fitters.put(OUTWINDLINE, PolynomialCurveFitter.create(temperatureSmooth));
        fitters.put(ACCOUTWINDLINE, PolynomialCurveFitter.create(temperatureAccSmooth));
    }

    public double getMockData(Entry nextData, int lineIndex) {
        Queue<WeightedObservedPoint> queue = weightedObservedPointsMap.get(new Integer(lineIndex));
        // 维持queue为 MAX_QUEUE_SIZE 的大小
        if (queue.size() >= MAX_QUEUE_SIZE) {
            queue.poll();
        }
        queue.offer(new WeightedObservedPoint(1d, nextData.getX(), nextData.getY()));

        double[] params = fitters.get(new Integer(lineIndex)).fit(queue);
        return new PolynomialFunction(params).value(nextData.getX());
    }

}
