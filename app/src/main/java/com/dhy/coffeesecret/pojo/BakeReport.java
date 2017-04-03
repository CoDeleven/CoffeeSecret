package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mxf
 */
public class BakeReport implements Serializable {

    private int id;
    // 烘焙报告名称
    private String name;
    // 设备
    private String device;
    // 熟豆重量
    private String cookedBeanWeight;
    // 烘焙度
    private String roastDegree;

    private TempratureSet tempratureSet;

    // 发展时间
    private String developmentTime;
    // 发展率
    private String developmentRate;
    // 环境温度
    private String ambientTemperature;
    // 结束温度
    private String endTemperature;
    // 入豆温度
    private String startTemperature;
    //烘焙日期
    private String date;
    //豆子缩略信息
    private List<BeanInfoSimple> beanInfoSimples = new ArrayList<>();
    //不需要持久化
//	@JsonIgnore
    private CuppingInfo cuppingInfo;
    //当烘焙报告中只有一种豆子的话 烘焙报告上添加 bean的ID
    private long beanId = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getCookedBeanWeight() {
        return cookedBeanWeight;
    }

    public void setCookedBeanWeight(String cookedBeanWeight) {
        this.cookedBeanWeight = cookedBeanWeight;
    }

    public String getRoastDegree() {
        return roastDegree;
    }

    public void setRoastDegree(String roastDegree) {
        this.roastDegree = roastDegree;
    }

    public String getDevelopmentTime() {
        return developmentTime;
    }

    public void setDevelopmentTime(String developmentTime) {
        this.developmentTime = developmentTime;
    }

    public String getDevelopmentRate() {
        return developmentRate;
    }

    public void setDevelopmentRate(String developmentRate) {
        this.developmentRate = developmentRate;
    }

    public String getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(String ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public String getEndTemperature() {
        return endTemperature;
    }

    public void setEndTemperature(String endTemperature) {
        this.endTemperature = endTemperature;
    }

    public String getStartTemperature() {
        return startTemperature;
    }

    public void setStartTemperature(String startTemperature) {
        this.startTemperature = startTemperature;
    }

    public CuppingInfo getCuppingInfo() {
        return cuppingInfo;
    }

    public void setCuppingInfo(CuppingInfo cuppingInfo) {
        this.cuppingInfo = cuppingInfo;
    }

    public List<BeanInfoSimple> getBeanInfoSimples() {
        return beanInfoSimples;
    }

    public void setBeanInfoSimples(List<BeanInfoSimple> beanInfoSimples) {
        this.beanInfoSimples = beanInfoSimples;
    }

    public TempratureSet getTempratureSet() {
        return tempratureSet;
    }

    public void setTempratureSet(TempratureSet tempratureSet) {
        this.tempratureSet = tempratureSet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // 用于测试
    public String getSingleBeanName() {
        return beanInfoSimples.get(0).getBeanName();
    }

    // 用于测试
    public void setSingleBeanName(String beanName) {
        beanInfoSimples.get(0).setBeanName(beanName);
    }

    public Long getBeanId() {
        return beanId;
    }

    public void setBeanId(Long beanId) {
        this.beanId = beanId;
    }

    public Map<String, String> getEvents(){
        return tempratureSet.getEvents();
    }
}
