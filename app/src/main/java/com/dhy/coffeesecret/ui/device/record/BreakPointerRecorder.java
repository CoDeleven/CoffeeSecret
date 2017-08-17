package com.dhy.coffeesecret.ui.device.record;

import android.util.Log;

import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.utils.Utils;

import java.util.LinkedList;

/**
 * Created by CoDeleven on 17-4-21.
 */

public class BreakPointerRecorder extends AbstractTimeSystem {
    private Temperature lastTemperature;
    private LinkedList<Temperature> avgTemperature = new LinkedList<>();
    private boolean isBreakPointer = false;
    private float breakPointerTime;
    public boolean record(Temperature temperature){
        if(isBreakPointer){
            return true;
        }
        if(lastTemperature == null && temperature.getAccBeanTemp() < 0){
            this.lastTemperature = temperature;
        }

        if(temperature.getAccBeanTemp() < 0){

            if(lastTemperature.getBeanTemp() >= temperature.getBeanTemp()){
                this.lastTemperature = temperature;
                return false;
            }
            if(temperature.getBeanTemp() - lastTemperature.getBeanTemp() > 0.3){
                breakPointerTime = getTimeIntervalFromNow();
                Log.e("wrong", lastTemperature.getBeanTemp() + "->" + Utils.getTimeWithFormat(breakPointerTime));
                return isBreakPointer = true;
            }
        }
        return false;
    }

    public Temperature getBreakPointerTemprature(){
        return lastTemperature;
    }
    public float getbreakPointerTime(){
        return breakPointerTime;
    }
}
