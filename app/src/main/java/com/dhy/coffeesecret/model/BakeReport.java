package com.dhy.coffeesecret.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * Created by CoDeleven on 17-2-7.
 */

public class BakeReport implements Serializable {
    private int reportId;
    private CoffeeBeanInfo coffeeBeanInfo;
    private String name;
    private String author;
    private String deviceName;
    private Date bakeDate;
    private float cookedWeight;
    private float rawWeight;
    private int bakeDegree;
    private String dataFilePath;
    private String eventFilePath;
    private Time devTime;
    private float devRate;
    private float envTemp;
    private float endTemp;
    private float inTemp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getBakeDate() {
        return bakeDate;
    }

    public void setBakeDate(Date bakeDate) {
        this.bakeDate = bakeDate;
    }

    public int getBakeDegree() {
        return bakeDegree;
    }

    public void setBakeDegree(int bakeDegree) {
        this.bakeDegree = bakeDegree;
    }

    public CoffeeBeanInfo getCoffeeBeanInfo() {
        return coffeeBeanInfo;
    }

    public void setCoffeeBeanInfo(CoffeeBeanInfo coffeeBeanInfo) {
        this.coffeeBeanInfo = coffeeBeanInfo;
    }

    public float getCookedWeight() {
        return cookedWeight;
    }

    public void setCookedWeight(float cookedWeight) {
        this.cookedWeight = cookedWeight;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public float getDevRate() {
        return devRate;
    }

    public void setDevRate(float devRate) {
        this.devRate = devRate;
    }

    public Time getDevTime() {
        return devTime;
    }

    public void setDevTime(Time devTime) {
        this.devTime = devTime;
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

    public String getEventFilePath() {
        return eventFilePath;
    }

    public void setEventFilePath(String eventFilePath) {
        this.eventFilePath = eventFilePath;
    }

    public float getInTemp() {
        return inTemp;
    }

    public void setInTemp(float inTemp) {
        this.inTemp = inTemp;
    }

    public float getRawWeight() {
        return rawWeight;
    }

    public void setRawWeight(float rawWeight) {
        this.rawWeight = rawWeight;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
}
