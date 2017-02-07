package com.dhy.coffeesecret.model;

import java.util.Date;

/**
 * Created by CoDeleven on 17-2-7.
 */

public class CoffeeBeanInfo {
    private int beanId;
    private String beanName;
    private String country;
    private String produceArea;
    private String manor;
    private String originElevation;
    private String genus;
    private String handler;
    private float stock;
    private float perUse;
    private float containWater;
    private float price;
    private Date purchase;

    public int getBeanId() {
        return beanId;
    }

    public void setBeanId(int beanId) {
        this.beanId = beanId;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public float getContainWater() {
        return containWater;
    }

    public void setContainWater(float containWater) {
        this.containWater = containWater;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getManor() {
        return manor;
    }

    public void setManor(String manor) {
        this.manor = manor;
    }

    public String getOriginElevation() {
        return originElevation;
    }

    public void setOriginElevation(String originElevation) {
        this.originElevation = originElevation;
    }

    public float getPerUse() {
        return perUse;
    }

    public void setPerUse(float perUse) {
        this.perUse = perUse;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getProduceArea() {
        return produceArea;
    }

    public void setProduceArea(String produceArea) {
        this.produceArea = produceArea;
    }

    public Date getPurchase() {
        return purchase;
    }

    public void setPurchase(Date purchase) {
        this.purchase = purchase;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }
}
