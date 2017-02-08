package com.dhy.coffeesecret.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by CoDeleven on 17-2-4.
 */

public class DownRefresh extends View {
    private float height;
    private float width;
    private String content = "上拉查看详情";
    private float marginLeft;

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
        paint.setTextSize(24);
        paint.setStrokeWidth(1);
        paint.setColor(Color.parseColor("#6D7C83"));
        canvas.drawLine(0, height / 2, (width / 2) - (paint.measureText(content) / 2) - 16, height / 2, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(content, (width / 2) - (paint.measureText(content) / 2), height / 2 + paint.getFontMetrics().descent, paint);
        canvas.drawLine((width / 2) + (paint.measureText(content) / 2) + 16, height / 2, width, height / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }
}
