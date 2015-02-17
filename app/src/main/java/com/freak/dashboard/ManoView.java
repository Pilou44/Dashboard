package com.freak.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ManoView extends View {

    private Paint mNeedlePaint;
    private Paint mManoPaint;
    private int mAngleMax;
    private int mAngleMin;
    private int mValueMax;
    private int mValueMin;
    private int mAngle;

    public ManoView(Context context) {
        super(context);
        init();
    }

    public ManoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mValueMin = 0;
        mValueMax = 100;
        mAngleMin = 0;
        mAngleMax = 360;
        mAngle = mAngleMin;

        mNeedlePaint = new Paint();
        mNeedlePaint.setColor(Color.RED);
        mNeedlePaint.setStrokeWidth(3);
        mNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mManoPaint = new Paint();
        mManoPaint.setColor(Color.BLUE);
        mManoPaint.setStrokeWidth(3);
        mManoPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = this.getWidth()/2;
        int centerY = this.getHeight()/2;

        canvas.drawCircle(centerX, centerY, centerX - 3, mManoPaint);

        canvas.save();

        canvas.rotate(mAngle, centerX, centerY);

        canvas.drawLine(3, centerY, centerX + 20, centerY, mNeedlePaint);
        canvas.drawCircle(centerX, centerY, 5, mNeedlePaint);

        canvas.restore();
    }

    public void setValues(int valueMin, int valueMax, int angleMin, int angleMax) {
        mValueMin = valueMin;
        mValueMax = valueMax;
        mAngleMin = angleMin;
        mAngleMax = angleMax;
        mAngle = mAngleMin;
    }

    public void setValue(int value) {
        if (value >= mValueMin && value <= mValueMax) {
            int offset = 0 - mAngleMin;
            mAngle = ((value * (mAngleMax + offset)) / mValueMax) - offset;
        }
    }

    public void setNeedleColor(int color) {
        mNeedlePaint.setColor(color);
    }

    public void setManoColor(int color) {
        mManoPaint.setColor(color);
    }
}
