package com.freak.dashboard.mano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ManoBackView extends View {

    private Paint mManoPaint;
    private Paint mTextPaint;
    private double mCosMin;
    private double mSinMin;
    private double mCosMax;
    private double mSinMax;
    private double mCosMed1;
    private double mSinMed1;
    private double mCosMed2;
    private double mSinMed2;
    private int mValueMin;
    private int mValueMax;
    private int mValueMed1;
    private int mValueMed2;

    public ManoBackView(Context context) {
        super(context);
        init();
    }

    public ManoBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManoBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mManoPaint = new Paint();
        mManoPaint.setColor(Color.BLUE);
        mManoPaint.setStrokeWidth(5);
        mManoPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(24);
    }

    public void setColor(int color) {
        mManoPaint.setColor(color);
        mTextPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = this.getWidth()/2;
        int centerY = this.getHeight()/2;
        int radius = centerX -10;

        canvas.drawCircle(centerX, centerY, radius, mManoPaint);

        double minX1 = centerX - (radius * mCosMin);
        double minX2 = centerX - ((radius - 20) * mCosMin);
        double minY1 = centerY - (radius * mSinMin);
        double minY2 = centerY - ((radius - 20) * mSinMin);

        double maxX1 = centerX - (radius * mCosMax);
        double maxX2 = centerX - ((radius - 20) * mCosMax);
        double maxY1 = centerY - (radius * mSinMax);
        double maxY2 = centerY - ((radius - 20) * mSinMax);

        double med1X1 = centerX - (radius * mCosMed1);
        double med1X2 = centerX - ((radius - 20) * mCosMed1);
        double med1Y1 = centerY - (radius * mSinMed1);
        double med1Y2 = centerY - ((radius - 20) * mSinMed1);

        double med2X1 = centerX - (radius * mCosMed2);
        double med2X2 = centerX - ((radius - 20) * mCosMed2);
        double med2Y1 = centerY - (radius * mSinMed2);
        double med2Y2 = centerY - ((radius - 20) * mSinMed2);

        canvas.drawLine((float)minX1, (float)minY1, (float)minX2, (float)minY2, mManoPaint);
        canvas.drawLine((float)maxX1, (float)maxY1, (float)maxX2, (float)maxY2, mManoPaint);
        canvas.drawLine((float)med1X1, (float)med1Y1, (float)med1X2, (float)med1Y2, mManoPaint);
        canvas.drawLine((float)med2X1, (float)med2Y1, (float)med2X2, (float)med2Y2, mManoPaint);

        canvas.drawText("" + mValueMin, (float)minX2 + 5, (float)minY2 + 5, mTextPaint);
        canvas.drawText("" + mValueMed1, (float)med1X2 + 5, (float)med1Y2 + 10, mTextPaint);
        canvas.drawText("" + mValueMed2, (float)med2X2 - 20, (float)med2Y2 + 20, mTextPaint);
        canvas.drawText("" + mValueMax, (float)maxX2 - 25, (float)maxY2 + 20, mTextPaint);
    }


    public void setValues(int valueMin, int valueMax, int angleMin, int angleMax) {
        mValueMin = valueMin;
        mValueMax = valueMax;
        mValueMed1 = (valueMax - valueMin) / 3 + valueMin;
        mValueMed2 = (valueMax - valueMin) * 2 / 3 + valueMin;
        double angleMed1 = (((double)angleMax - (double)angleMin) / 3) + (double)angleMin;
        double angleMed2 = (((double)angleMax - (double)angleMin) * 2 / 3) + (double)angleMin;
        mCosMin = Math.cos(Math.toRadians(angleMin));
        mSinMin = Math.sin(Math.toRadians(angleMin));
        mCosMax = Math.cos(Math.toRadians(angleMax));
        mSinMax = Math.sin(Math.toRadians(angleMax));
        mCosMed1 = Math.cos(Math.toRadians(angleMed1));
        mSinMed1 = Math.sin(Math.toRadians(angleMed1));
        mCosMed2 = Math.cos(Math.toRadians(angleMed2));
        mSinMed2 = Math.sin(Math.toRadians(angleMed2));
    }
}
