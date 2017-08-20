package com.dhy.coffeesecret.model.bake;

import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.model.bake.developbar.Presenter4DevelopBar;
import com.dhy.coffeesecret.model.chart.Presenter4Chart;
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
import java.util.LinkedList;
import java.util.List;

import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;
import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.RAW_BEAN;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Presenter4BakeActivity extends BaseBlePresenter {
    private static final String TAG = Presenter4BakeActivity.class.getSimpleName();
    private static final int[] LINE_INDEX = {BEANLINE, INWINDLINE, OUTWINDLINE, ACCBEANLINE, ACCINWINDLINE, ACCOUTWINDLINE};
    private static Presenter4BakeActivity mPresenter;
    // private IBakeView super.mViewOperator;
    // private IBakeModel mBakeModel;
    private List<Entry> mEventList;
    // 当前烘焙状态，一爆，二爆等状态
    private int mCurStatus;
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
        super.mModelOperator = Model4Bake.newInstance();
    }

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

    public IBaseView getCurOperatorView() {
        return super.mViewOperator;
    }

    public List<Entry> getEventList() {
        return mEventList;
    }

    public void setChartPresenter(Presenter4Chart presenter) {
        this.mChartPresenter = presenter;
    }
    public void setDevelopBarPresenter(Presenter4DevelopBar presenter){
        this.mDevelopBarPresenter = presenter;
    }
    public void init() {
        mCurStatus = RAW_BEAN;
        mEventList = new LinkedList<>();
        enabledFirstBurst = false;
        enabledSecondBurst = false;
        recorderSystem = new RecorderSystem();
        breakPointerRecorder = new BreakPointerRecorder();
        if (curBeanEntry != null) {
            super.mCurBakingProxy.setStartTemperature(curBeanEntry.getY() + "");
            super.mCurBakingProxy.setDate(Utils.data2Timestamp(new Date()));
            super.mCurBakingProxy.setAmbientTemperature(Temperature.getEnvTemp() + "");
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
        super.mViewOperator = baseView;
    }

    @Override
    public void notifyTemperature(Temperature temperature) {
        long start = System.currentTimeMillis();
        notifyTemperatureByManual(temperature);
        long end = System.currentTimeMillis();
        Log.w(TAG, "notifyTemperature: 经历了 " + (end - start) / 1000.0f + " 秒");
    }

    @Override
    public void toDisconnected() {
        // TODO 不断进行重连...
        super.mViewOperator.showToast(BluetoothProfile.STATE_DISCONNECTED, "蓝牙已断开，请勿远离...");
        String latestAddress = mBluetoothOperator.getLatestAddress();
        mBluetoothOperator.connect(latestAddress);
        /*while(!mBluetoothOperator.connect()){
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
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

        // ((IBakeView) (super.mViewOperator)).updateChart(curBeanEntry, BEANLINE);
        mChartPresenter.dynamicAddDataImm(curBeanEntry, BEANLINE, !MyApplication.test4IsMinimize);
        for (int i = 1; i < 6; ++i) {
            // ((IBakeView) (super.mViewOperator)).updateChart();
            mChartPresenter.dynamicAddDataImm(new Entry(lastTime, Utils.getCrspTempratureValue(tempratures[i] + "")), LINE_INDEX[i], !MyApplication.test4IsMinimize);
        }
        if (!MyApplication.test4IsMinimize) {
            // 更新发展率条的颜色
            ((IBakeView) (super.mViewOperator)).updateTimer(mCurStatus);
            updateDevelopStatus(mCurStatus);
            // 更新时间文本
            ((IBakeView) (super.mViewOperator)).updateTemperatureText(temperature);
        }
    }

    /**
     * 用于更新发展率状态
     *
     * @param developStatus
     */
    public void updateDevelopStatus(int developStatus) {
        this.mCurStatus = developStatus;
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
        ((IBakeModel) super.mModelOperator).recordOneEvent(status, info);
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
        ((IBakeModel) mModelOperator).updateEntry(entry, status, description);
        // 本地记录事件结点
        mEventList.add(entry);
        // 添加事件修改至temperature set
        recorderSystem.addEvent(lastTime + "", description + ":" + status);
        // 通知图表更新
        ((IBakeView) super.mViewOperator).notifyChartDataChanged();
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
     * @return
     */
    private BakeReportProxy handlerData2BakeReport() {
        String deviceName = mBluetoothOperator.getConnectedDevice().getName();
        // FIXME 建造者模式
        super.mCurBakingProxy.setTempratureSet(recorderSystem.getTemperatureSet());
        super.mCurBakingProxy.setDevice(deviceName);
        super.mCurBakingProxy.setDevelopmentTime(mDevelopBarPresenter.getDevelopTimeString());
        super.mCurBakingProxy.setDevelopmentRate(mDevelopBarPresenter.getDevelopRateString());
        super.mCurBakingProxy.setBreakPointerTime(breakPointerRecorder.getbreakPointerTime());
        super.mCurBakingProxy.setBreakPointerTemprature(breakPointerRecorder.getBreakPointerTemprature());

        Model4Bake.EventInfo dryEvent = ((IBakeModel) super.mModelOperator).getDryEventInfoByStatus(Event.DRY);
        Model4Bake.EventInfo firstBurstEvent = ((IBakeModel) super.mModelOperator).getDryEventInfoByStatus(Event.FIRST_BURST);
        Model4Bake.EventInfo endEvent = ((IBakeModel) super.mModelOperator).getDryEventInfoByStatus(Event.END);
        if (dryEvent != null) {
            // 开始烘焙与脱水的时间间隔和平均升温率
            super.mCurBakingProxy.setAvgDryTime(dryEvent.getConsumeTime());
            super.mCurBakingProxy.setAvgDryTemprature(dryEvent.getAvgTemperature());
            if (firstBurstEvent != null) {
                // 脱水与一爆的时间间隔和平均升温率
                super.mCurBakingProxy.setAvgFirstBurstTime(firstBurstEvent.getConsumeTime());
                super.mCurBakingProxy.setAvgFirstBurstTemprature(firstBurstEvent.getAvgTemperature());
                if (endEvent != null) {
                    // 一爆开始到结束花的时间和平均升温率
                    super.mCurBakingProxy.setAvgEndTime(endEvent.getConsumeTime());
                    super.mCurBakingProxy.setAvgEndTemprature(endEvent.getAvgTemperature());
                }
            }
        }

        super.mCurBakingProxy.setGlobalAccBeanTemp(recorderSystem.getGlobalAccTemprature());
        super.mCurBakingProxy.setEndTemp(curBeanEntry.getY());

        return super.mCurBakingProxy;
    }

    public void stopBake() {
        handlerData2BakeReport();

    }

    @Override
    public void toConnected() {
        mViewOperator.showToast(BluetoothProfile.STATE_CONNECTED, "蓝牙连接已恢复...");
    }
}
