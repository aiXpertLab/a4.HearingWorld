package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.R;

import static seeingvoice.jskj.com.seeingvoice.MyUtil.validatePassword;

public class ModifyPasswordActivity extends MyBaseActivity {
    final String TAG = this.getClass().getSimpleName();

    private View mLayout, mLayout1;

    TextView mTitleTv;

    String currentPwd = MySP.getInstance().getUPassword();
    String wxId = MySP.getInstance().getUWxId();

    EditText mWechatIdEt, mOldPasswordEt,mNewPasswordEt,mConfirmPasswordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_modify_password);
        mLayout = findViewById(R.id.a_modify_password);

        mTitleTv= findViewById(R.id.text_view_title);
        mWechatIdEt = findViewById(R.id.et_wechat_id);
        mOldPasswordEt = findViewById(R.id.et_old_password);
        mNewPasswordEt= findViewById(R.id.et_new_password);
        mConfirmPasswordEt= findViewById(R.id.et_confirm_password);

        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);
        mWechatIdEt.setText(wxId);

        findViewById(R.id.tv_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void submitForm() {
        String oldPassword = mOldPasswordEt.getText().toString();
        String newPassword = mNewPasswordEt.getText().toString();
        String confirmPassword = mConfirmPasswordEt.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.old_password_empty), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_old_password));
            sb.show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.new_password_empty), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_new_password));
            sb.show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.confirm_password_empty), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_confirm_password));
            sb.show();
            return;
        }

        if (!oldPassword.equals(currentPwd)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.old_password_incorrect), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_new_password));
            sb.show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.confirm_password_incorrect), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_confirm_password));
            sb.show();
            return;
        }

        if (!validatePassword(newPassword)) {
            Snackbar sb = Snackbar.make(mLayout, getString(R.string.password_rules), Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error));
            sb.setAnchorView(findViewById(R.id.view_confirm_password));
            sb.show();
            return;
        }

        MySP.getInstance().setUPassword(newPassword);

        finish();
    }
}
