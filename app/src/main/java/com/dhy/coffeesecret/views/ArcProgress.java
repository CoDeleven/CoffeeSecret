package com.dhy.coffeesecret.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.UnitConvert;

/**
 * Created by CoDeleven on 17-1-19.
 */

public class ArcProgress extends View {
    private Paint arcPaint = new Paint();
    private Paint textPaint = new Paint();
    private float strokeWidth;
    private String suffix;
    private float width;
    private float height;
    private float mFisrtArcRadius;
    private float mSecondArcRadius;
    private Resources res;
    private float mRotateAngle;
    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        res = getResources();
        initParam(attrs);
    }

    public void initPaint() {
        // 初始化arcProgress图形画笔
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(strokeWidth);

        // 初始化arcProgress文字画笔
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(UnitConvert.sp2px(res, 20));
    }

    /**
     * 初始化属性，包括标签内容，后缀等
     *
     * @param attrs
     */
    private void initParam(AttributeSet attrs) {

        // 参数初始化
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ArcProgress);
        suffix = typedArray.getString(R.styleable.ArcProgress_suffix);
        mFisrtArcRadius = typedArray.getDimension(R.styleable.ArcProgress_firstCircleRadius, 0);
        mSecondArcRadius = typedArray.getDimension(R.styleable.ArcProgress_secondCircleRadius, 0);
        mRotateAngle = typedArray.getFloat(R.styleable.ArcProgress_rotateAngle, 0);

        strokeWidth = UnitConvert.dp2px(res, 3f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 初始化画笔
        initPaint();
        drawCircle(canvas);

/*        // 绘制中间的进度内容
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
        canvas.drawText(accContent, height / 2 - accTextWidth / 2 + strokeWidth / 2, baseline, textPaint);*/


    }

    private void drawCircle(final Canvas canvas) {
        canvas.rotate(mRotateAngle, width / 2, height / 2);
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, new int[]{Color.parseColor("#635b42"), Color.parseColor("#c3b9b4")}, null);
        arcPaint.setShader(sweepGradient);
        canvas.drawArc(new RectF(0, 0, width, height), 0, 360, false, arcPaint);
        canvas.rotate(-mRotateAngle, width / 2, height / 2);

        // 取消shader
        arcPaint.setShader(null);
        // 绘制第二个圈
        arcPaint.setColor(Color.BLACK);
        canvas.drawOval(new RectF(mFisrtArcRadius, mFisrtArcRadius, width - mFisrtArcRadius, height - mFisrtArcRadius), arcPaint);
        // 绘制最内圈
        arcPaint.setColor(Color.WHITE);
        canvas.drawOval(new RectF(mFisrtArcRadius + mSecondArcRadius, mFisrtArcRadius + mSecondArcRadius, width - mFisrtArcRadius - mSecondArcRadius, height - mFisrtArcRadius - mSecondArcRadius), arcPaint);
        invalidate();
    }

}
