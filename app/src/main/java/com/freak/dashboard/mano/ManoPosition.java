package com.freak.dashboard.mano;

public class ManoPosition {

    private double mCos;
    private double mSin;
    private String mValue;
    private float mX1;
    private float mY1;
    private float mX2;
    private float mY2;
    private float mXText;
    private float mYText;
    private float mCenter;
    private float mRadiusInt;

    public float mCenterTextX;
    public float mCenterTextY;

    public ManoPosition(double cos, double sin, String value){
        mCos = cos;
        mSin = sin;
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void calculatePosition(float radiusExt, float radiusInt, float center, float textSize) {
        mCenter = center;
        mRadiusInt = radiusInt;

        double x1 = center - (radiusExt * mCos);
        double y1 = center - (radiusExt * mSin);
        double x2 = center - (radiusInt * mCos);
        double y2 = center - (radiusInt * mSin);

        mX1 = (float) x1;
        mY1 = (float) y1;
        mX2 = (float) x2;
        mY2 = (float) y2;

        //      2------3
        //      | Text |
        //      1------4
        float textWidth = (float)(textSize * 0.6 * mValue.length());
        float textHeight = (float)(textSize * 0.7);

        float radius = radiusInt;
        double centerTextX = center - (radius * mCos);
        double centerTextY = center - (radius * mSin);

        double textX1 = centerTextX - textWidth/2;
        double textY1 = centerTextY + textHeight/2;
        double textX2 = centerTextX - textWidth/2;
        double textY2 = centerTextY - textHeight/2;
        double textX3 = centerTextX + textWidth/2;
        double textY3 = centerTextY - textHeight/2;
        double textX4 = centerTextX + textWidth/2;
        double textY4 = centerTextY + textHeight/2;

        while ((!testPointInsideCircle(textX1, textY1) || !testPointInsideCircle(textX2, textY2) || !testPointInsideCircle(textX3, textY3) || !testPointInsideCircle(textX4, textY4)) && radius > 0){
            radius = radius - 1;
            centerTextX = center - (radius * mCos);
            centerTextY = center - (radius * mSin);

            textX1 = centerTextX - textWidth/2;
            textY1 = centerTextY + textHeight/2;
            textX2 = centerTextX - textWidth/2;
            textY2 = centerTextY - textHeight/2;
            textX3 = centerTextX + textWidth/2;
            textY3 = centerTextY - textHeight/2;
            textX4 = centerTextX + textWidth/2;
            textY4 = centerTextY + textHeight/2;
        }

        mXText = (float)textX1;
        mYText = (float)textY1;

        mCenterTextX = (float)centerTextX;
        mCenterTextY = (float)centerTextY;
    }

    private  boolean testPointInsideCircle(double x, double y){
        double hypoSquare = (mCenter - x) * (mCenter - x) + (mCenter - y) * (mCenter - y);
        double radiusSquare = (double)mRadiusInt * (double)mRadiusInt;
        return  hypoSquare < radiusSquare;
    }

    public float getX1() {
        return mX1;
    }

    public float getY1() {
        return mY1;
    }

    public float getX2() {
        return mX2;
    }

    public float getY2() {
        return mY2;
    }

    public float getXText() {
        return mXText;
    }

    public float getYText() {
        return mYText;
    }
}
