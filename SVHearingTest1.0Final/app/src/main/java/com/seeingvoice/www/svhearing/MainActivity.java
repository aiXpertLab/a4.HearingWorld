package com.seeingvoice.www.svhearing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mingle.widget.ShapeLoadingDialog;
import com.seeingvoice.www.svhearing.base.AntiShakeUtils;
import com.seeingvoice.www.svhearing.base.BaseActivity;
import com.seeingvoice.www.svhearing.base.util.ActivityStackManager;
import com.seeingvoice.www.svhearing.beans.AppUpdateBean;
import com.seeingvoice.www.svhearing.beans.QQLoginInfoBean;
import com.seeingvoice.www.svhearing.beans.TelLoginBean;
import com.seeingvoice.www.svhearing.beans.WechatLoginInfoBean;
import com.seeingvoice.www.svhearing.heartests.hearAgeTest.hearAgeTestActivity;
import com.seeingvoice.www.svhearing.heartests.hearing_aid.HearingAidActivity;
import com.seeingvoice.www.svhearing.heartests.newpuretest.newPureTestActivity;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities.musicPlayActivity;
import com.seeingvoice.www.svhearing.history.HistroryActivity;
import com.seeingvoice.www.svhearing.login.GiveFeedbackActivity;
import com.seeingvoice.www.svhearing.login.LoginActivity;
import com.seeingvoice.www.svhearing.noisetest.SPLMeterActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.share.ShareUtil;
import com.seeingvoice.www.svhearing.ui.SelfDialog;
import com.seeingvoice.www.svhearing.ui.headImgView;
import com.seeingvoice.www.svhearing.util.AlertDialogUtil;
import com.seeingvoice.www.svhearing.util.SPHelper;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;
import com.seeingvoice.www.svhearing.util.SpObjectUtil;
import com.seeingvoice.www.svhearing.util.TDevice;
import com.seeingvoice.www.svhearing.util.ToastUtil;
import com.seeingvoice.www.svhearing.util.UpdateManager;
import com.seeingvoice.www.svhearing.wxapi.mIUiListener;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;

