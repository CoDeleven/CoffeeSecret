package com.dhy.coffeesecret.ui.bake;

import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.bake.IBakeView;
import com.dhy.coffeesecret.model.bake.Presenter4BakeActivity;
import com.dhy.coffeesecret.model.bake.developbar.Presenter4DevelopBar;
import com.dhy.coffeesecret.model.chart.Presenter4Chart;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.ui.bake.fragments.FireWindDialog;
import com.dhy.coffeesecret.ui.bake.fragments.Other;
import com.dhy.coffeesecret.ui.bake.record.RecorderSystem;
import com.dhy.coffeesecret.utils.ConvertUtils;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.ui.common.views.BaseChart4Coffee;
import com.dhy.coffeesecret.ui.common.views.DevelopBar;
import com.facebook.rebound.ui.Util;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.jesse.nativelogger.NLogger;

import static com.dhy.coffeesecret.MyApplication.temperatureUnit;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;
import static com.github.mikephil.charting.data.Event.DRY;
import static com.github.mikephil.charting.data.Event.END;
import static com.github.mikephil.charting.data.Event.FIRST_BURST;
import static com.github.mikephil.charting.data.Event.FIRST_BURST_END;
import static com.github.mikephil.charting.data.Event.SECOND_BURST;
import static com.github.mikephil.charting.data.Event.SECOND_BURST_END;

public class BakeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        Other.OnOtherAddListener, FireWindDialog.OnFireWindAddListener, IBakeView {
    public static final String ENABLE_REFERLINE = "com.dhy.coffeesercret.ui.bake.BakeActivity.REFER_LINE";
    public static final int UPDATE_TEMPERATURE_TEXT = 0x123, UPDATE_TIME_TEXT = 0x112;
    private static final String TAG = BakeActivity.class.getSimpleName();
    @Bind(R.id.id_baking_chart)
    BaseChart4Coffee chart;
    @Bind(R.id.id_baking_lineOperator)
    TextView lineOperator;
    @Bind(R.id.id_baking_dry)
    Button mDry;
    @Bind(R.id.id_baking_firstBurst)
    Button mFirstBurst;
    @Bind(R.id.id_baking_secondBurst)
    Button mSecondBurst;
    @Bind(R.id.id_baking_end)
    Button mEnd;
    @Bind(R.id.id_baking_wind_fire)
    Button mFireWind;
    @Bind(R.id.id_baking_other)
    Button mOther;
    @Bind(R.id.id_baking_untilTime)
    TextView untilTime;
    @Bind(R.id.id_baking_developRate)
    TextView developRate;
    @Bind(R.id.id_baking_developTime)
    TextView developTime;
    @Bind(R.id.id_baking_developbar)
    DevelopBar developBar;
    @Bind(R.id.id_baking_start)
    ImageButton mStart;
    @Bind(R.id.id_baking_beanTemp)
    TextView idBakingBeanTemp;
    @Bind(R.id.id_baking_inwindTemp)
    TextView idBakingInwindTemp;
    @Bind(R.id.id_baking_accInwindTemp_before)
    TextView idBakingAccInwindTempBefore;
    @Bind(R.id.id_baking_accInwindTemp_after)
    TextView idBakingAccInwindTempAfter;
    @Bind(R.id.id_baking_outwindTemp)
    TextView idBakingOutwindTemp;
    @Bind(R.id.id_baking_accBeanTemp_before)
    TextView idBakingAccBeanTempBefore;
    @Bind(R.id.id_baking_accBeanTemp_after)
    TextView idBakingAccBeanTempAfter;
    @Bind(R.id.id_baking_accOutwindTemp_before)
    TextView idBakingAccOutwindTempBefore;
    @Bind(R.id.id_baking_accOutwindTemp_after)
    TextView idBakingAccOutwindTempAfter;
    @Bind(R.id.id_temp_unit0)
    TextView idTempUnit0;
    @Bind(R.id.id_temp_unit1)
    TextView idTempUnit1;
    @Bind(R.id.id_temp_unit2)
    TextView idTempUnit2;
    @Bind(R.id.id_baking_exit)
    Button mExit;

