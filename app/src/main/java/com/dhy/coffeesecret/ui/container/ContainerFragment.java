package com.dhy.coffeesecret.ui.container;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.dhy.coffeesecret.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ContainerFragment extends Fragment {

    private OnContainerInteractionListener mListener;

    private View mContent;
    private Button mSortButton;
    private Button mScreenButton;
    private PopupWindow mSortPopupWindow;
    private ExpandableLayoutListView mListView;
    private View shade;

    private boolean isSortWindowShowing = false;

    public ContainerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContent = inflater.inflate(R.layout.fragment_container, container, false);

        initView();
        initPopupWindow();
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 20;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View inflate = inflater.inflate(R.layout.bean_item, null);
                return inflate;
            }
        });

        return mContent;
    }


    public void initView(){
        mListView = (ExpandableLayoutListView) mContent.findViewById(R.id.lv_beans);
        mScreenButton = (Button) mContent.findViewById(R.id.btn_screen);
        mSortButton = (Button) mContent.findViewById(R.id.btn_sort);
        shade = mContent.findViewById(R.id.shade);
    }

    public void initPopupWindow(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final View contentView = inflater.inflate(R.layout.ppw_conta_screen,null);

        mSortPopupWindow = new PopupWindow(contentView, width*2/3, WRAP_CONTENT);
        mSortPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mSortPopupWindow.setOutsideTouchable(true);
        mSortPopupWindow.setFocusable(true);
        mScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSortWindowShowing){
                    mSortPopupWindow.dismiss();
                }else {
                    mSortPopupWindow.showAsDropDown(mScreenButton);
                    shade.setVisibility(View.VISIBLE);
                }
                isSortWindowShowing = !isSortWindowShowing;
            }
        });

        mSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                shade.setVisibility(View.GONE);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onContainerInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContainerInteractionListener) {
            mListener = (OnContainerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContainerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnContainerInteractionListener {
        void onContainerInteraction(Uri uri);
    }
}
