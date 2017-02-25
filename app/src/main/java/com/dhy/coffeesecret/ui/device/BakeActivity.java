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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.device.fragments.FireWindDialog;
import com.dhy.coffeesecret.ui.device.fragments.Other;
import com.dhy.coffeesecret.utils.BluetoothHelper;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.FIRST_BURST;
import static com.dhy.coffeesecret.views.DevelopBar.RAWBEAN;

public class BakeActivity extends AppCompatActivity implements BluetoothHelper.DataChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, Other.OnOtherAddListener, FireWindDialog.OnFireWindAddListener {
    public static final String RAW_BEAN_INFO = "com.dhy.coffeesercret.ui.device.BakeActivity.RAW_BEAN_INFO";
    public static final String DEVICE_NAME = "com.dhy.coffeesercret.ui.device.BakeActivity.DEVICE_NAME";
    public static final String START_TEMP = "com.dhy.coffeesercret.ui.device.BakeActivity.START_TIME";
    public static final String EVN_TEMP = "com.dhy.coffeesercret.ui.device.BakeActivity.EVN_TEMP";
    public static final String BAKE_DATE = "com.dhy.coffeesercret.ui.device.BakeActivity.BAKE_DATE";
    public static final String ENV_TEMP = "com.dhy.coffeesercret.ui.device.BakeActivity.ENV_TEMP";
    private BaseChart4Coffee chart;
    private TextView lineOperator;
    private PopupWindow popupWindow;
    private BluetoothHelper mHelper;
    private int count = 0;
    private TextView[] beanTemps = new TextView[2];
    private TextView[] inwindTemps = new TextView[2];
    private TextView[] outwindTemps = new TextView[2];
    private List<Entry> eventRecords = new ArrayList<>();
    private Button mDry;
    private Button mFirstBurst;
    private Button mSecondBurst;
    private Button mEnd;
    private Button mFireWind;
    private Button mOther;
    private float endTemp;
    private Event curEvent;
    private boolean enableDoubleConfirm;
    private boolean isDoubleClick;
    private View curStatusView;
    private UniversalConfiguration mConfig;
    private TextView untilTime;
    private TextView developRate;
    private TextView developTime;
    private DevelopBar developBar;
    private Long startTime;
    private boolean isOverBottom = false;
    private int curStatus = RAWBEAN;
    private boolean isReading = false;
    private Thread timer = null;
    private FragmentTool fragmentTool;
    private ProgressDialog dialog;
    private boolean isEnd = false;
    private Handler mShowHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                dialog = ProgressDialog.show(BakeActivity.this, "标题", "加载中，请稍后……");
            } else if (msg.what == 1) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            return false;
        }
    });
    // 执行UI操作
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            beanTemps[0].setText(String.format("%1$.2f", bundle.getFloat("bean")) + "℃");
            beanTemps[1].setText(String.format("%1$.2f", bundle.getFloat("accBean")) + "℃/s");

            inwindTemps[0].setText(String.format("%1$.2f", bundle.getFloat("inwind")) + "℃");
            inwindTemps[1].setText(String.format("%1$.2f", bundle.getFloat("accInwind")) + "℃/s");

            outwindTemps[0].setText(String.format("%1$.2f", bundle.getFloat("outwind")) + "℃");
            outwindTemps[1].setText(String.format("%1$.2f", bundle.getFloat("accOutwind")) + "℃/s");

            if (curStatus == FIRST_BURST) {
                developTime.setText(developBar.getDevelopTime());
                developRate.setText(developBar.getDevelopRate());
            }

            return false;
        }
    });
    private Handler mTimer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            // 转换成秒
            int now = ((int) (System.currentTimeMillis() - startTime) / 1000);
            Log.e("codelevex", "now:" + now);
            int minutes = now / 60;
            int seconds = now % 60;
            untilTime.setText(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
            if (!isOverBottom && seconds > 30) {
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
                curIndex = BaseChart4Coffee.BEANLINE;
                break;
            case R.id.id_baking_line_inwind:
                curIndex = BaseChart4Coffee.INWINDLINE;
                break;
            case R.id.id_baking_line_outwind:
                curIndex = BaseChart4Coffee.OUTWINDLINE;
                break;
            case R.id.id_baking_line_accBean:
                curIndex = BaseChart4Coffee.ACCBEANLINE;
                break;
            case R.id.id_baking_line_accInwind:
                curIndex = BaseChart4Coffee.ACCINWINDLINE;
                break;
            case R.id.id_baking_line_accOutwind:
                curIndex = BaseChart4Coffee.ACCOUTWINDLINE;
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
        float beanTemp = temprature.getBeanTemp();
        float inwindTemp = temprature.getInwindTemp();
        float outwindTemp = temprature.getOutwindTemp();
        float accBeanTemp = temprature.getAccBeanTemp();
        float accInwindTemp = temprature.getAccInwindTemp();
        float accOutwindTemp = temprature.getAccOutwindTemp();

        Entry beanEntry = new Entry(count, beanTemp);

        if (beanTemp > 160 && isOverBottom && curStatus != FIRST_BURST) {
            curStatus = AFTER160;
        }

        if (curEvent != null) {
            Log.e("codelevex", "啊，我有事件啊:" + curEvent.getDescription());
            beanEntry.setEvent(curEvent);
            eventRecords.add(beanEntry);
            if (!isEnd && dialog != null) {
                isEnd = true;
                endTemp = beanTemp;
            }
            curEvent = null;
        }

        chart.addOneDataToLine(beanEntry, BaseChart4Coffee.BEANLINE);
        chart.addOneDataToLine(new Entry(count, inwindTemp), BaseChart4Coffee.INWINDLINE);
        chart.addOneDataToLine(new Entry(count, outwindTemp), BaseChart4Coffee.OUTWINDLINE);
        chart.addOneDataToLine(new Entry(count, accBeanTemp), BaseChart4Coffee.ACCBEANLINE);
        chart.addOneDataToLine(new Entry(count, accInwindTemp), BaseChart4Coffee.ACCINWINDLINE);
        chart.addOneDataToLine(new Entry(count, accOutwindTemp), BaseChart4Coffee.ACCOUTWINDLINE);


        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloat("bean", beanTemp);
        bundle.putFloat("inwind", inwindTemp);
        bundle.putFloat("outwind", outwindTemp);
        bundle.putFloat("accBean", accBeanTemp);
        bundle.putFloat("accInwind", accInwindTemp);
        bundle.putFloat("accOutwind", accOutwindTemp);
        msg.setData(bundle);

        mHandler.sendMessage(msg);

        count += 5;
        // ++count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置横屏和隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_bake);

        chart = (BaseChart4Coffee) findViewById(R.id.id_baking_chart);
        lineOperator = (TextView) findViewById(R.id.id_baking_lineOperator);
        mConfig = SettingTool.getConfig(this);
        enableDoubleConfirm = mConfig.isDoubleClick();

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (mHelper == null) {
            mHelper = BluetoothHelper.getNewInstance();
        }
        mHelper.setDataListener(this);
        Log.e("codelevex", "我特么又被重启了？？");
/*        if (!mHelper.isTestThreadAlive()) {
            mHelper.test(getResources().openRawResource(R.raw.test));
        }*/
        startTime = System.currentTimeMillis();
        isReading = true;
        mHelper.setActivityDestroy(false);
        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReading) {
                    mTimer.sendMessage(new Message());
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();

    }

    /**
     * 初始化属性
     */
    private void init() {
        lineOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("codelevex", "被点击了");
                getPopupwindow().showAsDropDown(v);
            }
        });

        beanTemps[0] = (TextView) findViewById(R.id.id_baking_beanTemp);
        beanTemps[1] = (TextView) findViewById(R.id.id_baking_accBeantemp);

        inwindTemps[0] = (TextView) findViewById(R.id.id_baking_inwindTemp);
        inwindTemps[1] = (TextView) findViewById(R.id.id_baking_accInwindTemp);

        outwindTemps[0] = (TextView) findViewById(R.id.id_baking_outwindTemp);
        outwindTemps[1] = (TextView) findViewById(R.id.id_baking_accOutwindTemp);


        mDry = (Button) findViewById(R.id.id_baking_dry);
        mDry.setOnClickListener(this);
        mFirstBurst = (Button) findViewById(R.id.id_baking_firstBurst);
        mFirstBurst.setOnClickListener(this);
        mSecondBurst = (Button) findViewById(R.id.id_baking_secondBurst);
        mSecondBurst.setOnClickListener(this);
        mEnd = (Button) findViewById(R.id.id_baking_end);
        mEnd.setOnClickListener(this);

        mFireWind = (Button) findViewById(R.id.id_baking_wind_fire);
        mFireWind.setOnClickListener(this);
        mOther = (Button) findViewById(R.id.id_baking_other);
        mOther.setOnClickListener(this);

        untilTime = (TextView) findViewById(R.id.id_baking_untilTime);
        developRate = (TextView) findViewById(R.id.id_baking_developRate);
        developTime = (TextView) findViewById(R.id.id_baking_developTime);
        developBar = (DevelopBar) findViewById(R.id.id_baking_developbar);


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
            popupWindow = new PopupWindow(view, UnitConvert.dp2px(getResources(), 86), UnitConvert.dp2px(getResources(), 135), true);
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
                    isDoubleClick = true;
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

    private void addEvent(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_baking_dry:
                curEvent = new Event(Event.DRY);
                curEvent.setDescription("脱水");
                break;
            case R.id.id_baking_firstBurst:
                curEvent = new Event(Event.FIRST_BURST);
                curEvent.setDescription("一爆");
                curStatus = FIRST_BURST;
                break;
            case R.id.id_baking_secondBurst:
                curEvent = new Event(Event.SECOND_BURST);
                curEvent.setDescription("二爆");
                break;
            case R.id.id_baking_end:
                curEvent = new Event(Event.END);
                curEvent.setDescription("结束烘焙");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mShowHandler.sendEmptyMessage(0);
                        while (true) {
                            if (isEnd) {
                                Intent intent = new Intent(BakeActivity.this, EditBehindActiviy.class);
                                intent.putExtra(EditBehindActiviy.BEAN_EVENTS, eventRecords.toArray());
                                intent.putExtra(EditBehindActiviy.BAKE_REPORT, generateReport());
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                mShowHandler.sendEmptyMessage(1);
                                startActivity(intent);
                                mHelper.stopRead();
                                finish();
                                break;
                            }
                        }
                    }
                }).start();
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
        if (id != R.id.id_baking_wind_fire && id != R.id.id_baking_other) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onFireWindChanged(Event event) {
        curEvent = event;
    }

    @Override
    public void onDataChanged(Event event) {
        curEvent = event;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("codelevex", "尼玛的什么情况个");
        isReading = false;
    }

    private BakeReportImm generateReport() {
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(DEVICE_NAME);
        Object[] objs = (Object[]) intent.getSerializableExtra(RAW_BEAN_INFO);
        List<BeanInfoSimple> beanInfos = new ArrayList<>();
        Map<Integer, Float> rawBeanWeight = new HashMap<>();
        float totalRawWeight = 0;
        for (Object obj : objs) {
            DialogBeanInfo beanInfo = (DialogBeanInfo) obj;
            beanInfos.add(new BeanInfoSimple(beanInfo.getBeanInfo(), beanInfo.getWeight() + ""));
            totalRawWeight += beanInfo.getWeight();
            rawBeanWeight.put(beanInfo.getBeanInfo().getId(), beanInfo.getWeight());
        }
        float startTemp = intent.getFloatExtra(START_TEMP, -1);
        float evnTemp = intent.getFloatExtra(EVN_TEMP, -1);

        BakeReportImm bakeReportImm = new BakeReportImm();

        bakeReportImm.setBakeDate(intent.getStringExtra(BAKE_DATE));

        bakeReportImm.setDevice(deviceName);

        bakeReportImm.setRawBeanWeight(rawBeanWeight);

        bakeReportImm.setBeanInfos(beanInfos);

        bakeReportImm.setDevelopTime(developBar.getDevelopTimeWithoutFormat());

        bakeReportImm.lineData2Pojo(chart.getLineData());

        bakeReportImm.setDevelopRate(developBar.getDevelopRateWithoutFormat());

        bakeReportImm.setStartTemp(startTemp);

        bakeReportImm.setEndTemp(endTemp);

        bakeReportImm.setEnvTemp(intent.getFloatExtra(ENV_TEMP, -1));

        return bakeReportImm;
    }


}
