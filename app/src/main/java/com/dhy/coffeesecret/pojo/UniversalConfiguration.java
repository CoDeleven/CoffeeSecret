package com.dhy.coffeesecret.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by CoDeleven on 17-2-3.
 */

public class UniversalConfiguration {
    // 重量单位
    private String weightUnit;
    // 温度单位
    private String tempratureUnit;
    // 参考标准
    private String referDegree;
    // 快速事件
    private String quickEvents;
    // 三个开关
    private boolean quickStart;
    // 双击启动按钮
    private boolean doubleClick;
    // 圆形标注
    private boolean markByCircle;
    // X轴最大值(单位分钟)
    private int maxX;
    // 左y最大值(温度单位)
    private int maxLeftY;
    // 右y最大值(温度单位)
    private int maxRightY;
    // 温度平均位数
    private int tempratureSmooth;
    // 升温平均位数
    private int tempratureAccSmooth;
    // 豆温矫正
    private int checkBeanTemp;
    // 进风温矫正
    private int checkInwindTemp;
    // 出风温矫正
    private int checkOutwindTemp;
    // 环境温度矫正
    private int checkEvnTemp;
    // 颜色套餐名
    private String colorPackageName;
    // 豆温颜色
    private int beanColor;
    // 进风温
    private int inwindColor;
    // 出风温
    private int outwindColor;
    // 豆升温率
    private int accBeanColor;
    // 进风升温率
    private int accInwindColor;
    // 出风升温率
    private int accOutwindColor;
    // 环境温
    private int envColor;
    // 上一次关闭app时，连接着的蓝牙地址
    String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getTempratureUnit() {
        return tempratureUnit;
    }

    public void setTempratureUnit(String tempratureUnit) {
        this.tempratureUnit = tempratureUnit;
    }

    public String getReferDegree() {
        return referDegree;
    }

    public void setReferDegree(String referDegree) {
        this.referDegree = referDegree;
    }

    public String getQuickEvents() {
        return quickEvents;
    }

    public void setQuickEvents(String quickEvents) {
        this.quickEvents = quickEvents;
    }

    public boolean isQuickStart() {
        return quickStart;
    }

    public void setQuickStart(boolean quickStart) {
        this.quickStart = quickStart;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public boolean isMarkByCircle() {
        return markByCircle;
    }

    public void setMarkByCircle(boolean markByCircle) {
        this.markByCircle = markByCircle;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxLeftY() {
        return maxLeftY;
    }

    public void setMaxLeftY(int maxLeftY) {
        this.maxLeftY = maxLeftY;
    }

    public int getMaxRightY() {
        return maxRightY;
    }

    public void setMaxRightY(int maxRightY) {
        this.maxRightY = maxRightY;
    }

    public int getTempratureSmooth() {
        return tempratureSmooth;
    }

    public void setTempratureSmooth(int tempratureSmooth) {
        this.tempratureSmooth = tempratureSmooth;
    }

    public int getTempratureAccSmooth() {
        return tempratureAccSmooth;
    }

    public void setTempratureAccSmooth(int tempratureAccSmooth) {
        this.tempratureAccSmooth = tempratureAccSmooth;
    }

    public int getCheckBeanTemp() {
        return checkBeanTemp;
    }

    public void setCheckBeanTemp(int checkBeanTemp) {
        this.checkBeanTemp = checkBeanTemp;
    }

    public int getCheckInwindTemp() {
        return checkInwindTemp;
    }

    public void setCheckInwindTemp(int checkInwindTemp) {
        this.checkInwindTemp = checkInwindTemp;
    }

    public int getCheckOutwindTemp() {
        return checkOutwindTemp;
    }

    public void setCheckOutwindTemp(int checkOutwindTemp) {
        this.checkOutwindTemp = checkOutwindTemp;
    }

    public int getCheckEvnTemp() {
        return checkEvnTemp;
    }

    public void setCheckEvnTemp(int checkEvnTemp) {
        this.checkEvnTemp = checkEvnTemp;
    }

    public String getColorPackageName() {
        return colorPackageName;
    }

    public void setColorPackageName(String colorPackageName) {
        this.colorPackageName = colorPackageName;
    }

    public int getBeanColor() {
        return beanColor;
    }

    public void setBeanColor(int beanColor) {
        this.beanColor = beanColor;
    }

    public int getInwindColor() {
        return inwindColor;
    }

    public void setInwindColor(int inwindColor) {
        this.inwindColor = inwindColor;
    }

    public int getOutwindColor() {
        return outwindColor;
    }

    public void setOutwindColor(int outwindColor) {
        this.outwindColor = outwindColor;
    }

    public int getAccBeanColor() {
        return accBeanColor;
    }

    public void setAccBeanColor(int accBeanColor) {
        this.accBeanColor = accBeanColor;
    }

    public int getAccInwindColor() {
        return accInwindColor;
    }

    public void setAccInwindColor(int accInwindColor) {
        this.accInwindColor = accInwindColor;
    }

    public int getAccOutwindColor() {
        return accOutwindColor;
    }

    public void setAccOutwindColor(int accOutwindColor) {
        this.accOutwindColor = accOutwindColor;
    }

    public int getEnvColor() {
        return envColor;
    }

    public void setEnvColor(int envColor) {
        this.envColor = envColor;
    }

    @Override
    public String toString() {
        return "UniversalConfiguration{" +
                "weightUnit='" + weightUnit + '\'' +
                ", tempratureUnit='" + tempratureUnit + '\'' +
                ", referDegree='" + referDegree + '\'' +
                ", quickEvents='" + quickEvents + '\'' +
                ", quickStart=" + quickStart +
                ", doubleClick=" + doubleClick +
                ", markByCircle=" + markByCircle +
                ", maxX=" + maxX +
                ", maxLeftY=" + maxLeftY +
                ", maxRightY=" + maxRightY +
                ", tempratureSmooth=" + tempratureSmooth +
                ", tempratureAccSmooth=" + tempratureAccSmooth +
                ", checkBeanTemp=" + checkBeanTemp +
                ", checkInwindTemp=" + checkInwindTemp +
                ", checkOutwindTemp=" + checkOutwindTemp +
                ", checkEvnTemp=" + checkEvnTemp +
                ", colorPackageName=" + colorPackageName +
                ", beanColor=" + beanColor +
                ", inwindColor=" + inwindColor +
                ", outwindColor=" + outwindColor +
                ", accBeanColor=" + accBeanColor +
                ", accInwindColor=" + accInwindColor +
                ", accOutwindColor=" + accOutwindColor +
                ", envColor=" + envColor +
                '}';
    }
}
