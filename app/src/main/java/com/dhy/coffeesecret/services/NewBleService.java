package com.dhy.coffeesecret.services;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.dhy.coffeesecret.services.data.DataDigger4Ble;
import com.dhy.coffeesecret.services.data.TransferControllerTask;
import com.dhy.coffeesecret.services.interfaces.IBleConnCallback;
import com.dhy.coffeesecret.services.interfaces.IBleDataCallback;
import com.dhy.coffeesecret.services.interfaces.IBleScanCallback;
import com.dhy.coffeesecret.services.interfaces.IBluetoothOperator;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.concurrent.locks.ReentrantLock;

import cn.jesse.nativelogger.NLogger;

import static com.dhy.coffeesecret.services.BluetoothService.PRIMARY_SERVICE;
import static com.dhy.coffeesecret.services.BluetoothService.TAG_READ;
import static com.dhy.coffeesecret.services.BluetoothService.TAG_WRITE;

/**
 * Created by CoDeleven on 17-8-19.
 */

public class NewBleService implements IBluetoothOperator, DataDigger4Ble.IBleWROperator, TransferControllerTask.IConnEmergencyListener {
    private ReentrantLock lock = new ReentrantLock();
    private static final String TAG = NewBleService.class.getSimpleName();
    private Context context;
    private volatile IBleScanCallback mScanCallback;
    private volatile IBleConnCallback mConnStatusCallback;
    private BleManager mBleOperator;
    private BluetoothDevice mCurConnectedDevice;
    private Thread mRunningThread;
    private TransferControllerTask mCurTask;
    private String periodData = "";
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private volatile IBleDataCallback mTemperatureCallback;
    private String mLastConnectedDevice;
    // 这里自己加这个字段，因为框架的有问题
    private boolean isConnected;
    // 自行检测的是否断开连接
    // private boolean mSelfDetectedDisconned = false;
    private BleGattCallback mGattCallback4Conn = new BleGattCallback() {

        @Override
        public void onConnectError(BleException exception) {
            NLogger.e(TAG, "BleGattCallback::onConnectError(): 扫描不到对应设备");
            // 按照未连接来处理
            if(mConnStatusCallback != null){
                mConnStatusCallback.toDisconnected();
            }
            isConnected = false;
        }

        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            NLogger.i(TAG, "onConnectSuccess():连接成功");
            NewBleService.this.mCurConnectedDevice = gatt.getDevice();
            isConnected = true;
            // 保存这次连接的地址，便于下次重连
            mLastConnectedDevice = mCurConnectedDevice.getAddress();

            if(mConnStatusCallback != null){
                mConnStatusCallback.toConnected();
            }
            // 内部直接实现了discoverServices
        }

