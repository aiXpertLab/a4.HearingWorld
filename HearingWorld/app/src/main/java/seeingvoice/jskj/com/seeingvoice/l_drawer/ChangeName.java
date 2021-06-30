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

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.R;

/**
 * @author  LeoReny@hypech.com
 * @version 1.0
 * @since   2021-04-12
 */

public class ChangeName extends MyBaseActivity {
    TextView mTitleTv;
    EditText mNickNameEt;
    View     mNickView;
    TextView mSaveTv;

    MySP user = MySP.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_change_name);

        mTitleTv= findViewById(R.id.text_view_title);
        mNickNameEt= findViewById(R.id.edit_text_region);
        mNickView= findViewById(R.id.v_nick);

        mSaveTv= findViewById(R.id.tv_save);

        MySP.getInstance().init(this);

        initView();

        mSaveTv.setOnClickListener(view -> {
            String userNickName = mNickNameEt.getText().toString();
            updateUserNickName(user.getUPhone(), userNickName);
        });
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mNickNameEt.setText(user.getUNickName());
        // 光标移至最后
        CharSequence charSequence = mNickNameEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mNickNameEt.addTextChangedListener(new TextChange());

        mNickNameEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mNickView.setBackgroundColor(getColor(R.color.divider_green));
            } else {
                mNickView.setBackgroundColor(getColor(R.color.divider_grey));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newNickName = mNickNameEt.getText().toString();
            String oldNickName = user.getUNickName();
            // 是否填写
            boolean isNickNameHasText = newNickName.length() > 0;
            // 是否修改
            boolean isNickNameChanged = !oldNickName.equals(newNickName);

            if (isNickNameHasText && isNickNameChanged) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {        }
    }

    private void updateUserNickName(String userPhone, final String userNickName) {
        setResult(RESULT_OK);
        MySP.getInstance().setUNickName(userNickName);
        finish();
    }
}