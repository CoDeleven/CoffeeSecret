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

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.interfaces.IBleConnCallback;
import com.dhy.coffeesecret.services.interfaces.IBleDataCallback;
import com.dhy.coffeesecret.services.interfaces.IBleScanCallback;
import com.dhy.coffeesecret.services.interfaces.IBluetoothOperator;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static com.dhy.coffeesecret.utils.ConvertUtils.hexString2String;

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
public class BluetoothService extends Service implements IBluetoothOperator {
    public static final UUID PRIMARY_SERVICE = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_WRITE = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");
    public static final UUID TAG_READ = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    public static final UUID WRITE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final long SCAN_TIME = 12000;
    private static final String TAG = BluetoothService.class.getSimpleName();
    private static final int TRANSPORT_LE = 2;
    public static String FIRST_CHANNEL = "4348414e3b323130300a";
    public static String SECOND_CHANNEL = "4348414e3b333230300a";
    public static String READ_TEMP_COMMAND = "524541440a";
    public static IBluetoothOperator BLUETOOTH_OPERATOR;
    private static int retryCount = 0;
    private static volatile ReadTasker mRunThread;
    private final int sleepTime = 900;
    private volatile IBleDataCallback mTemperatureListener;
    private IBleScanCallback mScanListener;
    private IBleConnCallback mConnectionListener;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriter;
    private BluetoothGattCharacteristic mReader;
    private BluetoothDevice mCurDevice;
    private String lastAddress;
    private boolean tempStatus = false;
    private int mConnectionState = STATE_DISCONNECTED;
    private ViewControllerListener viewControllerListener;
    private boolean scanning = false; //标识当前蓝牙是否在扫描
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
            ScanResult scanResult = new ScanResult(bluetoothDevice, i, bytes, System.currentTimeMillis());
            Log.d(TAG, bluetoothDevice.getName() + "-" + bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
            // 回调通知界面有新设备
            // deviceChangedListener.notifyNewDevice(bluetoothDevice, i);
            if (mScanListener != null) {
                mScanListener.onScanning(scanResult);
            }
        }

    };
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 将bytes数组转换成16进制存储起来
            String hexData = null;
            hexData = ConvertUtils.bytesToHexString(characteristic.getValue());
            // 如果读到最后一行数据，返回true，并设置处理结束
            if (mRunThread != null && mRunThread.readData(hexData)) {
                mRunThread.setHandling(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 129) {
                BluetoothDevice device = gatt.getDevice();
                // 直接断开连接，然后释放资源等待gatt重连，这会导致回调onConnectionStateChange时的空指针异常。因为gatt已经被close了
                gatt.disconnect();
                gatt.close();
                connect(device.getAddress());
                Log.d(TAG, "status为129进行重连: retry:" + ++retryCount);
                return;
            }
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
                mRunThread.readable = false;
                // 阻断
                mRunThread.interrupt();
            }
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                mConnectionListener.toConnected();
            }
            // 如果当前状态是已连接
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 设置当前状态
                mConnectionState = STATE_CONNECTED;
                mBluetoothGatt.discoverServices();
                // 设置当前设备
                mCurDevice = gatt.getDevice();
                lastAddress = mCurDevice.getAddress();
                mConnectionListener.toConnected();
            } else if (newState == STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;

                if (mRunThread != null) {
                    mRunThread.clearData();
                    // 阻断
                    mRunThread.interrupt();
                }

                // mBluetoothGatt.discoverServices();
                mCurDevice = null;
                // 如果连接失败，断开和该资源的链接
                if (gatt != null) {
                    gatt.disconnect();
                    gatt.close();
                }
                mConnectionListener.toDisconnected();
            }
        }
    };
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

    public BluetoothService() {
    }

    @Override
    public String getLatestAddress() {
        return lastAddress;
    }

    @Override
    public void setConnectionListener(IBleConnCallback connectionListener) {
        this.mConnectionListener = connectionListener;
    }

    @Override
    public void disableBle() {
        if (mBluetoothGatt != null) {
            clearInfo();
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    private void clearInfo() {
        // TODO 清除一些回调接口之类的
    }

    @Override
    public void setTemperatureListener(IBleDataCallback temperatureListener) {
        this.mTemperatureListener = temperatureListener;
    }

    @Override
    public void setScanCallbackListener(IBleScanCallback scanCallbackListener) {
        this.mScanListener = scanCallbackListener;
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
    public boolean connect(final ScanResult device) {
        stopScanDevice();
        if (mBluetoothAdapter == null || device == null) {
            return false;
        }

        // 直接连接设备，如果配对了需要加上下面这个
//        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             mBluetoothGatt = device.connectGatt(this, false, mGattCallback, TRANSPORT_LE);
//         } else {
        mBluetoothGatt = device.getDevice().connectGatt(this, false, mGattCallback);
        // }

        Log.d(TAG, "Trying to create a new connection.:" + mBluetoothGatt.toString());
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    // 服务绑定返回唯一的操作对象
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if (BLUETOOTH_OPERATOR == null) {
            BLUETOOTH_OPERATOR = this;
        }
        initialize();
        return new BluetoothBinder();
    }

    /**
     * 开启新线程进行读取
     */
    private void startRead() {
        Log.d(TAG, "startRead~~~~");
        while (mRunThread != null && mRunThread.isAlive()) {
            mRunThread.clearData();
        }
        mRunThread = new ReadTasker();
        mRunThread.setReadable(true);
        try {
            Thread.currentThread().sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRunThread.start();
    }

    /**
     * 获取当前连接设备
     *
     * @return
     */
    @Override
    public BluetoothDevice getConnectedDevice() {
        return mCurDevice;
    }

    /**
     * 当前蓝牙是否启用
     *
     * @return
     */
    @Override
    public boolean isEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void enableBle() {
        mBluetoothAdapter.enable();
    }

    /**
     * 开启扫描
     */
    @Override
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
    @Override
    public void stopScanDevice() {
        if (scanning) {
            mBluetoothAdapter.stopLeScan(mScanCallback);
            Log.d(TAG, "停止扫描，scannng = " + scanning);
        }
        if (mScanListener != null) {
            mScanListener.onScanningComplete();
        }
        scanning = false;
    }

    /**
     * 通过地址进行重连
     *
     * @param address
     * @return
     */
    @Override
    public boolean connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device != null) {
            mCurDevice = device;
            return BluetoothService.this.connect(new ScanResult(device, 0, new byte[]{}, System.currentTimeMillis()));
        } else {
            return false;
        }
    }

    /**
     * 进行判断当前状态是否连接
     *
     * @return 已连接?
     */
    @Override
    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
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

    /**
     * 用于在开始读取前进行的View操作
     */
    public interface ViewControllerListener {
        void handleViewBeforeStartRead();
    }

    public class BluetoothBinder extends Binder {
        public IBluetoothOperator getBluetoothOperator() {
            return BluetoothService.this;
        }
    }

    protected abstract class DataReader {
        protected volatile Timer timer;
        protected volatile boolean state = false;
        private volatile boolean isHandling = true;

        protected abstract void setWriteCommand();

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

        void doWrite() {
            // timer = new Timer();
            setWriteCommand();
        }
    }

    class ReadTasker extends Thread {

        private String result = "";
        private Boolean readable = false;
        // 是否是被强迫取消读取的
        private volatile boolean isForcedCancel = false;
        private DataReader channelListener1 = new DataReader() {
            @Override
            public void setWriteCommand() {
                Log.d(TAG, "channelListener1 写入...");
                if (!"".equals(result)) {
                    result = "";
                }
                mWriter.setValue(ConvertUtils.hexStringToBytes(FIRST_CHANNEL));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                if (str.endsWith("65") || str.endsWith("0a")) {
                    // 设置回正常读取
                    dataReader = readData1;
                    // 启动温度读取器
                    dataReader.doWrite();
                }
                return false;
            }
        };
        private DataReader readData2 = new DataReader() {
            @Override
            public void setWriteCommand() {
                Log.d(TAG, "readData2 写入...");
                mWriter.setValue(ConvertUtils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                synchronized (result) {
                    Log.d(TAG, "第2通道数据:" + ConvertUtils.hexString2String(str));
                    result += str;
                    if (str.endsWith("0a")) {
                        dataReader = channelListener1;
                        if (mTemperatureListener != null) {
                            try {
                                mTemperatureListener.notifyTemperature(Temperature.parseHex2Temprature(hexString2String(result)));
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
                Log.d(TAG, "channelListener2 写入...");
                mWriter.setValue(ConvertUtils.hexStringToBytes(SECOND_CHANNEL));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                if (str.endsWith("65") || str.endsWith("0a")) {
                    dataReader = readData2;
                    dataReader.doWrite();
                }
                return false;
            }
        };
        private DataReader readData1 = new DataReader() {
            @Override
            public void setWriteCommand() {
                Log.d(TAG, "readData1 写入...");
                mWriter.setValue(ConvertUtils.hexStringToBytes(READ_TEMP_COMMAND));
                do {
                    state = mBluetoothGatt.writeCharacteristic(mWriter);
                } while (!state && readable);
            }

            @Override
            public boolean readData(String str) {
                result += str;
                if (str.endsWith("0a")) {
                    result += "2c";
                    dataReader = channelListener2;
                    channelListener2.doWrite();
                }
                return false;
            }
        };
        private volatile DataReader dataReader = channelListener1;

        @Override
        public void run() {
            while (readable) {
                // 恢复强制状态用于这次数据读取
                isForcedCancel = false;
                dataReader.doWrite();
                Timer waitTime = new Timer();
                // 等待dataRead返回数据
                try {
                    Log.d(TAG, "读取 readable=" + readable);
                    int count = 1;
                    // 当数据还在读取的路上，isHandling返回true
                    while (dataReader.isHandling()) {
                        // 只会执行一次，故意这样设置的
                        if (--count == 0) {
                            // 在0.2s以内如果没有接收到数据，即isHandling仍然是true
                            // 那么执行TimerTask() 强制取消，设置为已处理来跳出循环
                            waitTime.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // 如果0.2s以后isHanding依然处于true的状态则断开进行重连
                                    if (dataReader.isHandling()) {
                                        Log.d(TAG, "超时，放弃本次读取");
                                        // dataReader = channelListener1;
                                        dataReader.setHandling(false);
                                        isForcedCancel = true;
                                    }
                                }
                            }, 200);
                        } else {
                            // 一直while担心有性能问题，故设置为sleep
                            Thread.currentThread().sleep(50);
                        }
                    }
                    // 已经成功，则取消timer
                    waitTime.cancel();
                    dataReader.setHandling(true);
                    // 如果不是强制结束的，说明是正常读取的，完成了整个流程，给它睡眠一会
                    if (!isForcedCancel) {
                        Thread.currentThread().sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "已经停止运行该线程");
                    setHandling(false);
                    readable = false;
                    return;
                }
            }
            Log.d(TAG, "已经停止运行该线程-------------" + Thread.currentThread());

        }

        public void setReadable(boolean readable) {
            synchronized (this.readable) {
                this.readable = readable;
            }

        }

        public void setHandling(boolean isHandling) {
            dataReader.setHandling(isHandling);
        }

        public boolean readData(String str) {
            return dataReader.readData(str);
        }

        public void clearData() {
            readable = false;
            setHandling(false);
            dataReader = channelListener1;
            interrupt();
        }

    }
}
