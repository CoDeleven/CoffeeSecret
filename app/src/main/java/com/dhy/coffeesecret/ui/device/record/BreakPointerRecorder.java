package com.dhy.coffeesecret.ui.device.record;

import android.util.Log;

import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.utils.Utils;

import java.util.LinkedList;

/**
 * Created by CoDeleven on 17-4-21.
 */

public class BreakPointerRecorder extends AbstractTimeSystem {
    private Temprature lastTemprature;
    private LinkedList<Temprature> avgTemprature = new LinkedList<>();
    private boolean isBreakPointer = false;
    private float breakPointerTime;
    public boolean record(Temprature temprature){
        if(isBreakPointer){
            return true;
        }
        if(lastTemprature == null && temprature.getAccBeanTemp() < 0){
            this.lastTemprature = temprature;
        }

        if(temprature.getAccBeanTemp() < 0){

            if(lastTemprature.getBeanTemp() >= temprature.getBeanTemp()){
                this.lastTemprature = temprature;
                return false;
            }
            if(temprature.getBeanTemp() - lastTemprature.getBeanTemp() > 0.3){
                breakPointerTime = getTimeIntervalFromNow();
                Log.e("wrong", lastTemprature.getBeanTemp() + "->" + Utils.getTimeWithFormat(breakPointerTime));
                return isBreakPointer = true;
            }
        }
        return false;
    }

    public Temprature getBreakPointerTemprature(){
        return lastTemprature;
    }
    public float getbreakPointerTime(){
        return breakPointerTime;
    }
}
