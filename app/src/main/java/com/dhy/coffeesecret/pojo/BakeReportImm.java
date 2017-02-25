package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-25.
 */

public class BakeReportImm implements Serializable{
    private String baker;
    private String device;
    private Map<Integer, Float> rawBeanWeight;
    private List<List<EntryPojo>> tempratures;
    private List<Float> timex = new ArrayList<>();
    private float cookedBeanWeight;
    private int developTime;
    private float developRate;
    private float envTemp;
    private float endTemp;
    private float startTemp;

    public String getBaker() {
        return baker;
    }

    public void setBaker(String baker) {
        this.baker = baker;
    }

    public float getCookedBeanWeight() {
        return cookedBeanWeight;
    }

    public void setCookedBeanWeight(float cookedBeanWeight) {
        this.cookedBeanWeight = cookedBeanWeight;
    }

    public float getDevelopRate() {
        return developRate;
    }

    public void setDevelopRate(float developRate) {
        this.developRate = developRate;
    }

    public int getDevelopTime() {
        return developTime;
    }

    public void setDevelopTime(int developTime) {
        this.developTime = developTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public float getEndTemp() {
        return endTemp;
    }

    public void setEndTemp(float endTemp) {
        this.endTemp = endTemp;
    }

    public float getEnvTemp() {
        return envTemp;
    }

    public void setEnvTemp(float envTemp) {
        this.envTemp = envTemp;
    }


    public Map<Integer, Float> getRawBeanWeight() {
        return rawBeanWeight;
    }

    public void setRawBeanWeight(Map<Integer, Float> rawBeanWeight) {
        this.rawBeanWeight = rawBeanWeight;
    }

    public float getStartTemp() {
        return startTemp;
    }

    public void setStartTemp(float startTemp) {
        this.startTemp = startTemp;
    }

    public List<Float> getTimex(){
        return timex;
    }

    public BakeReportImm(){

    }

    public void setTempratures(LineData lineData){
        this.tempratures = lineData2Pojo(lineData);
    }

    private List<List<EntryPojo>> lineData2Pojo(LineData lineData){
        boolean isFillX = false;
        List<List<EntryPojo>> outter = new ArrayList<>();
        for(ILineDataSet lineDataSet: lineData.getDataSets()){
            List<EntryPojo> inner = new ArrayList<>();
            for(Entry entry: ((DataSet<Entry>)lineDataSet).getValues()){
                inner.add(new EntryPojo(entry.getY(), entry.getEvent().getDescription(), entry.getEvent().getCurStatus()));
                if(!isFillX){
                    timex.add(entry.getX());
                }
            }
            if(!isFillX){
                isFillX = true;
            }
            outter.add(inner);
        }
        return outter;
    }

    class EntryPojo {
        float y;
        String event;
        int curStatus;
        private EntryPojo(float y, String event, int curStatus){
            this.y = y;
            this.event = event;
            this.curStatus = curStatus;
        }
    }

}