    private PopupWindow popupWindow;
    private View popupOperator;
    private Presenter4BakeActivity mPresenter = Presenter4BakeActivity.newInstance();
    private Presenter4Chart mChartPresenter = Presenter4Chart.newInstance();
    private Presenter4DevelopBar mDevelopBarPresenter = Presenter4DevelopBar.newInstance();
    // private boolean enableDoubleConfirm;
    private boolean isDoubleClick;
    private View curStatusView;
    // private UniversalConfiguration mConfig;
    private FragmentTool fragmentTool;
    private BakeReportProxy referTempratures;
    // 执行UI操作
    private Handler mTextHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_TEMPERATURE_TEXT:
                    Temperature temperature = (Temperature) msg.obj;
                    updateTemperatureText(temperature);
                    break;
                case UPDATE_TIME_TEXT:
                    updateTimeText((int) msg.obj);
            }

            return false;
        }
    });
    private Handler mToast = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(BakeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT);
            return false;
        }
    });
    private Handler mDialogHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BluetoothProfile.STATE_DISCONNECTED:
                    showDisconnectedDialog();
                    break;
            }
        }
    };

    private void updateTemperatureText(Temperature temperature) {
        // 所有用户可见性数值均进行转换
        idBakingBeanTemp.setText(ConvertUtils.getCrspTemperatureValue(temperature.getBeanTemp() + "") + temperatureUnit);
        idBakingAccBeanTempBefore.setText(Utils.getCommaBefore(temperature.getAccBeanTemp()));
        idBakingAccBeanTempAfter.setText(Utils.getCommaAfter(temperature.getAccBeanTemp()));
        idTempUnit0.setText(temperatureUnit + "/min");

        idBakingInwindTemp.setText(ConvertUtils.getCrspTemperatureValue(temperature.getInwindTemp() + "") + temperatureUnit);
        idBakingAccInwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccInwindTemp()));
        idBakingAccInwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccInwindTemp()));
        idTempUnit1.setText(temperatureUnit + "/min");

        idBakingOutwindTemp.setText(ConvertUtils.getCrspTemperatureValue(temperature.getOutwindTemp() + "") + temperatureUnit);
        idBakingAccOutwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccOutwindTemp()));
        idBakingAccOutwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccOutwindTemp()));
        idTempUnit2.setText(temperatureUnit + "/min");

        // switchImage(temperature);
        chart.notifyDataSetChanged();
    }

    private void updateTimeText(int curStatus) {
        // 转换成秒
        int now = ((int) (System.currentTimeMillis() - RecorderSystem.getStartTime()) / 1000);
        untilTime.setText(FormatUtils.getTimeWithFormat(now));
        if (curStatus == DevelopBar.FIRST_BURST) {
            developTime.setText(mDevelopBarPresenter.getDevelopTimeString());
            developRate.setText("发展率：" + mDevelopBarPresenter.getDevelopRateString() + "%");
        }
    }

    @Override
    public void showWarnDialog(int index) {
        mDialogHandler.sendEmptyMessage(index);
    }

    @Override
    public void updateText(int index, Object updateContent) {
        Message msg = new Message();
        msg.obj = updateContent;
        msg.what = index;
        mTextHandler.sendMessage(msg);
    }

    @Override
    public void showToast(int index, String toastContent) {
        Message msg = new Message();
        msg.what = index;
        msg.obj = toastContent;
        mToast.sendMessage(msg);
    }


    /**
     * 选中显示，不选中隐藏
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        int curIndex = 0;
        switch (id) {
            case R.id.id_baking_line_bean:
                curIndex = BEANLINE;
                break;
            case R.id.id_baking_line_inwind:
                curIndex = INWINDLINE;
                break;
            case R.id.id_baking_line_outwind:
                curIndex = OUTWINDLINE;
                break;
            case R.id.id_baking_line_accBean:
                curIndex = ACCBEANLINE;
                break;
            case R.id.id_baking_line_accInwind:
                curIndex = ACCINWINDLINE;
                break;
            case R.id.id_baking_line_accOutwind:
                curIndex = ACCOUTWINDLINE;
                break;
        }
        mChartPresenter.toggleLineVisible(curIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: 被调用" + this.toString());
        // 设置该界面在前台是不允许黑屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置横屏和隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_bake);
        ButterKnife.bind(this);

        init();
        // 测试用
        if (mPresenter.isMinimized()) {
            // 因为可以退出的都是正在烘焙中的，故恢复也恢复到烘焙中的状态
            afterStartBtnClick();
            // 继续刚刚的烘焙
            continueBaking();
        } else {
            initChartPresenter();
            initBakePresenter();
            initDevelopBarPresenter();
        }

    }

    private void initDevelopBarPresenter() {
        mDevelopBarPresenter.init();
        mDevelopBarPresenter.setView(developBar);
    }

    private void initChartPresenter() {
        // 设置chart视图
        mChartPresenter.setView(chart);
        // 初始化曲线
        mChartPresenter.initLines();
        // 考虑是否添加参考曲线,用完即删，哈哈
        // BakeReport bakeReport = mPresenter.getNoBakingBakeReport(true);

        BakeReport bakeReport = (BakeReport)getIntent().getParcelableExtra(ENABLE_REFERLINE);

        if (bakeReport != null) {
            referTempratures = new BakeReportProxy(bakeReport);
            List<Entry> entries = new ArrayList<>();
            int count = 0;
            for (float temperature : referTempratures.getTempratureByIndex(BEANLINE)) {
                entries.add(new Entry(count, temperature));
                count += 1;
            }
            mChartPresenter.enableReferLine(entries);
        }
    }

    /**
     * 恢复烘焙视图
     */
    private void continueBaking() {
        // 设置图表视图
        mChartPresenter.setView(chart);
        // 添加曲线
        mChartPresenter.continueLastLines();
        mPresenter.setView(this);
        mPresenter.setMinimize(false);
        mDevelopBarPresenter.setView(developBar);
        // 获取事件列表，进行恢复
        List<Entry> events = mPresenter.getEventList();
        for (Entry entry : events) {
            Event event = entry.getEvent();
            switchBtnStatus(event.getCurStatus());
        }
    }


    private void afterStartBtnClick() {
        mDry.setVisibility(View.VISIBLE);
        mEnd.setVisibility(View.VISIBLE);
        mFireWind.setVisibility(View.VISIBLE);
        mFirstBurst.setVisibility(View.VISIBLE);
        mSecondBurst.setVisibility(View.VISIBLE);
        mOther.setVisibility(View.VISIBLE);
        mExit.setVisibility(View.VISIBLE);
        mStart.setVisibility(View.GONE);
    }

    private void initBakePresenter() {
        mPresenter.setView(this);
        mPresenter.initBluetoothListener();
        mPresenter.setChartPresenter(mChartPresenter);
        mPresenter.setDevelopBarPresenter(mDevelopBarPresenter);
        mPresenter.init();
    }


    /**
     * 初始化属性
     */
    private void init() {
        lineOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopupwindow().showAsDropDown(v);
            }
        });

        mDry.setOnClickListener(this);
        mFirstBurst.setOnClickListener(this);
        mSecondBurst.setOnClickListener(this);
        mEnd.setOnClickListener(this);

        mFireWind.setOnClickListener(this);
        mOther.setOnClickListener(this);

        fragmentTool = FragmentTool.getFragmentToolInstance(this);
    }


    /**
     * 获取popupwindow
     *
     * @return 返回自定义的popupwindow
     */
    private PopupWindow getPopupwindow() {
        if (popupWindow == null) {
            popupOperator = getLayoutInflater().inflate(R.layout.bake_lines_operator, null, false);

            popupWindow = new PopupWindow(popupOperator, Util.dpToPx(86, getResources()), Util.dpToPx(150, getResources()), true);
            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            popupOperator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupOperator != null && popupOperator.isShown()) {
                        popupWindow.dismiss();
                    }
                    return false;
                }
            });
            popupOperator.setBackgroundResource(R.drawable.bg_round_black_border);

            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_bean)).setOnCheckedChangeListener(this);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_inwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_outwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accBean)).setOnCheckedChangeListener(this);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accInwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accOutwind)).setOnCheckedChangeListener(this);

        }
        return popupWindow;

    }


    @Override
    public void onClick(View v) {
        // 如果启用了双点击生效
        if (mPresenter.isEnableDoubleClick() && R.id.id_baking_end == v.getId()) {
            doubleClickConfirm(v);
        } else {
            // 执行事件的分发
            addEvent(v);
        }
    }

    /**
     * 监听是否单击同个按钮两次
     *
     * @param v 被点击的视图
     */
    private void doubleClickConfirm(View v) {
        // 如果未进入双击状态
        if (!isDoubleClick) {
            // 设置进入双击状态
            isDoubleClick = true;
            // 设置当前点击的view,防止用户通过不同按钮完成双击
            curStatusView = v;
            // 提示用户
            Toast.makeText(this, "再次点击进行确定", Toast.LENGTH_SHORT).show();
            // 开启一个计时器
            Timer timer = new Timer();
            // 如果用户在2s内不进行双击，取消双击状态
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isDoubleClick = false;
                }
            }, 2000);
        } else {
            // 如果第一次点击的view不是现在的view,重新回调本方法
            if (curStatusView != v) {
                // 重新设置为未双击状态
                isDoubleClick = false;
                // 回调本方法，准备进入双击状态
                doubleClickConfirm(v);
                return;
            } else {
                isDoubleClick = false;
                addEvent(v);
            }
        }
    }

    /**
     * 开始烘焙按钮按下之后需要执行的操作
     */
    @OnClick(R.id.id_baking_start)
    public void onBakeStart() {
        // TODO 3-20日，在开始烘焙按钮按下后的操作在这里执行
        afterStartBtnClick();
        mPresenter.init();
        mChartPresenter.clear();
        // 设置开始烘焙
        mPresenter.startBaking();
        if (popupOperator != null) {
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_bean)).setChecked(true);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_inwind)).setChecked(true);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_outwind)).setChecked(true);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accBean)).setChecked(true);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accInwind)).setChecked(true);
            ((CheckBox) popupOperator.findViewById(R.id.id_baking_line_accOutwind)).setChecked(true);
        }
    }

    @OnClick({R.id.id_baking_dry, R.id.id_baking_firstBurst,
            R.id.id_baking_secondBurst, R.id.id_baking_end, R.id.id_baking_wind_fire,
            R.id.id_baking_other, R.id.id_baking_exit})
    public void addEvent(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_baking_dry:
                // 先当作脱水结束
                mPresenter.stopOneEvent(Event.DRY, "脱水");
                // 用于记录脱水结束到一爆开始的记录
                mPresenter.startNewRecordEvent();
                // 切换按钮状态为 不可用状态
                switchBtnStatus(Event.DRY);
                break;
            case R.id.id_baking_firstBurst:
                if (mPresenter.isEnabledFirstBurst()) {
                    mPresenter.stopOneEvent(FIRST_BURST_END, "一爆结束");
                    mPresenter.updateDevelopStatus(DevelopBar.FIRST_BURST);
                    // 切换按钮状态为不可用
                    switchBtnStatus(FIRST_BURST_END);
                } else {
                    mPresenter.stopOneEvent(FIRST_BURST, "一爆");
                    mPresenter.updateDevelopStatus(DevelopBar.FIRST_BURST);
                    // 切换按钮文本为 一爆结束
                    switchBtnStatus(Event.FIRST_BURST);
                    // 准备记录一爆开始到结束的记录
                    mPresenter.startNewRecordEvent();
                }
                break;
            case R.id.id_baking_secondBurst:
                if (mPresenter.isEnabledSecondBurst()) {
                    mPresenter.updateBeanEntryEvent(SECOND_BURST_END, "二爆结束");
                    // 切换状态为 不可用
                    switchBtnStatus(SECOND_BURST_END);
                } else {
                    mPresenter.updateBeanEntryEvent(SECOND_BURST, "二爆");
                    // 切换状态为 二爆结束 状态
                    switchBtnStatus(SECOND_BURST);
                }
                break;
            case R.id.id_baking_end:
                mPresenter.stopOneEvent(END, "结束");
                // 清除监听器防止后续修改数据
                mPresenter.resetBluetoothListener();
                FireWindDialog.setFireValue(0);
                FireWindDialog.setWindValue(0);
                // 停止烘焙,处理烘焙数据
                mPresenter.stopBake();

                Intent intent = new Intent(BakeActivity.this, EditBehindActivity.class);
                // 发送一个bundle来标识是否来自bakeactivity的请求
                intent.putExtra(EditBehindActivity.MODE_KEY, EditBehindActivity.MODE_GENERATE);
                startActivity(intent);
                finish();
                // 切换状态为 不可用
                switchBtnStatus(END);
                break;
            case R.id.id_baking_wind_fire:
                FireWindDialog fireWind = new FireWindDialog();
                fireWind.setOnFireWindAddListener(this);
                // 设置等待结点
                mPresenter.setAwaitEntry();
                fragmentTool.showDialogFragmen("fireWindFragment", fireWind);
                break;
            case R.id.id_baking_other:
                Other other = new Other();
                other.setOnOtherAddListener(this);
                // 设置等待结点
                mPresenter.setAwaitEntry();
                fragmentTool.showDialogFragmen("otherFragment", other);
                break;
            case R.id.id_baking_exit:
                onBackPressed();
                break;
        }

    }

    @OnLongClick(R.id.id_baking_exit)
    public boolean discardThisBake() {
        mPresenter.clearBakeReport();
        mPresenter.resetBluetoothListener();
        mChartPresenter.clearReferLine();

        finish();
        return true;
    }

    @Override
    public void onFireWindChanged(Event event) {
        if (event == null) {
            // 设置等待节点
            mPresenter.resetAwaitEntry();
            return;
        }
        // 更新等待结点的事件信息并更新至图标中
        mPresenter.dynamicUpdateEvent(event);
    }

    @Override
    public void onDataChanged(Event event) {
        onFireWindChanged(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 烘焙中的时候默认最小化
        if (mPresenter.isBakingNow()) {
            minimizeActivity();
            NLogger.i(TAG, "最小化界面");
            finish();
        } else {
            discardThisBake();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: BakeActivity被销毁:" + this.toString());
/*        if (!mPresenter.isMinimized()) {
            if (this == mPresenter.getCurOperatorView()) {
                mPresenter.resetBluetoothListener();
            }
        }*/
    }

    /**
     * 用于最小化烘焙界面
     * 便于烘焙过程中查看其他数据
     */
    private void minimizeActivity() {
        // 保存引用？让BluetoothService仍然能传递数据过来，只是不再刷新界面？

        mPresenter.setMinimize(true);
    }

    /**
     * 改变事件操作状态的方法
     *
     * @param btnStatus 按钮状态
     */
    private void switchBtnStatus(int btnStatus) {
        switch (btnStatus) {
            case DRY:
                mDry.setEnabled(false);
                break;
            case FIRST_BURST:
                mFirstBurst.setText("一爆结束");
                break;
            case FIRST_BURST_END:
                mFirstBurst.setEnabled(false);
                break;
            case SECOND_BURST:
                mSecondBurst.setText("二爆结束");
                break;
            case SECOND_BURST_END:
                mSecondBurst.setEnabled(false);
                break;
            case END:
                mEnd.setEnabled(false);
                break;
        }
    }

    private void showDisconnectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QAQ 断开连接了...")
                .setNegativeButton("重连", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.reconnect();
                    }
                })
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BakeActivity.this.discardThisBake();
                    }
                })
                .setMessage("重连成功后会丢失部分数据.../(ㄒoㄒ)/~~");
        builder.create().show();
    }
}