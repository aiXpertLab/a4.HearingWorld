package com.seeingvoice.www.svhearing.heartests.puretest.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Date:2019/10/14
 * Time:13:22
 * auther:zyy
 */
public class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int mMargin;
    private int mLines;

    public MyLeadingMarginSpan2(int mMargin, int mLines) {
        this.mMargin = mMargin;
        this.mLines = mLines;
    }

    @Override
    public int getLeadingMarginLineCount() {
        return mLines;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return first ? mMargin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        // TODO Auto-generated method stub
    }
}
