package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditToolBar.OnSaveListener} interface
 * to handle interaction events.
 */
public class EditToolBar extends Fragment {

    private OnSaveListener mListener;
    private OnTitleClickListener mTitleClickListener;
    private View mView;

    private Button mSaveButton;
    private Button mEditButton;
    private Toolbar mToolbar;
    private View.OnClickListener onClickListener;
    private String mTitle;
    private TextView mTextView;



    public EditToolBar() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToolbar.setNavigationOnClickListener(onClickListener);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });
    }


    public void setNavigationOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_edit_tool_bar, container, false);
        mSaveButton = (Button) mView.findViewById(R.id.btn_save);
        mToolbar = (Toolbar) mView.findViewById(R.id.toolBar);
        mTextView = (TextView) mView.findViewById(R.id.tv);
        mEditButton = (Button) mView.findViewById(R.id.btn_edit);
        if (mTitle != null) {
            mTextView.setText(mTitle);
        }
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitleClickListener != null){
                    mTitleClickListener.onTitleClick();
                }
            }
        });
        return mView;
    }

    public void onSave() {
        if (mListener != null) {
            mListener.onSave();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveListener) {
            mListener = (OnSaveListener) context;
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

    public void setTitle(String title) {
        this.mTitle = title;
        if(mTextView != null){
            mTextView.setText(title);
        }
    }

    public void setTitleClickListener(OnTitleClickListener titleClickListener) {
        this.mTitleClickListener = titleClickListener;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSaveListener {
        void onSave();
    }

    public interface OnTitleClickListener {
        void onTitleClick();
    }


}
