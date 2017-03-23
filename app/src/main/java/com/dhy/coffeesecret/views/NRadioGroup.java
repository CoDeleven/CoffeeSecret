package com.dhy.coffeesecret.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * Created by mxf on 2017/3/22.
 */
public class NRadioGroup extends RadioGroup {
    public NRadioGroup(Context context) {
        super(context);
    }

    public NRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = 0;
        int y;
        int row = 1;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                y = row * height;
                if (i == 3) {
                    row++;
                    x = width;
                    y = height * row;
                }
                child.layout(x - width, y - height, x, y);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int childCount = getChildCount();
        int y = 0;
        int row = 1;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                child.measure(layoutParams.width, layoutParams.height);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                y = height * row;
                if (i == 3) {
                    row++;
                    y = height * row;
                }
            }
            setMeasuredDimension(maxWidth, y);
        }
    }
}
