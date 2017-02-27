package com.dhy.coffeesecret.ui.cup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.cup.listener.FinishListener;

public class NormalToolBar extends Fragment {

    private View view;
    private Toolbar mToolbar;
    private Button mEditButton;
    private View.OnClickListener mListener;
    private TextView mTextView;
    private String mTitle;

    public NormalToolBar() {
        mTitle = "杯测详情";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditButton.setOnClickListener(mListener);
        mToolbar.setNavigationOnClickListener(new FinishListener(getActivity()));
        mTextView.setText(mTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_normal_tool_bar, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolBar);
        mEditButton = (Button) view.findViewById(R.id.btn_edit);
        mTextView = (TextView) view.findViewById(R.id.tv_title);
        return view;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        if(mTextView != null){
            mTextView.setText(title);
        }
    }

    public void setEditBtnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

}
