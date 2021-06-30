package com.seeingvoice.www.svhearing.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.AppConstant;
import com.seeingvoice.www.svhearing.DisclaimerStatementActivity;
import com.seeingvoice.www.svhearing.MainActivity;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.BaseActivity;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.beans.BaseBean;
import com.seeingvoice.www.svhearing.beans.QQLoginInfoBean;
import com.seeingvoice.www.svhearing.beans.TelLoginBean;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.permissions.PermissionsActivity;
import com.seeingvoice.www.svhearing.permissions.PermissionsChecker;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.LOGIN_URL;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.PHONE_LOGIN;
import static com.seeingvoice.www.svhearing.AppConstant.QQ_APP_ID;
import static com.seeingvoice.www.svhearing.AppConstant.QQ_LOGIN;
import static com.seeingvoice.www.svhearing.AppConstant.QQ_LOGIN_SEND_OPEN_ID;
import static com.seeingvoice.www.svhearing.permissions.PermissionsActivity.PERMISSIONS_GRANTED;

/**
 * Date:2019/5/7
 * Time:15:45
 * auther:zyy
 */
public class LoginActivity1 extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private Tencent mTencent;
    private TextView mTvRegister,tv_find_psw,tv_notice,tv_to_details;//返回键,显示的注册，找回密码
    private Button btn_login;//登录按钮
    private static String userTelStr,pswStr;//获取的用户名，密码，加密密码
    private EditText et_user_tel,et_psw;//编辑框
    private Context mContext;
    private String nicknameString,openidID;
    private BaseUiListener mIUiListener;
    private OkHttpManager mOkHttpManager;
    private CheckBox mRememberCheck;
    private SharedPreferences login_sp;
    private CheckBox mCheckDisClaim;
    private IWXAPI iwxapi;
    private Bitmap bitmap;
    private Intent mIntent;
    private Bundle bundle;
    private LinearLayout mLoginProgressBar;

    final String REMEMBER_PWD_PREF = "rememberPwd";
    final String ACCOUNT_PREF = "account";
    final String PASSWORD_PREF = "password";

    /** 权限申请 声明start*/
    private static final int REQUEST_CODE = 1001; // 请求码
    static final String[] PERMISSIONS = new String[]{    // 所需的全部权限
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器


    private String user_openid;
    private String user_name;
    private String user_tel;
    private String user_sex;
    private String user_pwd;
    private String user_headimgurl;
    private String user_province;
    private String user_city;
    private Long created_at;

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 开始检查动态授权
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login1);
        mPermissionsChecker = new PermissionsChecker(this);
        mContext = this;
        initUI();
        isRememberCheck();//上次是否选中 “记住密码”
    }


    /**
     * 判断是否记住密码
     */

    /**
     * AES 工具
     */
        //    private static AES aes = new AES("12345abcdef67890", "1234567890abcdef");
    private void isRememberCheck() {
        SharedPreferencesHelper.init(LoginActivity1.this);
        boolean isRemember = (boolean) SharedPreferencesHelper.getInstance().getData(REMEMBER_PWD_PREF,false);
        if (isRemember) {//设置【账号】与【密码】到文本框，并勾选【记住密码】
            et_user_tel.setText((CharSequence) SharedPreferencesHelper.getInstance().getData(ACCOUNT_PREF, ""));
            et_psw.setText((CharSequence) SharedPreferencesHelper.getInstance().getData(PASSWORD_PREF, ""));
            mRememberCheck.setChecked(true);
        }
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        mOkHttpManager = OkHttpManager.getInstence();
        mTvRegister = findViewById(R.id.tv_register);
        //从activity_login.xml中获取的
        mTvRegister = findViewById(R.id.tv_register);
        tv_find_psw = findViewById(R.id.tv_find_psw);
        btn_login = findViewById(R.id.btn_login);
        et_user_tel = findViewById(R.id.et_user_tel);
        et_psw = findViewById(R.id.et_psw);
        mRememberCheck = findViewById(R.id.Login_Remember);
        mCheckDisClaim = findViewById(R.id.CB_disclaim);
        tv_notice = findViewById(R.id.tv_notice);
        tv_to_details = findViewById(R.id.tv_to_details);


        //立即注册控件的点击事件
        mTvRegister.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //为了跳转到注册界面，并实现注册功能
                Intent intent=new Intent(LoginActivity1.this,TelRegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //找回密码控件的点击事件
        tv_find_psw.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        //跳转到找回密码界面（此页面暂未创建）
                        toNextActivity(null,LoginActivity1.this,FindBackPwdActivity.class);
                    }
        });


        tv_to_details.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                toNextActivity(null,LoginActivity1.this, DisclaimerStatementActivity.class);
            }
        });
        //登录按钮的点击事件
        btn_login.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                userTelStr = et_user_tel.getText().toString().trim();
                pswStr = et_psw.getText().toString().trim();
                //对当前用户输入的密码进行MD5加密再进行比对判断, MD5Utils.md5( ); psw 进行加密判断是否一致
