package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.T;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class ItemInputFragment extends Fragment implements NumberPicker.OnValueChangeListener {

    private static final String ARG_POSITION = "position";
    private static final String ARG_TITLE = "title";
    private static final String ARG_VALUE = "value";

    private String mTitle;
    private String mValue;
    private int mPosition;

    private TextView mTitleView;

    //    private MaterialNumberPicker mPickerFir;
    private MaterialNumberPicker mPickerSec;
    private MaterialNumberPicker mPickerThr;
//    private MaterialNumberPicker mPickerFou;

    private View mContentView;

    private OnItemValueChangeListener mListener;

    public ItemInputFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title      title.
     * @param defaultVal defaultVal.
     * @return A new instance of fragment ItemInputFragment.
     */
    public static ItemInputFragment newInstance(int position, String title, String defaultVal) {

        ItemInputFragment fragment = new ItemInputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_VALUE, defaultVal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        mTitleView = (TextView) mContentView.findViewById(R.id.title_text);
        mTitleView.setText(mTitle);

        mPickerSec = (MaterialNumberPicker) mContentView.findViewById(R.id.np_sec);
        mPickerThr = (MaterialNumberPicker) mContentView.findViewById(R.id.np_thr);
        if (mPosition > 7) {
            lockPicker();
        }

        mPickerSec.setFormatter(new Formatter(1));
        mPickerThr.setFormatter(new Formatter(2));

        Float aFloat = Float.valueOf(mValue);
        int i = (int) (aFloat * 100);
        mPickerSec.setValue(i / 100);
        mPickerThr.setValue((i % 100) / 25);
        mPickerSec.setOnValueChangedListener(this);
        mPickerThr.setOnValueChangedListener(this);
    }

    public void lockPicker() {
        mPickerSec.setMaxValue(5);
        mPickerSec.setMinValue(0);
        mPickerSec.setTextSize(120);
//        mPickerThr.setEnabled(false);
        mPickerThr.setVisibility(View.GONE);
        mContentView.findViewById(R.id.tv).setVisibility(View.GONE);
//        mPickerThr.setValue(0);
//        mPickerThr.setTextColor(Color.GRAY);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mValue = getArguments().getString(ARG_VALUE);
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_item_input, container, false);
        return mContentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnItemValueChangeListener) {
            mListener = (OnItemValueChangeListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemValueChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        int val1 = mPickerSec.getValue();
        int val2 = mPickerThr.getValue();
        float value;
        if (val1 == 10) {
            mPickerThr.setValue(0);
            value = 10;
        } else {
            value = val1 + val2 * 0.25f;
        }
        mListener.onItemValueChange(mPosition, value);
    }

    private class Formatter implements NumberPicker.Formatter {

        private int position;

        Formatter(int position) {
            this.position = position;
        }

        @Override
        public String format(int i) {
            if (i == 11) i = 5; // FIXME: 2017/3/16  这里有个11不知道哪里出来的

            switch (position) {
                case 1:
                    return i / 10 + " " + i % 10;
                case 2:
                    return i * 25 / 10 + " " + i * 25 % 10;
            }
            return "error";
        }
    }

    public interface OnItemValueChangeListener {
        void onItemValueChange(int position, float value);
    }
}
