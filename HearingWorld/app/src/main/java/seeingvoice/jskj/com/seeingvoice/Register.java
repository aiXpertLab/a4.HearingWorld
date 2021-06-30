package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.pm.PackageInfoCompat;

import com.google.android.material.snackbar.Snackbar;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_Privacy;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_UserAgreement;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

public class Register extends MyTopBar implements View.OnClickListener {

    private EditText editTextNickName, et_rgsPhoneNum, et_rgsPsw1, et_rgsPsw2;
    public MySQLite mDBOpenHelper;
    private TextView mTvRegister, tv_notice,tv_user_agreement,tv_privacy_policy,tv_version;      //返回键,显示的注册，找回密码
    private CheckBox mCheckDisClaim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBOpenHelper = new MySQLite(this);
    }

    @Override
    protected int getContentView_sv() {
        return R.layout.a_register;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.user_register_input));
        setToolbarBack(true);
        setToolBarMenuOne("",R.mipmap.share_icon,null);
        setToolBarMenuTwo("",R.mipmap.share_icon,null);
        editTextNickName = findViewById(R.id.edit_text_nick_name);
        editTextNickName.setText(getString(R.string.nick_name_example));
        et_rgsPhoneNum = findViewById(R.id.et_rgsPhoneNum);
        et_rgsPsw1 = findViewById(R.id.et_rgsPsw1);
        et_rgsPsw2 = findViewById(R.id.et_rgsPsw2);

        mCheckDisClaim = findViewById(R.id.CB_disclaim);
        tv_version = findViewById(R.id.tv_version);

        TextView tv_user_agreement = findViewById(R.id.tv_user_agreement);
        TextView tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        Button btn_register = findViewById(R.id.btn_rgs);

        btn_register.setOnClickListener(this);
        mCheckDisClaim.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);
        tv_user_agreement.setOnClickListener(this);

        PackageManager pkgMager = getPackageManager();
        try {
            PackageInfo pInfo = pkgMager.getPackageInfo(this.getPackageName(), 0);
            long longVersionCode= PackageInfoCompat.getLongVersionCode(pInfo);
            int versionCode = (int) longVersionCode; // avoid huge version numbers and you will be ok
            String versionName = pInfo.versionName;
            tv_version.setText(getString(R.string.version, versionName));
            MyApp.versionName = versionName;
//            tv_version.setText("当前版本："+versionCode+"-"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_rgs) {    //注册按钮
            if (mCheckDisClaim.isChecked()) {                        //选择了免责声明
                //获取用户输入的用户名、密码、验证码
                String password1 = et_rgsPsw1.getText().toString().trim();
                String password2 = et_rgsPsw2.getText().toString().trim();
                String name = editTextNickName.getText().toString().trim();
                String phonenum = et_rgsPhoneNum.getText().toString().trim();
                if (null == name || name.length()==0) name = getString(R.string.nick_name_example);
                //注册验证
                if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2) || TextUtils.isEmpty(phonenum)) {
                    ToastUtil.showLongToast(getString(R.string.user_register_remind));
                } else {
                    //判断两次密码是否一致
                    if (password1.equals(password2)) {
                        //将用户名和密码加入到数据库中
                        mDBOpenHelper.add("username", password2, name, phonenum);
                        Intent intent1 = new Intent(Register.this, Login.class);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(this, getString(R.string.user_register_ok), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.user_register_pwd_diff), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Snackbar sb = Snackbar.make(mCheckDisClaim, getString(R.string.user_login_agree_agreement), Snackbar.LENGTH_SHORT);
                sb.show();
            }
        }else if (v.getId() == R.id.tv_user_agreement) {
            toNextActivity(null, this, L_UserAgreement.class);
        }else if (v.getId() == R.id.tv_privacy_policy) {
            toNextActivity(null, this, L_Privacy.class);
        }
    }
}