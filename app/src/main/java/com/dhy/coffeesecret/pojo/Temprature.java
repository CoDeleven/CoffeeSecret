package com.dhy.coffeesecret.pojo;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-2-12.
 */

public class Temprature implements Serializable{
    private float beanTemp;
    private float inwindTemp;
    private float outwindTemp;
    private float accBeanTemp;
    private float accInwindTemp;
    private float accOutwindTemp;
    private static Temprature lastTemprature;
    public float getBeanTemp() {
        return beanTemp;
    }

    public void setBeanTemp(float beanTemp) {
        this.beanTemp = beanTemp;
    }

    public float getInwindTemp() {
        return inwindTemp;
    }

    public void setInwindTemp(float inwindTemp) {
        this.inwindTemp = inwindTemp;
    }

    public float getOutwindTemp() {
        return outwindTemp;
    }

    public void setOutwindTemp(float outwindTemp) {
        this.outwindTemp = outwindTemp;
    }

    public float getAccBeanTemp() {
        return accBeanTemp;
    }

    public void setAccBeanTemp(float accBeanTemp) {
        this.accBeanTemp = accBeanTemp;
    }

    public float getAccInwindTemp() {
        return accInwindTemp;
    }

    public void setAccInwindTemp(float accInwindTemp) {
        this.accInwindTemp = accInwindTemp;
    }

    public float getAccOutwindTemp() {
        return accOutwindTemp;
    }

    public void setAccOutwindTemp(float accOutwindTemp) {
        this.accOutwindTemp = accOutwindTemp;
    }

    public static Temprature parseHex2Temprature(String data) {
        String[] subStr = data.split(",");
        Temprature temp = new Temprature();
        float beanTemp = Float.parseFloat(subStr[0]);
        float inwindTemp = Float.parseFloat(subStr[1]);
        float outwindTemp = Float.parseFloat(subStr[2]);
        temp.setBeanTemp(beanTemp);
        temp.setInwindTemp(inwindTemp);
        temp.setOutwindTemp(outwindTemp);
        if(lastTemprature != null){
            temp.setAccBeanTemp(beanTemp - lastTemprature.getBeanTemp());
            temp.setAccInwindTemp(inwindTemp - lastTemprature.getInwindTemp());
            temp.setAccOutwindTemp(outwindTemp - lastTemprature.getOutwindTemp());
        }
        lastTemprature = temp;
        return temp;
    }
}
