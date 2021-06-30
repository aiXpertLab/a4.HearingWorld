package seeingvoice.jskj.com.seeingvoice.ui;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Date:2019/9/19
 * Time:15:34
 * auther:zyy
 */
public class MyImageTextViewNew extends LinearLayout {
    private ImageView mImageView = null;
    private TextView mTextView = null;
    private int imageId;
    private int textId, textColorId;

    public MyImageTextViewNew(Context context) {
        this(context, null);
    }

    public MyImageTextViewNew(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageTextViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.VERTICAL);//设置垂直排序
        this.setGravity(Gravity.CENTER);//设置居中
        if (mImageView == null) {
            mImageView = new ImageView(context);
        }
        if (mTextView == null) {
            mTextView = new TextView(context);
        }
        if (attrs == null)
            return;
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);//获取属性名称
            //根据属性获取资源ID
            switch (attrName) {
                case "image"://显示的图片
                    imageId = attrs.getAttributeResourceValue(i, 0);
                    break;
                case "text"://显示的文字
                    textId = attrs.getAttributeResourceValue(i, 0);
                    break;
                case "textColor"://显示的文字的颜色
                    textColorId = attrs.getAttributeResourceValue(i, 0);
                    break;
            }
        }
        init();
    }

    /**
     * 初始化状态
     */
    private void init() {
        this.setText(textId);
        mTextView.setGravity(Gravity.CENTER);//字体居中
        this.setTextColor(textColorId);
        this.setImgResource(imageId);
        addView(mImageView);//将图片控件加入到布局中
        addView(mTextView);//将文字控件加入到布局中
    }

    /**
     * 设置显示的图片
     *
     * @param resourceID 图片ID
     */
    private void setImgResource(int resourceID) {
        if (resourceID == 0) {
            this.mImageView.setImageResource(0);
        } else {
            this.mImageView.setImageResource(resourceID);
        }
    }

    /**
     * 设置显示的文字
     *
     * @param text
     */
    public void setText(int text) {
        this.mTextView.setText(text);
    }

    /**
     * 设置字体颜色(默认为黑色)
     *
     * @param color
     */
    private void setTextColor(int color) {
        if (color == 0) {
            this.mTextView.setTextColor(Color.BLACK);
        } else {
            this.mTextView.setTextColor(getResources().getColor(color));
        }
    }
}
