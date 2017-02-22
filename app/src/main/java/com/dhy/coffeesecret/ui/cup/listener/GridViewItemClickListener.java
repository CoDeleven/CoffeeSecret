package com.dhy.coffeesecret.ui.cup.listener;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;

import com.dhy.coffeesecret.ui.cup.fragment.InputDialogFragment;

/**
 * Created by mxf on 2017/2/20.
 */
public class GridViewItemClickListener implements AdapterView.OnItemClickListener {

    private String mGridView;
    private InputDialogFragment mFragment;
    private FragmentManager mManager;

    public static final String FEEL_GRID = "FEEL_GRID";
    public static final String FLAW_GRID = "FLAW_GRID";


    public GridViewItemClickListener(FragmentManager manager, InputDialogFragment fragment, String gridView ) {
        this.mManager = manager;
        this.mFragment = fragment;
        this.mGridView = gridView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int currentItem = 0;
        if (mGridView == FEEL_GRID) {
            currentItem = i;
        }else {
            currentItem = i + 8;
        }
        if (mFragment.getDialog() == null) {
            mFragment.show(currentItem,mManager,"");
        }
    }
}
