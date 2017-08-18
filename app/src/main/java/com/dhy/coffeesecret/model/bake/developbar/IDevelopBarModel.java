package com.dhy.coffeesecret.model.bake.developbar;

import com.dhy.coffeesecret.model.IBaseModel;

/**
 * Created by CoDeleven on 17-8-18.
 */

public interface IDevelopBarModel extends IBaseModel{
    void incrementRawBeanTime();

    void incrementAfter160Time();

    void incrementFirstBurstTime();

    float getRawTime();

    float getAfter160Time();

    float getFirstBurstTime();

    float getTotalTime();

    void clearData();
}
