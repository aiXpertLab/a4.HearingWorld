package com.seeingvoice.www.svhearing.share;

/**
 * Date:2019/7/15
 * Time:16:06
 * auther:zyy
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;


public class mTextView extends androidx.appcompat.widget.AppCompatTextView{

    private Context mContext;
    private int mWidth=100;
    private int mHeight=100;

    //保存设置的图片
    private Drawable mLeft,   mTop,   mRight,   mBottom;
    public mTextView(Context context) {
        this(context,null);
    }

    public mTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext=context;
        init();
    }



    private void init() {
        float density=mContext.getResources().getDisplayMetrics().density;
        //将dip转化成px
        mWidth*=density;
        mHeight*=density;
        //手动调用该方法，设置文字上下左右方向的图片宽高
        setCompoundDrawablesWithIntrinsicBounds(mLeft, mTop, mRight, mBottom);//设置图片的宽高
    }

    /**
     * 重写该方法，文字上下左右方向的图片大小设置
     * @param left
     * @param top
     * @param right
     * @param bottom
     */

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds( Drawable left,  Drawable top,  Drawable right,  Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, mWidth, mHeight);
            mLeft=left;
        }

        if (right != null) {
            right.setBounds(0, 0, mWidth, mHeight);
            mRight=right;
        }

        if (top != null) {
            top.setBounds(0, 0, mWidth, mHeight);
            mTop=top;
        }

        if (bottom != null) {
            bottom.setBounds(0, 0, mWidth, mHeight);
            mBottom=bottom;
        }

        setCompoundDrawables(left, top, right, bottom);//设置图片的宽高

    }

    /**
     * 设置图片的宽高
     * @param width
     * @param height
     */

    public void setBounds(int width,int height){
        mWidth=width;
        mHeight=height;
        init();
    }
}
