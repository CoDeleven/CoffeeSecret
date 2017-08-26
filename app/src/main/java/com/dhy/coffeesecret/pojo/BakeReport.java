package com.dhy.coffeesecret.pojo;

import com.google.gson.annotations.SerializedName;

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
    // 烘焙度, 如果用户没有移动SeekBar导致这里为空,会引发错误
    private String roastDegree = "0";
    @SerializedName("tempratureSet")
    private TemperatureSet temperatureSet;

    private float breakPointerTime;
    private float breakPointerTemprature;
    private float avgDryTemprature;
    private float avgDryTime;
    private float avgFirstBurstTime;
    private float avgFirstBurstTemprature;
    private float avgEndTime;
    private float avgEndTemprature;
    private float avgAccBeanTemprature;
    private float avgGlobalBeanTemprature;

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

    public TemperatureSet getTemperatureSet() {
        return temperatureSet;
    }

    public void setTemperatureSet(TemperatureSet temperatureSet) {
        this.temperatureSet = temperatureSet;
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
        return temperatureSet.getEvents();
    }

    public float getBreakPointerTime() {
        return breakPointerTime;
    }

    public void setBreakPointerTime(float breakPointerTime) {
        this.breakPointerTime = breakPointerTime;
    }

    public float getBreakPointerTemprature() {
        return breakPointerTemprature;
    }

    public void setBreakPointerTemprature(float breakPointerTemprature) {
        this.breakPointerTemprature = breakPointerTemprature;
    }

    public void setBeanId(long beanId) {
        this.beanId = beanId;
    }

    public float getAvgDryTemprature() {
        return avgDryTemprature;
    }

    public void setAvgDryTemprature(float avgDryTemprature) {
        this.avgDryTemprature = avgDryTemprature;
    }

    public float getAvgDryTime() {
        return avgDryTime;
    }

    public void setAvgDryTime(float avgDryTime) {
        this.avgDryTime = avgDryTime;
    }

    public float getAvgFirstBurstTime() {
        return avgFirstBurstTime;
    }

    public void setAvgFirstBurstTime(float avgFirstBurstTime) {
        this.avgFirstBurstTime = avgFirstBurstTime;
    }

    public float getAvgFirstBurstTemprature() {
        return avgFirstBurstTemprature;
    }

    public void setAvgFirstBurstTemprature(float avgFirstBurstTemprature) {
        this.avgFirstBurstTemprature = avgFirstBurstTemprature;
    }

    public float getAvgEndTime() {
        return avgEndTime;
    }

    public void setAvgEndTime(float avgEndTime) {
        this.avgEndTime = avgEndTime;
    }

    public float getAvgEndTemprature() {
        return avgEndTemprature;
    }

    public void setAvgEndTemprature(float avgEndTemprature) {
        this.avgEndTemprature = avgEndTemprature;
    }

    public float getAvgAccBeanTemprature() {
        return avgAccBeanTemprature;
    }

    public void setAvgAccBeanTemprature(float avgAccBeanTemprature) {
        this.avgAccBeanTemprature = avgAccBeanTemprature;
    }

    public float getAvgGlobalBeanTemprature() {
        return avgGlobalBeanTemprature;
    }

    public void setAvgGlobalBeanTemprature(float avgGlobalBeanTemprature) {
        this.avgGlobalBeanTemprature = avgGlobalBeanTemprature;
    }

    @Override
    public String toString() {
        return "BakeReport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", device='" + device + '\'' +
                ", cookedBeanWeight='" + cookedBeanWeight + '\'' +
                ", roastDegree='" + roastDegree + '\'' +
                ", temperatureSet=" + temperatureSet +
                ", breakPointerTime=" + breakPointerTime +
                ", breakPointerTemprature=" + breakPointerTemprature +
                ", avgDryTemprature=" + avgDryTemprature +
                ", avgDryTime=" + avgDryTime +
                ", avgFirstBurstTime=" + avgFirstBurstTime +
                ", avgFirstBurstTemprature=" + avgFirstBurstTemprature +
                ", avgEndTime=" + avgEndTime +
                ", avgEndTemprature=" + avgEndTemprature +
                ", avgAccBeanTemprature=" + avgAccBeanTemprature +
                ", avgGlobalBeanTemprature=" + avgGlobalBeanTemprature +
                ", developmentTime='" + developmentTime + '\'' +
                ", developmentRate='" + developmentRate + '\'' +
                ", ambientTemperature='" + ambientTemperature + '\'' +
                ", endTemperature='" + endTemperature + '\'' +
                ", startTemperature='" + startTemperature + '\'' +
                ", date='" + date + '\'' +
                ", beanInfoSimples=" + beanInfoSimples +
                ", cuppingInfo=" + cuppingInfo +
                ", beanId=" + beanId +
                '}';
    }
}
