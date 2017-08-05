package com.dhy.coffeesecret.model.bake;

import android.bluetooth.BluetoothDevice;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseModel;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.pojo.TemperatureSet;
import com.dhy.coffeesecret.ui.device.record.BreakPointerRecorder;
import com.dhy.coffeesecret.ui.device.record.RecorderSystem;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.Date;
import java.util.List;

import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCBEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCINWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.BEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.INWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.OUTWINDLINE;
import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.RAWBEAN;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Presenter4BakeActivity extends BaseBlePresenter {
    private static final int[] LINE_INDEX = {BEANLINE, INWINDLINE, OUTWINDLINE, ACCBEANLINE, ACCINWINDLINE, ACCOUTWINDLINE};
    private static Presenter4BakeActivity mPresenter;
    private IBakeView mBakeView;
    private IBakeModel mBakeModel;
    private List<Entry> mEventList;
    // 当前烘焙状态，一爆，二爆等状态
    private int mCurStatus = RAWBEAN;
    // 开始记录拐点发生的位置
    private BreakPointerRecorder breakPointerRecorder;
    // 开始计时,并初始化一系列任务
    private RecorderSystem recorderSystem;
    // 当前温度的坐标轴数据
    private Entry curBeanEntry;
    // 收到最后一个温度的时间，属于float类型，单位秒
    private float lastTime;
    private Entry awaitEntry;

    /**
     * 方便调用，第一次设置蓝牙操作接口后即可直接调用
     *
     * @return
     */
    public static Presenter4BakeActivity newInstance() {
        if (BaseBlePresenter.mBluetoothOperator == null) {
            throw new RuntimeException("BluetoothOperator Is Null");
        } else if (mPresenter == null) {
            mPresenter = new Presenter4BakeActivity();
        }
        return mPresenter;
    }

    public void init() {
        recorderSystem = new RecorderSystem();
        breakPointerRecorder = new BreakPointerRecorder();
        if (curBeanEntry != null) {
            super.bakeReportProxy.setStartTemperature(curBeanEntry.getY() + "");
            super.bakeReportProxy.setDate(Utils.data2Timestamp(new Date()));
            super.bakeReportProxy.setAmbientTemperature(Temperature.getEnvTemp() + "");
        }
    }

    /**
     * 先初始化RecorderSystem和BreakPointerRecorder再使用
     *
     * @return
     */
    public TemperatureSet getTemperatureSet() {
        if (recorderSystem == null) {
            throw new RuntimeException("没有初始化RecorderSystem...");
        }
        return recorderSystem.getTemperatureSet();
    }

    @Override
    public void setView(IBaseView baseView) {
        this.mBakeView = (IBakeView) baseView;
    }

    @Override
    public void setModel(IBaseModel baseModel) {
        this.mBakeModel = (IBakeModel) baseModel;
    }

    @Override
    public void onScanning(BluetoothDevice bluetoothDevice, int rssi) {

    }

    @Override
    public void notifyTemperature(Temperature temperature) {
        notifyTemperatureByManual(temperature);
    }

    @Override
    public void toPreConnect(int status) {

    }

    @Override
    public void onScanningComplete(BluetoothDevice... bluetoothDevice) {

    }

    @Override
    public void toConnecting(int status) {

    }

    @Override
    public void toConnected(int status) {

    }

    @Override
    public void toDisconnected(int status) {
        // TODO 不断进行重连...
        mBakeView.showToast(status, "蓝牙已断开，请勿远离...");
        /*while (false) {

        }*/
    }

    @Override
    public void toDisconnecting(int status) {

    }

    private void notifyTemperatureByManual(Temperature temperature) {
        // 温度数组: 0->豆温，1->进风温, 2->出风温, 3->加速豆温, 4->加速进风温, 5->加速出风温
        float[] tempratures = {temperature.getBeanTemp(), temperature.getInwindTemp(),
                temperature.getOutwindTemp(), temperature.getAccBeanTemp(), temperature.getAccInwindTemp(),
                temperature.getAccOutwindTemp()};

        if (breakPointerRecorder.record(temperature) && tempratures[0] > 160 && mCurStatus != DevelopBar.FIRST_BURST) {
            mCurStatus = AFTER160;
        }

        lastTime = recorderSystem.addTemprature(temperature);

        curBeanEntry = new Entry(lastTime, Utils.getCrspTempratureValue(tempratures[0] + ""));

        mBakeView.updateChart(curBeanEntry, BEANLINE);
        for (int i = 1; i < 6; ++i) {
            mBakeView.updateChart(new Entry(lastTime, Utils.getCrspTempratureValue(tempratures[i] + "")), LINE_INDEX[i]);
        }
        // 更新发展率条的颜色
        mBakeView.updateTimer(mCurStatus);
        // 更新时间文本
        mBakeView.updateTemperatureText(temperature);
    }

    /**
     * 用于更新发展率状态
     *
     * @param developStatus
     */
    public void updateDevelopStatus(int developStatus) {
        this.mCurStatus = developStatus;
    }

    public void stopOneEvent(int status, String description) {
        // 获取事件消耗时间间隔
        float timeInterval = recorderSystem.getTimeInterval();
        // 获取平均升温速率
        float avgTemperature = recorderSystem.getAvgAccBeanTemprature();
        // 设置当前时间x轴位置
        int curStartIndex = recorderSystem.getCurIndex();
        // 封装信息
        Model4Bake.EventInfo info = new Model4Bake.EventInfo(curStartIndex, timeInterval, Utils.get2PrecisionFloat(avgTemperature));
        // 进行存储
        mBakeModel.recordOneEvent(status, info);
        // 更新当前bean
        updateBeanEntryEvent(status, description);

        // 通知图表更新
        mBakeView.notifyChartDataChanged();
    }

    public void updateBeanEntryEvent(int status, String description) {
        // 更新当前bean
        mBakeModel.updateEntry(curBeanEntry, status, description);
        // 本地记录事件结点
        mEventList.add(curBeanEntry);
        // 添加事件修改至temperature set
        recorderSystem.addEvent(lastTime + "", description + ":" + status);
    }

    /**
     * 延迟添加数据的entry，用于风火、其他等事件设置
     */
    public void setAwaitEntry() {
        this.awaitEntry = curBeanEntry;
        this.mEventList.add(this.awaitEntry);
    }

    /**
     * 重置  等待结点
     */
    public void resetAwaitEntry() {
        this.mEventList.remove(awaitEntry);
        this.awaitEntry = null;
    }

    public void dynamicUpdateEvent(Event event) {
        awaitEntry.setEvent(event);
        recorderSystem.addEvent(awaitEntry.getX() + "", event.getDescription() + ":" + event.getCurStatus());
    }

    /**
     * 开始一个新的事件记录
     */
    public void startNewRecordEvent() {
        recorderSystem.startNewEvent();
    }


    public BakeReportProxy generateBakeReport() {
        String deviceName = mBluetoothOperator.getConnectedDevice().getName();
        // FIXME 建造者模式
        super.bakeReportProxy.setTempratureSet(recorderSystem.getTemperatureSet());
        super.bakeReportProxy.setDevice(deviceName);
        super.bakeReportProxy.setDevelopmentTime(mBakeView.getDevelopTime());
        super.bakeReportProxy.setDevelopmentRate(mBakeView.getDevelopRate());
        super.bakeReportProxy.setBreakPointerTime(breakPointerRecorder.getbreakPointerTime());
        super.bakeReportProxy.setBreakPointerTemprature(breakPointerRecorder.getBreakPointerTemprature());

        Model4Bake.EventInfo dryEvent = mBakeModel.getDryEventInfoByStatus(Event.DRY);
        Model4Bake.EventInfo firstBurstEvent = mBakeModel.getDryEventInfoByStatus(Event.FIRST_BURST);
        Model4Bake.EventInfo endEvent = mBakeModel.getDryEventInfoByStatus(Event.END);
        // 开始烘焙与脱水的时间间隔和平均升温率
        super.bakeReportProxy.setAvgDryTime(dryEvent.getConsumeTime());
        super.bakeReportProxy.setAvgDryTemprature(dryEvent.getAvgTemperature());
        // 脱水与一爆的时间间隔和平均升温率
        super.bakeReportProxy.setAvgFirstBurstTime(firstBurstEvent.getConsumeTime());
        super.bakeReportProxy.setAvgFirstBurstTemprature(firstBurstEvent.getAvgTemperature());
        //
        super.bakeReportProxy.setAvgEndTime(endEvent.getConsumeTime());
        super.bakeReportProxy.setAvgEndTemprature(endEvent.getAvgTemperature());
        super.bakeReportProxy.setGlobalAccBeanTemp(recorderSystem.getGlobalAccTemprature());
        super.bakeReportProxy.setEndTemp(curBeanEntry.getY());

        return super.bakeReportProxy;
    }
}
