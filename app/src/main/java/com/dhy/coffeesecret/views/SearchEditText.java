package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by CoDeleven on 17-2-6.
 */

public class SearchEditText extends EditText {
    /**
     * 是否显示在左边
     */
    private boolean isShowNormal = false;
    private SearchBarListener searchBarListener;
    private Drawable[] drawables; // 控件的图片资源
    private Drawable drawableLeft, drawableDel; // 搜索图标和删除按钮图标

    public SearchEditText(Context context) {
        this(context, null);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        drawables = getCompoundDrawables();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isShowNormal) { // 如果是默认样式，直接绘制
            if (length() < 1) {
                drawableDel = null;
            }
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null);
            super.onDraw(canvas);
        } else { // 如果不是默认样式，需要将图标绘制在中间
            drawableLeft = drawables[0];
            float textWidth = getPaint().measureText(getHint().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        searchBarListener.startSearchPage();
        return false;
    }


    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                              int arg3) {
    }

    public void setSearchBarListener(SearchBarListener searchBarListener) {
        this.searchBarListener = searchBarListener;
    }

    public interface SearchBarListener {
        void startSearchPage();
    }
}