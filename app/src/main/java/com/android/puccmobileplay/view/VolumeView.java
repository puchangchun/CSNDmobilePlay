package com.android.puccmobileplay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.puccmobileplay.R;

import com.android.puccmobileplay.Util.Utils;

/**
 * Created by 长春 on 2017/10/2.
 */

public class VolumeView extends View {
    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;

    private Paint mPaint, paint;

    private int mCurrentVolume;
    private int mMaxVolume;
    private int viewHeight;
    private int viewWidth;

    public VolumeView(Context context) {
        this(context, null);
    }

    public VolumeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VolumeView, defStyleAttr, 0);
/*        int n=a.getIndexCount();
        for(int i=0;i<n;i++){
            int attr=a.getIndex(i);
            switch(attr)
            {
                case R.styleable.CustomView_firstColor:
                    mFirstColor=a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomView_secondColor:
                    mSecondColor=a.getColor(attr, Color.GREEN);
                    break;\

                case R.styleable.CustomView_circleWidth:
                    mCircleWidth=a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics()));
                    break;
            }
        }*/
        mMaxVolume = a.getInteger(R.styleable.VolumeView_maxVolume, 15);
        mFirstColor = a.getColor(R.styleable.VolumeView_firstColor, Color.argb(0, 0, 0, 0));
        mSecondColor = a.getColor(R.styleable.VolumeView_secondColor, Color.GRAY);
        mCircleWidth = (int) a.getDimension(R.styleable.VolumeView_circleWidth, Utils.dip2px(getContext(), 16));
        a.recycle();

        mPaint = new Paint();
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        viewWidth = getWidth();
        viewHeight = getHeight();
        int centreX = getWidth() / 2;
        int centreY = getHeight() / 2;
        int radius = centreX - mCircleWidth / 2;//半径
        int imgLength = radius;//绘制图标的外切正方形的边长
        mPaint.setStrokeWidth(mCircleWidth);//设置线宽
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);//设置为空心
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置为线段形状为圆头
        //设定一个圆弧的边界矩形
        RectF oval = new RectF(centreX - radius, centreY - radius, centreX + radius, centreY + radius);
        mPaint.setColor(mFirstColor);
        int var1 = 360 / mMaxVolume;
        int var2 = var1  / 2;
        for (int i = 0; i < mMaxVolume; i++) {
            canvas.drawArc(oval, (270 + var1 * i) % 360, var2, false, mPaint);
        }
        if (mCurrentVolume < 0) mCurrentVolume = 0;
        mPaint.setColor(mSecondColor);
        for (int k = 0; k < mCurrentVolume; k++) {
            canvas.drawArc(oval, (90 + var1 * k) % 360, var2, false, mPaint);
        }

        paint.setColor(mSecondColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);//设置线宽
        paint.setStrokeCap(Paint.Cap.ROUND);//设置为线段形状为圆头
        canvas.drawLine(centreX - imgLength / 2, centreY - imgLength / 6, centreX - imgLength / 2, centreY + imgLength / 6, paint);
        canvas.drawLine(centreX - imgLength / 2, centreY - imgLength / 6, centreX - imgLength / 4, centreY - imgLength / 6, paint);
        canvas.drawLine(centreX - imgLength / 2, centreY + imgLength / 6, centreX - imgLength / 4, centreY + imgLength / 6, paint);

        canvas.drawLine(centreX - imgLength / 4, centreY - imgLength / 6, centreX, centreY - imgLength / 2, paint);
        canvas.drawLine(centreX, centreY - imgLength / 2, centreX, centreY + imgLength / 2, paint);
        canvas.drawLine(centreX - imgLength / 4, centreY + imgLength / 6, centreX, centreY + imgLength / 2, paint);

        paint.setStyle(Paint.Style.STROKE);//设置为空心
        RectF f1 = new RectF(centreX - imgLength / 2, centreY - imgLength / 6, centreX + imgLength / 6, centreY + imgLength / 6);
        RectF f2 = new RectF(centreX - imgLength / 2, centreY - imgLength * 2 / 6, centreX + imgLength * 2 / 6, centreY + imgLength * 2 / 6);
        RectF f3 = new RectF(centreX - imgLength / 2, centreY - imgLength * 3 / 6, centreX + imgLength * 3 / 6, centreY + imgLength * 3 / 6);
/*        canvas.drawArc(f1,-(int)(Math.atan2(imgLength/6,imgLength*2/6)*180/Math.PI),2*(int)(Math.atan2(imgLength/6,imgLength*2/6)*180/Math.PI),false,paint);
        canvas.drawArc(f1,-(int)(Math.atan2(imgLength*2/6,imgLength*3/6)*180/Math.PI),2*(int)(Math.atan2(imgLength*2/6,imgLength*3/6)*180/Math.PI),false,paint);
        canvas.drawArc(f1,-(int)(Math.atan2(imgLength*3/6,imgLength*4/6)*180/Math.PI),2*(int)(Math.atan2(imgLength*3/6,imgLength*4/6)*180/Math.PI),false,paint);*/
        canvas.drawArc(f1, -(int) (Math.atan2(1, 2) * 180 / Math.PI), 2 * (int) (Math.atan2(1, 2) * 180 / Math.PI), false, paint);
        canvas.drawArc(f2, -(int) (Math.atan2(2, 3) * 180 / Math.PI), 2 * (int) (Math.atan2(2, 3) * 180 / Math.PI), false, paint);
        canvas.drawArc(f3, -(int) (Math.atan2(3, 4) * 180 / Math.PI), 2 * (int) (Math.atan2(3, 4) * 180 / Math.PI), false, paint);
    }
    public void setCurrentVolume(int current) {
        mCurrentVolume = current;
        postInvalidate();
    }

    public void setMaxVolume(int maxVolume) {
        mMaxVolume = maxVolume;
    }
}