package com.dhy.coffeesecret.ui.mine.fragments;


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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyuncs.exceptions.ClientException;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SmsUtils;
import com.dhy.coffeesecret.ui.common.views.DisableButton;

import java.io.IOException;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

public class PasswordFragment extends Fragment implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {


    private static final int WAIT = 0x0;
    private static final int CANCEL = 0x1;
    private static final int SUCCESS = 0x2;


    private View mContentView;
    private EditText mEditTextFir;
    private EditText mEditTextSec;
    private View mButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAIT:
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在修改密码...");
                    break;
                case CANCEL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    Snackbar.make(mContentView, "密码更新了", Snackbar.LENGTH_LONG).show();
                    remove();
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private MyApplication application;
    private TextView mTvHint;
    private EditText mEditTextCode;
    private String mTelNumber;

    public PasswordFragment() {
    }

    public static PasswordFragment newInstance(String tel) {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putString("tel", tel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTelNumber = getArguments().getString("tel");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditTextFir = (EditText) mContentView.findViewById(R.id.et_pw_new);
        mEditTextSec = (EditText) mContentView.findViewById(R.id.et_pw_sec);
        mEditTextCode = (EditText) mContentView.findViewById(R.id.et_code);
        mTvHint = (TextView) mContentView.findViewById(R.id.tv_hint);
        mButton = mContentView.findViewById(R.id.btn_update);
        mContentView.findViewById(R.id.iv_back).setOnClickListener(this);
        CheckBox checkBox = (CheckBox) mContentView.findViewById(R.id.cb_see);
        checkBox.setOnCheckedChangeListener(this);
        mButton.setOnClickListener(this);
        mEditTextSec.addTextChangedListener(this);

        DisableButton disableButton = (DisableButton) mContentView.findViewById(R.id.btn_send_code);
        disableButton.setOnPressListener(new DisableButton.OnPressListener() {
            @Override
            public boolean onPress() {
                return sendCode();
            }
        });
    }

    private String mCode = ""; //验证码

    private boolean sendCode() {
        if (mTelNumber.length() == 11) {
            new Thread() {
                @Override
                public void run() {
                    mCode = SmsUtils.getCode();
                    try {
                        SmsUtils.sendSms(mTelNumber, mCode);
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
        mContentView = inflater.inflate(R.layout.fragment_password, container, false);
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
        if (mTelNumber.equals(code)) {
            return true;
        }
        if (code.length() == 0) {
            Snackbar.make(mContentView, "请输入验证码。", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mContentView, "验证码输入错误。", Snackbar.LENGTH_LONG).show();
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditTextSec.getWindowToken(), 0);

            final String sec = mEditTextSec.getText().toString().trim();
            String fir = mEditTextFir.getText().toString().trim();
            if (verify(sec, fir) && verifyCode()) {
                application = ((MyApplication) getActivity().getApplication());
                final String token = application.getToken();
                new Thread() {
                    @Override
                    public void run() {
                        String url = UrlLogin.updatePassword(token, sec);
                        try {
                            mHandler.sendEmptyMessage(WAIT);
                            String result = HttpUtils.getStringFromServer(url,token,getActivity());
                            if (result != null && !result.startsWith("error")) {
                                application.setToken(result);
                                Message msg = Message.obtain();
                                msg.what = SUCCESS;
                                mHandler.sendMessage(msg);
                            } else {
                                Snackbar.make(mContentView, "更新失败，请检测网络设置", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } else {
            remove();
        }
    }

    private void remove() {
        SystemClock.sleep(500);
        getFragmentManager()
                .popBackStack(getClass().getSimpleName(), 1);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String sec = s.toString().trim();
        String fir = mEditTextFir.getText().toString().trim();
        mButton.setEnabled(fir.equals(sec) && sec.length() >= 6);
        verify(sec, fir);
    }

    private boolean verify(String sec, String fir) {
        if (!fir.equals(sec)) {
            mTvHint.setText("两次输入的密码不一致");
        } else if (sec.length() < 6) {
            mTvHint.setText("输入6位以上字符");
        } else {
            mTvHint.setText("");
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mEditTextFir.setInputType(TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEditTextSec.setInputType(TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            mEditTextFir.setInputType(TYPE_TEXT_VARIATION_PASSWORD | TYPE_CLASS_TEXT);
            mEditTextSec.setInputType(TYPE_TEXT_VARIATION_PASSWORD | TYPE_CLASS_TEXT);
        }
    }
}
