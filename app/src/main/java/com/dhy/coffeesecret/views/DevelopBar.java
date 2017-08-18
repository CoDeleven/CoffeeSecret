package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.model.bake.developbar.IDevelopBarView;

/**
 * Created by CoDeleven on 17-1-21.
 */

public class DevelopBar extends View implements IDevelopBarView{
    public static final int RAW_BEAN = 1, AFTER160 = 2, FIRST_BURST = 3;
    private float greenLength;
    private float yellowLength;
    private float orangeLength;
    private int width;
    private int height;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DevelopBar.this.invalidate();

        }
    };
    @Override
    public void updateText(int index, Object updateContent) {

    }

    @Override
    public void showToast(int index, String toastContent) {

    }

    @Override
    public void showDialog(int index) {

    }

    public DevelopBar(Context context) {
        this(context, null);
    }

    public DevelopBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DevelopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Bitmap bitmap1 = drawBarRect();
        Bitmap bitmap2 = drawRoundRect();

        int sc = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(bitmap1, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap2, 0, 0, paint);

        paint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    public Bitmap drawRoundRect() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawRoundRect(new RectF(0, 0, width, height), 15, 15, paint);
        return bitmap;
    }

    public Bitmap drawBarRect() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // calculate();
        Paint paint = new Paint();

        RectF rectF1 = new RectF();
        rectF1.set(0, 0, greenLength, height);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectF1, paint);

        RectF rectF2 = new RectF();
        rectF2.set(greenLength, 0, yellowLength + greenLength, height);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rectF2, paint);

        RectF rectF3 = new RectF();
        rectF3.set(greenLength + yellowLength, 0, greenLength + yellowLength + orangeLength, height);
        paint.setColor(Color.rgb(250, 128, 10));
        canvas.drawRect(rectF3, paint);

        return bitmap;
    }

/*    public void calculate() {
        switch (curStatus) {
            case RAW_BEAN:
                rawBeanTime++;
                break;
            case AFTER160:
                after160Time++;
                break;
            case FIRST_BURST:
                firstBurstTime++;
                break;
        }

    }*/

    @Override
    public void updateDevelopBar(float rawLen, float after160Len, float firstLen) {
        // 绿条
        greenLength = rawLen * width;
        // 黄条
        yellowLength = after160Len * width;
        // 橙条
        orangeLength = firstLen * width;
        mHandler.sendEmptyMessage(0);
    }


}
