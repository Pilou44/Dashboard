package com.freak.dashboard.mano;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class Manometer extends RelativeLayout {

    private ManoHandView mHandView;
    private ManoBackView mBackView;

    public Manometer(Context context) {
        super(context);
        init(context);
    }

    public Manometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Manometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBackView = new ManoBackView(context);
        mHandView = new ManoHandView(context);
        this.addView(mBackView);
        this.addView(mHandView);
    }

    public void setValues(long period, int valueMin, int valueMax, int nbIntermediates, int angleMin, int angleMax, int redZoneStart, int redZoneSize) {
        mHandView.setValues(period, valueMax, angleMin, angleMax);
        mBackView.setValues(valueMin, valueMax, nbIntermediates, angleMin, angleMax, redZoneStart, redZoneSize);
    }

    public void setHandColor(int color) {
        mHandView.setColor(color);
    }

    public void setManoColor(int manoColor, int redZoneColor, int backColor) {
        mBackView.setColor(manoColor, redZoneColor, backColor);
    }

    /*public void setValue(int value) {
        mHandView.setValue(value);
    }*/

    public View getHand() {
        return mHandView;
    }
}
