package com.seeingvoice.www.svhearing.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Date:2019/5/10
 * Time:10:38
 * auther:zyy
 */
public class GridViewItemRllayout extends RelativeLayout {
    public GridViewItemRllayout(Context context) {
        super(context);
    }

    public GridViewItemRllayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewItemRllayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
