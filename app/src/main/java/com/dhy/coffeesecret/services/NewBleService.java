package com.dhy.coffeesecret.services;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.dhy.coffeesecret.services.data.TransferControllerTask;
import com.dhy.coffeesecret.utils.Utils;

import static com.dhy.coffeesecret.services.BluetoothService.PRIMARY_SERVICE;
import static com.dhy.coffeesecret.services.BluetoothService.TAG_READ;
import static com.dhy.coffeesecret.services.BluetoothService.TAG_WRITE;

/**
 * Created by CoDeleven on 17-8-19.
 */

public class NewBleService implements IBluetoothOperator, IBleWROperator {

    private static final String TAG = NewBleService.class.getSimpleName();
    private Context context;
    private IBleScanCallback mScanCallback;
    private IBleConnectionCallback mConnStatusCallback;
    private BleManager mBleOperator;
    private BluetoothDevice mCurConnectedDevice;
    private Thread mRunningThread;
    private TransferControllerTask mCurTask;
    private String periodData = "";
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private IBleTemperatureCallback mTemperatureCallback;
    public NewBleService(Context context) {
        this.context = context;
        if (mBleOperator == null) {
            mBleOperator = new BleManager(context);
        }
    }

    @Override
    public void setTemperatureListener(IBleTemperatureCallback temperatureListener) {
        this.mTemperatureCallback = temperatureListener;
        if(mRunningThread != null && mCurTask != null){
            mCurTask.setTemperatureCallback(temperatureListener);
        }
    }

    @Override
    public boolean isConnected() {
        return mBleOperator.isConnected();
    }

    @Override
    public void setScanCallbackListener(IBleScanCallback scanCallbackListener) {
        this.mScanCallback = scanCallbackListener;
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        return this.mCurConnectedDevice;
    }

    @Override
    public boolean isEnable() {
        return mBleOperator.isBlueEnable();
    }

    @Override
    public void setConnectionListener(IBleConnectionCallback connectionListener) {
        this.mConnStatusCallback = connectionListener;
    }

    @Override
    public void enable() {
        Log.d(TAG, "启用蓝牙...");
        if (!mBleOperator.isBlueEnable()) {
            mBleOperator.enableBluetooth();
        }
    }

    @Override
    public void startScanDevice() {
        mBleOperator.scanDevice(new ListScanCallback(10000) {
            @Override
            public void onScanning(ScanResult result) {
                mScanCallback.onScanning(result.getDevice(), result.getRssi());
            }

            @Override
            public void onScanComplete(ScanResult[] results) {
                mScanCallback.onScanningComplete();
            }
        });
    }

    @Override
    public void stopScanDevice() {
        mBleOperator.cancelScan();
    }

    @Override
    public boolean connect(BluetoothDevice device) {
        return connect(device.getAddress());
    }

    /**
     * 返回值不可信，保留布尔值以便兼容旧服务
     * @param address
     * @return
     */
    @Override
    public boolean connect(String address) {
        mBleOperator.scanMacAndConnect(address,
                5000, false, new BleGattCallback() {

                    @Override
                    public void onConnectError(BleException exception) {

                    }

                    @Override
                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                        NewBleService.this.mCurConnectedDevice = gatt.getDevice();
                        mConnStatusCallback.toConnected(BluetoothProfile.STATE_DISCONNECTED);
                        // 内部直接实现了discoverServices
                    }

                    @Override
                    public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
                        mConnStatusCallback.toDisconnected(BluetoothProfile.STATE_DISCONNECTED);
                        Log.e(TAG, "onDisConnected: " + exception.getDescription());
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        if (status == 129) {
                            // TODO 处理一些事情
                        }
                        enableListener();
                    }

                    @Override
                    public void onConnecting(BluetoothGatt gatt, int status) {
                        mConnStatusCallback.toDisconnecting(BluetoothProfile.STATE_CONNECTING);
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        String hexData = Utils.bytesToHexString(characteristic.getValue());
                        periodData += hexData;
                        // 判断一个周期是否结束
                        if (hexData.endsWith("0a") || hexData.endsWith("65")) {
                            mCurTask.acknowledgeData(periodData);
                            periodData = "";
                        }
                    }

                });
        return false;
    }

    @Override
    public String getLatestAddress() {
        if(mCurConnectedDevice != null){
            return mCurConnectedDevice.getAddress();
        }
        return "";
    }

    @Override
    public void closeBluetooth() {
        mBleOperator.closeBluetoothGatt();
    }

    private void startRead() {
        mCurTask = new TransferControllerTask(this);
        mCurTask.setTemperatureCallback(mTemperatureCallback);
        mRunningThread = new Thread(mCurTask);
        mRunningThread.start();
    }

    private boolean enableListener(){
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mBleOperator.indicate(PRIMARY_SERVICE.toString(), TAG_READ.toString(), new BleCharacterCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        // 不回调这里的，我日
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.e(TAG, "onFailure: " + exception);
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {
                        Log.d(TAG, "开启监听:" + NewBleService.this);
                        startRead();
                    }
                });
            }
        });
        return true;

    }

    @Override
    public void writeData2Device(final String command) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mBleOperator.writeDevice(PRIMARY_SERVICE.toString(),
                        TAG_WRITE.toString(), Utils.hexStringToBytes(command),
                        new BleCharacterCallback() {
                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                // 不回调这里的，我日
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                Log.e(TAG, "onFailure: " + exception);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                writeData2Device(command);
                            }

                            @Override
                            public void onInitiatedResult(boolean result) {
                                Log.d(TAG, "onSuccess: 写入成功:" + NewBleService.this);
                            }
                        });
            }
        });
    }
    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
