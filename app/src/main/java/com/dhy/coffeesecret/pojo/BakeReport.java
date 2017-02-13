package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.Date;

public class BakeReport implements Serializable {
    private int id;
    // 烘焙报告名称
    private String name;
    // 烘焙师
    private String baker;
    // 设备
    private String device;
    //咖啡豆名称
    private String beanName;
    // 烘焙日期
    private Date bakeDate;
    // 熟豆重量
    private String cookedBeanWeight;
    // 生豆重量
    private String rawBeanWeight;
    // 烘焙度
    private String roastDegree;
    // 曲线文件路径
    private String curveFilePath;
    // 事件文件路径
    private String eventFilePath;
    // 发展时间
    private String developmentTime;
    // 发展率
    private String developmentRate;
    // 环境温度
    private String ambientTemperature;
    // 结束温度
    private String endTemperature;
    // 入豆温度
    private String beanTemperature;

    public Date getBakeDate() {
        return bakeDate;
    }

    public void setBakeDate(Date bakeDate) {
        this.bakeDate = bakeDate;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

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

    public String getBaker() {
        return baker;
    }

    public void setBaker(String baker) {
        this.baker = baker;
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

    public String getRawBeanWeight() {
        return rawBeanWeight;
    }

    public void setRawBeanWeight(String rawBeanWeight) {
        this.rawBeanWeight = rawBeanWeight;
    }

    public String getRoastDegree() {
        return roastDegree;
    }

    public void setRoastDegree(String roastDegree) {
        this.roastDegree = roastDegree;
    }

    public String getCurveFilePath() {
        return curveFilePath;
    }

    public void setCurveFilePath(String curveFilePath) {
        this.curveFilePath = curveFilePath;
    }

    public String getEventFilePath() {
        return eventFilePath;
    }

    public void setEventFilePath(String eventFilePath) {
        this.eventFilePath = eventFilePath;
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

    public String getBeanTemperature() {
        return beanTemperature;
    }

    public void setBeanTemperature(String beanTemperature) {
        this.beanTemperature = beanTemperature;
    }

}
