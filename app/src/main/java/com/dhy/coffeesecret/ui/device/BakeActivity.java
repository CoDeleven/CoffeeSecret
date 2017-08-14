package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import com.dhy.coffeesecret.model.chart.Presenter4Chart;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.device.fragments.FireWindDialog;
import com.dhy.coffeesecret.ui.device.fragments.Other;
import com.dhy.coffeesecret.ui.device.record.RecorderSystem;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dhy.coffeesecret.MyApplication.tempratureUnit;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;

public class BakeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Other.OnOtherAddListener, FireWindDialog.OnFireWindAddListener, IBakeView {
    public static final String DEVICE_NAME = "com.dhy.coffeesercret.ui.device.BakeActivity.DEVICE_NAME";
    public static final String ENABLE_REFERLINE = "com.dhy.coffeesercret.ui.device.BakeActivity.REFER_LINE";
    public static final int I_AM_BAKEACTIVITY = 123;
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


    private PopupWindow popupWindow;
    private View popuoOperator;
    private Presenter4BakeActivity mPresenter = Presenter4BakeActivity.newInstance();
    private Presenter4Chart mChartPresenter = Presenter4Chart.newInstance();
    /*    private TextView[] beanTemps = new TextView[2];
        private TextView[] inwindTemps = new TextView[2];
        private TextView[] outwindTemps = new TextView[2];*/
    private boolean enableDoubleConfirm;
    private boolean isDoubleClick;
    private View curStatusView;
    private UniversalConfiguration mConfig;
    private FragmentTool fragmentTool;
    private boolean isFisrtBurstEnd = false;
    private boolean isSecondBurstEnd = false;
    // private Entry fireWindBeanEntry = null;
    private BakeReportProxy referTempratures;
    // 执行UI操作
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Temperature temperature = (Temperature) msg.obj;
            // 所有用户可见性数值均进行转换
            idBakingBeanTemp.setText(Utils.getCrspTempratureValue(temperature.getBeanTemp() + "") + tempratureUnit);
            idBakingAccBeanTempBefore.setText(Utils.getCommaBefore(temperature.getAccBeanTemp()));
            idBakingAccBeanTempAfter.setText(Utils.getCommaAfter(temperature.getAccBeanTemp()));
            idTempUnit0.setText(tempratureUnit + "/m");

