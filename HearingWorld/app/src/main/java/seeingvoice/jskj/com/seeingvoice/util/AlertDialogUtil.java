package seeingvoice.jskj.com.seeingvoice.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;

/**
 * Date:2019/5/23
 * Time:13:33
 * auther:zyy
 */
public class AlertDialogUtil extends Dialog implements View.OnClickListener {

    private Context context;
    private String title;
    private String message;
    private String strPositive;
    private String strNegative;
    private boolean showNegativeButton = true;
    private int requestCode;
    private OnDialogButtonClickListener listener;
    private boolean cancelable = true;

    /** 自定义Dialog监听器*/
    public interface OnDialogButtonClickListener {
        /**
         * 点击按钮事件的回调方法
         * @param requestCode 传入的用于区分某种情况下的showDialog
         * @param isPositive
         */
        void onDialogButtonClick(int requestCode, boolean isPositive);
    }

    //  private static final String TAG = "AlertDialog";

    /**
     * 带监听器参数的构造函数,预计需要多态
     */
    public AlertDialogUtil(Context context, boolean cancleable, String title, String message, boolean showNegativeButton,
                       int requestCode, OnDialogButtonClickListener listener) {
        super(context, R.style.MyDialog);
        this.cancelable = cancleable;
        this.context = context;
        this.title = title;
        this.message = message;
        this.showNegativeButton = showNegativeButton;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    public AlertDialogUtil(Context context, String title, String message, boolean showNegativeButton,
                        String strPositive, int requestCode, OnDialogButtonClickListener listener) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.title = title;
        this.message = message;
        this.showNegativeButton = showNegativeButton;
        this.strPositive = strPositive;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    public AlertDialogUtil(Context context, String title, String message,
                       String strPositive, String strNegative, int requestCode, OnDialogButtonClickListener listener) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.title = title;
        this.message = message;
        this.strPositive = strPositive;
        this.strNegative = strNegative;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    private TextView tvTitle,tvMessage;
    private Button btnPositive,btnNegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);

        tvTitle = findViewById(R.id.tvAlertDialogTitle);
        tvMessage = findViewById(R.id.tvAlertDialogMessage);
        btnPositive =  findViewById(R.id.btnAlertDialogPositive);
        btnNegative =  findViewById(R.id.btnAlertDialogNegative);

        tvTitle.setVisibility(StringUtil.isNotEmpty(title, true) ? View.VISIBLE : View.GONE);
        tvTitle.setText("" + StringUtil.getCurrentString());

        if (StringUtil.isNotEmpty(strPositive, true)) {
            btnPositive.setText(StringUtil.getCurrentString());
        }
        btnPositive.setOnClickListener(this);

        if (showNegativeButton) {
            if (StringUtil.isNotEmpty(strNegative, true)) {
                btnNegative.setText(StringUtil.getCurrentString());
            }
            btnNegative.setOnClickListener(this);
        } else {
            btnNegative.setVisibility(View.GONE);
        }

        tvMessage.setText(StringUtil.getTrimedString(message));
    }

    @Override
    public void onClick(final View v) {
//        if (AntiShakeUtils.isInvalidClick(v)){
            if (v.getId() == R.id.btnAlertDialogPositive) {
                listener.onDialogButtonClick(requestCode, true);
            } else if (v.getId() == R.id.btnAlertDialogNegative) {
                listener.onDialogButtonClick(requestCode, false);
            }
//        }
        this.dismiss();
    }

}
