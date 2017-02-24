package com.dhy.coffeesecret.ui.device.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.device.BakeActivity;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.github.mikephil.charting.data.Event;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by CoDeleven on 17-2-23.
 */

public class FireWindDialog extends DialogFragment implements CircleSeekBar.OnSeekBarChangeListener{
    @Bind(R.id.id_baking_circle1)
    CircleSeekBar circle1;
    @Bind(R.id.id_baking_circle2)
    CircleSeekBar circle2;
    private boolean isGroup = false;
    CircleSeekBar curCircle;
    @Bind(R.id.id_baking_group)
    CheckBox mGroup;
    @Bind(R.id.id_baking_circle1_text)
    TextView text1;
    @Bind(R.id.id_baking_circle2_text)
    TextView text2;
    @Bind(R.id.id_baking_confirm)
    Button mConfirm;
    @Bind(R.id.id_baking_cancel)
    Button mCancel;
    private String fireValue;
    private String windValue;
    private OnFireWindAddListener onFireWindAddListener;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int curValue = bundle.getInt("curValue");
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
        return view;
    }

    @OnTouch(R.id.id_baking_circle1)
    public boolean onCircle1Touch(){
        if (!isGroup) {
            if (curCircle == circle2) {
                resetSeekbar(circle2);
            }
        }
        curCircle = circle1;
        return false;
    }

    @OnTouch(R.id.id_baking_circle2)
    public boolean onCircle2Touch(){
        if (!isGroup) {
            if (curCircle == circle1) {
                resetSeekbar(circle1);
            }
        }
        curCircle = circle2;
        return false;
    }

    @OnCheckedChanged(R.id.id_baking_group)
    public void checkedChange(boolean isChecked){
        isGroup = isChecked;
        if (!isChecked) {
            if (curCircle == circle1) {
                resetSeekbar(circle2);
            } else {
                resetSeekbar(circle1);
            }
        }
    }

    @OnClick(R.id.id_baking_confirm)
    public void onConfirm(){
        windValue = text1.getText().toString();
        fireValue = text2.getText().toString();

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
    public void onChanged(CircleSeekBar seekbar, int curValue) {
        updateText(seekbar, curValue);
    }

    private void resetSeekbar(CircleSeekBar seekBar) {
        if (seekBar == circle1) {
            circle1.resetStatus();
            updateText(circle1, 0);
        } else {
            circle2.resetStatus();
            updateText(circle2, 0);
        }
    }

    private void updateText(CircleSeekBar seekBar, int curValue) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("curValue", curValue);
        if (seekBar == circle1) {
            bundle.putInt("curSeek", 1);
        } else {
            bundle.putInt("curSeek", 2);
        }
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!"".equals(fireValue) && !"".equals(windValue)){
            data.putExtra("fireValue", fireValue);
            data.putExtra("windValue", windValue);
        }
    }

    public void setOnFireWindAddListener(OnFireWindAddListener onFireWindAddListener){
        this.onFireWindAddListener = onFireWindAddListener;
    }

    public interface OnFireWindAddListener{
        void onFireWindChanged(Event event);
    }

    @OnClick(R.id.id_baking_confirm)
    void generateEvent(){
        Event event = new Event(Event.FIRE_WIND);
        if(isGroup){
            event.setDescription("FireValue:" + fireValue + ", WindValue:" + windValue);
        }else{
            if(curCircle == circle1){
                event.setDescription("WindValue:" + windValue);
            }else{
                event.setDescription("FireValue:" + fireValue);
            }
        }
        onFireWindAddListener.onFireWindChanged(event);
    }
}
