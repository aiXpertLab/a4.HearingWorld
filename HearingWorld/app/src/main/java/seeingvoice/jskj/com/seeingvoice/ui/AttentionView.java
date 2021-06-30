package seeingvoice.jskj.com.seeingvoice.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.ui.MyLeadingMarginSpan2;

/**
 * TODO: document your custom view class.
 */
public class AttentionView extends RelativeLayout {

    private TextView mTextView;
    private ImageView mImageView;
    private SpannableString mSpannableString;
    private int mImageWidth,mImageHeight;

    public AttentionView(Context context) {
        super(context);
    }

    public AttentionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.sample_attention_view,this);
        mTextView = findViewById(R.id.text);
        mImageView = findViewById(R.id.icon);
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mImageWidth = mImageView.getMeasuredWidth();
                mImageHeight = mImageView.getMeasuredHeight();
                try {
                    String text = String.valueOf(mTextView.getText());
                    Log.e("hahahahaha", "onGlobalLayout: "+text);
                    makeSpan(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    // 设置标题的方法
    public void setTitle(String content) {
        mTextView.setText(content);
    }

    protected void makeSpan(String text) {
//        postInvalidate();
        //        invalidate();
        // 获得文本
        int textStart = 0;
        int textEnd = text.length() - 1;
        // 计算ImageView的高所占文本的行数
        int lines;
        float fontSpacing = mTextView.getPaint().getFontSpacing();
        lines = (int) (mImageHeight / fontSpacing);
        MyLeadingMarginSpan2 span = new MyLeadingMarginSpan2(mImageWidth + 5, lines);
        mSpannableString = new SpannableString(text);
        mSpannableString.setSpan(span, textStart, textEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(mSpannableString);
    }
}
