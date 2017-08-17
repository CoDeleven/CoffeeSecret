package com.dhy.coffeesecret.ui.mine.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

import com.aliyuncs.exceptions.ClientException;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.SmsUtils;
import com.dhy.coffeesecret.views.DisableButton;

public class VerifyFragment extends Fragment implements View.OnClickListener, TextWatcher {


    private static final int WAIT = 0x0;
    private static final int CANCEL = 0x1;
    private static final int SUCCESS = 0x2;
    private static final int FAIL = 0x3;


    private View mContentView;
    private View mButton;
    private boolean isVerify = false;

    private OnRemoveListener mOnRemoveListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAIT:
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在验证...");
                    break;
                case CANCEL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    isVerify = true;
                    if (mOnRemoveListener != null) {
                        mOnRemoveListener.onSuccess();
                    }
                    remove();
                    break;
                case FAIL:
                    sendEmptyMessage(CANCEL);
                    isVerify = false;
                    Snackbar.make(mContentView, "验证码验证失败...", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private EditText mEditTextCode;
    private String mTelNumber;

    public VerifyFragment() {
    }

    public static VerifyFragment newInstance(String tel) {
        VerifyFragment fragment = new VerifyFragment();
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
        mEditTextCode.addTextChangedListener(this);
        TextView tv = (TextView) mContentView.findViewById(R.id.tv_tel);
        tv.setText("当前手机号：" + mTelNumber.substring(0, 3) + "****" + mTelNumber.substring(7));


        DisableButton disableButton = (DisableButton) mContentView.findViewById(R.id.btn_send_code);
        disableButton.setOnPressListener(new DisableButton.OnPressListener() {
            @Override
            public boolean onPress() {
                return sendCode();
            }
        });
    }

    private String mCode = "";

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
        mContentView = inflater.inflate(R.layout.fragment_verify, container, false);
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
            imm.hideSoftInputFromWindow(mEditTextCode.getWindowToken(), 0);
            mHandler.sendEmptyMessage(WAIT);
            if (verifyCode()) {
                mHandler.sendEmptyMessage(SUCCESS);
            } else {
                mHandler.sendEmptyMessage(FAIL);
            }
        } else {
            remove();
        }

    }

    private void remove() {
        getFragmentManager()
                .popBackStack(getClass().getSimpleName(), 1);
    }

    public void setOnRemoveListener(OnRemoveListener onRemoveListener) {
        this.mOnRemoveListener = onRemoveListener;
    }

    public interface OnRemoveListener {
        void onRemove(boolean isVerity);

        void onSuccess();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOnRemoveListener != null) {
            mOnRemoveListener.onRemove(isVerify);
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
        mButton.setEnabled(s.length() > 0);
    }
}
