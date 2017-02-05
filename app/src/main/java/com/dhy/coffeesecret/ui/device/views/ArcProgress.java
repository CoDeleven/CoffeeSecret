package com.dhy.coffeesecret.ui.device.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.UnitConvert;

/**
 * Created by CoDeleven on 17-1-19.
 */

public class ArcProgress extends View {
    private RectF rectF = new RectF();
    private Paint arcPaint = new Paint();
    private Paint textPaint = new Paint();
    private float strokeWidth;
    private float progress;
    private String accContent = "0";
    private float accContentSize;
    // arcProgress的标签
    private String label;
    private String content = "0";
    private String suffix;
    private float radius;
    private float bottomHeight;
    // 起始角度
    private float beginAngle;
    // 划过角度
    private float sweepAngle;
    private float finishBeginAngle;
    private float finishSweepAngle;
    private float contentSize;
    private float suffixSize;
    private float labelSize;
    private float maxProgress;

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initParam(attrs);

        initPaint();
    }

    public void initPaint() {
        // 初始化arcProgress图形画笔
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(strokeWidth);

        // 初始化arcProgress文字画笔
        textPaint.setColor(Color.BLACK);
    }

    /**
     * 初始化属性，包括标签内容，后缀等
     *
     * @param attrs
     */
    private void initParam(AttributeSet attrs) {
        // 参数初始化
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ArcProgress);
        label = typedArray.getString(R.styleable.ArcProgress_arcLabel);
        suffix = typedArray.getString(R.styleable.ArcProgress_suffix);
        beginAngle = typedArray.getInteger(R.styleable.ArcProgress_beginAngle, 120);
        sweepAngle = typedArray.getInteger(R.styleable.ArcProgress_sweepAngle, 300);
        finishBeginAngle = typedArray.getInteger(R.styleable.ArcProgress_finishedBeginAngle, 120);
        maxProgress = typedArray.getInteger(R.styleable.ArcProgress_maxValue, 350);

        Resources res = getResources();
        contentSize = UnitConvert.sp2px(res, 24);
        suffixSize = UnitConvert.sp2px(res, 10);
        labelSize = UnitConvert.sp2px(res, 12);
        strokeWidth = UnitConvert.dp2px(res, 3f);
        accContentSize = UnitConvert.sp2px(res, 16);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, width - strokeWidth / 2);
        radius = (width - strokeWidth) / 2;
        float angle = (360 - sweepAngle) / 2;
        bottomHeight = (float) Math.cos(angle / 180 * Math.PI) * radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        arcPaint.setColor(Color.BLUE);
        canvas.drawArc(rectF, beginAngle, sweepAngle, false, arcPaint);
        if (finishSweepAngle > 0) {
            arcPaint.setColor(Color.RED);
            canvas.drawArc(rectF, finishBeginAngle, finishSweepAngle, false, arcPaint);
        }


        // 绘制中间的进度内容
        textPaint.setTextSize(contentSize);
        float textWidth1 = textPaint.measureText(content);
        float height = radius * 2;
        float textHeight = textPaint.descent() + textPaint.ascent();
        float baseline = height / 2;
        canvas.drawText(content, height / 2 - textWidth1 / 2 + strokeWidth / 2, baseline, textPaint);

        // 绘制后缀
        textPaint.setTextSize(suffixSize);
        float textHeight2 = textPaint.descent() + textPaint.ascent();
        baseline = height / 2 + textHeight - textHeight2;
        canvas.drawText(suffix, height / 2 + textWidth1 / 2, baseline, textPaint);

        // 绘制加速度内容
        textPaint.setTextSize(accContentSize);
        float accTextWidth = textPaint.measureText(accContent);
        float accTextHeight = textPaint.descent() - textPaint.ascent();
        baseline = height / 2 + Math.abs(accTextHeight);
        canvas.drawText(accContent, height / 2 - accTextWidth / 2 + strokeWidth / 2, baseline, textPaint);


        // 绘制底部标签
        textPaint.setTextSize(labelSize);
        textWidth1 = textPaint.measureText(label);
        textHeight = textPaint.descent() + textPaint.ascent();
        baseline = height / 2 + bottomHeight - textHeight / 2;
        canvas.drawText(label, height / 2 - textWidth1 / 2 + strokeWidth / 2, baseline + strokeWidth / 2, textPaint);


    }

    /**
     * 设置当前数值，计算后重绘
     *
     * @param progress
     * @param acceleration
     */
    public void setProgress(float progress, float acceleration) {
        this.progress = progress;
        this.accContent = (acceleration > 0 ? "↑" : "↓") + String.format("%.2f", acceleration) + suffix + "/s";
        this.content = "" + progress;
        finishSweepAngle = (progress / maxProgress) * sweepAngle;
        invalidate();
    }


}
