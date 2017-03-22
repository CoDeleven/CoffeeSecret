package com.dhy.coffeesecret.ui.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.pojo.TempratureSet;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.fragments.FireWindDialog;
import com.dhy.coffeesecret.ui.device.fragments.Other;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dhy.coffeesecret.MyApplication.tempratureUnit;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCBEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCINWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.BEANLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.INWINDLINE;
import static com.dhy.coffeesecret.views.BaseChart4Coffee.OUTWINDLINE;
import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.RAWBEAN;

public class BakeActivity extends AppCompatActivity implements BluetoothService.DataChangedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, Other.OnOtherAddListener, FireWindDialog.OnFireWindAddListener {
    public static final String DEVICE_NAME = "com.dhy.coffeesercret.ui.device.BakeActivity.DEVICE_NAME";
    public static final String ENABLE_REFERLINE = "com.dhy.coffeesercret.ui.device.BakeActivity.REFER_LINE";
    public static final int I_AM_BAKEACTIVITY = 123;
    private static final int[] LINE_INDEX = {BEANLINE, INWINDLINE, OUTWINDLINE, ACCBEANLINE, ACCINWINDLINE, ACCOUTWINDLINE};
    private static Thread timer = null;
    @Bind(R.id.id_baking_chart)
    BaseChart4Coffee chart;
    @Bind(R.id.id_baking_lineOperator)
    TextView lineOperator;
    @Bind(R.id.ic_baking_accBeanImg)
    ImageView accBeanImg;
    @Bind(R.id.ic_baking_accInwindImg)
    ImageView accInwindImg;
    @Bind(R.id.ic_baking_accOutwindImg)
    ImageView accOutwindImg;
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
    Button mStart;
    private PopupWindow popupWindow;
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private float lastTime = 0;
    private TextView[] beanTemps = new TextView[2];
    private TextView[] inwindTemps = new TextView[2];
    private TextView[] outwindTemps = new TextView[2];
    private List<Entry> eventRecords = new ArrayList<>();
    private float endTemp;
    private float startTemp;
    private Event curEvent;
    private boolean enableDoubleConfirm;
    private boolean isDoubleClick;
    private View curStatusView;
    private UniversalConfiguration mConfig;
    private long startTime;
    private boolean isOverBottom = false;
    private int curStatus = RAWBEAN;
    private boolean isReading = false;
    private FragmentTool fragmentTool;
    private ProgressDialog dialog;
    private boolean isEnd = false;
    private Entry curBeanEntry;
    private boolean isFisrtBurstEnd = false;
    private boolean isSecondBurstEnd = false;
    private ArrayList<Float> tempratures;
    private TempratureSet set = new TempratureSet();
    // 执行UI操作
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            float accBean = bundle.getFloat("accBean");
            float accInwind = bundle.getFloat("accInwind");
            float accOutwind = bundle.getFloat("accOutwind");
            // 所有用户可见性数值均进行转换
            beanTemps[0].setText(Utils.getCrspTempratureValue(bundle.getFloat("bean") + "") + tempratureUnit);
            beanTemps[1].setText(Utils.getCrspTempratureValue(accBean + "") + tempratureUnit + "/m");

            inwindTemps[0].setText(Utils.getCrspTempratureValue(bundle.getFloat("inwind") + "") + tempratureUnit);
            inwindTemps[1].setText(Utils.getCrspTempratureValue(accInwind + "") + tempratureUnit + "/m");

            outwindTemps[0].setText(Utils.getCrspTempratureValue(bundle.getFloat("outwind") + "") + tempratureUnit);
            outwindTemps[1].setText(Utils.getCrspTempratureValue(accOutwind + "") + tempratureUnit + "/m");

