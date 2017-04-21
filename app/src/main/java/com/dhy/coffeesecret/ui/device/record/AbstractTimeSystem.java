package com.dhy.coffeesecret.ui.device.record;

import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.utils.Utils;

import java.util.List;

/**
 * Created by CoDeleven on 17-4-21.
 */

public abstract class AbstractTimeSystem {
    protected static long startTime;
    private long startEventTime;

    public AbstractTimeSystem(){
        startEventTime = System.currentTimeMillis();
    }

    public static long getStartTime() {
        return startTime;
    }

    public float getTimeInterval(){
        return Utils.get2PrecisionFloat((System.currentTimeMillis() - startEventTime) / 1000f);
    }

    public float getTimeIntervalFromNow(){
        return Utils.get2PrecisionFloat((System.currentTimeMillis() - startTime) / 1000f);
    }
}
