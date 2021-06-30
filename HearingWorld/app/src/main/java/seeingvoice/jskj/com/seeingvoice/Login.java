package seeingvoice.jskj.com.seeingvoice;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;

import java.util.ArrayList;
import java.util.zip.Deflater;

import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.l_user.L_UserBean;
import seeingvoice.jskj.com.seeingvoice.l_user.TelRegisterL;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

/**
 * Date:    2021/2/26
 * Author:  LeoReny@hypech.com
 */
public class Login extends MyTopBar {

    public static Deflater instance;
    private static String userTelStr,pswStr;//获取的用户名，密码，加密密码
    private EditText et_user_tel,et_psw;    //sv

    public MySQLite mDBOpenHelper;   //sv

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HiAnalyticsTools.enableLog();
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);

        mDBOpenHelper = new MySQLite(this);
        if(!MySP.getInstance().getWelcome().equals("DONE")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        initUI();//初始化界面
    }

    @Override
    protected int getContentView_sv() {
        return R.layout.a_login;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.app_name));
        setToolbarBack(false);
        setToolBarMenuOne("", R.mipmap.ic_home, null);
        setToolBarMenuTwo("", R.mipmap.ic_share, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                allShare(getApplicationContext().getString(R.string.topbar_share));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (MySP.getInstance().isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化UI控件
     */
    @SuppressLint("SetTextI18n")
    private void initUI() {
        et_user_tel = findViewById(R.id.et_user_tel);   //sv
        et_psw      = findViewById(R.id.et_psw);     //sv
        //登录按钮
        Button btn_login = findViewById(R.id.btn_login);
        TextView mTvRegister = findViewById(R.id.tv_register);
        //返回键,显示的注册，找回密码
        TextView tv_version = findViewById(R.id.tv_version);

        PackageManager pkgMager = getPackageManager();
        try {
            PackageInfo pInfo = pkgMager.getPackageInfo(this.getPackageName(), 0);
            String versionName = pInfo.versionName;
            tv_version.setText(getString(R.string.version, versionName));
            MyApp.versionName = versionName;
//            tv_version.setText("当前版本："+versionCode+"-"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mTvRegister.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent=new Intent(Login.this, Register.class);
                startActivityForResult(intent, 1);
            }
        });

        //登录按钮的点击事件
        btn_login.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                userTelStr  = et_user_tel.getText().toString().trim();
                pswStr      = et_psw.getText().toString().trim();
                if(TextUtils.isEmpty(userTelStr)){
                    ToastUtil.showLongToast(getString(R.string.user_login_username));
                }else if(TextUtils.isEmpty(pswStr)){
                    ToastUtil.showLongToast(getString(R.string.user_login_enterPWD));
                }else {
                    ArrayList<L_UserBean> userFromSQL = mDBOpenHelper.getAllData();
                    int i6 = userFromSQL.size();
                    if (i6 == 0) {
                        ToastUtil.showShortToastCenter(getString(R.string.user_register_lead));
                    }

                    boolean bPwdOk = false;

                    for (int i=0 ; i < userFromSQL.size(); i++) {
                        L_UserBean user = userFromSQL.get(i);
                        if (userTelStr.equals(user.getPhonenum()) && pswStr.equals(user.getPassword())) {
                            bPwdOk = true;
                            ToastUtil.showShortToast(getString(R.string.user_login_success));

                            String avatar = (Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                    + getResources().getResourcePackageName(R.mipmap.default_user_avatar) + "/"
                                    + getResources().getResourceTypeName(R.mipmap.default_user_avatar) + "/"
                                    + getResources().getResourceEntryName(R.mipmap.default_user_avatar))).toString();

                            MySP.getInstance().setLogin(true);
                            MySP.getInstance().setUPhone(userTelStr);
                            MySP.getInstance().setUNickName(user.getEmail());   //nickname
                            MySP.getInstance().setUPassword(userTelStr);
                            MySP.getInstance().setUAvatar(avatar);
                            MySP.getInstance().setUSignature(getString(R.string.slogan));

                            //用线程启动
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        // sleep(2000);//2秒 模拟登录时间
                                        Intent intent1 = new Intent(Login.this, MainActivity.class);//设置自己跳转到成功的界面
                                        startActivity(intent1);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();//打开线程
                        }
                    }
                    if (!bPwdOk) ToastUtil.showShortToast(getString(R.string.user_login_fail));
                }
            }
        });

    }

    public void register(View view){startActivity(new Intent(Login.this, TelRegisterL.class));}

    /** 防抖 返回键*/
    private long exittime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exittime < 2000){//小于2000ms则认为是用户确实希望关闭程序-调用System.exit()方法进行退出
            MySP.getInstance().setLogin(true);
            finishAffinity();
        }else {
            ToastUtil.showShortToast(getString(R.string.global_exit));
            exittime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {        super.onDestroy();    }
}