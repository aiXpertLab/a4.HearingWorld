package com.seeingvoice.www.svhearing.heartests.puretest.mpchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class MyXFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public MyXFormatter(String[] values) {
        this.mValues = values;
    }
    private static final String TAG = "MyXFormatter";

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" 代表再X/Y上的位置 represents the position of the label on the axis (x or y)
        //Log.d(TAG, "----->getFormattedValue: "+value);
        return mValues[(int) value % mValues.length];
    }
}
