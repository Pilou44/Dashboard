package com.freak.dashboard.mano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.freak.dashboard.GetInfoThread;
import com.freak.dashboard.bus.RPMEvent;

import org.greenrobot.eventbus.Subscribe;

public class ManoHandView extends View {

    private static final String TAG = ManoHandView.class.getSimpleName();
    private static final boolean DEBUG = true;
    private Paint   mHandPaint;
    private float   mAngleMax;
    private float   mAngleMin;
    private int     mValueMax;
    private float   mPreviousAngle;
    private long    mPeriod;
    private Handler handler;

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

        mHandPaint = new Paint();
        mHandPaint.setColor(Color.RED);
        mHandPaint.setStrokeWidth(3);
        mHandPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        handler = new Handler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;

        canvas.drawLine(10, centerY, centerX + 20, centerY, mHandPaint);
        canvas.drawCircle(centerX, centerY, 5, mHandPaint);
    }

    private void createAnimation(int centerX, int centerY, float newAngle) {
        if (DEBUG)
            Log.i(TAG, "Animate from " + mPreviousAngle + "° to " + newAngle + "°");
        Animation anim = new RotateAnimation(mPreviousAngle, newAngle, centerX, centerY);
        anim.setRepeatCount(0);
        anim.setDuration(mPeriod);
        anim.restrictDuration(GetInfoThread.PERIOD);
        anim.setFillAfter(true);
        mPreviousAngle = newAngle;
        startAnimation(anim);
    }

    public void setValues(long period, int valueMax, int angleMin, int angleMax) {
        mPeriod = period;
        mValueMax = valueMax;
        mAngleMin = angleMin;
        mAngleMax = angleMax;
    }

    public void setColor(int color) {
        mHandPaint.setColor(color);
    }

    @Subscribe
    public void onRPMEvent(final RPMEvent event) {
        handler.post(new Runnable() {
            public void run() {
                float offset = 0 - mAngleMin;
                int centerX = ManoHandView.this.getWidth()/2;
                int centerY = ManoHandView.this.getHeight()/2;

                float newAngle = ((float)event.getRpm()) * (mAngleMax + offset) / ((float)mValueMax) - offset;

                if (newAngle != mPreviousAngle) {
                    //if (DEBUG)
                    //    Log.i(TAG, "New value = " + event.getRpm() + ", new angle =  " + newAngle);

                    createAnimation(centerX, centerY, newAngle);
                }
            }
        });
    }
}
