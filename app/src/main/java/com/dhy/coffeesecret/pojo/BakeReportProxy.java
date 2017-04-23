package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCBEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCINWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.BEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.INWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.OUTWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.getLableByIndex;

/**
 * Created by CoDeleven on 17-3-3.
 */

public class BakeReportProxy implements Serializable{
    private BakeReport bakeReport;
    private Map<Integer, ILineDataSet> lines = new HashMap<>();
    private List<Entry> entriesWithEvents;
    private float rawBeanWeight;
    private Map<String, String> events;


    public BakeReportProxy(BakeReport bakeReport) {
        this.bakeReport = bakeReport;
        if (bakeReport != null) {
            // 将获取到的基本数据类型转换为终端可以直接使用的类型
            deseriData();
        }
    }


    public BakeReportProxy() {
        this.bakeReport = new BakeReport();
    }

    public float getEnvTemp() {
        return Float.parseFloat(bakeReport.getAmbientTemperature());
    }

    public float getStartTemp() {
        return Float.parseFloat(bakeReport.getStartTemperature());
    }

    private void deseriData() {
        TempratureSet set = bakeReport.getTempratureSet();
        List<Float> timex = set.getTimex();
        ILineDataSet beanSet = new LineDataSet(convertTempData2Entry(set.getBeanTemps(), set.getEvents(), timex), getLableByIndex(BEANLINE));
        ILineDataSet inwindSet = new LineDataSet(convertFloatData2Entry(set.getInwindTemps(), timex), getLableByIndex(INWINDLINE));
        ILineDataSet outwindSet = new LineDataSet(convertFloatData2Entry(set.getOutwindTemps(), timex), getLableByIndex(OUTWINDLINE));
        ILineDataSet accBeanSet = new LineDataSet(convertFloatData2Entry(set.getAccBeanTemps(), timex), getLableByIndex(ACCBEANLINE));
        ILineDataSet accInwindSet = new LineDataSet(convertFloatData2Entry(set.getAccInwindTemps(), timex), getLableByIndex(ACCINWINDLINE));
        ILineDataSet accOutwindSet = new LineDataSet(convertFloatData2Entry(set.getAccOutwindTemps(), timex), getLableByIndex(ACCOUTWINDLINE));
        lines.put(BEANLINE, beanSet);
        lines.put(INWINDLINE, inwindSet);
        lines.put(OUTWINDLINE, outwindSet);
        lines.put(ACCBEANLINE, accBeanSet);
        lines.put(ACCINWINDLINE, accInwindSet);
        lines.put(ACCOUTWINDLINE, accOutwindSet);
    }

    public void setTempratureSet(TempratureSet tempratureSet) {
        this.bakeReport.setTempratureSet(tempratureSet);
        deseriData();
    }

    public LineDataSet getLineDataSetByIndex(int index) {
        return (LineDataSet) lines.get(index);
    }


    public List<Entry> convertTempData2Entry(List<Float> beanTemps, Map<String, String> events, List<Float> timex) {

        if (entriesWithEvents == null) {
            entriesWithEvents = new ArrayList<>();
        }
        List<Entry> entries = new ArrayList<>();
        Entry.clearEvents();
        for (int i = 0; i < timex.size(); ++i) {
            Entry entry = new Entry(timex.get(i), beanTemps.get(i));
            String time = timex.get(i) + "";
            String str = "";
            if ((str = events.get(time)) != null) {
                // 转换id
                String id = str.substring(str.lastIndexOf(":") + 1, str.length());
                // 转换描述
                String descriptor = str.substring(0, str.lastIndexOf(":"));
                // 给实体设计事件
                entry.setEvent(new Event(Integer.parseInt(id), descriptor));
                entriesWithEvents.add(entry);
            }
            entries.add(entry);
        }
        return entries;
    }

