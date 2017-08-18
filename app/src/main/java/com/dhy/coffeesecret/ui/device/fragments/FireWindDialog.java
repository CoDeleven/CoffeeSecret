package com.dhy.coffeesecret.ui.device.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.github.mikephil.charting.data.Event;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by CoDeleven on 17-2-23.
 */

public class FireWindDialog extends DialogFragment implements CircleSeekBar.OnSeekBarChangeListener {
    @Bind(R.id.id_baking_circle1)
    CircleSeekBar circle1;
    @Bind(R.id.id_baking_circle2)
    CircleSeekBar circle2;
    CircleSeekBar curCircle;
    @Bind(R.id.id_baking_circle1_text)
    TextView text1;
    @Bind(R.id.id_baking_circle2_text)
    TextView text2;
    @Bind(R.id.id_baking_confirm)
    Button mConfirm;
    @Bind(R.id.id_baking_cancel)
    Button mCancel;
    // 按客户的需求，需要默认isGroup为true
    private boolean isGroup = true;
    private static float fireValue;
    private static float windValue;
    private OnFireWindAddListener onFireWindAddListener;

    /**
     * 更新数值视图
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            float curValue = bundle.getFloat("curValue");
            int curSeek = bundle.getInt("curSeek");
            if (1 == curSeek) {
                text1.setText("" + curValue);
            } else {
                text2.setText("" + curValue);
            }
            return false;
        }
    });


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.bake_fire_wind_dialog, container, false);
        ButterKnife.bind(this, view);

        circle1.setOnSeekBarChangeListener(this);
        circle2.setOnSeekBarChangeListener(this);

        circle1.setCurProcess((int)windValue);
        circle2.setCurProcess((int)fireValue);
        return view;
    }

    /**
     * 当触摸圆圈1时触发的条件
     *
     * @return
     */
    @OnTouch(R.id.id_baking_circle1)
    public boolean onCircle1Touch() {
        // 如果没有进行组合，则重置另外一个圈
        if (!isGroup) {
            if (curCircle == circle2) {
                resetSeekbar(circle2);
            }
        }
        // 设置当前curCircle
        curCircle = circle1;
        return false;
    }

    /**
     * 当触摸圆圈2时触发的条件
     *
     * @return
     */
    @OnTouch(R.id.id_baking_circle2)
    public boolean onCircle2Touch() {
        // 如果没有进行组合，则重置另外一个圈
        if (!isGroup) {
            if (curCircle == circle1) {
                resetSeekbar(circle1);
            }
        }
        // 设置当前curCircle
        curCircle = circle2;
        return false;
    }

    @OnClick(R.id.id_baking_confirm)
    public void onConfirm() {
        dismiss();
    }

    @OnClick(R.id.id_baking_cancel)
    public void onCancel() {
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(UnitConvert.dp2px(getResources(), 400), UnitConvert.dp2px(getResources(), 250));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onChanged(CircleSeekBar seekbar, int curValue, double angle) {
        // Log.d("GG", "onChanged: curValue" + curValue + ", angle:" + angle);
        // 因为客户需求，需要精确到0.1，故再传入angle自行根据 半分比获取相应的数值
        double processVal = angle / 360 * seekbar.getMaxProcess();
        updateText(seekbar, Utils.get1PrecisionFloat(processVal));
    }

    /**
     * 重置circle seekbar
     *
     * @param seekBar
     */
    private void resetSeekbar(CircleSeekBar seekBar) {
        if (seekBar == circle1) {
            circle1.resetStatus();
            updateText(circle1, 0);
        } else {
            circle2.resetStatus();
            updateText(circle2, 0);
        }
    }

    /**
     * 更新视图
     *
     * @param seekBar
     * @param curValue
     */
    private void updateText(CircleSeekBar seekBar, float curValue) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloat("curValue", curValue);

        if (seekBar == circle1) {
            windValue = curValue;
            bundle.putInt("curSeek", 1);
        } else {
            fireValue = curValue;
            bundle.putInt("curSeek", 2);
        }
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public void setOnFireWindAddListener(OnFireWindAddListener onFireWindAddListener) {
        this.onFireWindAddListener = onFireWindAddListener;
    }

    /**
     * 对图标上产生事件按钮
     */
    @OnClick(R.id.id_baking_confirm)
    void generateEvent() {
        Event event = new Event(Event.FIRE_WIND);
        if (isGroup) {
            event.setDescription("FireValue:" + fireValue + ", WindValue:" + windValue);
        } else {
            if (curCircle == circle1) {
                event.setDescription("WindValue:" + windValue);
            } else {
                event.setDescription("FireValue:" + fireValue);
            }
        }
        onFireWindAddListener.onFireWindChanged(event);
    }

    public interface OnFireWindAddListener {
        void onFireWindChanged(Event event);
    }
}
