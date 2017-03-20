package com.dhy.coffeesecret.pojo;

import android.util.Log;

import com.bumptech.glide.util.Util;
import com.dhy.coffeesecret.utils.Utils;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by CoDeleven on 17-2-12.
 */

public class Temprature implements Serializable {
    private static Temprature lastTemprature;
    private float beanTemp;
    private float inwindTemp;
    private float outwindTemp;
    private float accBeanTemp;
    private float accInwindTemp;
    private float accOutwindTemp;
    private static float envTemp;
    public Temprature() {

    }

    public Temprature(float beanTemp, float inwindTemp, float outwindTemp) {
        this.beanTemp = beanTemp;
        this.inwindTemp = inwindTemp;
        this.outwindTemp = outwindTemp;
    }

    public static Temprature parseHex2Temprature(String data) {
        String[] subStr = data.split(",");
        Temprature temp = new Temprature();
        float beanTemp = Float.parseFloat(subStr[4]);
        float inwindTemp = Float.parseFloat(subStr[1]);
        float outwindTemp = Float.parseFloat(subStr[2]);
        temp.setBeanTemp(beanTemp);
        temp.setInwindTemp(inwindTemp);
        temp.setOutwindTemp(outwindTemp);
        temp.setEnvTemp(Float.parseFloat(subStr[0]));

        if (lastTemprature != null) {
            temp.setAccBeanTemp(Utils.get2PrecisionFloat(beanTemp - lastTemprature.getBeanTemp()));
            temp.setAccInwindTemp(Utils.get2PrecisionFloat(inwindTemp - lastTemprature.getInwindTemp()));
            temp.setAccOutwindTemp(Utils.get2PrecisionFloat(outwindTemp - lastTemprature.getOutwindTemp()));
        }
        lastTemprature = temp;
        return temp;
    }

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

    public static float getEnvTemp() {
        return envTemp;
    }

    public void setEnvTemp(float envTemp) {
        this.envTemp = envTemp;
    }

}
