package com.dhy.coffeesecret.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.utils.Utils;

import java.util.UUID;

public class BluetoothService extends Service {
    public static final UUID PRIMARY_SERVICE = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_WRITE = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_READ = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    public static final UUID WRITE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "codelevex";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public static String FIRST_CHANNEL = "4348414e3b323130300a";
    public static String SECOND_CHANNEL = "4348414e3b333230300a";
    public static String READ_TEMP_COMMAND = "524541440a";
    public static volatile boolean READABLE = false;
    public static BluetoothService.BluetoothOperator BLUETOOTH_OPERATOR;
    private static ReadTasker mRunThread;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriter;
    private BluetoothGattCharacteristic mReader;
    private BluetoothDevice mCurDevice;
    private int mConnectionState = STATE_DISCONNECTED;
    private DataChangedListener dataChangedListener;
    private DeviceChangedListener deviceChangedListener;
    private ViewControllerListener viewControllerListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewControllerListener.handleViewBeforeStartRead();
        }
    };

    /**
     * 扫描到新设备进行回调
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.e("codelevex", bluetoothDevice.getName() + "-" + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
            // 回调通知界面有新设备
            deviceChangedListener.notifyNewDevice(bluetoothDevice, i);
        }

    };
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 将bytes数组转换成16进制存储起来
            String hexData = null;
            hexData = Utils.bytesToHexString(characteristic.getValue());
            // 如果读到最后一行数据，返回true，并设置处理结束
            if (mRunThread.readData(hexData)) {
                mRunThread.setHandling(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("codelevex", "获取单片机服务");

            BluetoothGattService service = gatt.getService(PRIMARY_SERVICE);

            // 获取写特性
            mWriter = service.getCharacteristic(TAG_WRITE);
            // 获取读特性
            mReader = service.getCharacteristic(TAG_READ);

            if (mWriter == null || mReader == null) {
                throw new RuntimeException("特性异常");
            }

            // 设置通知
            mBluetoothGatt.setCharacteristicNotification(mReader, true);
            BluetoothGattDescriptor descriptor = mReader.getDescriptor(WRITE_DESCRIPTOR);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            Log.d(TAG, mBluetoothGatt.writeDescriptor(descriptor) + "状态");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "descriptor写入成功");

            mHandler.sendMessage(new Message());

            startRead();
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e("codelevex", gatt.getDevice().toString() + ":" + status + "->" + newState + ", " + gatt.getDevice().getName());
            if(gatt.getDevice().getAddress().equals(mCurDevice.getAddress())){
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (mRunThread != null) {
                        mRunThread.setReadable(false);
                        mRunThread.interrupt();
                        mRunThread.setDataChangedListener(null);
                        mRunThread = null;
                    }
                    mConnectionState = STATE_CONNECTED;
                    mBluetoothGatt.discoverServices();
                    deviceChangedListener.notifyDeviceConnectStatus(true);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mConnectionState = STATE_DISCONNECTED;
                    disconnect();
                    deviceChangedListener.notifyDeviceConnectStatus(false);
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.e(TAG, "信号强度：" + rssi);
        }
    };


    public BluetoothService() {
    }

    public boolean initialize() {
        // 获取蓝牙
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "无法获取BluetoothManager");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "无法获取BluetoothAdapter");
            return false;
        }

        return true;
    }


    /**
     * 连接设备
     *
     * @param device 想连接的设备
     * @return 如果连接成功返回true
     */
    public boolean connect(final BluetoothDevice device) {
        if (mBluetoothAdapter == null || device == null) {
            return false;
        }

        // 如果先前已经连接，尝试重新连接
        if (device != null && mCurDevice != null && mCurDevice.getAddress().equals(device.getAddress()) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        // 直接连接设备
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.e("codelevex", "Trying to create a new connection.");
        mCurDevice = device;
        mConnectionState = STATE_CONNECTING;
        return true;
    }



    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter尚未初始化");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if (BLUETOOTH_OPERATOR == null) {
            BLUETOOTH_OPERATOR = new BluetoothOperator();
        }
        initialize();
        return BLUETOOTH_OPERATOR;
    }

    /**
     * 开启新线程进行读取
     */
    public void startRead() {
        mRunThread = new ReadTasker();
        mRunThread.setReadable(true);
        mRunThread.setDataChangedListener(dataChangedListener);
        mRunThread.start();
    }

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }


    public interface ViewControllerListener {
        void handleViewBeforeStartRead();
    }

    public interface DataChangedListener {
        void notifyDataChanged(Temprature temprature);
    }

    public interface DeviceChangedListener {
        void notifyDeviceConnectStatus(boolean isConnected);

        void notifyNewDevice(BluetoothDevice device, int rssi);

    }

    protected abstract class DataReader {
        private boolean isHandling = false;

        abstract void setWriteCommand();

        /**
         * 读取数据，并进行是否结尾的处理
         *
         * @param str 收到下位机发送的信息
         * @return 返回该种数据是否到尾
         */
        abstract boolean readData(String str);

        boolean isHandling() {
            return isHandling;
        }

        void setHandling(boolean isHandling) {
            this.isHandling = isHandling;
        }
    }

    public class BluetoothOperator extends Binder {
        public boolean isConnected() {
            return BluetoothService.this.isConnected();
        }

        public void setDataChangedListener(DataChangedListener dataChangedListener) {
            BluetoothService.this.dataChangedListener = dataChangedListener;
            if (mRunThread != null) {
                mRunThread.setDataChangedListener(dataChangedListener);
            }
        }

        public void setDeviceChangedListener(DeviceChangedListener deviceChangedListener) {
            BluetoothService.this.deviceChangedListener = deviceChangedListener;
        }

        public void setViewControllerListener(ViewControllerListener viewControllerListener) {
            BluetoothService.this.viewControllerListener = viewControllerListener;
        }

        public void read() {
            startRead();
        }

        public String getCurDeviceName() {
            return mCurDevice.getName();
        }

        public boolean isEnable() {
            return mBluetoothAdapter.isEnabled();
        }

        public void enable() {
            mBluetoothAdapter.enable();
        }

        public void disableBluetooth() {
            if(mRunThread != null){
                mRunThread.clearData();
            }
            Log.w(TAG, "关闭蓝牙");
            mCurDevice = null;
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
            }

            READABLE = false;
            if(mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.disable();
            }
        }

        public void startScanDevice() {
            mBluetoothAdapter.startLeScan(mScanCallback);
        }

        public void stopScanDevice() {
            mBluetoothAdapter.stopLeScan(mScanCallback);
        }

        public boolean connect(BluetoothDevice device) {
            return BluetoothService.this.connect(device);
        }

        public boolean reConnect(){
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mCurDevice.getAddress());
            if(device == null){
                return false;
            }else{
                return connect(device);
            }
        }
    }

    class ReadTasker extends Thread {
        private String result = "";
        private DataChangedListener dataChangedListener;
        private boolean readable = false;
        private DataReader channelListener1 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                if (!"".equals(result)) {
                    result = "";
                }
                mWriter.setValue(Utils.hexStringToBytes(FIRST_CHANNEL));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state);
            }

            @Override
            public boolean readData(String str) {
                if (str.endsWith("0a")) {
                    // 设置回正常读取
                    dataReader = readData1;
                    // 启动温度读取器
                    dataReader.setWriteCommand();
                }
                return false;
            }
        };
        private DataReader readData2 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                mWriter.setValue(Utils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state);
            }

            @Override
            public synchronized boolean readData(String str) {
                // result.set(result.get() + str);
                synchronized (result) {
                    result += str;
                    if (str.endsWith("0a")) {
                        dataReader = channelListener1;
                        if (dataChangedListener != null) {
                            dataChangedListener.notifyDataChanged(Temprature.parseHex2Temprature(Utils.hexString2String(result)));
                            result = "";
                        }
                        return true;
                    }
                    return false;
                }
            }
        };
        private DataReader channelListener2 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                mWriter.setValue(Utils.hexStringToBytes(SECOND_CHANNEL));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state);
            }

            @Override
            public boolean readData(String str) {
                if (str.endsWith("0a")) {
                    dataReader = readData2;
                    dataReader.setWriteCommand();
                }
                return false;
            }
        };
        private DataReader readData1 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                mWriter.setValue(Utils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state);
            }

            @Override
            public boolean readData(String str) {
                // result.set(result.get() == null ? "" :result.get() + str);
                result += str;
                if (str.endsWith("0a")) {
                    result += "2c";
                    dataReader = channelListener2;
                    channelListener2.setWriteCommand();
                }
                return false;
            }
        };
        private DataReader dataReader = channelListener1;

        @Override
        public void run() {
            while (readable) {

                dataReader.setWriteCommand();
                // 等待dataRead返回数据
                while (dataReader.isHandling()) {
                }

                dataReader.setHandling(true);
                Log.e(TAG, Thread.currentThread().toString() + "->当前");
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("codelevex", "已经停止运行该线程");
                    return;
                }
            }
        }

        public void setDataChangedListener(DataChangedListener dataChangedListener) {
            this.dataChangedListener = dataChangedListener;
        }

        public void setReadable(boolean readable) {
            this.readable = readable;
        }

        public void setHandling(boolean isHandling) {
            dataReader.setHandling(isHandling);
        }

        public boolean readData(String str) {
            return dataReader.readData(str);
        }

        public void clearData(){
            dataChangedListener = null;
            readable = false;
            dataReader = channelListener1;
        }
    }
}
