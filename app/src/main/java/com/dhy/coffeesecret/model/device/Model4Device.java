package com.dhy.coffeesecret.model.device;

import com.dhy.coffeesecret.model.base.BaseModel;
import com.dhy.coffeesecret.pojo.Temperature;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Model4Device extends BaseModel implements IDeviceModel {
    private Temperature mBeginTemperature;
    private static String mLastAddress;
    private static Model4Device mModel4Device;
    private Model4Device(){

    }

    public static Model4Device newInstance(){
        if(mModel4Device == null){
            mModel4Device = new Model4Device();
        }
        return mModel4Device;
    }

    public Temperature getmBeginTemperature() {
        return mBeginTemperature;
    }

    @Override
    public void updateBeginTemperature(Temperature temperature) {
        this.mBeginTemperature = temperature;
    }

    @Override
    public void updateLastConnectedAdd(String lastAddress) {
        Model4Device.mLastAddress = lastAddress;
    }

    @Override
    public String getLastConnectedAddr() {
        if(mLastAddress == null){
            return "";
        }
        return mLastAddress;
    }
}
