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
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.pojo.TempratureSet;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.fragments.FireWindDialog;
import com.dhy.coffeesecret.ui.device.fragments.Other;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.UnitConvert;
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

import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.RAWBEAN;

public class BakeActivity extends AppCompatActivity implements BluetoothService.DataChangedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, Other.OnOtherAddListener, FireWindDialog.OnFireWindAddListener {
    public static final String RAW_BEAN_INFO = "com.dhy.coffeesercret.ui.device.BakeActivity.RAW_BEAN_INFO";
    public static final String DEVICE_NAME = "com.dhy.coffeesercret.ui.device.BakeActivity.DEVICE_NAME";
    public static final String START_TEMP = "com.dhy.coffeesercret.ui.device.BakeActivity.START_TIME";
    public static final String BAKE_DATE = "com.dhy.coffeesercret.ui.device.BakeActivity.BAKE_DATE";
    public static final String ENV_TEMP = "com.dhy.coffeesercret.ui.device.BakeActivity.ENV_TEMP";
    public static final String ENABLE_REFERLINE = "com.dhy.coffeesercret.ui.device.BakeActivity.REFER_LINE";
    public static final int DRY = 1, FIRST_BURST = 2, SECOND_BURST = 3, END = 4;
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
    private PopupWindow popupWindow;
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private float lastTime = 0;
    private TextView[] beanTemps = new TextView[2];
    private TextView[] inwindTemps = new TextView[2];
    private TextView[] outwindTemps = new TextView[2];
    private List<Entry> eventRecords = new ArrayList<>();
    private float endTemp;
    private Event curEvent;
    private boolean enableDoubleConfirm;
    private boolean isDoubleClick;
    private View curStatusView;
    private UniversalConfiguration mConfig;
    private Long startTime;
    private boolean isOverBottom = false;
    private int curStatus = RAWBEAN;
    private boolean isReading = false;
    private Thread timer = null;
    private FragmentTool fragmentTool;
    private ProgressDialog dialog;
    private boolean isEnd = false;
    private int curFlow = 0;
    private Entry curBeanEntry;
    private ArrayList<Float> tempratures;
    private TempratureSet set = new TempratureSet();
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
            float accBean = bundle.getFloat("accBean");
            float accInwind = bundle.getFloat("accInwind");
            float accOutwind = bundle.getFloat("accOutwind");
            beanTemps[0].setText(String.format("%1$.2f", bundle.getFloat("bean")) + "℃");
            beanTemps[1].setText(String.format("%1$.2f", accBean) + "℃/m");

            inwindTemps[0].setText(String.format("%1$.2f", bundle.getFloat("inwind")) + "℃");
            inwindTemps[1].setText(String.format("%1$.2f", accInwind) + "℃/m");

            outwindTemps[0].setText(String.format("%1$.2f", bundle.getFloat("outwind")) + "℃");
            outwindTemps[1].setText(String.format("%1$.2f", accOutwind) + "℃/m");

            if (curStatus == FIRST_BURST) {
                developTime.setText(developBar.getDevelopTime());
                developRate.setText(developBar.getDevelopRate());
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

        lastTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        lastTime = ((int) (lastTime * 100)) / 100.0f;

        curBeanEntry = new Entry(lastTime, beanTemp);

        if (beanTemp > 160 && isOverBottom && curStatus != FIRST_BURST) {
            curStatus = AFTER160;
        }

        if (curEvent != null) {
            curBeanEntry.setEvent(curEvent);
            eventRecords.add(curBeanEntry);
            if (!isEnd && dialog != null) {
                isEnd = true;
                endTemp = beanTemp;
            }
            curEvent = null;
        }

        set.addBeanTemp(beanTemp);
        set.addInwindTemp(inwindTemp);
        set.addOutwindTemp(outwindTemp);
        set.addAccBeanTemp(accBeanTemp);
        set.addAccInwindTemp(accInwindTemp);
        set.addAccOutwindTemp(accOutwindTemp);
        if (curEvent != null) {
            set.addEvent(lastTime + "", curEvent.getDescription() + ":" + curEvent.getCurStatus());
        }
        set.addTimex(lastTime);

        chart.addOneDataToLine(curBeanEntry, BaseChart4Coffee.BEANLINE);
        chart.addOneDataToLine(new Entry(lastTime, inwindTemp), BaseChart4Coffee.INWINDLINE);
        chart.addOneDataToLine(new Entry(lastTime, outwindTemp), BaseChart4Coffee.OUTWINDLINE);
        chart.addOneDataToLine(new Entry(lastTime, accBeanTemp), BaseChart4Coffee.ACCBEANLINE);
        chart.addOneDataToLine(new Entry(lastTime, accInwindTemp), BaseChart4Coffee.ACCINWINDLINE);
        chart.addOneDataToLine(new Entry(lastTime, accOutwindTemp), BaseChart4Coffee.ACCOUTWINDLINE);


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

        chart.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置横屏和隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_bake);
        ButterKnife.bind(this);

