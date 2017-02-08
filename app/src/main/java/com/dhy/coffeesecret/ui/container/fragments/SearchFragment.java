package com.dhy.coffeesecret.ui.container.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.interfaces.ToolbarOperation;
import com.dhy.coffeesecret.utils.FragmentTool;


public class SearchFragment extends Fragment {
    private EditText editText;
    private ToolbarOperation toolbarOperation;
    private Button cancel;
    private ImageButton clear;
    private InputMethodManager imm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.cancel = (Button) view.findViewById(R.id.id_btn_cancel);

        this.clear = (ImageButton) view.findViewById(R.id.id_search_clear);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_2);
        editText = (EditText) rl.getChildAt(0);


        initCancel();
        initClear();
        initEditText();

        return view;
    }

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                FragmentTool.getFragmentToolInstance(getContext()).hideCur();
                toolbarOperation.getToolbar().setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void initEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initClear() {
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }

    public void setToolbarOperation(ToolbarOperation toolbarOperation) {
        this.toolbarOperation = toolbarOperation;
    }

    @Override
    public void onStart() {
        super.onStart();
        editText.requestFocus();
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        toolbarOperation.getToolbar().setVisibility(View.GONE);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            toolbarOperation.getToolbar().setVisibility(View.GONE);
            editText.requestFocus();
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }
}
