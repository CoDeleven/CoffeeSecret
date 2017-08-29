package com.dhy.coffeesecret;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.aliyuncs.exceptions.ClientException;
import com.dhy.coffeesecret.ui.common.views.DisableButton;
import com.dhy.coffeesecret.ui.mine.fragments.VerifyFragment;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SmsUtils;

import java.io.IOException;

public class RegFragment extends Fragment implements View.OnClickListener, TextWatcher, VerifyFragment.OnRemoveListener, CompoundButton.OnCheckedChangeListener {


    private static final int WAIT = 0x0;
    private static final int CANCEL = 0x1;
    private static final int SUCCESS = 0x2;


    private View mContentView;
    private EditText mEditTextTel;
    private EditText mEditTextCode;
    private EditText mEditTextPw;
    private EditText mEditTextPwSec;
    private View mButton;

    private OnSuccessListener mOnSuccessListener;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAIT:
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在发送请求...");
                    break;
                case CANCEL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    remove();
                    break;
            }
        }
    };
    private MyApplication application;
    private String mCode = ""; //验证码

    public RegFragment() {
    }

    public static RegFragment newInstance() {
        RegFragment fragment = new RegFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mButton = mContentView.findViewById(R.id.btn_update);
        mContentView.findViewById(R.id.iv_back).setOnClickListener(this);
        mButton.setOnClickListener(this);
        mEditTextCode = (EditText) mContentView.findViewById(R.id.et_code);
        mEditTextPw = (EditText) mContentView.findViewById(R.id.et_pw);
        mEditTextPwSec = (EditText) mContentView.findViewById(R.id.et_pw_sec);
        mEditTextTel = (EditText) mContentView.findViewById(R.id.et_tel);

        mEditTextTel.addTextChangedListener(this);
        mEditTextPw.addTextChangedListener(this);
        mEditTextPwSec.addTextChangedListener(this);
        mEditTextCode.addTextChangedListener(this);

        CheckBox cb = (CheckBox) mContentView.findViewById(R.id.cb_see);
        cb.setOnCheckedChangeListener(this);


        DisableButton disableButton = (DisableButton) mContentView.findViewById(R.id.btn_send_code);
        disableButton.setOnPressListener(new DisableButton.OnPressListener() {
            @Override
            public boolean onPress() {
                return sendCode();
            }
        });
    }

    private boolean sendCode() {
        final String tel = mEditTextTel.getText().toString().trim();
        if (tel.length() == 11) {
            new Thread() {
                @Override
                public void run() {
                    mCode = SmsUtils.getCode();
                    try {
                        SmsUtils.sendSms(tel, mCode);
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_reg, container, false);
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return mContentView;
    }

    //验证码验证
    private boolean verifyCode() {
        String code = mEditTextCode.getText().toString().trim();
        if (mCode.equals(code)) {
            return true;
        }
        if (code.length() == 0) {
            Snackbar.make(mContentView, "请输入验证码。", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mContentView, "验证码输入错误。", Snackbar.LENGTH_LONG).show();
        }

        return false;
    }

    private boolean verifyPassword() {
        String fir = mEditTextPw.getText().toString().trim();
        String sec = mEditTextPwSec.getText().toString().trim();
        if (!fir.equals(sec)) {
            Snackbar.make(mContentView, "两次密码输入不一致。", Snackbar.LENGTH_LONG).show();
        } else if (sec.length() < 6) {
            Snackbar.make(mContentView, "请输入6位以上密码。", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return fir.equals(sec);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditTextPwSec.getWindowToken(), 0);
            if (verifyCode() && verifyPassword()) {
                application = ((MyApplication) getActivity().getApplication());
                final String tel = mEditTextTel.getText().toString().trim();
                final String password = mEditTextPwSec.getText().toString().trim();
                new Thread() {
                    @Override
                    public void run() {
                        String url = UrlLogin.register(tel, password);
                        try {
                            mHandler.sendEmptyMessage(WAIT);
                            String result = HttpUtils.getStringFromServer(url);
                            if (result != null && !result.startsWith("error")) {
                                application.setToken(result);
                                Message msg = Message.obtain();
                                msg.what = SUCCESS;
                                msg.obj = result;
                                mHandler.sendMessage(msg);

                                if (mOnSuccessListener != null) {
                                    mOnSuccessListener.onRegSuccess(result, tel);
                                }
                            } else if ("error:该号码已被注册!".equals(result)) {
                                mHandler.sendEmptyMessage(CANCEL);
                                Snackbar.make(mContentView, "该号码已被注册。", Snackbar.LENGTH_LONG).show();
                            } else {
                                mHandler.sendEmptyMessage(CANCEL);
                                Snackbar.make(mContentView, "请检测网络设置", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            mHandler.sendEmptyMessage(CANCEL);
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } else {
            remove();
        }
    }

    public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.mOnSuccessListener = onSuccessListener;
    }

    private void remove() {
        SystemClock.sleep(500);
        getFragmentManager()
                .popBackStack(getClass().getSimpleName(), 1);
    }

    @Override
    public void onRemove(boolean isVerity) {
        if (!isVerity) {
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.none, R.anim.none)
                    .remove(this)
                    .commit();
            getFragmentManager()
                    .popBackStack(this.getClass().getSimpleName(), 1);
        }
    }

    @Override
    public void onSuccess() {
        mContentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mEditTextPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEditTextPwSec.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            mEditTextPw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            mEditTextPwSec.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mButton.setEnabled(
                mEditTextCode.length() == 6 && mEditTextTel.length() == 11
                        && mEditTextPw.length() > 0 && mEditTextPwSec.length() > 0);
    }

    public interface OnSuccessListener {
        void onRegSuccess(String result, String tel);
    }
}
