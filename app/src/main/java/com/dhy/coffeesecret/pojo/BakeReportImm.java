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

public class BakeReportImm implements Serializable {
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
    private transient List<BeanInfoSimple> beanInfos;
    private transient LineData lineData;
    private transient List<Entry> entriesWithEvents;
    public BakeReportImm() {

    }

    public List<Entry> getEntriesWithEvents() {
        return entriesWithEvents;
    }

    public void setEntriesWithEvents(List<Entry> entriesWithEvents) {
        this.entriesWithEvents = entriesWithEvents;
    }

    public String getBakeDate() {
        return bakeDate;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
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

    public List<Float> getTimex() {
        return timex;
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

    public LineData getLineData() {
        return lineData;
    }

    public class EntryPojo implements Serializable {
        public float y;
        public String event;
        public int curStatus;

        private transient Entry entry;

        public EntryPojo(Entry entry) {
            this.entry = entry;

        }

        public String getEvent() {
            return entry.getEvent().getDescription();
        }
        public int getCurStatus() {
            return entry.getEvent().getCurStatus();
        }

        public float getY() {
            return entry.getY();
        }

        public EntryPojo() {

        }
    }

}
