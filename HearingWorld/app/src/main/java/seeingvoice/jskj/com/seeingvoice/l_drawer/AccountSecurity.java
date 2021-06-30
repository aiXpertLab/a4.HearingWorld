package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.R;

public class AccountSecurity extends MyBaseActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();

    String uPhone = MySP.getInstance().getUPhone();
    String uWxId = MySP.getInstance().getUWxId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_account_security);

        TextView mTitleTv   = findViewById(R.id.text_view_title);
        TextView mWeChatIdTv= findViewById(R.id.tv_wechat_id);
        TextView mPhoneTv   = findViewById(R.id.text_view_phone);

        RelativeLayout mPasswordRl= findViewById(R.id.rl_password);
        RelativeLayout mCloseAccount = findViewById(R.id.rl_close_account);
        RelativeLayout mSecurityCenter = findViewById(R.id.rl_wechat_services);
        RelativeLayout mPermissions = findViewById(R.id.relative_layout_check_permissions);

        mCloseAccount.setOnClickListener(this);
        mPermissions.setOnClickListener(this);
        mPasswordRl.setOnClickListener(this);
        mSecurityCenter.setOnClickListener(this);

        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mWeChatIdTv.setText(uWxId);
        mPhoneTv.setText(uPhone);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==  R.id.rl_password){
            startActivity(new Intent(AccountSecurity.this, ModifyPasswordActivity.class));
        }else if (view.getId()==  R.id.relative_layout_check_permissions){
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            // Open the corresponding setting interface according to the package name.
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivityForResult(intent, 200);
            this.startActivity(intent);

        }else if (view.getId()==  R.id.rl_close_account){
            startActivity(new Intent(AccountSecurity.this, CloseAccount.class));

        }else if (view.getId()==  R.id.rl_wechat_services){
            Intent intent = new Intent(AccountSecurity.this, SVQRCode.class);
            intent.putExtra("from", "AccountSecurity");
            startActivity(intent);
        }
    }
}
