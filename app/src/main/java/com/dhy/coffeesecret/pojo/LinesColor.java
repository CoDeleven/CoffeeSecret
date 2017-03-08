package com.dhy.coffeesecret.pojo;

import java.io.Serializable;

/**
 * CoffeeSecret
 * Created by Simo on 2017/3/3.
 */

public class LinesColor implements Serializable{
    // 曲线套餐的名字
    private String packageName;
    // 豆温
    private String beanColor;
    // 进风温
    private String inwindColor;
    // 出风温
    private String outwindColor;
    // 豆升温率
    private String accBeanColor;
    // 进风升温率
    private String accInwindColor;
    // 出风升温率
    private String accOutwindColor;
    // 环境温
    private String envColor;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBeanColor() {
        return beanColor;
    }

    public void setBeanColor(String beanColor) {
        this.beanColor = beanColor;
    }

    public String getInwindColor() {
        return inwindColor;
    }

    public void setInwindColor(String inwindColor) {
        this.inwindColor = inwindColor;
    }

    public String getOutwindColor() {
        return outwindColor;
    }

    public void setOutwindColor(String outwindColor) {
        this.outwindColor = outwindColor;
    }

    public String getAccBeanColor() {
        return accBeanColor;
    }

    public void setAccBeanColor(String accBeanColor) {
        this.accBeanColor = accBeanColor;
    }

    public String getAccInwindColor() {
        return accInwindColor;
    }

    public void setAccInwindColor(String accInwindColor) {
        this.accInwindColor = accInwindColor;
    }

    public String getAccOutwindColor() {
        return accOutwindColor;
    }

    public void setAccOutwindColor(String accOutwindColor) {
        this.accOutwindColor = accOutwindColor;
    }

    public String getEnvColor() {
        return envColor;
    }

    public void setEnvColor(String envColor) {
        this.envColor = envColor;
    }

    @Override
    public String toString() {
        return "LinesColor{" +
                "packageName='" + packageName + '\'' +
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
