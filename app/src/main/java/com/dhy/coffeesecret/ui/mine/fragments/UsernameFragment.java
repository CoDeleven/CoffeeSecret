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

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;

import java.io.IOException;

public class UsernameFragment extends Fragment implements View.OnClickListener, TextWatcher {


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
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在更新用户名...");
                    break;
                case CANCEL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    Snackbar.make(mContentView, "用户名更新了", Snackbar.LENGTH_LONG).show();
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

    public UsernameFragment() {
    }

    public static UsernameFragment newInstance() {
        UsernameFragment fragment = new UsernameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditText = (EditText) mContentView.findViewById(R.id.et_username);
        mButton = mContentView.findViewById(R.id.btn_update);
        mContentView.findViewById(R.id.iv_back).setOnClickListener(this);
        mButton.setOnClickListener(this);
        mEditText.addTextChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_username, container, false);
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return mContentView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            application = ((MyApplication) getActivity().getApplication());
            final String username = mEditText.getText().toString().toString();
            final String token = application.getToken();
            new Thread() {
                @Override
                public void run() {
                    String url = UrlLogin.updateUsername(token, username);
                    try {
                        mHandler.sendEmptyMessage(WAIT);
                        String result = HttpUtils.getStringFromServer(url,token,getActivity());
                        if (result != null && !result.startsWith("error")) {
                            application.setToken(result);
                            Message msg = Message.obtain();
                            msg.what = SUCCESS;
                            msg.obj = username;
                            mHandler.sendMessage(msg);
                        } else {
                            Snackbar.make(mContentView, "更新失败，请检测网络设置", Snackbar.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else {
            remove();
        }
    }

    private void remove() {
        SystemClock.sleep(500);
        getFragmentManager()
                .popBackStack(getClass().getSimpleName(), 1);
    }

    public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.mOnSuccessListener = onSuccessListener;
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
        mButton.setEnabled(s.length() > 0);
    }
}
