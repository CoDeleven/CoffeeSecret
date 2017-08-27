package com.dhy.coffeesecret.ui.common.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by mxf on 2017/8/14.
 */
public class DisableButton extends Button {
    public DisableButton(Context context) {
        super(context);
    }

    public DisableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DisableButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int time;
    private OnPressListener mOnPressListener;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(time > 0){
                setText("发送验证码("+time+")");
            }else {
                setText("发送验证码");
                setEnabled(true);
            }
        }
    };

    public interface OnPressListener{
        boolean onPress();
    }

    public void setOnPressListener(OnPressListener onPressListener) {
        this.mOnPressListener = onPressListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if(isEnabled()){
                    if(mOnPressListener != null){
                        if(mOnPressListener.onPress()){
                            setEnabled(false);
                            disable();
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void disable() {
        new Thread(){
            @Override
            public void run() {
                time = 50;
                while (time > 0) {
                    time --;
                    mHandler.sendEmptyMessage(0);
                    SystemClock.sleep(1000);
                }
            }
        }.start();
    }
}
