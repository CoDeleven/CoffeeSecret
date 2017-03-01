package com.dhy.coffeesecret.utils;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dhy.coffeesecret.pojo.Temprature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by CoDeleven on 17-2-15.
 */

public class BluetoothHelper {
    private static final UUID PRIMARY_SERVICE = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    private static final UUID TAG_WRITE = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");
    private static final UUID TAG_READ = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static String firstChannel = "4348414e3b323130300a";
    public static String secondChannel = "4348414e3b333230300a";
    private static BluetoothHelper mHelper;
    private static BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mCurDevice;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mGatt;
    private boolean readable = false;
    private DeviceChangeListener mDeviceListener;
    private DataChangeListener mDataListener;
    private ViewHandlerListener mViewListener;
    // 向特性写入命令
    private BluetoothGattCharacteristic mWriter = null;
    // 读取特性里面的数据
    private BluetoothGattCharacteristic mReader = null;
    private Thread mThread = null;
    private String result = "";
    private int count = 0;
    private boolean changeChannel = false;
    private boolean readingData = false;
    private boolean readNewData = false;
    private boolean isActivityDestroy = false;
    private Thread testThread = null;
    private ConnectStatusChangeListener mConnectionStatusChangeLisntener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mViewListener.handleViewBeforeStartRead();
        }
    };
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 将bytes数组转换成16进制存储起来
            String hexData = null;
            hexData = Utils.bytesToHexString(characteristic.getValue());

            // 第一次设置channel成功后进入后续读取
            if (changeChannel) {
                if (hexData.endsWith("0a")) {
                    // 通道设置结束
                    changeChannel = false;
                    // 可以读取需要的数据
                    readData();
                }
                // 只要是设置通道的调用均结束
                return;
            }
            // 获取转换后的数据
            result += hexData;
            ++count;
            // 当读取数据到末尾而且计数为2
            if (readingData && hexData.endsWith("0a")) {
                result += "2c";
                if (count == 2) {
                    // 设置下一个通道
                    readNextChannel();
                }
            }
            // 当计数为4，一次温度读取结束
            if (count == 4) {
                result = Utils.hexString2String(result);
                // 通知数据改变
                if (mDataListener != null) {
                    mDataListener.notifyDataChanged(Temprature.parseHex2Temprature(result));
                }
                // 初始化数据
                result = "";
                count = 0;
                readNewData = true;
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
            mGatt.setCharacteristicNotification(mReader, true);
            BluetoothGattDescriptor descriptor = mReader.getDescriptor(WRITE_DESCRIPTOR);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            mGatt.writeDescriptor(descriptor);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e("codelevex", "descriptor写入成功");
            readable = true;

            mHandler.sendMessage(new Message());

            read();
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mGatt.discoverServices();
                mConnectionStatusChangeLisntener.notifyDeviceConnectStatus(true);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionStatusChangeLisntener.notifyDeviceConnectStatus(false);
            }
        }
    };
    private Context mContext;
    /**
     * 扫描到新设备进行回调
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.e("codelevex", bluetoothDevice.getName() + "-" + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
            // 回调通知界面有新设备
            mDeviceListener.notifyNewDevice(bluetoothDevice);
        }
    };

    private BluetoothHelper(Context context) {
        this.mContext = context;

        initialize();
    }

    public static BluetoothHelper getNewInstance(Context context) {
        if (mHelper == null) {
            mHelper = new BluetoothHelper(context);
        }
        return mHelper;
    }

    public static BluetoothHelper getNewInstance() {
        if (mHelper != null) {
            return mHelper;
        }
        return null;
    }

    public void setConnectionStatusChangeListener(ConnectStatusChangeListener mConnectionStatusChangeLisntener) {
        this.mConnectionStatusChangeLisntener = mConnectionStatusChangeLisntener;
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e("codelevex", "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("codelevex", "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public String getCurBluetoothName() {
        return mCurDevice.getName();
    }

    /**
     * 扫描设备
     *
     * @return
     */
    public void scanBluetoothDevice() {
        mBluetoothAdapter.startLeScan(mScanCallback);
    }
    public void stopScanBluetoothDevice(){
        mBluetoothAdapter.stopLeScan(mScanCallback);
    }
    public void close() {
        stopRead();
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
        mBluetoothAdapter.stopLeScan(mScanCallback);


    }

    /**
     * 连接设备
     *
     * @param bluetoothDevice
     * @return 成功返回true，失败返回false
     */
    public boolean connectDevice(BluetoothDevice bluetoothDevice) {

        if (bluetoothDevice == null) {
            Log.e("codelevex", "bluetoothdevice is null");
            return false;
        }

        // 重新连接,第一次判断bluetoothDevice和当前连接设备是否一致，
        if (mCurDevice == bluetoothDevice && mGatt != null) {
            if (mGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        mGatt = bluetoothDevice.connectGatt(mContext, false, mGattCallback);
        if (mGatt != null) {
            mCurDevice = bluetoothDevice;
        }
        return true;
    }

    /**
     * 开启新线程进行读取
     */
    public void read() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (readable) {
                    setFirstChannel();
                    while (!readNewData) {
                    }
                    try {
                        readNewData = false;
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mThread.start();
    }

    public boolean isReading() {
        return readable;
    }

    /**
     * 发送读取数据命令
     */
    private void readData() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                readingData = true;
                mWriter.setValue(Utils.hexStringToBytes("524541440a"));
                mGatt.writeCharacteristic(mWriter);
            }
        });

        mThread.start();
    }

    /**
     * 设置通道 进风+出风
     */
    private void setFirstChannel() {
        changeChannel = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mWriter.setValue(Utils.hexStringToBytes(firstChannel));
                mGatt.writeCharacteristic(mWriter);
            }
        }).start();
    }

    /**
     * 设置通道 进风+豆表
     */
    private void readNextChannel() {
        changeChannel = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mWriter.setValue(Utils.hexStringToBytes(secondChannel));
                mGatt.writeCharacteristic(mWriter);
            }
        }).start();
    }

    public void stopRead() {
        readable = false;
    }
    public void startRead(){
        readable = true;
    }
    /**
     * 仅用于模拟烘焙过程的数据
     */
    public void test(final InputStream is) {
        testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStreamReader ireader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(ireader);
                String str = null;
                try {
                    while ((str = br.readLine()) != null) {
                        for (String temp : str.trim().split(",")) {
                            final Temprature temprature = new Temprature(Float.parseFloat(temp), 0, 0);
                            mDataListener.notifyDataChanged(temprature);
                            if (isActivityDestroy) {
                                return;
                            }
                            try {
                                Thread.currentThread().sleep(1000);
                                // Thread.currentThread().sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        testThread.start();
    }

    public void setDeviceListener(DeviceChangeListener mDeviceListener) {
        this.mDeviceListener = mDeviceListener;
    }

    public void setDataListener(DataChangeListener mDataListener) {
        this.mDataListener = mDataListener;
    }

    public void setViewHandlerListener(ViewHandlerListener mViewListener) {
        this.mViewListener = mViewListener;
    }

    public void setActivityDestroy(boolean activityDestroy) {
        isActivityDestroy = activityDestroy;
    }

    public BluetoothAdapter getmAdapter() {
        return mBluetoothAdapter;
    }

    public boolean getEnableStatus() {
        int status = mBluetoothAdapter.getState();
        Log.e("codelevex", "当前的状态啊：" + status);
        return status == BluetoothAdapter.STATE_ON;
    }

    public interface DeviceChangeListener {
        /**
         * 发现新设备时回调
         *
         * @param device 新设备
         */
        void notifyNewDevice(BluetoothDevice device);
    }

    public interface DataChangeListener {
        /**
         * 获取到新数据时回调该方法
         *
         * @param temprature 获得的温度
         */
        void notifyDataChanged(Temprature temprature);
    }

    public interface ViewHandlerListener {
        /**
         * 在处理UI前进行的操作
         */
        void handleViewBeforeStartRead();
    }

    public interface ConnectStatusChangeListener {
        void notifyDeviceConnectStatus(boolean isConnected);
    }
}
