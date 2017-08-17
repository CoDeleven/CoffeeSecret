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
import android.widget.EditText;

import com.aliyuncs.exceptions.ClientException;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SmsUtils;
import com.dhy.coffeesecret.views.DisableButton;

import java.io.IOException;

public class TelFragment extends Fragment implements View.OnClickListener, TextWatcher, VerifyFragment.OnRemoveListener {


    private static final int WAIT = 0x0;
    private static final int CANCEL = 0x1;
    private static final int SUCCESS = 0x2;


    private View mContentView;
    private EditText mEditText;
    private View mButton;

    private OnSuccessListener mOnSuccessListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAIT:
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在修改绑定的电话...");
                    break;
                case CANCEL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    Snackbar.make(mContentView, "您更换了绑定的电话。", Snackbar.LENGTH_LONG).show();
                    if (mOnSuccessListener != null) {
                        mOnSuccessListener.onUserNameUpdate(msg.obj.toString());
                    }
                    remove();
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private MyApplication application;
    private EditText mEditTextCode;
    private String mTelNumber;

    public TelFragment() {
    }

    public static TelFragment newInstance(String tel) {
        TelFragment fragment = new TelFragment();
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
        mButton = mContentView.findViewById(R.id.btn_update);
        mContentView.findViewById(R.id.iv_back).setOnClickListener(this);
        mButton.setOnClickListener(this);
        mEditTextCode = (EditText) mContentView.findViewById(R.id.et_code);
        mEditText = (EditText) mContentView.findViewById(R.id.et_tel);
        mEditText.addTextChangedListener(this);
        mContentView.setVisibility(View.GONE);


        DisableButton disableButton = (DisableButton) mContentView.findViewById(R.id.btn_send_code);
        disableButton.setOnPressListener(new DisableButton.OnPressListener() {
            @Override
            public boolean onPress() {
                return sendCode();
            }
        });
        verifyTel();
    }

    private String mCode = ""; //验证码

    private boolean sendCode() {
        final String tel = mEditText.getText().toString().trim();
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

    private void verifyTel() {
        VerifyFragment fragment = VerifyFragment.newInstance(mTelNumber);
        fragment.setOnRemoveListener(this);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_bottom, R.anim.in_from_right, R.anim.out_to_bottom)
                .add(R.id.layout, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_tel, container, false);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            if (verifyCode()) {
                application = ((MyApplication) getActivity().getApplication());
                final String tel = mEditText.getText().toString().toString();
                final String token = application.getToken();
                new Thread() {
                    @Override
                    public void run() {
                        String url = UrlLogin.updateMobilePhone(token, tel);
                        try {
                            mHandler.sendEmptyMessage(WAIT);
                            String result = HttpUtils.getStringFromServer(url);
                            if (result != null && !result.startsWith("error")) {
                                application.setToken(result);
                                Message msg = Message.obtain();
                                msg.what = SUCCESS;
                                msg.obj = tel;
                                mHandler.sendMessage(msg);
                            } else if ("error:该号码已被绑定".equals(result)) {
                                Snackbar.make(mContentView, "该号码已被绑定。", Snackbar.LENGTH_LONG).show();
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

    public interface OnSuccessListener {
        void onUserNameUpdate(String username);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mButton.setEnabled(s.length() == 11);
    }
}
