package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.List;

/**
 * Created by CoDeleven on 17-2-8.
 */

public class FormView extends View {
    private List<BakeReport> reports;
    private Paint paint;
    private float width;
    private float height;
    // 假设默认为5
    private int heads = 5;
    private float paddingLeft = 0;
    private float gridWidth = 0;

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FormView(Context context) {
        this(context, null);
    }

    public FormView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setFormList(List<BakeReport> reports) {
        this.reports = reports;
    }

    private void init() {
        this.paddingLeft = getPaddingLeft();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setTextSize(ConvertUtils.spToPx(getResources(), 13));
/*        for(BakeReport report: reports){
            canvas.drawText();
        }*/
        float textWidth = paint.measureText("00:01");
        float curX = gridWidth / 2 - textWidth / 2 + paddingLeft;
        float textHeight = paint.descent() - paint.ascent();
 /*       canvas.drawRect(new RectF(paddingLeft, 0, gridWidth * 1, textHeight), paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(new RectF(gridWidth, 0, gridWidth * 2, textHeight), paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(new RectF(gridWidth * 2, 0, gridWidth * 3, textHeight), paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(new RectF(gridWidth * 3, 0, gridWidth * 4, textHeight), paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(new RectF(gridWidth * 4, 0, gridWidth * 5, textHeight), paint);*/

        canvas.drawText("00:01", paddingLeft, textHeight, paint);
        canvas.drawText("200℃", gridWidth * 1, textHeight, paint);
        canvas.drawText("352℃", gridWidth * 2, textHeight, paint);
        canvas.drawText("125℃", gridWidth * 3, textHeight, paint);
        canvas.drawText("20℃", gridWidth * 4, textHeight, paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.gridWidth = (width - getPaddingLeft() - getPaddingRight()) / 5;
        this.height = MeasureSpec.getSize(heightMeasureSpec);
    }
}
