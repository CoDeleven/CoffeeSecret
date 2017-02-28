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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnItemValueChangeListener} interface
 * to handle interaction events.
 * Use the {@link ItemInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemInputFragment extends Fragment implements NumberPicker.OnValueChangeListener {

    private static final String ARG_POSITION = "position";
    private static final String ARG_TITLE = "title";
    private static final String ARG_VALUE = "value";

    private String mTitle;
    private String mValue;
    private int mPosition;

    private TextView mTitleView;

    private MaterialNumberPicker mPickerFir;
    private MaterialNumberPicker mPickerSec;
    private MaterialNumberPicker mPickerThr;
    private MaterialNumberPicker mPickerFou;

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
        mPickerFir = (MaterialNumberPicker) mContentView.findViewById(R.id.np_fir);
        mPickerSec = (MaterialNumberPicker) mContentView.findViewById(R.id.np_sec);
        mPickerThr = (MaterialNumberPicker) mContentView.findViewById(R.id.np_thr);
        mPickerFou = (MaterialNumberPicker) mContentView.findViewById(R.id.np_fou);
        Float aFloat = Float.valueOf(mValue);
        int i = (int) (aFloat * 100);
        mTitleView.setText(mTitle);
        mPickerFou.setValue(i % 10);
        mPickerThr.setValue((i / 10) % 10);
        mPickerSec.setValue((i / 100) % 10);
        mPickerFir.setValue(i / 1000);

        if (mPosition > 7) {
            lockPicker();
        }

        mPickerFir.setOnValueChangedListener(this);
        mPickerSec.setOnValueChangedListener(this);
        mPickerThr.setOnValueChangedListener(this);
        mPickerFou.setOnValueChangedListener(this);
    }

    public void lockPicker() {
        mPickerSec.setMaxValue(5);
        mPickerFir.setEnabled(false);
        mPickerFou.setEnabled(false);
        mPickerThr.setEnabled(false);
        mPickerFou.setValue(0);
        mPickerThr.setValue(0);
        mPickerFir.setValue(0);
        mPickerFou.setTextColor(Color.GRAY);
        mPickerThr.setTextColor(Color.GRAY);
        mPickerFir.setTextColor(Color.GRAY);
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

        int val0 = mPickerFir.getValue();
        int val1 = mPickerSec.getValue();
        int val2 = mPickerThr.getValue();
        int val3 = mPickerFou.getValue();
        float value;
        if(val0 == 1){
            mPickerSec.setValue(0);
            mPickerThr.setValue(0);
            mPickerFou.setValue(0);
            T.showShort(getActivity(),"分数上限为10分");
            value = 10.00f;
        }else {
            value = val0 * 10 + val1 + val2 * 0.1f + val3 * 0.01f;
        }

        if (mListener != null) {
            mListener.onItemValueChange(mPosition, value);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemValueChangeListener {
        void onItemValueChange(int position, float value);
    }
}
