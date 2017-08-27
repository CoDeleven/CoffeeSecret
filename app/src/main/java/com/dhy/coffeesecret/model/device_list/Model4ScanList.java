package com.dhy.coffeesecret.model.device_list;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.model.base.BaseModel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-8-22.
 */

public class Model4ScanList extends BaseModel implements IScanListModel {
    private static Model4ScanList mSelf;
    private Map<String, ScanResult> mAddedDevice = new LinkedHashMap<>();

    @Override
    public List<ScanResult> listAllAddedDevices() {
        return new LinkedList<>(mAddedDevice.values());
    }

    @Override
    public void addDevice(ScanResult scanResult) {
        mAddedDevice.put(scanResult.getDevice().getAddress(), scanResult);
    }

    @Override
    public void clearAllAddedDevices() {
        mAddedDevice.clear();
    }

    @Override
    public void removeDevice(String deviceAddress) {
        mAddedDevice.remove(deviceAddress);
    }

    @Override
    public void removeDevice(BluetoothDevice device) {
        mAddedDevice.remove(device.getAddress());
    }

    @Override
    public boolean isExisted(String address) {
        return mAddedDevice.containsKey(address);
    }



    private Model4ScanList(){

    }
    public static Model4ScanList newInstance(){
        if (mSelf == null){
            mSelf = new Model4ScanList();
        }
        return mSelf;
    }

}
