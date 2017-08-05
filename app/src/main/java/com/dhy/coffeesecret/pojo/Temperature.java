package com.dhy.coffeesecret.pojo;

import com.dhy.coffeesecret.utils.Utils;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-2-12.
 */

public class Temperature implements Serializable {
    private static Temperature lastTemperature;
    private float beanTemp;
    private float inwindTemp;
    private float outwindTemp;
    private float accBeanTemp;
    private float accInwindTemp;
    private float accOutwindTemp;
    private static float envTemp;
    public Temperature() {

    }

    public Temperature(float beanTemp, float inwindTemp, float outwindTemp) {
        this.beanTemp = beanTemp;
        this.inwindTemp = inwindTemp;
        this.outwindTemp = outwindTemp;
    }
    // 0-》环境温，1-》进风温，2-》出风问，3-》环境温，4->豆温，5-》？
    // 12，44，55
    public static Temperature parseHex2Temprature(String data) {
        String[] subStr = data.split(",");
        Temperature temp = new Temperature();
        float beanTemp = Float.parseFloat(subStr[4]);
        float inwindTemp = Float.parseFloat(subStr[1]);
        float outwindTemp = Float.parseFloat(subStr[2]);
        temp.setBeanTemp(beanTemp);
        temp.setInwindTemp(inwindTemp);
        temp.setOutwindTemp(outwindTemp);
        temp.setEnvTemp(Float.parseFloat(subStr[0]));

        if (lastTemperature != null) {
            temp.setAccBeanTemp(Utils.get2PrecisionFloat((beanTemp - lastTemperature.getBeanTemp()) * 60));
            temp.setAccInwindTemp(Utils.get2PrecisionFloat((inwindTemp - lastTemperature.getInwindTemp()) * 60));
            temp.setAccOutwindTemp(Utils.get2PrecisionFloat((outwindTemp - lastTemperature.getOutwindTemp()) * 60));
        }
        lastTemperature = temp;
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

    @Override
    public String toString() {
        return "Temperature{" +
                "beanTemp=" + beanTemp +
                ", inwindTemp=" + inwindTemp +
                ", outwindTemp=" + outwindTemp +
                ", accBeanTemp=" + accBeanTemp +
                ", accInwindTemp=" + accInwindTemp +
                ", accOutwindTemp=" + accOutwindTemp +
                '}';
    }
}