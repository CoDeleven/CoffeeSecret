package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-3-8.
 */

public class TempratureSet implements Serializable{

    private List<Float> beanTemps = new ArrayList<>();
    private List<Float> inwindTemps = new ArrayList<>();
    private List<Float> outwindTemps = new ArrayList<>();
    private List<Float> accBeanTemps = new ArrayList<>();
    private List<Float> accInwindTemps = new ArrayList<>();
    private List<Float> accOutwindTemps = new ArrayList<>();
    // 第一个String为时间，第二个String为事件详情和事件类别编号的组合，用:来划分
    private Map<String, String> events = new HashMap<>();
    private List<Float> timex = new ArrayList<>();


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

    public List<Float> getBeanTemps() {
        return beanTemps;
    }

    public void setBeanTemps(List<Float> beanTemps) {
        this.beanTemps = beanTemps;
    }

    public Map<String, String> getEvents() {
        return events;
    }

    public void setEvents(Map<String, String> events) {
        this.events = events;
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

    public List<Float> getTimex() {
        return timex;
    }

    public void setTimex(List<Float> timex) {
        this.timex = timex;
    }

    public void addBeanTemp(Float beanTemp) {
        this.beanTemps.add(beanTemp);
    }

    public void addInwindTemp(Float inwindTemp) {
        this.inwindTemps.add(inwindTemp);
    }

    public void addOutwindTemp(Float outwindTemp) {
        this.outwindTemps.add(outwindTemp);
    }

    public void addAccBeanTemp(Float accBeanTemp) {
        this.accBeanTemps.add(accBeanTemp);
    }

    public void addAccInwindTemp(Float accInwindTemp) {
        this.accInwindTemps.add(accInwindTemp);
    }

    public void addAccOutwindTemp(Float accOutwindTemp) {
        this.accOutwindTemps.add(accOutwindTemp);
    }

    public void addEvent(String time, String descriptor) {
        this.events.put(time, descriptor);
    }

    public void addTimex(float timex) {
        this.timex.add(timex);
    }
}
