package com.dhy.coffeesecret.pojo;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-2-19.
 */

public class DialogBeanInfo implements Serializable {
    private BeanInfo beanInfo;
    private float weight;

    public BeanInfo getBeanInfo() {
        return beanInfo;
    }

    public void setBeanInfo(BeanInfo beanInfo) {
        this.beanInfo = beanInfo;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
