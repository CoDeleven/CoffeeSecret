package com.dhy.coffeesecret.services.data;

import android.util.Log;

import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.IBleTemperatureCallback;
import com.dhy.coffeesecret.services.IBleWROperator;
import com.dhy.coffeesecret.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CoDeleven on 17-8-19.
 */

public class TransferControllerTask implements Runnable {
    private static final String TAG = TransferControllerTask.class.getSimpleName();
    private final Lock lock = new ReentrantLock();
    private final Condition mReceiveCondition = lock.newCondition();
    private IBleWROperator mOperator;
    private DataDigger4Ble mCurDigger;
    // 芯片所想要的下一个序号,参考计算机网络ack
    private volatile int mAck;
    // 当前的序号
    private volatile int mSeq;
    private DataDigger4Ble mToFirstChannel = new SwitchChannelToOne();
    private DataDigger4Ble mToSecondChannel = new SwitchChannelToTwo();
    private DataDigger4Ble mReadDataFromFirst = new ReadDataFromChannelOne();
    private DataDigger4Ble mReadDataFromSecond = new ReadDataFromChannelTwo();
    private StringBuilder mData = new StringBuilder();
    private Timer mHistoryTimer = new Timer();
    private IBleTemperatureCallback mTemperatureCallback;
    // 记录强制取消的次数
    private int forcedCancelCount = 0;
    private long mStartOnePeriodTime;

    public TransferControllerTask(IBleWROperator operator) {
        this.mOperator = operator;
    }

    public void setTemperatureCallback(IBleTemperatureCallback callback) {
        this.mTemperatureCallback = callback;
    }

    /**
     * 确认已经收到,让控制器进行下一个写入
     */
    public void acknowledgeData(String temperatureStr) {
        // 停止旧的计时器
        stopOldTimer();

        lock.lock();
        Log.d(TAG, "收到新数据:" + temperatureStr);
        try {
            // 收到新数据，将期望ack递增
            mAck = ++mAck % 4;
            // 处理新的数据
            handlePiggyBackingAck(mAck, temperatureStr);

            // 开启一个新计时器,从写入到收到吓一条信息如果时间超过1s，重新发起
            startNewTimer();

            // 唤醒线程，按照ack进行处理
            mReceiveCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 用于处理捎带数据, 即携带所需数据的Ack
     * 这里的ack再为0或2的时候 一定会携带所需的数据
     * 因为为0 是表示mSeq为3的命令已经发送成功了，收到的是来自通道2的温度数据
     * 为2时，表示mSeq为1的命令已经发送成功了，收到的时来自通道1的温度数据
     *
     * @param ack
     * @param temperatureStr
     */
    private void handlePiggyBackingAck(int ack, String temperatureStr) {
        if (ack == 2 || ack == 0) {
            if (ack == 0) {
                // 相当于添加一个逗号
                mData.append("2c");
            }
            mData.append(temperatureStr);
        }
        if (ack == 0) {
            long durationTimeUntilNow = (System.currentTimeMillis() - mStartOnePeriodTime);
            // TODO 通知消息给监听器
            Log.e(TAG, "历经: " + durationTimeUntilNow + "ms ----> " + mData);
            if (mTemperatureCallback != null) {
                mTemperatureCallback.notifyTemperature(Temperature.parseHex2Temprature(Utils.hexString2String(mData.toString())));
            }
            // 休眠
            try {
                Thread.currentThread().sleep(1000 - durationTimeUntilNow);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startNewPeriodData();
        }
    }

    @Override
    public void run() {
        // 第一次run时，mSeq和mAck均为0，故先执行这里
        if (mSeq == 0 && mAck == 0) {
            try {
                lock.lock();
                dispatchWrite(mAck);
                // 因为第一次是从这里开始，所以这里记录一次；
                // 以后都是acknowledgeData()来记录时间
                mStartOnePeriodTime = System.currentTimeMillis();
                try {
                    mReceiveCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }

        }
        while (true) {
            // 以后会由mAck驱动mSeq
            if (mSeq != mAck) {
                try {
                    lock.lock();
                    dispatchWrite(mAck);
                    // 递增seqNum
                    mSeq = incrementSequenceNum(mSeq);
                    // 经过上一步，mSeq应该等于mAck
                    try {
                        mReceiveCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private int incrementSequenceNum(int seq) {
        return (seq + 1) % 4;
    }

    private int decrementSequenceNum(int seq) {
        return (seq + 4 - 1) % 4;
    }

    private void dispatchWrite(int ack) {
        switch (ack) {
            case 0:
                mCurDigger = mToFirstChannel;
                break;
            case 1:
                mCurDigger = mReadDataFromFirst;
                break;
            case 2:
                mCurDigger = mToSecondChannel;
                break;
            case 3:
                mCurDigger = mReadDataFromSecond;
                break;
        }
        mCurDigger.doWrite(mOperator);
    }

    private void startNewTimer() {
        mHistoryTimer = new Timer();
        mHistoryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "半天write不进去，只好重新发一遍楼");
                lock.lock();
                try {
                    mSeq = decrementSequenceNum(mSeq);
                    ++forcedCancelCount;
                    mStartOnePeriodTime = System.currentTimeMillis();
                    mReceiveCondition.signal();
                } finally {
                    lock.unlock();
                }
            }
        }, 1000 - (System.currentTimeMillis() - mStartOnePeriodTime));
    }

    private void stopOldTimer() {
        mHistoryTimer.cancel();
        mHistoryTimer.purge();
        mHistoryTimer = null;
    }

    private void startNewPeriodData() {
        mData = new StringBuilder();
        mStartOnePeriodTime = System.currentTimeMillis();
    }
}
