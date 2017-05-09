package com.dhy.coffeesecret.ui.device.record;

import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.pojo.TempratureSet;
import com.dhy.coffeesecret.utils.Utils;
import com.github.mikephil.charting.data.Entry;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by CoDeleven on 17-4-21.
 */

public class RecorderSystem extends AbstractTimeSystem {
    private List<Temprature> tempratureList;
    private int startIndex = 0;
    private TempratureSet tempratureSet = new TempratureSet();
    private Entry curBeanEntry;
    /**
     * 每次创建新对象的时候都初始化开始时间和温度数据列表
     */
    public RecorderSystem() {
        // 每次新建本对象，相当于 开始烘焙 事件，故需要重置事件时间
        AbstractTimeSystem.startTime = System.currentTimeMillis();
        resetEventTime();
        tempratureList = new LinkedList<>();
    }

    /**
     * 添加温度
     * @param temprature
     * @return 返回添加温度时候的时间
     */
    public float addTemprature(Temprature temprature){
        float curTime = super.getTimeIntervalFromNow();
        tempratureList.add(temprature);

        tempratureSet.addBeanTemp(temprature.getBeanTemp());
        tempratureSet.addInwindTemp(temprature.getInwindTemp());
        tempratureSet.addOutwindTemp(temprature.getOutwindTemp());
        tempratureSet.addAccBeanTemp(temprature.getAccBeanTemp());
        tempratureSet.addAccInwindTemp(temprature.getAccInwindTemp());
        tempratureSet.addAccOutwindTemp(temprature.getAccOutwindTemp());
        tempratureSet.addTimex(curTime);

        curBeanEntry = new Entry(curTime, Utils.getCrspTempratureValue(temprature.getBeanTemp() + ""));
        return curTime;
    }

    /**
     * 获取全局升温速率
     * @return
     */
    public float getGlobalAccTemprature(){
        return getAvgAccBeanTemprature(0, tempratureList.size());
    }

    /**
     * 获取当前开始时间的序列号（用温度列表的个数来表示，1s一个温度）
     * 方便后续获取局部升温速率
     * @return
     */
    public int getCurIndex(){
        return tempratureList.size();
    }

    protected float getAvgAccBeanTemprature(int start, int end){
        float total = 0f;
        for(Temprature temprature : tempratureList.subList(start, end)){
            total += temprature.getAccBeanTemp();
        }
        return total / (end - start);
    }

    public float getAvgAccBeanTemprature(){
        return getAvgAccBeanTemprature(startIndex, tempratureList.size());
    }

    public void startNewEvent(){
        // 保存当前的序号
        startIndex = tempratureList.size();
        resetEventTime();
    }

    public TempratureSet getTempratureSet(){
        return tempratureSet;
    }

    public void addEvent(String s, String s1) {
        tempratureSet.addEvent(s, s1);
    }

}
