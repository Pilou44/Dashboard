package com.freak.dashboard.mano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class ManoHandView extends View {

    private static final String TAG = ManoHandView.class.getSimpleName();
    private static final boolean DEBUG = false;
    private Paint mHandPaint;
    private int mAngleMax;
    private int mAngleMin;
    private int mValueMax;
    private int mPreviousAngle;
    private int mNewAngle;
    private long mPeriod;

    public ManoHandView(Context context) {
        super(context);
        init();
    }

    public ManoHandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManoHandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mValueMax = 100;
        mAngleMin = 0;
        mAngleMax = 360;
        mPreviousAngle = 0;
        mNewAngle = 0;

        mHandPaint = new Paint();
        mHandPaint.setColor(Color.RED);
        mHandPaint.setStrokeWidth(3);
        mHandPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = this.getWidth()/2;
        int centerY = this.getHeight()/2;

        canvas.drawLine(3, centerY, centerX + 20, centerY, mHandPaint);
        canvas.drawCircle(centerX, centerY, 5, mHandPaint);
    }

    private void createAnimation(int centerX, int centerY) {
        Animation anim = new RotateAnimation(mPreviousAngle, mNewAngle, centerX, centerY);
        anim.setRepeatCount(0);
        anim.setDuration(mPeriod);
        startAnimation(anim);
        if (DEBUG)
            Log.i(TAG, "Animate from " + mPreviousAngle + "° to " + mNewAngle + "°");
        mPreviousAngle = mNewAngle;
    }

    public void setValues(long period, int valueMax, int angleMin, int angleMax) {
        mPeriod = period;
        mValueMax = valueMax;
        mAngleMin = angleMin;
        mAngleMax = angleMax;
        mPreviousAngle = 0;
    }

    public void setValue(int value) {
        int offset = 0 - mAngleMin;
        int centerX = this.getWidth()/2;
        int centerY = this.getHeight()/2;

        mNewAngle = ((value * (mAngleMax + offset)) / mValueMax) - offset;

        if (DEBUG)
            Log.i(TAG, "New value = " + value + ", new angle =  " + mNewAngle);

        createAnimation(centerX, centerY);
    }

    public void setColor(int color) {
        mHandPaint.setColor(color);
    }

}
