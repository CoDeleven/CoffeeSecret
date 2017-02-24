package com.dhy.coffeesecret.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.utils.UnitConvert;

/**
 * Created by CoDeleven on 17-2-4.
 */

public class DownRefresh extends View {
    private float height;
    private float width;
    private String content = "上拉查看详情";

    public DownRefresh(Context context) {
        this(context, null);
    }

    public DownRefresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextSize(UnitConvert.sp2px(getResources(), 10.125f));
        paint.setColor(Color.parseColor("#7f7f7f"));
        float xBase = width / 2 - paint.measureText(content) / 2;
        canvas.drawText(content, xBase, (int) (height / 10 * 4.5), paint);
        float singleFontWidth = paint.measureText("哈");
        paint.setStrokeWidth(UnitConvert.dp2px(getResources(), 1.125f));
        canvas.drawLine(xBase + singleFontWidth, height / 10 * 6, xBase + paint.measureText(content) - singleFontWidth, height / 10 * 6, paint);
        canvas.drawLine(xBase + singleFontWidth * 2, height / 10 * 7, xBase + paint.measureText(content) - singleFontWidth * 2, height / 10 * 7, paint);
        canvas.drawLine(xBase + singleFontWidth * 2 + singleFontWidth / 2, height / 10 * 8, xBase + singleFontWidth * 2 + singleFontWidth / 2 + singleFontWidth, height / 10 * 8, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }
}