    /**
     * 将服务器端的Float数据转换成本地可以直接使用的数据
     *
     * @param temp
     * @param timex
     * @return
     */
    public List<Entry> convertFloatData2Entry(List<Float> temp, List<Float> timex) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < timex.size(); ++i) {
            Entry entry = new Entry(timex.get(i), temp.get(i));
            entries.add(entry);
        }
        return entries;
    }

    public String getEndTemp() {
        return bakeReport.getEndTemperature();
    }

    public void setEndTemp(float y) {
        this.bakeReport.setEndTemperature(y + "");
    }

    public String getDevelopTime() {
        return bakeReport.getDevelopmentTime();
    }

    public String getDevelopRate() {
        return bakeReport.getDevelopmentRate();
    }

    public List<BeanInfoSimple> getBeanInfos() {
        return bakeReport.getBeanInfoSimples();
    }

    public String getBakeDate() {
        return bakeReport.getDate();
    }

    public String getDevice() {
        return bakeReport.getDevice();
    }

    public void setDevice(String device) {
        bakeReport.setDevice(device);
    }

    public String getBakeDegree() {
        return bakeReport.getRoastDegree();
    }

    public void setBakeDegree(float bakeDegree) {
        bakeReport.setRoastDegree(bakeDegree + "");
    }

    public void setDate(String date) {
        bakeReport.setDate(date);
    }

    public void setBeanInfoSimples(List<BeanInfoSimple> beanInfoSimples) {
        bakeReport.setBeanInfoSimples(beanInfoSimples);
        computeRawBeanWeight(beanInfoSimples);
    }
    private void computeRawBeanWeight(List<BeanInfoSimple> beanInfoSimples){
        for(BeanInfoSimple simple:beanInfoSimples){
            rawBeanWeight += Float.parseFloat(simple.getUsage());
        }
    }
    public void setDevelopmentTime(String developmentTime) {
        bakeReport.setDevelopmentTime(developmentTime);
    }

    public void setDevelopmentRate(String developmentRate) {
        bakeReport.setDevelopmentRate(developmentRate);
    }

    public void setStartTemperature(String startTemperature) {
        bakeReport.setStartTemperature(startTemperature);
    }

    public void setAmbientTemperature(String ambientTemperature) {
        bakeReport.setAmbientTemperature(ambientTemperature);
    }

    public List<Entry> getEntriesWithEvents() {
        return entriesWithEvents;
    }

    public void setEntriesWithEvents(List<Entry> entriesWithEvents) {
        this.entriesWithEvents = entriesWithEvents;
    }

    public void setCookedBeanWeight(float cookedBeanWeight) {
        bakeReport.setCookedBeanWeight(cookedBeanWeight + "");
    }

    public BakeReport getBakeReport() {
        return bakeReport;
    }

    public void setBakeReport(BakeReport bakeReport) {
        this.bakeReport = bakeReport;
    }

    public void setBakeReport(BakeReport bakeReport, boolean toDesired){
        this.bakeReport = bakeReport;
        deseriData();
        computeRawBeanWeight(bakeReport.getBeanInfoSimples());
    }

    public List<Float> getTimex(){
        return bakeReport.getTempratureSet().getTimex();
    }

    public void setSingleBeanId(long id){
        bakeReport.setBeanId(id);
    }

    public float getRawBeanWeight() {
        return rawBeanWeight;
    }

    public List<Float> getAccBeanTempratures(){
        return bakeReport.getTempratureSet().getAccBeanTemps();
    }
    public List<Float> getAccInwindTempratures(){
        return bakeReport.getTempratureSet().getAccInwindTemps();
    }
    public List<Float> getAccOutwindTempratures(){
        return bakeReport.getTempratureSet().getAccOutwindTemps();
    }
    public List<Float> getTempratureByIndex(int index){
        if(index == BEANLINE){
            return bakeReport.getTempratureSet().getBeanTemps();
        }else if(index == INWINDLINE){
            return bakeReport.getTempratureSet().getInwindTemps();
        }else if(index == OUTWINDLINE){
            return bakeReport.getTempratureSet().getOutwindTemps();
        }
        return new ArrayList<Float>();
    }
    public Event getEventByX(int x){
        if(events == null){
            events = bakeReport.getTempratureSet().getEvents();
        }
        String time = getTimex().get(x) + "";
        String eventStr = events.get(time);
        if(eventStr == null){
            return null;
        }
        Event event = new Event();
        // 存入事件的类型
        event.setCurStatus(Integer.parseInt(eventStr.substring(eventStr.length() - 1, eventStr.length())));
        // 存入事件的具体情况
        event.setDescription(eventStr.substring(0, eventStr.length() - 2));
        return event;
    }
    public float getBreakPointerTime(){
        return bakeReport.getBreakPointerTime();
    }
    public void setBreakPointerTime(float time){
        bakeReport.setBreakPointerTime(time);

    }
    public void setBreakPointerTemprature(Temprature pointerTemprature){
        bakeReport.setBreakPointerTemprature(pointerTemprature.getBeanTemp());
    }
    public float getBreakPointerTemprature(){
        return bakeReport.getBreakPointerTemprature();
    }
    public float getAvgDryTemprature() {
        return bakeReport.getAvgDryTemprature();
    }

    public void setAvgDryTemprature(float avgDryTemprature) {
        this.bakeReport.setAvgDryTemprature(avgDryTemprature);
    }

    public float getAvgDryTime() {
        return this.bakeReport.getAvgDryTime();
    }

    public void setAvgDryTime(float avgDryTime) {
        this.bakeReport.setAvgDryTime(avgDryTime);
    }

    public float getAvgFirstBurstTime() {
        return bakeReport.getAvgFirstBurstTime();
    }

    public void setAvgFirstBurstTime(float avgFirstBurstTime) {
        this.bakeReport.setAvgFirstBurstTime(avgFirstBurstTime);
    }

    public float getAvgFirstBurstTemprature() {
        return this.bakeReport.getAvgFirstBurstTemprature();
    }

    public void setAvgFirstBurstTemprature(float avgFirstBurstTemprature) {
        this.bakeReport.setAvgFirstBurstTemprature(avgFirstBurstTemprature);
    }

    public float getAvgEndTime() {
        return this.bakeReport.getAvgEndTime();
    }

    public void setAvgEndTime(float avgEndTime) {
        this.bakeReport.setAvgEndTime(avgEndTime);
    }

    public float getAvgEndTemprature() {
        return this.bakeReport.getAvgEndTemprature();
    }

    public void setAvgEndTemprature(float avgEndTemprature) {
        this.bakeReport.setAvgEndTemprature(avgEndTemprature);
    }

    public float getGlobalAccBeanTemp() {
        return this.bakeReport.getAvgGlobalBeanTemprature();
    }

    public void setGlobalAccBeanTemp(float globalAccBeanTemp) {
        this.bakeReport.setAvgGlobalBeanTemprature(globalAccBeanTemp);
    }
}