            idBakingInwindTemp.setText(Utils.getCrspTempratureValue(temperature.getInwindTemp() + "") + tempratureUnit);
            idBakingAccInwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccInwindTemp()));
            idBakingAccInwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccInwindTemp()));
            idTempUnit1.setText(tempratureUnit + "/m");

            idBakingOutwindTemp.setText(Utils.getCrspTempratureValue(temperature.getOutwindTemp() + "") + tempratureUnit);
            idBakingAccOutwindTempBefore.setText(Utils.getCommaBefore(temperature.getAccOutwindTemp()));
            idBakingAccOutwindTempAfter.setText(Utils.getCommaAfter(temperature.getAccOutwindTemp()));
            idTempUnit2.setText(tempratureUnit + "/m");

            // switchImage(temperature);
            chart.notifyDataSetChanged();

            return false;
        }
    });
    private Handler mTimer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int curStatus = (int) msg.obj;
            // 转换成秒
            int now = ((int) (System.currentTimeMillis() - RecorderSystem.getStartTime()) / 1000);
            int minutes = now / 60;
            int seconds = now % 60;
            untilTime.setText(Utils.getTimeWithFormat(now));
            /*if (!isOverBottom && minutes > 1 && seconds > 30) {
                isOverBottom = true;
            }*/
            if (curStatus == DevelopBar.FIRST_BURST) {
                developTime.setText(developBar.getDevelopTime());
                developRate.setText("发展率：" + developBar.getDevelopRate() + "%");
            }
            developBar.setCurStatus(curStatus);
            return false;
        }
    });

    @Override
    public void updateText(int index, String updateContent) {

    }

    @Override
    public void showToast(int index, String toastContent) {

    }

    @Override
    public void updateChart(Entry entry, int lineIndex) {
        chart.addOneDataToLine(entry, lineIndex);
        mChartPresenter.dynamicAddDataImm(entry, lineIndex, true);
    }

    @Override
    public void updateTemperatureText(Temperature temperature) {
        Message msg = new Message();
        msg.obj = temperature;
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateTimer(int developStatus) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = developStatus;
        mTimer.sendMessage(msg);
    }

    @Override
    public void notifyChartDataChanged() {
        chart.invalidate();
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
        if (isChecked) {
            chart.showLine(curIndex);
        } else {
            chart.hideLine(curIndex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置该界面在前台是不允许黑屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置横屏和隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_bake);
        ButterKnife.bind(this);

        chart.initLine();

        mConfig = SettingTool.getConfig(this);
        enableDoubleConfirm = mConfig.isDoubleClick();
        BakeReport bakeReport = (BakeReport) getIntent().getSerializableExtra(ENABLE_REFERLINE);
        if (bakeReport != null) {
            referTempratures = new BakeReportProxy(bakeReport);
            List<Entry> entries = new ArrayList<>();
            int count = 0;
            for (float temprature : referTempratures.getTempratureByIndex(BEANLINE)) {
                entries.add(new Entry(count, temprature));
                count += 1;
            }
            chart.enableReferLine(entries);
        }
        init();

    }


    private void afterStartBtnClick() {
        mDry.setVisibility(View.VISIBLE);
        mEnd.setVisibility(View.VISIBLE);
        mFireWind.setVisibility(View.VISIBLE);
        mFirstBurst.setVisibility(View.VISIBLE);
        mSecondBurst.setVisibility(View.VISIBLE);
        mOther.setVisibility(View.VISIBLE);
        mStart.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPresenter.initBluetoothListener();
        mPresenter.setView(this);
        mChartPresenter.setView(chart);
        // startTime = System.currentTimeMillis();

        mPresenter.init();

        // 设置tempraturset
        chart.setTemperatureSet(mPresenter.getTemperatureSet());

 /*       if (timer == null) {
            isReading = true;
            timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isReading) {
                        mTimer.sendEmptyMessage(0);
                        try {
                            Thread.currentThread().sleep(1250);
                        } catch (InterruptedException e) {
                            Log.e("BakeActivity", "已经中断");
                            break;
                        }
                    }
                }
            });
            timer.start();
        }*/
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
            popuoOperator = getLayoutInflater().inflate(R.layout.bake_lines_operator, null, false);
            popupWindow = new PopupWindow(popuoOperator, UnitConvert.dp2px(getResources(), 86), UnitConvert.dp2px(getResources(), 150), true);
            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            popuoOperator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popuoOperator != null && popuoOperator.isShown()) {
                        popupWindow.dismiss();
                    }
                    return false;
                }
            });
            popuoOperator.setBackgroundResource(R.drawable.bg_round_black_border);

            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_bean)).setOnCheckedChangeListener(this);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_inwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_outwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accBean)).setOnCheckedChangeListener(this);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accInwind)).setOnCheckedChangeListener(this);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accOutwind)).setOnCheckedChangeListener(this);

        }
        return popupWindow;

    }


    @Override
    public void onClick(View v) {
        // 如果启用了双点击生效
        if (enableDoubleConfirm && R.id.id_baking_end == v.getId()) {
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
        chart.clear();
        if (popuoOperator != null) {
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_bean)).setChecked(true);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_inwind)).setChecked(true);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_outwind)).setChecked(true);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accBean)).setChecked(true);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accInwind)).setChecked(true);
            ((CheckBox) popuoOperator.findViewById(R.id.id_baking_line_accOutwind)).setChecked(true);
        }
    }

    private void addEvent(View v) {
        int id = v.getId();
        boolean status = false;
        switch (id) {
            case R.id.id_baking_dry:
                // 先当作脱水结束
                mPresenter.stopOneEvent(Event.DRY, "脱水");
                // 用于记录脱水结束到一爆开始的记录
                mPresenter.startNewRecordEvent();
                status = true;
                break;
            case R.id.id_baking_firstBurst:
                if (isFisrtBurstEnd) {
                    mPresenter.stopOneEvent(Event.FIRST_BURST, "一爆结束");
                    mPresenter.updateDevelopStatus(DevelopBar.FIRST_BURST);
                    v.setEnabled(false);
                } else {
                    mPresenter.stopOneEvent(Event.FIRST_BURST, "一爆");
                    mPresenter.updateDevelopStatus(DevelopBar.FIRST_BURST);
                    isFisrtBurstEnd = true;
                    ((TextView) v).setText("一爆结束");
                    // 准备记录一爆开始到结束的记录
                    mPresenter.startNewRecordEvent();
                    return;
                }
                status = true;
                break;
            case R.id.id_baking_secondBurst:
                if (isSecondBurstEnd) {
                    mPresenter.updateBeanEntryEvent(Event.SECOND_BURST, "二爆结束");
                    v.setEnabled(false);
                } else {
                    mPresenter.updateBeanEntryEvent(Event.SECOND_BURST, "二爆");
                    isSecondBurstEnd = true;
                    ((TextView) v).setText("二爆结束");
                    return;
                }
                status = true;
                break;
            case R.id.id_baking_end:
                mPresenter.stopOneEvent(Event.END, "结束");
                // 清除监听器防止后续修改数据
                mPresenter.destroyBluetoothListener();
                // 停止烘焙,处理烘焙数据
                mPresenter.stopBake();

                Intent intent = new Intent(BakeActivity.this, EditBehindActivity.class);
                // 发送一个bundle来标识是否来自bakeactivity的请求
                intent.putExtra("status", I_AM_BAKEACTIVITY);
                startActivity(intent);
                finish();
                status = true;
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
        }
        if (id != R.id.id_baking_wind_fire & id != R.id.id_baking_other & id != R.id.id_baking_firstBurst & id != R.id.id_baking_secondBurst) {
            if (status) {
                v.setEnabled(false);
            }
        }
    }

    @Override
    public String getDevelopTime() {
        return developBar.getDevelopTime();
    }

    @Override
    public String getDevelopRate() {
        return developBar.getDevelopRate();
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
        // 在烘焙过程中退出 即 视为放弃这次烘焙
        mPresenter.clearBakeReportProxy();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroyBluetoothListener();
    }

}
