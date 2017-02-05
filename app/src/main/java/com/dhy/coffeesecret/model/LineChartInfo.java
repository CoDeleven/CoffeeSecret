package com.dhy.coffeesecret.model;

import java.util.Date;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class LineChartInfo {
    private String productionArea;
    private int level;
    private String type;
    private String device;
    private Date beanDate;
    private Date bakeDate;
    private String author;
    private float cookedWeight;
    private float rawWeight;
    private String handler;

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
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

    public Date getBeanDate() {
        return beanDate;
    }

    public void setBeanDate(Date beanDate) {
        this.beanDate = beanDate;
    }

    public float getCookedWeight() {
        return cookedWeight;
    }

    public void setCookedWeight(float cookedWeight) {
        this.cookedWeight = cookedWeight;
    }


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProductionArea() {
        return productionArea;
    }

    public void setProductionArea(String productionArea) {
        this.productionArea = productionArea;
    }

    public float getRawWeight() {
        return rawWeight;
    }

    public void setRawWeight(float rawWeight) {
        this.rawWeight = rawWeight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