            if (curStatus == DevelopBar.FIRST_BURST) {
                developTime.setText(developBar.getDevelopTime());
                developRate.setText("发展率：" + developBar.getDevelopRate() + "%");
            }
            Temprature temprature = new Temprature();
            temprature.setAccBeanTemp(accBean);
            temprature.setAccInwindTemp(accInwind);
            temprature.setAccOutwindTemp(accOutwind);
            switchImage(temprature);
            return false;
        }
    });
    private Handler mTimer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 转换成秒
            int now = ((int) (System.currentTimeMillis() - startTime) / 1000);
            int minutes = now / 60;
            int seconds = now % 60;
            untilTime.setText(Utils.getTimeWithFormat(now));
            if (!isOverBottom && minutes > 1 && seconds > 30) {
                isOverBottom = true;
            }
            developBar.setCurStatus(curStatus);
            return false;
        }
    });

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
    public void notifyDataChanged(Temprature temprature) {
        // 温度数组: 0->豆温，1->进风温, 2->出风温, 3->加速豆温, 4->加速进风温, 5->加速出风温
        float[] tempratures = {temprature.getBeanTemp(), temprature.getInwindTemp(),
                temprature.getOutwindTemp(), temprature.getAccBeanTemp(), temprature.getAccInwindTemp(),
                temprature.getAccOutwindTemp()};

        startTemp = tempratures[0];

        lastTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        lastTime = ((int) (lastTime * 100)) / 100.0f;

        curBeanEntry = new Entry(lastTime, Utils.getCrspTempratureValue(tempratures[0] + ""));

        if (tempratures[0] > 160 && isOverBottom && curStatus != DevelopBar.FIRST_BURST) {
            curStatus = AFTER160;
        }

        set.addBeanTemp(tempratures[0]);
        set.addInwindTemp(tempratures[1]);
        set.addOutwindTemp(tempratures[2]);
        set.addAccBeanTemp(tempratures[3]);
        set.addAccInwindTemp(tempratures[4]);
        set.addAccOutwindTemp(tempratures[5]);
        set.addTimex(lastTime);

        chart.addOneDataToLine(curBeanEntry, BEANLINE);
        for (int i = 1; i < 6; ++i) {
            chart.addOneDataToLine(new Entry(lastTime, Utils.getCrspTempratureValue(tempratures[i] + "")), LINE_INDEX[i]);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloat("bean", tempratures[0]);
        bundle.putFloat("inwind", tempratures[1]);
        bundle.putFloat("outwind", tempratures[2]);
        bundle.putFloat("accBean", tempratures[3]);
        bundle.putFloat("accInwind", tempratures[4]);
        bundle.putFloat("accOutwind", tempratures[5]);
        msg.setData(bundle);

        mHandler.sendMessage(msg);

        chart.notifyDataSetChanged();
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

        tempratures = (ArrayList<Float>) getIntent().getSerializableExtra(ENABLE_REFERLINE);
        if (tempratures != null) {
            List<Entry> entries = new ArrayList<>();
            int count = 0;
            for (float temprature : tempratures) {
                entries.add(new Entry(count, temprature));
                count += 1;
            }
            chart.enableReferLine(entries);
        }
        init();
    }


    private void showButton() {
        mDry.setVisibility(View.VISIBLE);
        mEnd.setVisibility(View.VISIBLE);
        mFireWind.setVisibility(View.VISIBLE);
        mFirstBurst.setVisibility(View.VISIBLE);
        mSecondBurst.setVisibility(View.VISIBLE);
        mOther.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothOperator == null) {
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
        }
        mBluetoothOperator.setDataChangedListener(this);
        startTime = System.currentTimeMillis();
        if (timer == null) {
            isReading = true;
            timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isReading) {
                        mTimer.sendEmptyMessage(0);
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e("BakeActivity", "已经中断");
                            break;
                        }
                    }
                }
            });
            timer.start();
        }
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

        beanTemps[0] = (TextView) findViewById(R.id.id_baking_beanTemp);
        beanTemps[1] = (TextView) findViewById(R.id.id_baking_accBeantemp);

        inwindTemps[0] = (TextView) findViewById(R.id.id_baking_inwindTemp);
        inwindTemps[1] = (TextView) findViewById(R.id.id_baking_accInwindTemp);

        outwindTemps[0] = (TextView) findViewById(R.id.id_baking_outwindTemp);
        outwindTemps[1] = (TextView) findViewById(R.id.id_baking_accOutwindTemp);


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
            final View view = getLayoutInflater().inflate(R.layout.bake_lines_operator, null, false);
            popupWindow = new PopupWindow(view, UnitConvert.dp2px(getResources(), 86), UnitConvert.dp2px(getResources(), 150), true);
            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (view != null && view.isShown()) {
                        popupWindow.dismiss();
                    }
                    return false;
                }
            });
            view.setBackgroundResource(R.drawable.bg_round_black_border);

            ((CheckBox) view.findViewById(R.id.id_baking_line_bean)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_inwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_outwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accBean)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accInwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accOutwind)).setOnCheckedChangeListener(this);

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
        showButton();
        startTime = System.currentTimeMillis();
        chart.clear();
        mStart.setVisibility(View.GONE);
        BakeReportProxy bakeReport = ((MyApplication) getApplication()).getBakeReport();
        bakeReport.setStartTemperature(startTemp + "");
        bakeReport.setDate(Utils.data2Timestamp(new Date()));
        bakeReport.setAmbientTemperature(Temprature.getEnvTemp() + "");
    }

    private void addEvent(View v) {
        int id = v.getId();
        boolean status = false;
        switch (id) {
            case R.id.id_baking_dry:
                updateCurBeanEntryEvent(new Event(Event.DRY, "脱水"));
                status = true;
                break;
            case R.id.id_baking_firstBurst:
                if (isFisrtBurstEnd) {
                    updateCurBeanEntryEvent(new Event(Event.FIRST_BURST, "一爆结束"));
                    curStatus = DevelopBar.FIRST_BURST;
                    v.setEnabled(false);
                } else {
                    updateCurBeanEntryEvent(new Event(Event.FIRST_BURST, "一爆"));
                    curStatus = DevelopBar.FIRST_BURST;
                    isFisrtBurstEnd = true;
                    ((TextView) v).setText("一爆结束");
                    return;

                }

                status = true;

                break;
            case R.id.id_baking_secondBurst:
                if (isSecondBurstEnd) {
                    updateCurBeanEntryEvent(new Event(Event.SECOND_BURST, "二爆结束"));
                    v.setEnabled(false);

                } else {
                    updateCurBeanEntryEvent(new Event(Event.SECOND_BURST, "二爆"));
                    isSecondBurstEnd = true;
                    ((TextView) v).setText("二爆结束");
                    return;
                }
                status = true;

                break;
            case R.id.id_baking_end:

                updateCurBeanEntryEvent(new Event(Event.END, "结束"));
                BakeReportProxy imm = generateReport();
                imm.setEndTemp(Utils.get2PrecisionFloat(curBeanEntry.getY()));
                Intent intent = new Intent(BakeActivity.this, EditBehindActiviy.class);
                // 停止读取
                BluetoothService.READABLE = false;
                // 发送一个bundle来标识是否来自bakeactivity的请求
                intent.putExtra("status", I_AM_BAKEACTIVITY);
                startActivity(intent);

                finish();
                status = true;

                break;
            case R.id.id_baking_wind_fire:
                FireWindDialog fireWind = new FireWindDialog();
                fireWind.setOnFireWindAddListener(this);
                fragmentTool.showDialogFragmen("fireWindFragment", fireWind);
                break;
            case R.id.id_baking_other:
                Other other = new Other();
                other.setOnOtherAddListener(this);
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
    public void onFireWindChanged(Event event) {
        updateCurBeanEntryEvent(event);
    }

    @Override
    public void onDataChanged(Event event) {
        updateCurBeanEntryEvent(event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putLong("startTime", startTime);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReading = false;
        timer.interrupt();
        timer = null;
    }

    private BakeReportProxy generateReport() {
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(DEVICE_NAME);

        BakeReportProxy bakeReport = ((MyApplication) getApplication()).getBakeReport();

        bakeReport.setTempratureSet(set);

        bakeReport.setDevice(deviceName);

        bakeReport.setDevelopmentTime(developBar.getDevelopTime());

        bakeReport.setDevelopmentRate(developBar.getDevelopRate());

        return bakeReport;
    }

    private void updateCurBeanEntryEvent(final Event event) {
        curBeanEntry.setEvent(event);
        eventRecords.add(curBeanEntry);
        // 存储事件详情时，最后一个冒号后面是该事件的类别
        set.addEvent(lastTime + "", event.getDescription() + ":" + event.getCurStatus());
        chart.invalidate();
    }

    private void switchImage(Temprature temprature) {
        float t1 = temprature.getAccBeanTemp();
        float t2 = temprature.getAccInwindTemp();
        float t3 = temprature.getAccOutwindTemp();
        if (t1 > 0) {
            accBeanImg.setImageResource(R.drawable.ic_bake_acc_up_small);
        } else if (t1 < 0) {
            accBeanImg.setImageResource(R.drawable.ic_bake_acc_down_small);
        } else {
            accBeanImg.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
        if (t2 > 0) {
            accInwindImg.setImageResource(R.drawable.ic_bake_acc_up_small);
        } else if (t2 < 0) {
            accInwindImg.setImageResource(R.drawable.ic_bake_acc_down_small);
        } else {
            accInwindImg.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
        if (t3 > 0) {
            accOutwindImg.setImageResource(R.drawable.ic_bake_acc_up_small);
        } else if (t3 < 0) {
            accOutwindImg.setImageResource(R.drawable.ic_bake_acc_down_small);
        } else {
            accOutwindImg.setImageResource(R.drawable.ic_bake_acc_invariant_small);
        }
    }
}
