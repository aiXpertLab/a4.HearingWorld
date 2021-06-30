package com.seeingvoice.www.svhearing.heartests.verbaltests.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.seeingvoice.www.svhearing.R;

/**
 * Date:2019/2/22
 * Time:14:52
 * auther:zyy
 */
public class CustomVolumeControlBar extends View {
    /* 第一圈的颜色*/
    private int mFirstColor;
    /* 第二圈的颜色*/
    private int mSecondColor;
    /* 圈的宽度*/
    private int mCircleWidth;
    /* 画笔*/
    private Paint mPaint;
    /* 当前进度*/
    public int mCurrentCount = 8;
    /* 中间的图片*/
    private Bitmap mImage;
    /* 每个块 块间的间距*/
    private int mSplitSize;
    /* 个数*/
    public int mCount;
    private Rect mRect;

    /** 音量改变监听接口*/
    private OnVolumeChangeListener onVolumeChangeListener;

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener){
        this.onVolumeChangeListener = onVolumeChangeListener;
    }

    public CustomVolumeControlBar(Context context) {
        this(context, null);
    }

    public CustomVolumeControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 必要的初始化，获得一些自定义的值
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomVolumeControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomVolumeControlBar, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomVolumeControlBar_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomVolumeControlBar_secondColor:
                    mSecondColor = a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomVolumeControlBar_dotCount:
                    mCount = a.getInt(attr, 20);
                    break;
                case R.styleable.CustomVolumeControlBar_bg:
                    mImage = BitmapFactory.decodeResource(getResources(),a.getResourceId(attr,0));
                    break;
                case R.styleable.CustomVolumeControlBar_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomVolumeControlBar_splitSize:
                    mSplitSize = a.getInt(attr, 20);
            }
        }
        a.recycle();
        mPaint = new Paint();
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 消除锯齿
        mPaint.setAntiAlias(true);
        // 设置圆环的宽度
        mPaint.setStrokeWidth(mCircleWidth);
        // 定义线段断点形状为圆头
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 消除锯齿
        mPaint.setAntiAlias(true);
        // 设置空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 获取圆心的x坐标
        int centre = getWidth() / 2;
        // 半径
        int radius = centre - mCircleWidth / 2;

        drawOval(canvas, centre, radius);

        /**
         * 计算内切正方形的位置
         */
        int relRadius = radius - mCircleWidth / 2;// 获得内圆的半径

        /**
         * 内切正方形距离左边的距离(或顶部):
         * (内圆半径 -  (更2 / 2) * 内圆半径) + 圆弧的宽度
         */
        mRect.left = (int) (relRadius - Math.sqrt(2) / 2 * relRadius) + mCircleWidth;
        mRect.top = (int) (relRadius - Math.sqrt(2) / 2 * relRadius) + mCircleWidth;
        /**
         * 内切正方形距离左边的距离 + 正方形的边长(Math.sqrt(2) * relRadius)
         */
        mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);
        mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);


        /**
         * 如果图片比较小,那么根据图片的尺寸放置到正中心
         */
        if (mImage.getWidth() < Math.sqrt(2) * relRadius) {
            mRect.left = mCircleWidth + (relRadius - mImage.getWidth() / 2);
            mRect.top = mCircleWidth + (relRadius - mImage.getWidth() / 2);
            mRect.right = mCircleWidth + (relRadius + mImage.getWidth() / 2);
            mRect.bottom = mCircleWidth + (relRadius + mImage.getWidth() / 2);
        }
        canvas.drawBitmap(mImage, null, mRect, mPaint);

    }


    /**
     * 根据参数画出每个小块
     *
     * @param canvas
     * @param centre
     * @param radius
     */
    private void drawOval(Canvas canvas, int centre, int radius) {
        /**
         * 根据需要画的个数以及间隙计算每个块块所占的比例*360
         */
        float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;
        // 用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
        // 设置圆环的颜色
        mPaint.setColor(mFirstColor);
        for (int i = 0; i < mCount; i++) {
            // 根据进度画圆弧
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);
        }
        mPaint.setColor(mSecondColor); // 设置圆环的颜色
        for (int i = 0; i < mCurrentCount; i++) {
            // 根据进度画圆弧
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);
        }
    }

    /**
     * 当前数量+1
     */
    public void up() {
        if (mCurrentCount < mCount){
            mCurrentCount++;
        }
        mImage = BitmapFactory.decodeResource(getResources(),R.drawable.volumejian);

        postInvalidate();
    }

    /**
     * 当前数量-1
     */
    public void down() {
        if (mCurrentCount > 0){
            mCurrentCount--;
        }
        mImage = BitmapFactory.decodeResource(getResources(),R.drawable.volumeplus);
        postInvalidate();
    }

    private float x1, y1, x2, y2;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                xDown = (int) event.getY();
                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
                mImage = BitmapFactory.decodeResource(getResources(),R.drawable.volume);
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                if (y1 - y2 > 50) {
                    Log.e("6", "onTouchEvent: down()");
                    up();
//                    Toast.makeText(getContext(), "向上滑", Toast.LENGTH_SHORT).show();
                } else if (y2 - y1 > 50) {
                    Log.e("6", "onTouchEvent: down()");
                    down();
//                    Toast.makeText(getContext(), "向下滑", Toast.LENGTH_SHORT).show();
                } else if (x1 - x2 > 50) {
//                    Toast.makeText(getContext(), "向左滑", Toast.LENGTH_SHORT).show();
                } else if (x2 - x1 > 50) {
//                    Toast.makeText(getContext(), "向右滑", Toast.LENGTH_SHORT).show();
                }
                if (mCurrentCount == mCount){
                    mImage = BitmapFactory.decodeResource(getResources(),R.drawable.selience);
                    postInvalidate();
                }
                break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    mImage = BitmapFactory.decodeResource(getResources(),R.drawable.volume);
                    postInvalidate();
                default:
                    break;

//                mImage = BitmapFactory.decodeResource(getResources(),R.drawable.volume);
        }
        if (onVolumeChangeListener != null){
            onVolumeChangeListener.OnVolumeChange();
        }
        return true;
//        return super.onTouchEvent(event);
    }

    public interface OnVolumeChangeListener{
        void OnVolumeChange();
    }
}
