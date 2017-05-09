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
        startTime = System.currentTimeMillis();
    }

    public static long getStartTime() {
        return startTime;
    }

    protected void resetEventTime(){
        this.startEventTime = System.currentTimeMillis();
    }

    /**
     * 当前系统时间减去开始该事件的时间
     * @return
     */
    public float getTimeInterval(){
        return Utils.get2PrecisionFloat((System.currentTimeMillis() - startEventTime) / 1000f);
    }

    public float getTimeIntervalFromNow(){
        return Utils.get2PrecisionFloat((System.currentTimeMillis() - startTime) / 1000f);
    }
}
