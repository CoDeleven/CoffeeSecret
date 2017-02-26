package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;

import com.dhy.coffeesecret.utils.ImageHandler;

/**
 * Created by CoDeleven on 17-2-26.
 */

public class BakeDegreeCircleSeekBar extends CircleSeekBar {
    public BakeDegreeCircleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BakeDegreeCircleSeekBar(Context context) {
        super(context);
    }

    public BakeDegreeCircleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Bitmap generateGradientBg(RectF rectF){
        int width = (int)rectF.right - (int)rectF.left;
        int height = (int)rectF.bottom - (int)rectF.top;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int[] colors = {Color.parseColor("#282828"), Color.parseColor("#3b3e36"),
                Color.parseColor("#3f4138"), Color.parseColor("#695c4d"), Color.parseColor("#635548"),
                Color.parseColor("#69594a"), Color.parseColor("#7c6550"), Color.parseColor("#b59379"),
                Color.parseColor("#a59c7f")};
        float[] positions = {0, 0.12f, 0.23f, 0.36f, 0.49f, 0.62f, 0.74f, 0.88f, 1f};
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, positions);
        Paint paint = new Paint();
        paint.setShader(sweepGradient);
        canvas.drawRect(rectF, paint);
        bitmap = ImageHandler.rotateBitmap(bitmap, -90);
        bitmap = ImageHandler.reverseBitmap(bitmap, 0);
        return bitmap;
    }
}
