package com.hypech.SV3.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hypech.SV3.R;

/**
 * Created by Administrator on 2016/12/20.
 */

//自定义dialog
public class SelfDialogconnectFaild extends Dialog {
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;
    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    private TextView selfMSG,selfTtile;
    private Button yes, no;
    private String versonMSG,titleMSG;

    public SelfDialogconnectFaild(Context context) {
        super(context);
    }

    public SelfDialogconnectFaild(Context context, int themeResId) {
        super(context, themeResId);
    }
    public SelfDialogconnectFaild(Context context, int themeResId ,String titleMSG, String versonMSG) {
        super(context, themeResId);
        this.titleMSG = titleMSG;
        this.versonMSG=versonMSG;
    }


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        no.setText(str);
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        yes.setText(str);
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfdialog_connectfailed);
        initView();
        setCancelable(false);
        //初始化界面控件的事件
        initEvent();
    }

    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    private void initView() {
        selfMSG = ((TextView) findViewById(R.id.selfdg_mesg));
        selfTtile = findViewById(R.id.selfdg_title);
        yes = (Button) findViewById(R.id.selfdg_yes);
        no = (Button) findViewById(R.id.selfdg_no);
        selfMSG.setText(versonMSG);
        selfTtile.setText(titleMSG);
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

}