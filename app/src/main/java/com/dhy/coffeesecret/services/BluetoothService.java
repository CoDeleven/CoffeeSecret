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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' &gt;'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
public class BluetoothService extends Service {

    public static final UUID PRIMARY_SERVICE = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_WRITE = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_READ = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    public static final UUID WRITE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final String TAG = BluetoothService.class.getSimpleName();

    public static final long SCAN_TIME = 12000;
    public static String FIRST_CHANNEL = "4348414e3b323130300a";
    public static String SECOND_CHANNEL = "4348414e3b333230300a";
    public static String READ_TEMP_COMMAND = "524541440a";
    public static volatile boolean READABLE = false;
    public static BluetoothService.BluetoothOperator BLUETOOTH_OPERATOR;
    private static ReadTasker mRunThread;
    private final int sleepTime = 750;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriter;
    private BluetoothGattCharacteristic mReader;
    private BluetoothDevice mCurDevice;
    private String lastAddress;
    private boolean tempStatus = false;
    private int mConnectionState = STATE_DISCONNECTED;
    private DataChangedListener dataChangedListener;
    private DeviceChangedListener deviceChangedListener;
    private ViewControllerListener viewControllerListener;
    private boolean scanning = false; //标识当前蓝牙是否在扫描
    private static final int TRANSPORT_LE = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (viewControllerListener != null) {
                viewControllerListener.handleViewBeforeStartRead();
            }
        }
    };

    /**
     * 扫描到新设备进行回调
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.d(TAG, bluetoothDevice.getName() + "-" + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
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
            if (mRunThread != null && mRunThread.readData(hexData)) {
                mRunThread.setHandling(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "发现服务");

            BluetoothGattService service = gatt.getService(PRIMARY_SERVICE);
            // 获取写特性
            mWriter = service.getCharacteristic(TAG_WRITE);
            // 获取读特性
            mReader = service.getCharacteristic(TAG_READ);

            if (mWriter == null || mReader == null) {
                throw new RuntimeException("特性异常");
            }
            // 设置通知
            Log.d(TAG, "通知写入状态：" + mBluetoothGatt.setCharacteristicNotification(mReader, true));
            BluetoothGattDescriptor descriptor = mReader.getDescriptor(WRITE_DESCRIPTOR);

            Log.d(TAG, "监听器状态：" + descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
            Log.d(TAG, "温度写入状态：" + mBluetoothGatt.writeDescriptor(descriptor));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "descriptor写入成功：" + (BluetoothGatt.GATT_SUCCESS == status));
            mHandler.sendMessage(new Message());
            startRead();
        }

        /**
         * 状态发生改变时进行回调
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "newState" + newState);
            Log.d(TAG, "gatt:" + gatt.toString());
            if (mRunThread != null) {
                mRunThread.clearData();
                mRunThread.setHandling(false);
                // 阻断
                mRunThread.interrupt();
            }
            // 如果当前状态是已连接
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 设置当前状态
                mConnectionState = STATE_CONNECTED;
                mBluetoothGatt.discoverServices();
                // 设置当前设备
                mCurDevice = gatt.getDevice();
                lastAddress = mCurDevice.getAddress();
                deviceChangedListener.notifyDeviceConnectStatus(true, gatt.getDevice());
            } else if (newState == STATE_DISCONNECTED || newState == STATE_DISCONNECTING) {
                mConnectionState = STATE_DISCONNECTED;
                mBluetoothGatt.discoverServices();
                mCurDevice = null;
                // 如果连接失败，断开和该资源的链接
                disconnect();
                Log.d(TAG, "close");
                deviceChangedListener.notifyDeviceConnectStatus(false, gatt.getDevice());
            }
        }
    };

    public BluetoothService() {
    }

    public boolean initialize() {
        // 获取蓝牙
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.d(TAG, "无法获取BluetoothManager");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "无法获取BluetoothAdapter");
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
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
            Log.d(TAG, "BluetoothService 重连");
            if (mBluetoothGatt.connect()) {
                Log.d(TAG, "BluetoothService 重连成功状态");
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        // 直接连接设备
//        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(this, false, mGattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }

        Log.d(TAG, "Trying to create a new connection.:" + mBluetoothGatt.toString());
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter尚未初始化");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    // 服务绑定返回唯一的操作对象
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
        Log.d(TAG, "startRead~~~~");
        while (mRunThread != null && mRunThread.isAlive()) {
            mRunThread.clearData();
        }
        mRunThread = new ReadTasker();
        mRunThread.setReadable(true);
        mRunThread.setDataChangedListener(dataChangedListener);
        try {
            Thread.currentThread().sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRunThread.start();
    }

    /**
     * 进行判断当前状态是否连接
     *
     * @return 已连接?
     */
    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    /**
     * 用于在开始读取前进行的View操作
     */
    public interface ViewControllerListener {
        void handleViewBeforeStartRead();
    }

    /**
     * 数据监听器
     */
    public interface DataChangedListener {
        void notifyDataChanged(Temprature temprature);
    }


    /**
     * 设备状态监听器
     */
    public interface DeviceChangedListener {
        void notifyDeviceConnectStatus(boolean isConnected, BluetoothDevice device);

        void notifyNewDevice(BluetoothDevice device, int rssi);

    }

    public abstract static class ScanListener {
        protected void onScanStart() {
        }

        protected void onScanStop() {
        }
    }

    protected abstract class DataReader {
        private boolean isHandling = true;

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

        private ScanListener scanListener;

        /**
         * 提供给用户的借口
         *
         * @return
         */
        public boolean isConnected() {
            return BluetoothService.this.isConnected();
        }

        /**
         * 更改当前数据监听器的引用
         *
         * @param dataChangedListener
         */
        public void setDataChangedListener(DataChangedListener dataChangedListener) {
            BluetoothService.this.dataChangedListener = dataChangedListener;
            if (mRunThread != null) {
                mRunThread.setDataChangedListener(dataChangedListener);
            }
        }

        public void setDeviceChangedListener(DeviceChangedListener deviceChangedListener) {
            BluetoothService.this.deviceChangedListener = deviceChangedListener;
        }

        public boolean isDataChangedNull() {
            return BluetoothService.this.dataChangedListener == null;
        }

        public void setViewControllerListener(ViewControllerListener viewControllerListener) {
            BluetoothService.this.viewControllerListener = viewControllerListener;
        }

        public void setScanListener(ScanListener listener) {
            this.scanListener = listener;
        }

        /**
         * 获取当前连接设备的名字
         *
         * @return
         */
        public String getCurDeviceName() {
            return mCurDevice.getName();
        }

        /**
         * 获取当前连接设备
         *
         * @return
         */
        public BluetoothDevice getBluetoothDevice() {
            return mCurDevice;
        }

        /**
         * 当前蓝牙是否启用
         *
         * @return
         */
        public boolean isEnable() {
            return mBluetoothAdapter.isEnabled();
        }

        public void enable() {
            mBluetoothAdapter.enable();
        }


        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 停止扫描设备
                stopScanDevice();
                // 关闭蓝牙时清空当前连接设备
                mCurDevice = null;
                if (mRunThread != null) {
                    mRunThread.clearData();
                }
                Log.w(TAG, "关闭蓝牙");
                mCurDevice = null;
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                }

                // READABLE = false;
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.cancelDiscovery();
                    mBluetoothAdapter.disable();
                }
                // 设置当前连接状态为DISCONNECTED
                mConnectionState = STATE_DISCONNECTED;

            }
        };

        /**
         * 关闭蓝牙
         */
        public void disableBluetooth() {
            handler.sendEmptyMessage(0);
        }

        /**
         * 开启扫描
         */
        public void startScanDevice() {
            if (!scanning) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopScanDevice();
                    }
                }, SCAN_TIME);
                Log.d(TAG, "扫描成功：？" + mBluetoothAdapter.startLeScan(mScanCallback));
                scanning = true;
            }
        }

        /**
         * 停止扫描
         */
        public void stopScanDevice() {
            if (scanning) {
                mBluetoothAdapter.stopLeScan(mScanCallback);
                Log.d(TAG, "停止扫描，scannng = " + scanning);
            }
            if (scanListener != null) {
                scanListener.onScanStop();
            }
            scanning = false;
        }

        /**
         * 正常的连接
         *
         * @param device 需要连接的设备
         * @return
         */
        public boolean connect(BluetoothDevice device) {
            stopScanDevice();
            return BluetoothService.this.connect(device);
        }

        /**
         * 通过地址进行重连
         *
         * @param address
         * @return
         */
        public boolean connect(String address) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (device != null) {
                mCurDevice = device;
                return BluetoothService.this.connect(device);
            } else {
                return false;
            }
        }

        // FIXME: 17-3-24 三思

        /**
         * 用于进行重连
         *
         * @return
         */
        public boolean reConnect() {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(lastAddress);
            if (device == null) {
                return false;
            } else {
                return connect(device);
            }
        }
    }

    class ReadTasker extends Thread {

        private String result = "";
        private DataChangedListener dataChangedListener;
        private boolean readable = false;

        private DataReader readData1 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                mWriter.setValue(Utils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                Log.d(TAG, "第1通道数据:" + Utils.hexString2String(str));
                result += str;
                if (str.endsWith("0a")) {
                    result += "2c";
                    dataReader = channelListener2;
                    channelListener2.setWriteCommand();
                }
                return false;
            }
        };
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
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                Log.d(TAG, Thread.currentThread() + ":通道1:" + Utils.hexString2String(str));
                if (str.endsWith("65") || str.endsWith("0a")) {
                    // 设置回正常读取
                    dataReader = readData1;
                    // 启动温度读取器
                    dataReader.setWriteCommand();
                }
                return false;
            }
        };
        private DataReader dataReader = channelListener1;

        private DataReader readData2 = new DataReader() {
            @Override
            public void setWriteCommand() {
                boolean state = false;
                mWriter.setValue(Utils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public synchronized boolean readData(String str) {
                synchronized (result) {
                    Log.d(TAG, "第2通道数据:" + Utils.hexString2String(str));
                    result += str;
                    if (str.endsWith("0a")) {
                        dataReader = channelListener1;
                        if (dataChangedListener != null) {
                            try {
                                dataChangedListener.notifyDataChanged(Temprature.parseHex2Temprature(Utils.hexString2String(result)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                Log.d(TAG, Thread.currentThread() + ":通道2:" + Utils.hexString2String(str));
                if (str.endsWith("65") || str.endsWith("0a")) {
                    dataReader = readData2;
                    dataReader.setWriteCommand();
                }
                return false;
            }
        };

        @Override
        public void run() {
            while (readable) {
                dataReader.setWriteCommand();
                final Timer waitTime = new Timer();
                // 等待dataRead返回数据
                try {
                    Log.d(TAG, "读取");
                    int count = 1;
                    while (dataReader.isHandling()) {
                        if (--count == 0) {
                            waitTime.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // 如果3s以后isHanding依然处于true的状态则断开进行重连
                                    if (dataReader.isHandling()) {
                                        dataReader = channelListener1;
                                        dataReader.setHandling(false);
                                    }
                                }
                            }, 3000);
                        }
                    }
                    // 已经成功，则取消timer
                    waitTime.cancel();
                    dataReader.setHandling(true);
                    Thread.currentThread().sleep(sleepTime);
                } catch (InterruptedException e) {
                    Log.d(TAG, "已经停止运行该线程");
                    setHandling(false);
                    readable = false;
                }
            }
            Log.d(TAG, "已经停止运行该线程-------------" + Thread.currentThread());

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

        public void clearData() {
            dataChangedListener = null;
            setHandling(false);
            readable = false;
            dataReader = channelListener1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRunThread.clearData();
        mRunThread.interrupt();
        mRunThread = null;
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }
}
