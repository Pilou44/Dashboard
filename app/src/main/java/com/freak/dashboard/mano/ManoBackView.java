package com.freak.dashboard.mano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Vector;

public class ManoBackView extends View {

    private Paint mManoPaint;
    private Paint mTextPaint;
    private Paint mRedZonePaint;
    private Vector<ManoPosition> positions;
    private int mAngleMax;

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

        mRedZonePaint = new Paint();
        mRedZonePaint.setColor(Color.RED);
        mRedZonePaint.setStrokeWidth(10);
        mRedZonePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(26);

        positions = new Vector<>();
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

        float margin = (this.getWidth() - (radius * 2)) / 2 + mManoPaint.getStrokeWidth() / 2 + mRedZonePaint.getStrokeWidth() / 2;
        RectF rect = new RectF(margin, margin, this.getWidth() - margin, this.getHeight() - margin);
        canvas.drawArc(rect, 180 + mAngleMax, 45, false, mRedZonePaint);
        canvas.drawCircle(centerX, centerY, radius, mManoPaint);

        for (int i = 0 ; i < positions.size() ; i++){
            positions.get(i).calculatePosition(radius, radius - 20, centerX, mTextPaint.getTextSize());
            canvas.drawLine(positions.get(i).getX1(), positions.get(i).getY1(), positions.get(i).getX2(), positions.get(i).getY2(), mManoPaint);
            canvas.drawText(positions.get(i).getValue(), positions.get(i).getXText(), positions.get(i).getYText(), mTextPaint);
       }
    }

    public void setValues(int valueMin, int valueMax, int nbIntermediates, int angleMin, int angleMax) {
        mAngleMax = angleMax;
        positions.removeAllElements();

        double cosMin = Math.cos(Math.toRadians(angleMin));
        double sinMin = Math.sin(Math.toRadians(angleMin));
        positions.add(new ManoPosition(cosMin, sinMin, "" + valueMin));

        for (int i = 0 ; i < nbIntermediates ; i++) {
            int value = (valueMax - valueMin) * (i + 1) / (nbIntermediates + 1) + valueMin;
            double angle = ((double)angleMax - (double)angleMin) * (i + 1) / (nbIntermediates + 1) + (double)angleMin;
            double cos = Math.cos(Math.toRadians(angle));
            double sin = Math.sin(Math.toRadians(angle));
            positions.add(new ManoPosition(cos, sin, "" + value));
        }

        double cosMax = Math.cos(Math.toRadians(angleMax));
        double sinMax = Math.sin(Math.toRadians(angleMax));
        positions.add(new ManoPosition(cosMax, sinMax, "" + valueMax));
    }
}
