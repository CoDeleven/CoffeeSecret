package com.dhy.coffeesecret.model.bake.developbar;

import com.dhy.coffeesecret.model.IBaseModel;

/**
 * Created by CoDeleven on 17-8-18.
 */

public class Model4DevelopBar implements IBaseModel, IDevelopBarModel {
    private float rawBeanTime;
    private float after160Time;
    private float firstBurstTime;

    @Override
    public void incrementRawBeanTime() {
        ++rawBeanTime;
    }

    @Override
    public void incrementAfter160Time() {
        ++after160Time;
    }

    @Override
    public void incrementFirstBurstTime() {
        ++firstBurstTime;
    }

    @Override
    public float getRawTime() {
        return rawBeanTime;
    }

    @Override
    public float getAfter160Time() {
        return after160Time;
    }

    @Override
    public float getFirstBurstTime() {
        return firstBurstTime;
    }


    @Override
    public float getTotalTime() {
        return rawBeanTime + after160Time + firstBurstTime;
    }

    @Override
    public void clearData() {
        rawBeanTime = 0;
        after160Time = 0;
        firstBurstTime = 0;

    }
}
