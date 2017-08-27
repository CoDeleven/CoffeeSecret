package com.dhy.coffeesecret.ui.common.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;

import com.dhy.coffeesecret.utils.ImageHandler;

/**
 * Created by CoDeleven on 17-2-26.
 */

public class BakeDegreeCircleSeekBar extends CircleSeekBar {
    public static int[] colors = {Color.parseColor("#3F4138"), Color.parseColor("#695C4D"), Color.parseColor("#635548"),
            Color.parseColor("#69594A"), Color.parseColor("#7C6550"), Color.parseColor("#886F58"),
            Color.parseColor("#B59379"), Color.parseColor("#A59C7F")};
    public static float[] positions = {0, 1 / 7f, 2 / 7f, 3 / 7f, 4/ 7f, 5/7f, 6/7f, 7/7f};
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


        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, positions);

        Paint paint = new Paint();
        paint.setShader(sweepGradient);
        canvas.drawRect(rectF, paint);
        bitmap = ImageHandler.rotateBitmap(bitmap, -90);
        bitmap = ImageHandler.reverseBitmap(bitmap, 0);
        return bitmap;
    }
}
