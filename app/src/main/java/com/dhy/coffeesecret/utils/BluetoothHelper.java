package com.dhy.coffeesecret.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dhy.coffeesecret.pojo.Temprature;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by CoDeleven on 17-2-15.
 */

public class BluetoothHelper {
    private static final UUID PRIMARY_SERVICE = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    private static final UUID TAG_WRITE = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");
    private static final UUID TAG_READ = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static BluetoothHelper mHelper;
    private static BluetoothAdapter mAdapter;
    private BluetoothDevice mCurDevice;
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
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mViewListener.handleViewBeforeStartRead();
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 空数据直接跳过
            if (characteristic.getValue().length > 5) {
                // 将bytes数组转换成16进制存储起来
                String hexData = null;
                try {
                    hexData = new String(characteristic.getValue(), "UTF-8");
                    Log.e("codelevex", "数据更新:" + hexData);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mDataListener.notifyDataChanged(Utils.getTempratures(hexData));
            }
            return;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("codelevex", "获取单片机服务");
            // 获取读写的服务
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
            Log.e("codelevex", "连接成功");
            mGatt.discoverServices();
        }
    };
    private Context mContext;
    /**
     * 扫描到新设备进行回调
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            // 回调通知界面有新设备
            mDeviceListener.notifyNewDevice(bluetoothDevice);
        }
    };

    private BluetoothHelper(Context context) {
        this.mContext = context;
        mAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

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

    /**
     * 扫描设备
     *
     * @return
     */
    public boolean scanBluetoothDevice() {
        return mAdapter.startLeScan(mScanCallback);
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
    protected void read() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (readable) {
                    // 设置descriptor
                    mWriter.setValue(Utils.hexStringToBytes("524541440a"));

                    // 直达发送成功才退出循环
                    boolean isSuccessful = false;
                    do {
                        isSuccessful = mGatt.writeCharacteristic(mWriter);
                        Log.e("codelevex", "写入命令：" + isSuccessful);
                    } while (!isSuccessful);

                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    public void stopRead() {
        readable = false;
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


    public interface DeviceChangeListener {
        void notifyNewDevice(BluetoothDevice device);
    }

    public interface DataChangeListener {
        void notifyDataChanged(Temprature temprature);
    }

    public interface ViewHandlerListener {
        void handleViewBeforeStartRead();
    }
}