import static com.seeingvoice.www.svhearing.AppConstant.ALEART_DIALOG_REQUEST_CODE;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getName();
    private static final int REFRESH_COMPLETE = 0x006;

    private int[] mIcons = new int[]{ R.mipmap.ic_noise
            , R.mipmap.ic_age_test
            , R.mipmap.history_icon
            , R.mipmap.ic_hear_assists, R.mipmap.ic_music_assist
            , R.mipmap.invite_icon, R.mipmap.feedback_icon
            , R.mipmap.about_us_icon, R.mipmap.ic_logout,R.mipmap.quit_icon};
         // , R.mipmap.ic_verbal_test, R.mipmap.ic_hear_assist
    private String[] mContents = new String[]{"噪音检测","听龄测试","测试记录", "助听器", "音乐助听", "邀请好友", "意见反馈","关于我们","退出登录","关闭程序"};

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mLvItem;
    private DrawerItemAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;

    private List<Integer> dataList;
    private TextView tv_Name,tv_personal_info,tv_Version_Code;
    private ImageView mDelImgView,ImgHeader;
    private IWXAPI iwxapi;


    private AlertDialogUtil alertDialog;
    private ProgressBar mProgressBar;
    private Intent mIntent;
    private WechatLoginInfoBean mWechatLoginInfoBean;
    private QQLoginInfoBean mQQLoginInfoBean;
    private int login_type = -1;
    private int user_id = -1;
    private String user_name;
    private Bitmap headBitmap;
    private headImgView headImg;
    private Handler mHandler = new Handler();

    private Button mBtnPureTest;
    private TextView mTvVerbalTest,mTvAgeTest,mTvNoiseTest,mTvAsis;

    //软件升级
    private ShapeLoadingDialog shapeLoadingDialog;
    private OkHttpManager mOkHttpManager;
    private AppUpdateBean appUpdateBean = null;
    private  AppUpdateBean.DataBean.InfoBean appUpdateInfoBean;
    //升级update
    private String BANBENHAO = "";//被忽略的版本号
    private boolean isFirst;//判断发现新版本后是否是第一次弹出升级框
    private SelfDialog selfDialog;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_drawer_layout);
        mBtnPureTest = findViewById(R.id.btn_start_pure_test);

        mBtnPureTest.setOnClickListener(this);


        Log.e("监控", "MainActivity 监控几次访问");

        /** 左侧抽屉*/
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLvItem = findViewById(R.id.lv_item);
        mAdapter = new DrawerItemAdapter();
        mLvItem.setAdapter(mAdapter);
        mToolbar = findViewById(R.id.tool_bar);
        tv_Name = findViewById(R.id.tv_name);
        headImg = findViewById(R.id.img_header);
        mOkHttpManager = OkHttpManager.getInstence();
        tv_personal_info = findViewById(R.id.edit_personal_info);
        tv_personal_info.setOnClickListener(this);
        tv_Version_Code = findViewById(R.id.tv_version_code);
        tv_Version_Code.setText("版本号："+MyApplication.versionName);
        InitToolbar();        //初始化标题栏
        leftDrawerFunction();        //设置左侧抽屉点击事件
        initLoginInfo();        //得到登录信息
        checkApkUpdate(TDevice.getVersionCode());//检查当前是否有新版本
    }

    /** 检查当前是否有新版本*/
    private void checkApkUpdate(int versionCode) {
        mOkHttpManager.getNet(AppConstant.URL_APK_UPDATE+"?version="+versionCode, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Log.e("789456","APK更新访问失败");
            }

            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                try {
                    appUpdateBean = gson.fromJson(response, AppUpdateBean.class);
                    if (appUpdateBean == null){
                        return;
                    }
                    if (appUpdateBean.getMessage_code().equals(AppConstant.NET_STATE_SUCCESS)){
                        switch (appUpdateBean.getError_code()){
                            case AppConstant.ERROR_CODE_UPDATE_IS_NEWEST:// 当前已经是最新版本
                                Log.e("789456","当前已经是最新版本，无需更新");
                                break;
                            case AppConstant.ERROR_CODE_FIND_UPDATE_NO_FORCE:// 检测到新版本 不需要强制升级
                                ToastUtil.showShortToastCenter("检测到新版本"+appUpdateBean.getData().getInfo().get(0).getVersion()+"是否强制："+appUpdateBean.getData().getInfo().get(0).getIs_force_update());
                                appUpdateInfoBean = appUpdateBean.getData().getInfo().get(0);
                                appUpdateInfoBean.setIs_force_update(0);
                                if (null != appUpdateInfoBean){
                                    updateApp(appUpdateInfoBean);
                                }
                                break;
                            case AppConstant.ERROR_CODE_FIND_UPDATE_FORCE:// 检测到新版本 需要强制升级
                                appUpdateInfoBean = appUpdateBean.getData().getInfo().get(0);
                                appUpdateInfoBean.setIs_force_update(1);
                                if (null != appUpdateInfoBean){
                                    updateApp(appUpdateInfoBean);
                                }
                                break;
                            case AppConstant.ERROR_CODE_UPDATE_NOT_IN_DATABASE:// 用户传上来的版本号在数据库中查不到
                                Log.e("789456","用户传上来的版本号在数据库中查不到");
                                ToastUtil.showShortToastCenter("版本号在数据库中查不到");
                                break;
                            case AppConstant.ERROR_CODE_UPDATE_DATABASE:// 数据库错误
                                Log.e("789456","检测到新版本 不需要强制升级");
                                ToastUtil.showShortToastCenter("数据库错误！");
                                break;
                        }
                    }else {
                        Log.e("789456","appUpdateBean.getMessage_code()"+appUpdateBean.getMessage_code());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化标题栏
    private void InitToolbar() {
        /** toolbar 开始*/
        /** 注意，设置 Toolbar 及相关点击事件最好放在 setSupportActionBar 后，否则很可能无效
        设置 Navigation 图标和点击事件必须放在 setSupportActionBar 后，否则无效*/
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
//        mToolbar.setRight(R.mipmap.more_icon);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("见声听见");
//        tvTitle.setVisibility(View.GONE);
        //抽屉开关
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        //通过设置toolbar进行监听,在setSupportActionBar(Toolbar toolbar)之前设置可能会失效.
        /** 菜单和 标题栏显示可能会冲突*/
        mToolbar.setNavigationIcon(R.mipmap.ic_gerencenter);
//        mToolbar.setLogo(R.drawable.ic_index_sv_logo);
//        mToolbar.setLogoDescription("见声科技");
        /** toolbar 结束*/
    }

    /** 判断登录方式，显示*/
    private void initLoginInfo() {
        mIntent = getIntent();
        if (MyApplication.isLogin){
            switch (MyApplication.loginType){
                case AppConstant.PHONE_LOGIN:
                    MyApplication.mTelLoginBean = SpObjectUtil.getObject(MyApplication.getAppContext(), TelLoginBean.class);
                    tv_Name.setText(MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_tel()+"您好！");
                    if (MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_sex() == 1){
                        headImg.setImageResource(R.drawable.ic_woman);
                    }else {
                        headImg.setImageResource(R.drawable.ic_man);
                    }
                    break;
                case AppConstant.WECHAT_LOGIN:
                    mWechatLoginInfoBean = SpObjectUtil.getObject(MyApplication.getAppContext(), WechatLoginInfoBean.class);
                    String URL = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
                    login_type = MyApplication.loginType;
                    user_id = MyApplication.userId;
                    user_name = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(URL);
                    break;
                case AppConstant.QQ_LOGIN:
                    mQQLoginInfoBean = SpObjectUtil.getObject(MyApplication.getAppContext(),QQLoginInfoBean.class);
                    String url = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
                    login_type = MyApplication.loginType;
                    user_id = MyApplication.userId;
                    user_name = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(url);
                    break;
            }
        }
    }

    /**
     * 左侧抽屉的点击事件 start
     */
    private void leftDrawerFunction() {
        SharedPreferencesHelper.init(MyApplication.getAppContext());
        //左侧抽屉列表点击事件
        mLvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Bundle bundle1 = new Bundle();
                        bundle1.putBoolean("fromIndex",true);
                        toNextActivity(bundle1,MainActivity.this, SPLMeterActivity.class);
                        break;
                    case 1:
                        if (MyApplication.loginType == AppConstant.PHONE_LOGIN){
                            toNextActivity(null,MainActivity.this, hearAgeTestActivity.class);
                        }else {
                            String flag = new String();
                            if (MyApplication.loginType == AppConstant.QQ_LOGIN){
                                flag = (String)MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                            }
                            if (MyApplication.loginType == AppConstant.WECHAT_LOGIN){
                                flag = (String) MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();

                            }
                            if (!flag.isEmpty() && flag.length() == 11){
                                toNextActivity(null, MainActivity.this, hearAgeTestActivity.class);
                            }else{
                                ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                bondPhoneFunction();
                            }
                        }
                        break;
//                    case 2:
//                        if (MyApplication.loginType == AppConstant.PHONE_LOGIN){
//                            toNextActivity(null,MainActivity.this, DisclaimerActivity.class);
//                        }else {
//                            if (MyApplication.loginType == AppConstant.QQ_LOGIN){
//                                String flag = (String)MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
//                                if (!flag.isEmpty() && flag.length() == 11){
//                                    toNextActivity(null, MainActivity.this, DisclaimerActivity.class);
//                                }else{
//                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
//                                    bondPhoneFunction();
//                                }
//                            }
//                            if (MyApplication.loginType == AppConstant.WECHAT_LOGIN){
//                                String flag = (String) MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
//                                if (!flag.isEmpty() && flag.length() == 11){
//                                    toNextActivity(null,MainActivity.this, DisclaimerActivity.class);
//                                }else{
//                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
//                                    bondPhoneFunction();
//                                }
//                            }
//                        }
//                        break;
                    case 2:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null, MainActivity.this, HistroryActivity.class);
                        }
                        break;
                    case 3:
                        if (MyApplication.loginType == AppConstant.PHONE_LOGIN){
                            toNextActivity(null,MainActivity.this, HearingAidActivity.class);
                        }else {
                            if (MyApplication.loginType == AppConstant.QQ_LOGIN){
                                String flag = (String)MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                if (!flag.isEmpty() && flag.length() == 11){
                                    toNextActivity(null, MainActivity.this, HearingAidActivity.class);
                                }else{
                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                    bondPhoneFunction();
                                }
                            }
                            if (MyApplication.loginType == AppConstant.WECHAT_LOGIN){
                                String flag = (String) MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                if (!flag.isEmpty() && flag.length() == 11){
                                    toNextActivity(null, MainActivity.this, HearingAidActivity.class);
                                }else{
                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                    bondPhoneFunction();
                                }
                            }
                        }
                        break;
                    case 4:
                        if (MyApplication.loginType == AppConstant.PHONE_LOGIN){
                            toNextActivity(null,MainActivity.this, musicPlayActivity.class);
                        }else {
                            if (MyApplication.loginType == AppConstant.QQ_LOGIN){
                                String flag = (String)MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                if (!flag.isEmpty() && flag.length() == 11){
                                    toNextActivity(null, MainActivity.this, musicPlayActivity.class);
                                }else{
                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                    bondPhoneFunction();
                                }
                            }
                            if (MyApplication.loginType == AppConstant.WECHAT_LOGIN){
                                String flag = (String) MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                if (!flag.isEmpty() && flag.length() == 11){
                                    toNextActivity(null, MainActivity.this, musicPlayActivity.class);
                                }else{
                                    ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                    bondPhoneFunction();
                                }
                            }
                        }
                        break;
                    case 5:
                        if (!AntiShakeUtils.isInvalidClick(view,800)) {
                            ShareUtil.getInstance().shareFunction(MainActivity.this);
                        }
                        break;
                    case 6:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null,MainActivity.this, GiveFeedbackActivity.class);
                        }
                        break;
                    case 7:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null,MainActivity.this, AboutUsActivity.class);
                        }
                        break;
                    case 8:
                        if (ActivityStackManager.getActivityStackManager().checkActivity(LoginActivity.class))
                            ActivityStackManager.getActivityStackManager().popActivity(MainActivity.this);
                        else{
                            toNextActivity(null,MainActivity.this,LoginActivity.class);
                        }
                        SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                        break;
                    case 9:
                        SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
                        finishAffinity();
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /** 左侧抽屉的点击事件 end*/



    @Override
    public void onClick(View v) {
        if (!AntiShakeUtils.isInvalidClick(v,800)){
            switch (v.getId()){
                case R.id.btn_start_pure_test://绑定的操作
                    alertDialog = new AlertDialogUtil(MainActivity.this, "温馨提示：",getResources().getString(R.string.before_test_tips_str)
                            ,"确定", "取消",ALEART_DIALOG_REQUEST_CODE , mDialogListenner);
                    alertDialog.show();
                    break;
                case R.id.edit_personal_info:
                    toNextActivity(null,MainActivity.this,personal_info_activity.class);
                    break;
            }
        }
    }

    //微信绑定手机号
    private void bondPhoneFunction() {
        Intent mIntent = null;
        switch (login_type){
            case AppConstant.WECHAT_LOGIN://微信登录
            case AppConstant.QQ_LOGIN://QQ登录
                mIntent = new Intent(MainActivity.this, com.seeingvoice.www.svhearing.bondphone.WECHATbondPhoneActivity.class);
                break;
        }
        startActivity(mIntent);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 抽屉列表适配器
     */
    class DrawerItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
                return mContents.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_drawer, null);
                vh.ivIcon =  convertView.findViewById(R.id.iv_icon);
                vh.tvContent = convertView.findViewById(R.id.tv_content1);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            vh.ivIcon.setImageResource(mIcons[position]);
            vh.tvContent.setText(mContents[position]);
            return convertView;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvContent;
        }
    }


    /**
     *     没有蓝牙粗略定位权限，则提示没有该权限程序不能运行
     */
    private void showWaringDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIUiListener myListener = new mIUiListener();
        Tencent.onActivityResultData(requestCode,resultCode,data,myListener);
    }

    private AlertDialogUtil.OnDialogButtonClickListener mDialogListenner = new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            if (isPositive){
                switch (requestCode){
                    case ALEART_DIALOG_REQUEST_CODE:
                        if (isPositive) {
                            if (MyApplication.loginType == AppConstant.PHONE_LOGIN){
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("fromIndex",false);
                                toNextActivity(bundle,MainActivity.this, newPureTestActivity.class);
                            }else {
                                if (MyApplication.loginType == AppConstant.QQ_LOGIN){
                                    String flag = (String)MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                    if (flag.length() == 11){
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("fromIndex",false);
                                        toNextActivity(bundle,MainActivity.this, newPureTestActivity.class);
                                    }else{
                                        ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                        bondPhoneFunction();
                                    }
                                }
                                if (MyApplication.loginType == AppConstant.WECHAT_LOGIN){
                                    String flag = (String) MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                    if (flag.length() == 11){//!flag.isEmpty() && 去掉了
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("fromIndex",false);
                                        toNextActivity(bundle,MainActivity.this, newPureTestActivity.class);
                                    }else{
                                        ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                        bondPhoneFunction();
                                    }
                                }
                            }
                        }else {
                            alertDialog.dismiss();
                        }
                        break;
                    case 125:
                        Intent settintIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(settintIntent);
                        Toast.makeText(MainActivity.this, "进入设置页面删除蓝牙耳机", Toast.LENGTH_SHORT).show();
                        break;
                    case 126:
                    case 127:
                        alertDialog.dismiss();
                        break;
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//显示菜单
        getMenuInflater().inflate(R.menu.main_menu,menu);
//        menu.add(Menu.NONE,Menu.FIRST+1,1,"联系我们");
//        menu.add(Menu.NONE,Menu.FIRST+2,2,"qq空间");
//        menu.add(Menu.NONE,Menu.FIRST+3,3,"微信好友分享");
//        menu.add(Menu.NONE,Menu.FIRST+4,4,"微信朋友圈分享");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            ShareUtil.getInstance().shareFunction(MainActivity.this);
//        if (!AntiShakeUtils.isInvalidClick((View) item,800)) {
//        }
//        if (item.getItemId() == Menu.FIRST) {
//
//        }
        return false;
    }

//    @Override
//    public void onOptionsMenuClosed(Menu menu) {//菜单退出时调用
//        ToastUtil.showShortToastCenter("菜单项关闭了");
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {//菜单显示前调用
////        ToastUtil.showShortToastCenter("在菜单显示（onCreateOptionsMenu()方法）之前会调用此操作，可以在此操作之中完成一些预处理操作。");
//        return true;
//    }


    //广播监听接口回调 end

    private long exittime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exittime < 2000){//小于2000ms则认为是用户确实希望关闭程序-调用System.exit()方法进行退出
//            super.onBackPressed();
//            ActivityStackManager.getActivityStackManager().popAllActivity();
//            System.exit(0);会出现程序重启的情况，不能采用
            SharedPreferencesHelper.getInstance().getData("isFirstLogin",true);
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


    private double comparation(){
        double x = 10000;
        while (Math.sin(x) != x){
            x =Math.min(Math.sin(x),x);
        }
        return x;
    }
    //进行升级的代码块
    public void updateApp(AppUpdateBean.DataBean.InfoBean bean){
//        bean.setVersion("3");
//        bean.setIs_force_update(1);
//        bean.setUpdate_describe("强制升级");
        Log.e("565656","强制升级？"+bean.getIs_force_update());
        final String versionCode = bean.getVersion();//服务端获得版本号
        String downLoadUrl = "";
        if (bean.getFile_url() != null) {
            downLoadUrl = bean.getFile_url();
        }
        final String mFinalDownloadUrl = downLoadUrl;

        double serviceCode = Double.parseDouble(versionCode);
        double code = TDevice.getVersionCode();//现在APP版本号

        //从网上获取是否需强制升级 1 代表强制升级，0 代表非强制升级
        String isForceUpdate = "";
        if (bean.getIs_force_update() == 0 || bean.getIs_force_update() == 1) {
            isForceUpdate = String.valueOf(bean.getIs_force_update());
        }
        //从网上获取的更新信息内容
        String updateInfo = "";
        if (!bean.getUpdate_describe().equals("")) {
            updateInfo = bean.getUpdate_describe();//得到版本更新描述
        }
        final String updateDes = updateInfo;

        //保存从网上获取的serviceCode
        SPHelper.getInst().saveString("serviceCode", versionCode);
        //判断是否忽略过版本
        BANBENHAO = SPHelper.getInst().getString("BANBENHAO");
        if (!BANBENHAO.equals("")) {//如果没有被忽略的版本号
            double SERVICECD = Double.parseDouble(BANBENHAO);
            if (SERVICECD < serviceCode) {//忽略的版本号小于当前网络端版本号
                isFirst = true;
            } else {//忽略的版本号大于当前网络端版本号
                isFirst = false;
            }
            SPHelper.getInst().saveBoolean("isFirst", isFirst);
        } else {//有被忽略的版本号
            isFirst = true;
            BANBENHAO = versionCode;//当前服务端版本号赋值给 被忽略的版本号
            SPHelper.getInst().saveBoolean("isFirst", isFirst);
        }
        //判断发现新版本后是否是第一次弹出升级框
        isFirst = SPHelper.getInst().getBoolean("isFirst");
        //判断是否需要版本升级
        if (code != serviceCode && code < serviceCode) {
            SPHelper.getInst().saveString("downLoadUrl", downLoadUrl);
            if (isFirst || isForceUpdate.equals("1")) {//发现新版本后第一次弹出提示，或者需要强制升级时
                selfDialog = new SelfDialog(MainActivity.this, R.style.dialog, updateDes);
                selfDialog.show();
                selfDialog.setYesOnclickListener("立即升级", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        new UpdateManager(MainActivity.this, MainActivity.this, mFinalDownloadUrl);
                        selfDialog.dismiss();
                    }
                });

                //若强制升级显示
                if (isForceUpdate.equals("1")) {
                    selfDialog.setNoOnclickListener("退出", new SelfDialog.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            selfDialog.dismiss();
                            //结束APP
                            SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
                            finishAffinity();
                        }
                    });
                } else if (isForceUpdate.equals("0")) {
                    //若非强制升级时显示
                    selfDialog.setNoOnclickListener("忽略此次", new SelfDialog.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            isFirst = false;
                            SPHelper.getInst().saveBoolean("isFirst", isFirst);
                            //保存到本地
                            BANBENHAO = versionCode;
                            SPHelper.getInst().saveString("updateDes", updateDes);
                            SPHelper.getInst().saveString("BANBENHAO", BANBENHAO);
                            Log.e("8567", "onNoClick: 1 不需要升级");
                            selfDialog.dismiss();
                            //TODO Handler 不需要升级逻辑通知
//                            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                            ToastUtil.showShortToastCenter("不需要升级");
                        }
                    });
                }
            }
        }else{//APP跟服务端版本一样
            // TODO Handler 不需要升级逻辑通知
            ToastUtil.showShortToastCenter("当前为最新版本！");
        }
    }
}