//                String md5Psw = MD5Utils.md5(psw);
                // md5Psw ; spPsw 为 根据从SharedPreferences中用户名读取密码
                // 定义方法 readPsw为了读取用户名，得到密码
//                spPsw = readPsw(userName);
                if (mCheckDisClaim.isChecked()){//选择了免责声明
                    // TextUtils.isEmpty
                    if(TextUtils.isEmpty(userTelStr)){
                        ToastUtil.showLongToast("请输入用户名");
                    }else if(TextUtils.isEmpty(pswStr)){
                        ToastUtil.showLongToast("请输入密码");
                        // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
                    }else {
                        //if(md5Psw.equals(spPsw))
                        mOkHttpManager.postNet(LOGIN_URL, new OkHttpManager.ResultCallback() {
                            @Override
                            public void onFailed(Request request, IOException e) {
                                ToastUtil.showLongToast("登录失败："+request.toString()+"异常："+e.toString());
                            }
                            @Override
                            public void onSuccess(String response) {
                                TelLoginBean telLoginBean = null;
                                try {
                                    Gson gson = new Gson();
                                    telLoginBean = gson.fromJson(response,TelLoginBean.class);
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                    ToastUtil.showLongToast("服务端异常，运行到这里来了");
                                    Log.e(TAG,"服务端异常，运行到这里来了");
                                }
                                if (telLoginBean == null){
                                    return;
                                }
                                String messageCode = telLoginBean.getMessage_code();
                                if (messageCode.equals(NET_STATE_SUCCESS)){
                                    loginSuccessFunction(telLoginBean.getClass(),telLoginBean);
                                }else {
                                    ToastUtil.showLongToast("错误："+telLoginBean.getError_info()+telLoginBean.getError_code());
                                    Log.e(TAG,telLoginBean.getError_info()+telLoginBean.getError_code());
                                }
                            }
                        },new OkHttpManager.Param("user_tel",userTelStr),new OkHttpManager.Param("user_pwd",pswStr));
                    }
//                else if((spPsw != null && !TextUtils.isEmpty(spPsw) && !md5Psw.equals(spPsw))){
//                    Toast.makeText(LoginActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
//                    return;
//                }else{
//                    Toast.makeText(LoginActivity.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
//                }
                }else {//不选择免责声明不会让你登录的
                    tv_notice.setText("请勾选免责声明,同意后才能登录");
                }
            }
        });
        mLoginProgressBar = findViewById(R.id.login_progressBar);
    }


    /**
     *保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(String userName,String pwd){
        SharedPreferencesHelper.getInstance().saveData(REMEMBER_PWD_PREF,true);
        SharedPreferencesHelper.getInstance().saveData(ACCOUNT_PREF,userName);
        SharedPreferencesHelper.getInstance().saveData(PASSWORD_PREF,pwd);
    }

    /**
     * 手机登录成功后，关闭此页面进入主页，并把登录信息传入主页
     * @param clz  用户登录信息
     * @param baseBean  用户登录信息
     */
    private void loginSuccessFunction(Class clz, BaseBean baseBean) {
        String type = clz.getSimpleName();
        switch (type){
            case "TelLoginBean":
                TelLoginBean telLoginBean = (TelLoginBean) baseBean;
                MyApplication.setLoginSuccess(true,PHONE_LOGIN,telLoginBean.getData().getUser_info().get(0).getUid(),telLoginBean, System.currentTimeMillis());
                if (mRememberCheck.isChecked()){
                    saveLoginStatus(userTelStr,pswStr);
                }
                toNextActivity(null,LoginActivity1.this, MainActivity.class);
                finish();
                break;
            case "QQLoginInfoBean":
                QQLoginInfoBean qqLoginInfoBean = (QQLoginInfoBean) baseBean;
                MyApplication.setLoginSuccess(true,QQ_LOGIN,qqLoginInfoBean.getData().getUser_info().get(0).getUid(),qqLoginInfoBean,System.currentTimeMillis());
                toNextActivity(null,LoginActivity1.this, MainActivity.class);
                finish();
                break;
        }
    }


    /**
     *从SharedPreferences中根据用户名读取密码
     */
    private String readPsw(String userName){
        //getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(userName , "");
    }

    //按钮的点击事件
    public void qqlogin(View view) {
        if (mCheckDisClaim.isChecked()){
            mLoginProgressBar.setVisibility(View.VISIBLE);
            mIUiListener = new BaseUiListener();
            mTencent = Tencent.createInstance(QQ_APP_ID, this);
            mTencent.login(this, "all", mIUiListener);
        }else {
            ToastUtil.showLongToast("请勾选免责声明！");
        }
    }

    //按钮的点击事件
    public void wechatlogin(View view) {
        if (mCheckDisClaim.isChecked()){
            iwxapi = WXAPIFactory.createWXAPI(this, AppConstant.WX_APP_ID, false);
            iwxapi.registerApp(AppConstant.WX_APP_ID);
            if (iwxapi != null && iwxapi.isWXAppInstalled()) {
                SendAuth.Req req = new SendAuth.Req();
        /*
        授权作用域（scope）代表用户授权给第三方的接口权限，第三方应用需要向微信开放平台申请使用相应scope的权限后，
        使用文档所述方式让用户进行授权，经过用户授权，获取到相应access_token后方可对接口进行调用。
        如获取用户个人信息则填写snsapi_userinfo
         */
                req.scope = "snsapi_userinfo";
        /*
        用于保持请求和回调的状态，授权请求后原样带回给第三方。
        该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
         */
                req.state = "wechat_sdk_demo";
                iwxapi.sendReq(req);
            }else {
                Toast.makeText(this, "您未安装微信", Toast.LENGTH_SHORT).show();
            }
        }else {
            ToastUtil.showLongToast("请勾选免责声明！");
        }

    }

    public void register(View view){
        Toast.makeText(this,"注册用户",Toast.LENGTH_SHORT);
        Intent mIntent = new Intent(LoginActivity1.this,TelRegisterActivity.class);
        startActivity(mIntent);
    }

    /**
     * 当自定义的监听器实现IUiListener接口后，必须要实现接口的三个方法，
     * onComplete  onCancel onError
     * 分别表示第三方登录成功，取消 ，错误。
     */
    private class BaseUiListener implements IUiListener {

        public void onCancel() {
            mLoginProgressBar.setVisibility(View.GONE);
        }

        public void onComplete(Object response) {
            JSONObject obj = (JSONObject) response;
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                openidID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openidID);
                mTencent.setAccessToken(accessToken, expires);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像什么的，这个时候怎么办？
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(mContext, qqToken);
            //这样我们就拿到这个类了，之后的操作就跟上面的一样了，同样是解析JSON
            info.getUserInfo(new IUiListener() {
                public void onComplete(final Object response) {
                    bundle = new Bundle();
//                    mHandler.obtainMessage(0, response).sendToTarget();
                    /**由于图片需要下载所以这里使用了线程，如果是想获得其他文字信息直接在mHandler里进行操作*/
                    new Thread() {
                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            Log.e("333333",(response).toString());
                            try {
                                user_openid = openidID;
                                user_name = json.getString("nickname");
                                user_tel = "";
                                String gender = json.getString("gender");
                                if (gender.equals("女")){
                                    user_sex = "2";
                                }else {
                                    user_sex = "1";
                                }
                                user_pwd = "";
                                user_headimgurl = json.getString("figureurl_qq_2");
                                user_province = json.getString("province");
                                user_city = json.getString("city");
                                created_at = System.currentTimeMillis();

                                OkHttpManager.getInstence().postNet(QQ_LOGIN_SEND_OPEN_ID, new OkHttpManager.ResultCallback() {
                                    @Override
                                    public void onFailed(Request request, IOException e) {
                                        Log.e(TAG, "onFailed: QQ 提交数据给服务器"+request.toString());
                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        Log.e(TAG, "onSuccess: QQ 提交数据给服务器"+user_openid);
                                        Log.e(TAG, "onSuccess: QQ 提交数据给服务器"+response+user_openid+user_name+user_tel+user_sex+user_pwd+user_headimgurl+user_province+
                                        user_city+
                                        created_at);
                                        QQLoginInfoBean qqLoginInfoBean = null;
                                        try {
                                            Gson gson = new Gson();
                                            qqLoginInfoBean = gson.fromJson(response,QQLoginInfoBean.class);
                                        } catch (JsonSyntaxException e) {
                                            e.printStackTrace();
                                        }
                                        if (null != qqLoginInfoBean){
                                            QQLoginInfoBean.DataBean.UserInfoBean userInfoBean = null;
                                            userInfoBean = qqLoginInfoBean.getData().getUser_info().get(0);
                                            if (null != userInfoBean){
                                                if (qqLoginInfoBean.getMessage_code().equals(NET_STATE_SUCCESS))
//                                                    mHandler.obtainMessage(1, bundle).sendToTarget();
                                                    loginSuccessFunction(qqLoginInfoBean.getClass(),qqLoginInfoBean);
                                            }
                                        }
                                    }
                                },new OkHttpManager.Param("user_openid",user_openid),new OkHttpManager.Param("user_name",user_name),
                                        new OkHttpManager.Param("user_tel",user_tel),new OkHttpManager.Param("user_sex",user_sex),
                                        new OkHttpManager.Param("user_pwd",user_pwd),new OkHttpManager.Param("user_headimgurl",user_headimgurl),
                                        new OkHttpManager.Param("user_province",user_province),new OkHttpManager.Param("user_city",user_city),
                                        new OkHttpManager.Param("created_at",String.valueOf(created_at)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

                @Override
                public void onError(UiError uiError) {

                }

                public void onCancel() {
                    mLoginProgressBar.setVisibility(View.GONE);
                }
            });
        }

        public void onError(UiError arg0) {
        }
    }

//    public void qqShareFriends() {
//        if (mTencent == null) {
//            mTencent = Tencent.createInstance(QQ_APP_ID,mContext);
//        }
//        Bundle params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, "见声听见");
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"便携式手机听力计");
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,"http://blog.csdn.net/u013451048");
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://avatar.csdn.net/C/3/D/1_u013451048.jpg");
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "SV");
//        mTencent.shareToQQ(this, params, new LoginActivity.BaseUiListener());
//    }

    /**
     * 注册成功的数据返回至此
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE ||
                    resultCode == Constants.REQUEST_QZONE_SHARE ||
                    resultCode == Constants.REQUEST_OLD_SHARE) {
                mTencent.handleResultData(data, mIUiListener);
            }
        }

        if (requestCode == PERMISSIONS_GRANTED){//动态申请的权限
            Toast.makeText(this,"所需权限全部授权",Toast.LENGTH_SHORT);
//            switch (resultCode){
//                case PERMISSIONS_GRANTED:
//                    Toast.makeText(this,"所需权限全部授权",Toast.LENGTH_SHORT);
//                    break;
//                case PermissionsActivity.PERMISSIONS_DENIED:
//                    // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
//                    ToastUtil.showShortToastCenter("关闭页面, 缺少主要权限, 无法运行");
//                    finish();
//                    break;
//            }
        }

        switch (resultCode){
            case RESULT_OK:
                if(data!=null){
                    //是获取注册界面回传过来的用户名
                    // getExtra().getString("***");
                    String userName = data.getStringExtra("userName");
                    if(!TextUtils.isEmpty(userName)){
                        //设置用户名到 et_user_name 控件
                        et_user_tel.setText(userName);
                        //et_user_name控件的setSelection()方法来设置光标位置
                        et_user_tel.setSelection(userName.length());
                        et_psw.setText("");
                    }
                }
                break;
        }
    }

    private long exittime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exittime < 2000){//小于2000ms则认为是用户确实希望关闭程序-调用System.exit()方法进行退出
//            super.onBackPressed();
//            ActivityStackManager.getActivityStackManager().popAllActivity();
//            System.exit(0);  会出现APP重启的情况  这里不能用
            SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
            finishAffinity();
        }else {
            ToastUtil.showShortToast(getString(R.string.string_exit));
            exittime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}