        chart.initLine();
        // chart.changeColorByIndex("#000000", BaseChart4Coffee.BEANLINE);
        mConfig = SettingTool.getConfig(this);
        enableDoubleConfirm = mConfig.isDoubleClick();

        tempratures = (ArrayList<Float>) getIntent().getSerializableExtra(ENABLE_REFERLINE);
        if (tempratures != null) {
            List<Entry> entries = new ArrayList<>();
            int count = 0;
            for (float temprature : tempratures) {
                entries.add(new Entry(count, temprature));
                count += 5;
            }
            chart.enableReferLine(entries);
        }

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (mBluetoothOperator == null) {
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
        }
        mBluetoothOperator.setDataChangedListener(this);
        startTime = System.currentTimeMillis();
        isReading = true;
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

    private void addEvent(View v) {
        int id = v.getId();
        boolean status = false;
        switch (id) {
            case R.id.id_baking_dry:
                updateCurBeanEntryEvent(new Event(Event.DRY, "脱水"));
                curFlow = DRY;
                status = true;
                break;
            case R.id.id_baking_firstBurst:

                updateCurBeanEntryEvent(new Event(Event.FIRST_BURST, "一爆"));
                curStatus = DevelopBar.FIRST_BURST;
                curFlow = FIRST_BURST;
                status = true;

                break;
            case R.id.id_baking_secondBurst:
                updateCurBeanEntryEvent(new Event(Event.SECOND_BURST, "二爆"));
                curFlow = SECOND_BURST;
                status = true;

                break;
            case R.id.id_baking_end:

                updateCurBeanEntryEvent(new Event(Event.END, "结束"));
                BakeReportProxy imm = generateReport();
                imm.setEntriesWithEvents(eventRecords);
                imm.setEndTemp(curBeanEntry.getY());
                Intent intent = new Intent(BakeActivity.this, EditBehindActiviy.class);
                // 停止读取
                BluetoothService.READABLE = false;
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
        if (id != R.id.id_baking_wind_fire & id != R.id.id_baking_other) {
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

    private BakeReportProxy generateReport() {
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(DEVICE_NAME);
        Object[] objs = (Object[]) intent.getSerializableExtra(RAW_BEAN_INFO);
        List<BeanInfoSimple> beanInfos = new ArrayList<>();

        for (Object obj : objs) {
            DialogBeanInfo beanInfo = (DialogBeanInfo) obj;
            beanInfos.add(new BeanInfoSimple(beanInfo.getBeanInfo(), beanInfo.getWeight() + ""));
        }
        float startTemp = intent.getFloatExtra(START_TEMP, -1);

        BakeReportProxy bakeReport = new BakeReportProxy();

        bakeReport.setTempratureSet(set);

        bakeReport.setDate(intent.getStringExtra(BAKE_DATE));

        bakeReport.setDevice(deviceName);

        bakeReport.setBeanInfoSimples(beanInfos);

        bakeReport.setDevelopmentTime(developBar.getDevelopTime());

        bakeReport.setDevelopmentRate(developBar.getDevelopRate());

        bakeReport.setStartTemperature(startTemp + "");

        bakeReport.setAmbientTemperature(intent.getFloatExtra(ENV_TEMP, -1) + "");

        ((MyApplication) getApplication()).setBakeReport(bakeReport);

        return bakeReport;
    }

    private void updateCurBeanEntryEvent(final Event event) {
        curBeanEntry.setEvent(event);
        eventRecords.add(curBeanEntry);
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
