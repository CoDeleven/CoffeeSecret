package com.dhy.coffeesecret.ui.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

/**
 * Created by CoDeleven on 17-4-17.
 */

public class ReSpinner extends AppCompatSpinner {
    public ReSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    public ReSpinner(Context context) {
        super(context);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        boolean sameSelected = position == getSelectedItemPosition();
        if(sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        boolean sameSelected = position == getSelectedItemPosition();
        if(sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
