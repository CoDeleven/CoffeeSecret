package com.dhy.coffeesecret.ui.common.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.utils.Utils;
import com.facebook.rebound.ui.Util;

/**
 * Created by CoDeleven on 17-8-19.
 */

public class BakeDegreeShowBar extends View {
    private static final String[] tags = {"Green", "Light", "Cinnamon", "Medium", "High", "city", "Full"};
    public static int[] colors = {Color.parseColor("#A59C7F"), Color.parseColor("#B59379"), Color.parseColor("#886F58"), Color.parseColor("#7C6550"), Color.parseColor("#69594A"), Color.parseColor("#635548"), Color.parseColor("#695C4D"), Color.parseColor("#3F4138")};
    public static float[] positions = {0, 1 / 7f, 2 / 7f, 3 / 7f, 4 / 7f, 5 / 7f, 6 / 7f, 7 / 7f};
    private final int space = 40;
    private final int min = 30;
    private final int max = 80;
    private int width;
    private int height;
    private int barHeight;
    private int barWidth;
    private int curProcess = 0;
    private int bottomTitleMarginTop = 10;
    private float contentWidth;

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
        contentWidth = width - space * 2;
    }

    public void setCurProcess(int process) {
        this.curProcess = process;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Canvas canvas = new Canvas(bitmap);
        barHeight = height * 1 / 3;
        barWidth = width - space;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.dpToPx(10, getResources()));
        LinearGradient sweepGradient = new LinearGradient(0, 0, barWidth + 5, barHeight, colors, positions, Shader.TileMode.REPEAT);
        for (int i = 0; i < 7; ++i) {
            generateText(i, tags[i], paint, canvas);
        }
        // float bottom = measureTextHeight(paint) + barHeight + 10;
        paint.setShader(sweepGradient);
        canvas.drawRect(space, height - measureTextHeight(paint) - barHeight, width - space, height - 5 - measureTextHeight(paint) - bottomTitleMarginTop, paint);
        drawSeparator(canvas, paint);
        drawWater(canvas, paint);
    }

    private float measureTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }

    private float measureTextHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.leading - metrics.ascent + metrics.descent;
    }

    private void generateText(int index, String text, Paint paint, Canvas canvas) {
        float pointCenterPercent = index / 7.0f;
        float textWidth = measureTextWidth(text, paint);
        float drawTextPointer = pointCenterPercent * barWidth - textWidth / 2;
        drawTextPointer = drawTextPointer < 0 ? 0 : drawTextPointer;
        // 从最底部开始绘制
        canvas.drawText(text, drawTextPointer, height - 5, paint);
        canvas.drawLine(1 + drawTextPointer + textWidth / 2, height - measureTextHeight(paint) - bottomTitleMarginTop - 20, drawTextPointer + textWidth / 2, height - 6 - measureTextHeight(paint), paint);
    }

    private void drawSeparator(Canvas canvas, Paint paint) {

        float separatorStartPointer = (curProcess / (float) (max - min)) * barWidth + space;
        canvas.drawLine(separatorStartPointer, 0, separatorStartPointer, measureTextHeight(paint), paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(separatorStartPointer, measureTextHeight(paint), separatorStartPointer, barHeight - 20, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Util.dpToPx(10, getResources()));
    }

    private int computeToastValue(double angle) {
        int index = ((int) angle) / 45;
        switch (index) {
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return 80;
            case 2:
                return (-(int) Math.floor(angle % 45 / 45f * 10)) + 70;
            case 3:
                return (-(int) Math.floor(angle % 45 / 45f * 5)) + 55;
            case 4:
                return (-(int) Math.floor(angle % 45 / 45f * 5)) + 50;
            case 5:
                return (-(int) Math.floor(angle % 45 / 45f * 5)) + 45;
            case 6:
                return (-(int) Math.floor(angle % 45 / 45f * 5)) + 40;
            case 7:
                return (-(int) Math.floor(angle % 45 / 45f * 5)) + 35;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private void drawWater(Canvas canvas, Paint paint) {

        int radius = Utils.dip2px(getResources(), 10);
        int waterHeight = 3 * radius;
        int waterWidth = waterHeight;
        int offsetX = (int) (curProcess * contentWidth / (max - min));
        RectF rectF = new RectF();
        rectF.set(offsetX + waterWidth / 2f - radius, 0, offsetX + waterWidth / 2f + radius, 2 * radius);

        Path path = new Path();
        float x0 = offsetX + waterWidth / 2;
        float y0 = waterHeight - radius / 3f;
        path.moveTo(x0, y0);
        float x1 = offsetX + (float) (waterWidth / 2f - Math.sqrt(3) / 2f * radius);
        float y1 = 3 / 2f * radius;
        path.quadTo(
                x1 - Utils.dip2px(getResources(), 2), y1 - Utils.dip2px(getResources(), 2),
                x1, y1
        );
        path.arcTo(rectF, 150, 240);
        float x2 = offsetX + (float) (waterWidth / 2f + Math.sqrt(3) / 2f * radius);
        path.quadTo(
                x2 + Utils.dip2px(getResources(), 2), y1 - Utils.dip2px(getResources(), 2),
                x0, y0
        );
        path.close();
        paint.setColor(Color.BLUE);
        canvas.drawPath(path, paint);

        Rect rect = new Rect();
        paint.getTextBounds(curProcess + "", 0, (curProcess + "").length(), rect);

        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseline = radius + (fm.descent - fm.ascent) / 2f - fm.descent;

        paint.setShader(null);
        paint.setColor(Color.WHITE);

        int toastValue = computeToastValue(curProcess / 50f * 360);

        canvas.drawText((toastValue == Integer.MAX_VALUE ? "N/A" : (toastValue + "")), offsetX + waterWidth / 2 - radius / 2, baseline, paint);
    }

}
