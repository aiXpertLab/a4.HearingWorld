package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import seeingvoice.jskj.com.seeingvoice.MySQLite;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.Login;

public class L_CancelAccount extends MyTopBar implements View.OnClickListener {

    private EditText et_rgsPhoneNum;
    public MySQLite mDBOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBOpenHelper = new MySQLite(this);
    }

    @Override
    protected int getContentView_sv() {
        return R.layout.a_cancel_account;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        Intent mIntent = getIntent();
        setToolbarTitle(getString(R.string.user_register_input));
        setToolbarBack(true);
        setToolBarMenuOne("",R.mipmap.share_icon,null);
        setToolBarMenuTwo("",R.mipmap.share_icon,null);
        et_rgsPhoneNum = findViewById(R.id.et_rgsPhoneNum);
        Button btn_register = findViewById(R.id.btn_rgs);

        btn_register.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_rgs) {    //注册按钮
            //获取用户输入的用户名、密码、验证码
            String sPhonenum = et_rgsPhoneNum.getText().toString().trim();
            mDBOpenHelper.deleteByPhone(sPhonenum);
//            Toast.makeText(this, getString(R.string.user_cancel_account_rep), Toast.LENGTH_SHORT).show();
            toNextActivity(null, this, Login.class);        }
    }
}
