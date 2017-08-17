package com.dhy.coffeesecret.ui.device.record;

import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.pojo.TemperatureSet;
import com.dhy.coffeesecret.utils.Utils;
import com.github.mikephil.charting.data.Entry;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by CoDeleven on 17-4-21.
 */

public class RecorderSystem extends AbstractTimeSystem {
    private List<Temperature> temperatureList;
    private int startIndex = 0;
    private TemperatureSet temperatureSet;
    private Entry curBeanEntry;
    /**
     * 每次创建新对象的时候都初始化开始时间和温度数据列表
     */
    public RecorderSystem() {
        // 每次新建本对象，相当于 开始烘焙 事件，故需要重置事件时间
        AbstractTimeSystem.startTime = System.currentTimeMillis();
        resetEventTime();
        temperatureList = new LinkedList<>();
        temperatureSet = new TemperatureSet();
    }

    /**
     * 添加温度
     * @param temperature
     * @return 返回添加温度时候的时间
     */
    public float addTemprature(Temperature temperature){
        float curTime = super.getTimeIntervalFromNow();
        temperatureList.add(temperature);

        temperatureSet.addBeanTemp(temperature.getBeanTemp());
        temperatureSet.addInwindTemp(temperature.getInwindTemp());
        temperatureSet.addOutwindTemp(temperature.getOutwindTemp());
        temperatureSet.addAccBeanTemp(temperature.getAccBeanTemp());
        temperatureSet.addAccInwindTemp(temperature.getAccInwindTemp());
        temperatureSet.addAccOutwindTemp(temperature.getAccOutwindTemp());
        temperatureSet.addTimex(curTime);

        curBeanEntry = new Entry(curTime, Utils.getCrspTempratureValue(temperature.getBeanTemp() + ""));
        return curTime;
    }

    /**
     * 获取全局升温速率
     * @return
     */
    public float getGlobalAccTemprature(){
        return getAvgAccBeanTemprature(0, temperatureList.size());
    }

    /**
     * 获取当前开始时间的序列号（用温度列表的个数来表示，1s一个温度）
     * 方便后续获取局部升温速率
     * @return
     */
    public int getCurIndex(){
        return temperatureList.size();
    }

    protected float getAvgAccBeanTemprature(int start, int end){
        float total = 0f;
        for(Temperature temperature : temperatureList.subList(start, end)){
            total += temperature.getAccBeanTemp();
        }
        return total / (end - start);
    }

    public float getAvgAccBeanTemprature(){
        return getAvgAccBeanTemprature(startIndex, temperatureList.size());
    }

    public void startNewEvent(){
        // 保存当前的序号
        startIndex = temperatureList.size();
        resetEventTime();
    }

    public TemperatureSet getTemperatureSet(){
        return temperatureSet;
    }

    public void addEvent(String s, String s1) {
        temperatureSet.addEvent(s, s1);
    }

}
