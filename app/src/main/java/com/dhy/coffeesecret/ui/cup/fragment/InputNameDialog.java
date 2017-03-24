package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.cup.NewCuppingActivity;
import com.dhy.coffeesecret.utils.T;

/**
 * Created by mxf on 2017/2/26.
 */
public class InputNameDialog extends DialogFragment {

    public static final String CUPPING_NAME =  "cuppingName";

    private String mCuppingName;
    private View mView;
    private EditText mEditText;
    private Button mConfirm;
    private Button mCancel;
    private InputMethodManager methodManager;

    private OnConfirmListener mOnConfirmListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mCuppingName = getArguments().getString(CUPPING_NAME);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditText = (EditText) mView.findViewById(R.id.et_name);
        mConfirm = (Button) mView.findViewById(R.id.btn_confirm);
        mCancel = (Button) mView.findViewById(R.id.btn_cancel);
        mEditText.setText(mCuppingName);
        mEditText.setSelection(mCuppingName.length());
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setText(mCuppingName);
                mEditText.setSelection(mCuppingName.length());
                dismiss();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mEditText.getText().toString().trim();
                if(mOnConfirmListener != null){
                   if(name == null || "".equals(name)){
                       T.showShort(getActivity(), "请输入杯测名称");
                   }else {
                       mOnConfirmListener.onConfirm(name);
                       dismissAllowingStateLoss();
                   }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dialog_input_name, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return mView;
    }

    public static InputNameDialog newInstance(String name) {
        InputNameDialog dialog = new InputNameDialog();
        Bundle args = new Bundle();
        args.putString(CUPPING_NAME, name);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnConfirmClickListener(OnConfirmListener listener){
        this.mOnConfirmListener = listener;
    }

    public interface OnConfirmListener{
        void onConfirm(String name);
    }
}
