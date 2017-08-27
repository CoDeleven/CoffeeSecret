package com.dhy.coffeesecret.model.bake;

import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.bake.developbar.Presenter4DevelopBar;
import com.dhy.coffeesecret.model.chart.Presenter4Chart;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.ui.bake.BakeActivity;
import com.dhy.coffeesecret.ui.bake.record.BreakPointerRecorder;
import com.dhy.coffeesecret.ui.bake.record.RecorderSystem;
import com.dhy.coffeesecret.utils.ConvertUtils;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.ui.common.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.jesse.nativelogger.NLogger;

import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;
import static com.dhy.coffeesecret.ui.common.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.ui.common.views.DevelopBar.RAW_BEAN;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Presenter4BakeActivity extends BaseBlePresenter<IBakeView, Model4Bake> {
    private static final String TAG = Presenter4BakeActivity.class.getSimpleName();
    private static final int[] LINE_INDEX = {BEANLINE, INWINDLINE, OUTWINDLINE, ACCBEANLINE, ACCINWINDLINE, ACCOUTWINDLINE};
    private static Presenter4BakeActivity mSelf;
    private List<Entry> mEventList;
    // 当前烘焙状态，一爆，二爆等状态
    private int tCurStatus;
    // 开始记录拐点发生的位置
    private BreakPointerRecorder breakPointerRecorder;
    // 开始计时,并初始化一系列任务
    private RecorderSystem recorderSystem;
    // 当前温度的坐标轴数据
    private Entry curBeanEntry;
    // 收到最后一个温度的时间，属于float类型，单位秒
    private float lastTime;
    private Entry awaitEntry;
    private Presenter4Chart mChartPresenter;
    private Presenter4DevelopBar mDevelopBarPresenter;
    private boolean enabledFirstBurst = false;
    private boolean enabledSecondBurst = false;

    private Presenter4BakeActivity() {
        super(Model4Bake.newInstance());
        NLogger.i(TAG, "当前使用的烘焙报告：" + mModelOperator.getCurBakingReport());
    }

    /**
     * 方便调用，第一次设置蓝牙操作接口后即可直接调用
     *
     * @return 返回Presenter4BakeActivity实体类
     */
    public static Presenter4BakeActivity newInstance() {
        if (BaseBlePresenter.mBluetoothOperator == null) {
            throw new RuntimeException("BluetoothOperator Is Null");
        } else if (mSelf == null) {
            mSelf = new Presenter4BakeActivity();
        }
        return mSelf;
    }

    public List<Entry> getEventList() {
        return mEventList;
    }

    public boolean isEnableDoubleClick(){
        return mModelOperator.getAppConfig().isDoubleClick();
    }

    public void setChartPresenter(Presenter4Chart presenter) {
        this.mChartPresenter = presenter;
    }

    public void setDevelopBarPresenter(Presenter4DevelopBar presenter) {
        this.mDevelopBarPresenter = presenter;
    }

    public void init() {
        tCurStatus = RAW_BEAN;
        mEventList = new LinkedList<>();
        enabledFirstBurst = false;
        enabledSecondBurst = false;
        recorderSystem = new RecorderSystem();
        breakPointerRecorder = new BreakPointerRecorder();
        if (curBeanEntry != null) {
            mModelOperator.getCurBakingReport().setStartTemperature(curBeanEntry.getY() + "");
            mModelOperator.getCurBakingReport().setDate(FormatUtils.data2Timestamp(new Date()));
            mModelOperator.getCurBakingReport().setAmbientTemperature(Temperature.getEnvTemp() + "");
        }
    }

    public void reconnect() {
        String latestAddress = mBluetoothOperator.getLatestAddress();
        mBluetoothOperator.connect(latestAddress);
    }

    /**
     * 先初始化RecorderSystem和BreakPointerRecorder再使用
     *
     * @return
     */
/*    public TemperatureSet getTemperatureSet() {
        if (recorderSystem == null) {
            throw new RuntimeException("没有初始化RecorderSystem...");
        }
        return recorderSystem.getTemperatureSet();
    }*/

    @Override
    public void notifyTemperature(Temperature temperature) {
        long start = System.currentTimeMillis();
        notifyTemperatureByManual(temperature);
        long end = System.currentTimeMillis();
        Log.i(TAG, "notifyTemperature: 经历了 " + (end - start) / 1000.0f + " 秒");
    }

    @Override
    public void toDisconnected() {
        mViewOperator.showToast(BluetoothProfile.STATE_DISCONNECTED, "蓝牙已断开，请勿远离...");
        // 通过下面的方法，弹出是否重连的对话框
        mViewOperator.showWarnDialog(BluetoothProfile.STATE_DISCONNECTED);
    }

    @Override
    public void toUrgentDisconnected() {
        toDisconnected();
    }

    private void notifyTemperatureByManual(Temperature temperature) {
        // 温度数组: 0->豆温，1->进风温, 2->出风温, 3->加速豆温, 4->加速进风温, 5->加速出风温
        float[] temperatures = {temperature.getBeanTemp(), temperature.getInwindTemp(),
                temperature.getOutwindTemp(), temperature.getAccBeanTemp(), temperature.getAccInwindTemp(),
                temperature.getAccOutwindTemp()};

        if (breakPointerRecorder.record(temperature) && temperatures[0] > 160 && tCurStatus != DevelopBar.FIRST_BURST) {
            tCurStatus = AFTER160;
        }

        lastTime = recorderSystem.addTemprature(temperature);

        curBeanEntry = new Entry(lastTime, ConvertUtils.getCrspTemperatureValue(temperatures[0] + ""));

        // ((IBakeView) (super.mViewOperator)).updateChart(curBeanEntry, BEANLINE);
        mChartPresenter.dynamicAddDataImm(curBeanEntry, BEANLINE, !super.isMinimized());
        for (int i = 1; i < 6; ++i) {
            // ((IBakeView) (super.mViewOperator)).updateChart();
            mChartPresenter.dynamicAddDataImm(new Entry(lastTime, ConvertUtils.getCrspTemperatureValue(temperatures[i] + "")), LINE_INDEX[i], !super.isMinimized());
        }
        // 更新发展率条的数据
        updateDevelopStatus(tCurStatus);
        if (!mSelf.isMinimized()) {
            // 更新发展率条的颜色
            mViewOperator.updateText(BakeActivity.UPDATE_TIME_TEXT, tCurStatus);
            // 更新温度文本
            mViewOperator.updateText(BakeActivity.UPDATE_TEMPERATURE_TEXT, temperature);
        }
    }

    /**
     * 用于更新发展率状态
     *
     * @param developStatus 发展率的状态
     */
    public void updateDevelopStatus(int developStatus) {
        this.tCurStatus = developStatus;
        // 用于更新DevelopBar的时间
        this.mDevelopBarPresenter.updateDevelopBar(developStatus);
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
        mModelOperator.recordOneEvent(status, info);
        // 更新当前bean
        updateBeanEntryEvent(status, description);
        // 记录一下中间状态的按钮事件,比如一爆、一爆结束 两种状态
        recordMiddleBtnStatus(status);
    }

    public boolean isEnabledFirstBurst() {
        return enabledFirstBurst;
    }

    public boolean isEnabledSecondBurst() {
        return enabledSecondBurst;
    }

    private void recordMiddleBtnStatus(int btnStatus) {
        if (btnStatus == Event.FIRST_BURST) {
            enabledFirstBurst = true;
        } else if (btnStatus == Event.SECOND_BURST) {
            enabledSecondBurst = true;
        }
    }

    public void updateBeanEntryEvent(int status, String description) {
        updateBeanEntryEvent(curBeanEntry, status, description);
    }

    private void updateBeanEntryEvent(Entry entry, int status, String description) {
        // 更新当前bean
        mModelOperator.updateEntry(entry, status, description);
        // 本地记录事件结点
        mEventList.add(entry);
        // 添加事件修改至temperature set
        recorderSystem.addEvent(lastTime + "", description + ":" + status);
        // 通知图表更新
        mChartPresenter.refreshChart();
        // ((IBakeView) super.mViewOperator).notifyChartDataChanged();
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
        // awaitEntry.setEvent(event);
        // recorderSystem.addEvent(awaitEntry.getX() + "", event.getDescription() + ":" + event.getCurStatus());
        updateBeanEntryEvent(awaitEntry, event.getCurStatus(), event.getDescription() + ":" + event.getCurStatus());
    }

    /**
     * 开始一个新的事件记录
     */
    public void startNewRecordEvent() {
        recorderSystem.startNewEvent();
    }

    /**
     * 处理数据，加入到BakeReport中
     *
     * @return 返回一个结束烘焙的报告，但是还没有进行补充
     */
    private BakeReportProxy handlerData2BakeReport() {
        BakeReportProxy localProxy = getModel().getCurBakingReport();
        // String deviceName = mBluetoothOperator.getConnectedDevice().getName();
        // FIXME 建造者模式

        localProxy.setTempratureSet(recorderSystem.getTemperatureSet());
        // localProxy.setDevice(deviceName);
        localProxy.setDevelopmentTime(mDevelopBarPresenter.getDevelopTimeString());
        localProxy.setDevelopmentRate(mDevelopBarPresenter.getDevelopRateString());
        localProxy.setBreakPointerTime(breakPointerRecorder.getbreakPointerTime());
        localProxy.setBreakPointerTemprature(breakPointerRecorder.getBreakPointerTemprature());

        Model4Bake.EventInfo dryEvent = mModelOperator.getDryEventInfoByStatus(Event.DRY);
        Model4Bake.EventInfo firstBurstEvent = mModelOperator.getDryEventInfoByStatus(Event.FIRST_BURST);
        Model4Bake.EventInfo endEvent = mModelOperator.getDryEventInfoByStatus(Event.END);
        if (dryEvent != null) {
            // 开始烘焙与脱水的时间间隔和平均升温率
            localProxy.setAvgDryTime(dryEvent.getConsumeTime());
            localProxy.setAvgDryTemprature(dryEvent.getAvgTemperature());
            if (firstBurstEvent != null) {
                // 脱水与一爆的时间间隔和平均升温率
                localProxy.setAvgFirstBurstTime(firstBurstEvent.getConsumeTime());
                localProxy.setAvgFirstBurstTemprature(firstBurstEvent.getAvgTemperature());
                if (endEvent != null) {
                    // 一爆开始到结束花的时间和平均升温率
                    localProxy.setAvgEndTime(endEvent.getConsumeTime());
                    localProxy.setAvgEndTemprature(endEvent.getAvgTemperature());
                }
            }
        }

        localProxy.setGlobalAccBeanTemp(recorderSystem.getGlobalAccTemprature());
        localProxy.setEndTemp(curBeanEntry.getY());

        NLogger.i(TAG, "最终生成的报告为：" + localProxy.toString());
        return localProxy;
    }

    public void stopBake() {
        handlerData2BakeReport();
        mChartPresenter.clearReferLine();
        super.setBakingNow(false);
    }

    @Override
    public void toConnected() {
        mViewOperator.showToast(BluetoothProfile.STATE_CONNECTED, "蓝牙连接已恢复...");
    }

    public void startBaking() {
        super.setBakingNow(true);
    }
}