        @Override
        public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
            if(mConnStatusCallback != null){
                mConnStatusCallback.toDisconnected();
            }
            isConnected = false;
            mCurConnectedDevice = null;
            NLogger.e(TAG, "BleGattCallback::onDisConnected(): " + exception.getDescription());
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            NLogger.i(TAG, "BleGattCallback::onServicesDiscovered():发现服务...");
            if (status == 129) {
                String targetDeviceMac = gatt.getDevice().getAddress();
                // 如果遇到129状态，那么一直重连，直到成功
                mBleOperator.closeBluetoothGatt();
                // T.showShort(context, "服务状态129，重连...");
                NLogger.e(TAG, "BleGattCallback::onServicesDiscovered():服务状态129，正在重连...");
                connect(targetDeviceMac);
                return;
            }
            NLogger.i(TAG, "BleGattCallback::onServicesDiscovered():服务状态正常...");
            enableListener();
        }

        @Override
        public void onConnecting(BluetoothGatt gatt, int status) {
            if(mConnStatusCallback != null){
                mConnStatusCallback.toConnecting();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            String hexData = ConvertUtils.bytesToHexString(characteristic.getValue());
            periodData += hexData;
            // 判断一个周期是否结束
            if (hexData.endsWith("0a") || hexData.endsWith("65")) {
                mCurTask.acknowledgeData(periodData);
                periodData = "";
            }
        }

    };

    public NewBleService(Context context) {
        NLogger.i(TAG, "NewBleService(): 创建NewBleService");
        this.context = context;
        if (mBleOperator == null) {
            mBleOperator = new BleManager(context);
        }
    }

    @Override
    public synchronized void setTemperatureListener(IBleDataCallback temperatureListener) {
        this.mTemperatureCallback = temperatureListener;
        if (mRunningThread != null && mCurTask != null) {
            mCurTask.setTemperatureCallback(temperatureListener);
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public synchronized void setScanCallbackListener(IBleScanCallback scanCallbackListener) {
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
    public synchronized void setConnectionListener(IBleConnCallback connectionListener) {

        this.mConnStatusCallback = connectionListener;
    }

    @Override
    public void enableBle() {
        NLogger.i(TAG, "enable():启用蓝牙...");
        if (!mBleOperator.isBlueEnable()) {
            mBleOperator.enableBluetooth();
        }
    }

    @Override
    public void startScanDevice() {
        NLogger.i(TAG, "startScanDevice():开始扫描设备...");
        mBleOperator.scanDevice(new ListScanCallback(10000) {
            @Override
            public void onScanning(ScanResult result) {
                if(mScanCallback != null){
                    mScanCallback.onScanning(result);
                }
            }

            @Override
            public void onScanComplete(ScanResult[] results) {
                if (mScanCallback != null) {
                    mScanCallback.onScanningComplete();
                }
            }
        });
    }

    @Override
    public void stopScanDevice() {
        mBleOperator.cancelScan();
    }

    @Override
    public boolean connect(ScanResult device) {
        NLogger.i(TAG, "connect():开始连接设备：" + device.getDevice().getName());
        mBleOperator.connectDevice(device, false, mGattCallback4Conn);
        return false;
    }

    /**
     * 返回值不可信，保留布尔值以便兼容旧服务
     *
     * @param address
     * @return
     */
    @Override
    public boolean connect(String address) {
        NLogger.i(TAG, "connect():开始连接设备：" + address);
        mBleOperator.scanMacAndConnect(address,
                5000, false, mGattCallback4Conn);
        return false;
    }

    @Override
    public String getLatestAddress() {
        return this.mLastConnectedDevice;
    }

    @Override
    public void disableBle() {
        NLogger.i(TAG, "disableBle():关闭蓝牙");
        mBleOperator.closeBluetoothGatt();
        mBleOperator.disableBluetooth();
        mConnStatusCallback.toDisable();
        clearInfoAfterDisconnected();
        isConnected = false;
    }

    private void startRead() {
        NLogger.i(TAG, "startRead():开始读取数据...");
        mCurTask = new TransferControllerTask(this);
        mCurTask.setTemperatureCallback(mTemperatureCallback);
        mCurTask.setEmergencyAccess(this);
        mRunningThread = new Thread(mCurTask);
        mRunningThread.start();
    }

    private boolean enableListener() {
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
                        NLogger.e(TAG, "onFailure: " + exception);
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {
                        NLogger.i(TAG, "开启监听:" + NewBleService.this);
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
                        TAG_WRITE.toString(), ConvertUtils.hexStringToBytes(command),
                        new BleCharacterCallback() {
                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                // 不回调这里的，我日
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                NLogger.e(TAG, "onFailure: " + exception);
                                switch (exception.getCode()) {
                                    case BleException.ERROR_CODE_GATT:
                                        if(mConnStatusCallback != null){
                                            mConnStatusCallback.toDisconnected();
                                        }
                                        break;
                                    case BleException.ERROR_CODE_OTHER:
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        writeData2Device(command);
                                        break;
                                }
                            }

                            @Override
                            public void onInitiatedResult(boolean result) {
                                NLogger.d(TAG, "onSuccess: 写入成功:" + NewBleService.this);
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

    @Override
    public void occurDisconnectedBySelfDetect() {
        isConnected = false;
        NLogger.e(TAG, "occurDisconnectedBySelfDetect():紧急停止线程...通知监听器");
        if(mConnStatusCallback != null){
            mConnStatusCallback.toDisconnected();
        }
        mBleOperator.closeBluetoothGatt();
        // 清除这些线程
        clearInfoAfterDisconnected();

    }

    private void clearInfoAfterDisconnected() {
        if (mRunningThread != null && mRunningThread.isAlive()) {
            // 线程只有在写的时候才是非阻塞的，其余时间均阻塞
            if (!mCurTask.isWriting()) {
                mRunningThread.interrupt();
            }
            NLogger.e(TAG, "mRunningThread(): isAlive -> " + mRunningThread.isAlive());
        }
        mRunningThread = null;
        mCurTask = null;
        mCurConnectedDevice = null;
        periodData = "";
    }
}
