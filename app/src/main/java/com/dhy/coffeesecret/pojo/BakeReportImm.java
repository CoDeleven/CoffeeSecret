package com.dhy.coffeesecret.pojo;

import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-25.
 */

public class BakeReportImm implements Serializable{
    private String bakeDate;
    private String device;
    private Map<Integer, Float> rawBeanWeight;
    private List<EntryPojo> beanTemps = new ArrayList<>();
    private List<Float> inwindTemps = new ArrayList<>();
    private List<Float> outwindTemps = new ArrayList<>();
    private List<Float> accBeanTemps = new ArrayList<>();
    private List<Float> accInwindTemps = new ArrayList<>();
    private List<Float> accOutwindTemps = new ArrayList<>();
    private List<Float> timex = new ArrayList<>();
    private float cookedBeanWeight;
    private int developTime;
    private float developRate;
    private float envTemp;
    private float endTemp;
    private float startTemp;
    private float bakeDegree;
    // 以下字段均不持久化
    private LineData lineData;
    private List<BeanInfoSimple> beanInfos;

    public String getBakeDate() {
        return bakeDate;
    }

    public void setBakeDate(String bakeDate) {
        this.bakeDate = bakeDate;
    }

    public List<BeanInfoSimple> getBeanInfos() {
        return beanInfos;
    }

    public void setBeanInfos(List<BeanInfoSimple> beanInfos) {
        this.beanInfos = beanInfos;
    }

    public float getBakeDegree() {
        return bakeDegree;
    }

    public void setBakeDegree(float bakeDegree) {
        this.bakeDegree = bakeDegree;
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

    public void setTimex(List<Float> timex) {
        this.timex = timex;
    }

    public List<Float> getAccBeanTemps() {
        return accBeanTemps;
    }

    public void setAccBeanTemps(List<Float> accBeanTemps) {
        this.accBeanTemps = accBeanTemps;
    }

    public List<Float> getAccInwindTemps() {
        return accInwindTemps;
    }

    public void setAccInwindTemps(List<Float> accInwindTemps) {
        this.accInwindTemps = accInwindTemps;
    }

    public List<Float> getAccOutwindTemps() {
        return accOutwindTemps;
    }

    public void setAccOutwindTemps(List<Float> accOutwindTemps) {
        this.accOutwindTemps = accOutwindTemps;
    }

    public List<EntryPojo> getBeanTemps() {
        return beanTemps;
    }

    public void setBeanTemps(List<EntryPojo> beanTemps) {
        this.beanTemps = beanTemps;
    }

    public List<Float> getInwindTemps() {
        return inwindTemps;
    }

    public void setInwindTemps(List<Float> inwindTemps) {
        this.inwindTemps = inwindTemps;
    }

    public List<Float> getOutwindTemps() {
        return outwindTemps;
    }

    public void setOutwindTemps(List<Float> outwindTemps) {
        this.outwindTemps = outwindTemps;
    }

    public void lineData2Pojo(LineData lineData){
        this.lineData = lineData;
        for(ILineDataSet dataSet: lineData.getDataSets()){
            DataSet<Entry> temp = (DataSet<Entry>) dataSet;
            switch (dataSet.getLabel()){
                case "豆温":
                    for(Entry entry: temp.getValues()){
                        beanTemps.add(new EntryPojo(entry));
                        timex.add(entry.getX());
                    }
                    break;
                case "豆升温":
                    for(Entry entry: temp.getValues()){
                        accBeanTemps.add(entry.getY());
                    }
                    break;
                case "进风温":
                    for(Entry entry: temp.getValues()){
                        inwindTemps.add(entry.getY());
                    }
                    break;
                case "进风升温":
                    for(Entry entry: temp.getValues()){
                        accInwindTemps.add(entry.getY());
                    }
                    break;
                case "出风温":
                    for(Entry entry: temp.getValues()){
                        outwindTemps.add(entry.getY());
                    }
                    break;
                case "出风升温":
                    for(Entry entry: temp.getValues()){
                        accOutwindTemps.add(entry.getY());
                    }
                    break;

            }
        }
    }


    class EntryPojo implements Serializable{
        public float y;
        public String event;
        public int curStatus;
        public EntryPojo(Entry entry){

            this.y = entry.getY();
            this.event = entry.getEvent().getDescription();
            this.curStatus = entry.getEvent().getCurStatus();
        }
        public EntryPojo(){

        }
    }


    public LineData getLineData() {
        return lineData;
    }

}
