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
    private View mView;

    private Button mSaveButton;
    private Toolbar mToolbar;
    private View.OnClickListener onClickListener;
    private String mTitle;


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
        if (mTitle != null) {
            TextView tv = (TextView) mView.findViewById(R.id.tv);
            tv.setText(mTitle);
        }
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
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
        // TODO: Update argument type and name
        void onSave();
    }


}
