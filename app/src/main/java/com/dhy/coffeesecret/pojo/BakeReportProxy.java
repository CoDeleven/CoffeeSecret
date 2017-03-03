package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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

public class BakeReportProxy {
    private BakeReport bakeReport;
    private Map<Integer, ILineDataSet> lines = new HashMap<>();

    private List<Entry> entriesWithEvents;

    public BakeReportProxy(BakeReport bakeReport) {
        this.bakeReport = bakeReport;

        // 将获取到的基本数据类型转换为终端可以直接使用的类型
        seriData();
    }



    public BakeReportProxy() {
        this.bakeReport = new BakeReport();
    }

    public void setBakeReport(BakeReport bakeReport) {
        this.bakeReport = bakeReport;
    }

    public float getEnvTemp() {
        return Float.parseFloat(bakeReport.getEndTemperature());
    }

    public float getStartTemp() {
        return Float.parseFloat(bakeReport.getStartTemperature());
    }

    private void seriData() {
        List<Float> timex = bakeReport.getTimex();
        ILineDataSet beanSet = new LineDataSet(convertTempData2Entry(bakeReport.getBeanTemps(), timex), getLableByIndex(BEANLINE));
        ILineDataSet inwindSet = new LineDataSet(convertFloatData2Entry(bakeReport.getInwindTemps(), timex), getLableByIndex(INWINDLINE));
        ILineDataSet outwindSet = new LineDataSet(convertFloatData2Entry(bakeReport.getOutwindTemps(), timex), getLableByIndex(OUTWINDLINE));
        ILineDataSet accBeanSet = new LineDataSet(convertFloatData2Entry(bakeReport.getAccBeanTemps(), timex), getLableByIndex(ACCBEANLINE));
        ILineDataSet accInwindSet = new LineDataSet(convertFloatData2Entry(bakeReport.getAccInwindTemps(), timex), getLableByIndex(ACCINWINDLINE));
        ILineDataSet accOutwindSet = new LineDataSet(convertFloatData2Entry(bakeReport.getAccOutwindTemps(), timex), getLableByIndex(ACCOUTWINDLINE));
        lines.put(BEANLINE, beanSet);
        lines.put(INWINDLINE, inwindSet);
        lines.put(OUTWINDLINE, outwindSet);
        lines.put(ACCBEANLINE, accBeanSet);
        lines.put(ACCINWINDLINE, accInwindSet);
        lines.put(ACCOUTWINDLINE, accOutwindSet);
    }

    public void deseriData(LineData linedate){
        LineDataSet beanSet = (LineDataSet)linedate.getDataSetByLabel("豆温", true);
        List<BakeReport.EntryTemp> entryTemp = new ArrayList<>();
        for(Entry entry : beanSet.getValues()){
            entryTemp.add(bakeReport.new EntryTemp(entry.getY(), entry.getEvent().getDescription(), entry.getEvent().getCurStatus()));
        }
        bakeReport.setBeanTemps(entryTemp);
        lines.put(BEANLINE, beanSet);

        LineDataSet inwindSet = (LineDataSet)linedate.getDataSetByLabel("进风温", true);
        List<Float> inwind = new ArrayList<>();
        for(Entry entry: inwindSet.getValues()){
            inwind.add(entry.getY());
        }
        bakeReport.setInwindTemps(inwind);
        lines.put(INWINDLINE, inwindSet);

        LineDataSet outwindSet = (LineDataSet)linedate.getDataSetByLabel("出风温", true);
        List<Float> outwind = new ArrayList<>();
        for(Entry entry: outwindSet.getValues()){
            outwind.add(entry.getY());
        }
        bakeReport.setOutwindTemps(outwind);
        lines.put(OUTWINDLINE, outwindSet);

        LineDataSet accBeanSet = (LineDataSet)linedate.getDataSetByLabel("豆升温", true);
        List<Float> accBean = new ArrayList<>();
        for(Entry entry: accBeanSet.getValues()){
            accBean.add(entry.getY());
        }
        bakeReport.setAccBeanTemps(accBean);
        lines.put(ACCBEANLINE, accBeanSet);

        LineDataSet accInwindSet = (LineDataSet)linedate.getDataSetByLabel("进风升温", true);
        List<Float> accInwind = new ArrayList<>();
        for(Entry entry: accInwindSet.getValues()){
            accInwind.add(entry.getY());
        }
        bakeReport.setAccInwindTemps(accInwind);
        lines.put(ACCINWINDLINE, accInwindSet);

        LineDataSet accOutwindSet = (LineDataSet)linedate.getDataSetByLabel("出风升温", true);
        List<Float> accOutwind = new ArrayList<>();
        for(Entry entry: accOutwindSet.getValues()){
            accOutwind.add(entry.getY());
        }
        bakeReport.setAccOutwindTemps(accOutwind);
        lines.put(ACCOUTWINDLINE, accOutwindSet);

    }

    public LineDataSet getLineDataSetByIndex(int index) {
        return (LineDataSet) lines.get(index);
    }

    /**
     * 将服务器端的豆温数据转换成本地可以直接使用的数据
     *
     * @param entryTemps Entry的可序列换类
     * @param timex      时间节点
     * @return
     */
    public List<Entry> convertTempData2Entry(List<BakeReport.EntryTemp> entryTemps, List<Float> timex) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < timex.size(); ++i) {
            BakeReport.EntryTemp entryTemp = entryTemps.get(i);
            Entry entry = new Entry(timex.get(i), entryTemp.y);
            if (!"".equals(entryTemp.event)) {
                entry.setEvent(new Event(entryTemp.curStatus, entryTemp.event));
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
            Entry entry = new Entry(timex.get(i), temp.get(0));
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

    public void setDate(String date) {
        bakeReport.setDate(date);
    }

    public void setBeanInfoSimples(List<BeanInfoSimple> beanInfoSimples) {
        bakeReport.setBeanInfoSimples(beanInfoSimples);
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

    public void setEntriesWithEvents(List<Entry> entriesWithEvents) {
        this.entriesWithEvents = entriesWithEvents;
    }

    public List<Entry> getEntriesWithEvents() {
        return entriesWithEvents;
    }

    public void setCookedBeanWeight(float cookedBeanWeight) {
        bakeReport.setCookedBeanWeight(cookedBeanWeight + "");
    }

    public void setBakeDegree(float bakeDegree) {
        bakeReport.setRoastDegree(bakeDegree + "");
    }

    public BakeReport getBakeReport() {
        return bakeReport;
    }

}
