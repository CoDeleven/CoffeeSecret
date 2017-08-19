package com.dhy.coffeesecret.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.rebound.ui.Util;

/**
 * Created by CoDeleven on 17-8-19.
 */

public class BakeDegreeShowBar extends View {
    public static int[] colors = {Color.parseColor("#A59C7F"), Color.parseColor("#B59379"), Color.parseColor("#886F58"), Color.parseColor("#7C6550"), Color.parseColor("#69594A"), Color.parseColor("#635548"), Color.parseColor("#695C4D"), Color.parseColor("#3F4138")};
    private static final String[] tags = {"Green", "Cinnamon", "High City", "Full City"};
    public static float[] positions = {0, 1 / 7f, 2 / 7f, 3 / 7f, 4 / 7f, 5 / 7f, 6 / 7f, 7 / 7f};
    private int width;
    private int height;
    private int barHeight;
    private final int space = 40;
    private int barWidth;
    private final int min = 30;
    private final int max = 70;
    private int curProcess = 0;
    public BakeDegreeShowBar(Context context) {
        super(context);
    }

    public BakeDegreeShowBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BakeDegreeShowBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = this.getMeasuredWidth();
        height = this.getMeasuredHeight();
    }

    public void setCurProcess(int process){
        this.curProcess = process;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Canvas canvas = new Canvas(bitmap);
        barHeight = height * 1 / 2;
        barWidth = width - space;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.dpToPx(14, getResources()));
        LinearGradient sweepGradient = new LinearGradient(0, 0, barWidth, barHeight, colors, positions, Shader.TileMode.REPEAT);
        for(int i = 0; i < 7; i += 2){
            generateText(i, tags[i / 2], paint, canvas);
        }

        paint.setShader(sweepGradient);
        canvas.drawRect(space, measureTextHeight(paint), width - space, measureTextHeight(paint)  + barHeight - 40, paint);
        drawSeparator(canvas, paint);
    }

    private float measureTextWidth(String text, Paint paint){
        return paint.measureText(text);
    }
    private float measureTextHeight(Paint paint){
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.leading - metrics.ascent + metrics.descent;
    }
    private void generateText(int index, String text, Paint paint, Canvas canvas){
        float pointCenterPercent = index / 7.0f;
        float textWidth = measureTextWidth(text, paint);
        float drawTextPointer = pointCenterPercent *  barWidth - textWidth / 2;
        canvas.drawText(text, drawTextPointer < 0 ? 0 :drawTextPointer, measureTextHeight(paint) + 20 + barHeight, paint);
    }

    private void drawSeparator(Canvas canvas, Paint paint){
        float separatorStartPointer = ((curProcess - min) / (float)(max - min)) * barWidth + space;
        canvas.drawLine(separatorStartPointer, 0, separatorStartPointer, measureTextHeight(paint), paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(separatorStartPointer, measureTextHeight(paint), separatorStartPointer, barHeight, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.dpToPx(10, getResources()));
        canvas.drawText("烘焙度:" + curProcess, separatorStartPointer + 20, 30, paint);
    }
}
