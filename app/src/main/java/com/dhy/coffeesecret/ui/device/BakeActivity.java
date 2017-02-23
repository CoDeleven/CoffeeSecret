package com.dhy.coffeesecret.ui.device;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
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
import com.dhy.coffeesecret.pojo.Temprature;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.device.fragments.Other;
import com.dhy.coffeesecret.utils.BluetoothHelper;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.DevelopBar;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.Timer;
import java.util.TimerTask;

import static com.dhy.coffeesecret.views.DevelopBar.AFTER160;
import static com.dhy.coffeesecret.views.DevelopBar.FIRST_BURST;
import static com.dhy.coffeesecret.views.DevelopBar.RAWBEAN;

public class BakeActivity extends AppCompatActivity implements BluetoothHelper.DataChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private BaseChart4Coffee chart;
    private TextView lineOperator;
    private PopupWindow popupWindow;
    private BluetoothHelper mHelper;
    private int count = 0;
    private TextView[] beanTemps = new TextView[2];
    private TextView[] inwindTemps = new TextView[2];
    private TextView[] outwindTemps = new TextView[2];

    private Button mDry;
    private Button mFirstBurst;
    private Button mSecondBurst;
    private Button mEnd;
    private Button mFireWind;
    private Button mOther;

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

            if(curStatus == FIRST_BURST){
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
            if (!isOverBottom && (seconds > 30 && minutes < 1)) {
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
            beanEntry.setEvent(curEvent);
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
        // 横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        if(!mHelper.isTestThreadAlive()){
            mHelper.test(getResources().openRawResource(R.raw.test));
        }
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

        chart.addTempratureLine(BaseChart4Coffee.BEANLINE);
        chart.addTempratureLine(BaseChart4Coffee.INWINDLINE);
        chart.addTempratureLine(BaseChart4Coffee.OUTWINDLINE);
        chart.addTempratureLine(BaseChart4Coffee.ACCBEANLINE, true);
        chart.addTempratureLine(BaseChart4Coffee.ACCINWINDLINE, true);
        chart.addTempratureLine(BaseChart4Coffee.ACCOUTWINDLINE, true);

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
                break;
            case R.id.id_baking_firstBurst:
                curEvent = new Event(Event.FIRST_BURST);
                curStatus = FIRST_BURST;
                break;
            case R.id.id_baking_secondBurst:
                curEvent = new Event(Event.SECOND_BURST);
                break;
            case R.id.id_baking_end:
                curEvent = new Event(Event.END);
            case R.id.id_baking_wind_fire:
                curEvent = new Event(Event.FIRE_WIND);
                break;
            case R.id.id_baking_other:
                curEvent = new Event(Event.OTHER);
                DialogFragment other = new Other();
                Log.e("codelevex", "启用otherfragment");
                fragmentTool.showDialogFragmen("otherFragment", other);
                break;
        }
        if (id != R.id.id_baking_wind_fire && id != R.id.id_baking_other) {
            v.setEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("codelevex", "尼玛的什么情况个");
        isReading = false;
    }
}
