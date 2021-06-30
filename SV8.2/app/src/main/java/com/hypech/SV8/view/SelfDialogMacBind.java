package com.hypech.SV8.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hypech.SV8.R;

import tech.oom.idealrecorder.utils.Log;

/**
 * Created by Administrator on 2016/12/20.
 */

//自定义dialog
public class SelfDialogMacBind extends Dialog {
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;
    private onScanOnclickListener scanOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    private TextView selfTtile;
    private Button yes;
    private String versonMSG,titleMSG;
    private EditText dialogEt;
    private ImageView dialog_scan;

    public SelfDialogMacBind(Context context) {
        super(context);
    }

    public SelfDialogMacBind(Context context, int themeResId) {
        super(context, themeResId);
    }
    public SelfDialogMacBind(Context context, int themeResId , String titleMSG, String versonMSG) {
        super(context, themeResId);
        this.titleMSG = titleMSG;
        this.versonMSG=versonMSG;
    }


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param
     * @param onScanOnclickListener
     */
    public void setScanOnclickListener(onScanOnclickListener onScanOnclickListener) {

        this.scanOnclickListener = onScanOnclickListener;
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
        setContentView(R.layout.selfdialog_mac);
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
                    Log.d("ljp","dialogEt.getText().toString()1"+dialogEt.getText().toString());
                    yesOnclickListener.onYesClick(dialogEt.getText().toString());
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        dialog_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanOnclickListener != null) {
                    Log.d("ljp","dialogEt.getText().toString()"+dialogEt.getText().toString());
                    scanOnclickListener.onScanClick(dialogEt.getText().toString());
                }
            }
        });
    }

    private void initView() {
        selfTtile = findViewById(R.id.selfdg_title);
        yes = (Button) findViewById(R.id.selfdg_yes);
        dialog_scan = findViewById(R.id.dialog_scan);
        dialogEt = findViewById(R.id.dialog_Et);
        selfTtile.setText(titleMSG);
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(String macEt);
    }

    public interface onScanOnclickListener {
        public void onScanClick(String macEt);
    }

}