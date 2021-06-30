package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yanzhenjie.loading.dialog.LoadingDialog;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.MySQLite;
import seeingvoice.jskj.com.seeingvoice.MyUtil;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.Register;

/**
 * @author  LeoReny@hypech.com
 * @version 1.0
 * @since   2021-04-08
 */
public class MeMoreSignature extends MyBaseActivity {
    
    TextView mTitleTv;
    EditText et_sign;
    TextView mSaveTv;
    EditText mSignEt;
    TextView mSignLengthTv;

    final int maxSignLenth = 16;

    LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sign);
        
        mTitleTv    = findViewById(R.id.text_view_title);
        mSaveTv     = findViewById(R.id.tv_save);
        mSignEt     = findViewById(R.id.et_sign);
        mSignLengthTv = findViewById(R.id.tv_sign_length);

        mDialog = new LoadingDialog(MeMoreSignature.this);
        initView();

        mSaveTv.setOnClickListener(view -> {
            mDialog.setMessage(getString(R.string.save));
            mDialog.show();
            String userSign = mSignEt.getText().toString();

            mDialog.dismiss();

            MySP.getInstance().setUSignature(userSign);
            finish();
        });

    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

//        mSignEt.setText(mUser.getUserSignature());

        // 剩余可编辑字数
        int leftSignLenth = maxSignLenth - mSignEt.length();
        if (leftSignLenth >= 0) {
            mSignLengthTv.setText(String.valueOf(leftSignLenth));
        }

        // 光标移至最后
        CharSequence charSequence = mSignEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mSignEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newSign = mSignEt.getText().toString();
     //       String oldSign = mUser.getSerial() == null ? "" : mUser.getSerial();
            // 是否填写
            boolean isSignHasText = mSignEt.length() > 0;
            // 是否修改
            boolean isSignChanged = true; //!oldSign.equals(newSign);

            if (isSignHasText && isSignChanged) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }

            int leftSignLenth = maxSignLenth - mSignEt.length();
            if (leftSignLenth >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLenth));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 是否填写
            int leftSignLenth = maxSignLenth - mSignEt.length();
            if (leftSignLenth >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLenth));
            }
        }
    }

    public void back(View view) {
        finish();
    }
}
