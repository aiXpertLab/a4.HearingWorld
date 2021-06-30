package seeingvoice.jskj.com.seeingvoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
// import com.mingle.widget.ShapeLoadingDialog;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.base.util.ActivityStackManager;
import seeingvoice.jskj.com.seeingvoice.beans.AppUpdateBean;
import seeingvoice.jskj.com.seeingvoice.beans.QQLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.beans.WechatLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.bondphone.WECHATbondPhoneL;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.hearAgeTest.hearAgeTestL;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.L_PureToneTest;
import seeingvoice.jskj.com.seeingvoice.history.HistroryL;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_AboutUs;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_Feedback;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_SPLMeterL;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.share.ShareWXQQ;
import seeingvoice.jskj.com.seeingvoice.ui.SelfDialog;
import seeingvoice.jskj.com.seeingvoice.ui.headImgView;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;
import seeingvoice.jskj.com.seeingvoice.util.SPHelper;
import seeingvoice.jskj.com.seeingvoice.util.SharedPreferencesHelper;
import seeingvoice.jskj.com.seeingvoice.util.TDevice;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
import seeingvoice.jskj.com.seeingvoice.util.UpdateManager;
import seeingvoice.jskj.com.seeingvoice.wxapi.mIUiListener;

import static seeingvoice.jskj.com.seeingvoice.MyData.ALEART_DIALOG_REQUEST_CODE;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity_original extends MyBaseActivity implements View.OnClickListener {
    public static final String TAG = MainActivity_original.class.getName();
    private static final int REFRESH_COMPLETE = 0x006;

    private final int[] mIcons = new int[]{
            R.mipmap.ic_noise, R.mipmap.ic_age_test, R.mipmap.ic_history_icon, R.mipmap.invite_icon, R.mipmap.feedback_icon
            , R.mipmap.about_us_icon, R.mipmap.ic_logout,R.mipmap.quit_icon};
    private final String[] mContents = new String[]{"噪音检测","听龄测试","测试记录", "邀请好友", "意见反馈","查看权限","关于我们","退出登陆","关闭程序"};

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mLvItem;
    private DrawerItemAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;

    private List<Integer> dataList;
    private TextView tv_Name,tv_personal_info,tv_Version_Code; //sv,tv_User_Agreement,tv_Privacy_Policy;
    private ImageView mDelImgView,ImgHeader;
    private IWXAPI iwxapi;


    private AlertDialogUtil alertDialog;
    private ProgressBar mProgressBar;
    private Intent mIntent;
    private WechatLoginInfoBean mWechatLoginInfoBean;
    private QQLoginInfoBean mQQLoginInfoBean;
    private final int login_type = -1;
    private final int user_id = -1;
    private String user_name;
    private Bitmap headBitmap;
    private headImgView headImg;
    //private Handler mHandler = new Handler();

    private Button mBtnPureTest;
    private TextView mTvVerbalTest,mTvAgeTest,mTvNoiseTest,mTvAsis;

    //软件升级
    // private ShapeLoadingDialog shapeLoadingDialog;
    private OkHttpManager mOkHttpManager;
    private AppUpdateBean appUpdateBean = null;
    private  AppUpdateBean.DataBean.InfoBean appUpdateInfoBean;
    //升级update
    private String BANBENHAO = "";  //被忽略的版本号
    private boolean isFirst;        //判断发现新版本后是否是第一次弹出升级框
    private SelfDialog selfDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_drawer);
        mBtnPureTest = findViewById(R.id.btn_start_pure_test);
        mBtnPureTest.setOnClickListener(this);

        Log.e("监控", "MainActivity 监控几次访问");

        // 左侧抽屉
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLvItem = findViewById(R.id.lv_item);
        mAdapter = new DrawerItemAdapter();
        mLvItem.setAdapter(mAdapter);
        mToolbar = findViewById(R.id.tool_bar);
        tv_Name = findViewById(R.id.text_view_name);
        headImg = findViewById(R.id.img_header);
        mOkHttpManager = OkHttpManager.getInstence();
        tv_personal_info = findViewById(R.id.text_view_signature);
        tv_personal_info.setOnClickListener(this);
        //sv tv_Version_Code = findViewById(R.id.tv_version_code);
        //sv tv_Version_Code.setText("版本号："+MyApplication.versionName);
        //sv tv_User_Agreement = findViewById(R.id.tv_user_agreement);
        //sv tv_Privacy_Policy = findViewById(R.id.tv_privacy_policy);
        //sv tv_User_Agreement.setOnClickListener(this);
        //sv tv_Privacy_Policy.setOnClickListener(this);
        InitToolbar();        //初始化标题栏
        leftDrawerFunction();        //设置左侧抽屉点击事件
        initLoginInfo();        //得到登陆信息
        checkApkUpdate(TDevice.getVersionCode());//检查当前是否有新版本
    }

    /** 检查当前是否有新版本*/
    private void checkApkUpdate(int versionCode) {
        mOkHttpManager.getNet(MyData.URL_APK_UPDATE+"?version="+versionCode, new OkHttpManager.ResultCallback() {
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
                    if (appUpdateBean.getMessage_code().equals(MyData.NET_STATE_SUCCESS)){
                        switch (appUpdateBean.getError_code()){
                            case MyData.ERROR_CODE_UPDATE_IS_NEWEST:// 当前已经是最新版本
                                break;
                            case MyData.ERROR_CODE_FIND_UPDATE_NO_FORCE:// 检测到新版本 不需要强制升级
                                ToastUtil.showShortToastCenter("检测到新版本"+appUpdateBean.getData().getInfo().get(0).getVersion()+"是否强制："+appUpdateBean.getData().getInfo().get(0).getIs_force_update());
                                appUpdateInfoBean = appUpdateBean.getData().getInfo().get(0);
                                appUpdateInfoBean.setIs_force_update(0);
                                if (null != appUpdateInfoBean){
                                    updateApp(appUpdateInfoBean);
                                }
                                break;
                            case MyData.ERROR_CODE_FIND_UPDATE_FORCE:// 检测到新版本 需要强制升级
                                appUpdateInfoBean = appUpdateBean.getData().getInfo().get(0);
                                appUpdateInfoBean.setIs_force_update(1);
                                if (null != appUpdateInfoBean){
                                    updateApp(appUpdateInfoBean);
                                }
                                break;
                            case MyData.ERROR_CODE_UPDATE_NOT_IN_DATABASE:// 用户传上来的版本号在数据库中查不到
                                Log.e("789456","用户传上来的版本号在数据库中查不到");
                                ToastUtil.showShortToastCenter("版本号在数据库中查不到");
                                break;
                            case MyData.ERROR_CODE_UPDATE_DATABASE:// 数据库错误
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
        TextView tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("见声听力测试");

        //抽屉开关
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity_original.this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        //通过设置toolbar进行监听,在setSupportActionBar(Toolbar toolbar)之前设置可能会失效.
        // 菜单和 标题栏显示可能会冲突
        mToolbar.setNavigationIcon(R.mipmap.ic_gerencenter);
        // toolbar 结束
    }

    /** 判断登录方式，显示*/
    private void initLoginInfo() {
        mIntent = getIntent();
        tv_Name.setText("明心见性");     //sv
        headImg.setImageResource(R.drawable.ic_sv);
    }

    /**
     * 左侧抽屉的点击事件 start
     */
    private void leftDrawerFunction() {
        SharedPreferencesHelper.init(MyApp.getAppContext());
        //左侧抽屉列表点击事件
        mLvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Bundle bundle1 = new Bundle();
                        bundle1.putBoolean("fromIndex",true);
                        toNextActivity(bundle1, MainActivity_original.this, L_SPLMeterL.class);
                        break;
                    case 1:
                        if (MyApp.loginType == MyData.PHONE_LOGIN){
                            toNextActivity(null, MainActivity_original.this, hearAgeTestL.class);
                        }else {
                            String flag = new String();
                            if (MyApp.loginType == MyData.QQ_LOGIN){
                                flag = (String) MyApp.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                            }
                            if (MyApp.loginType == MyData.WECHAT_LOGIN){
                                flag = (String) MyApp.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();

                            }
                            if (!flag.isEmpty() && flag.length() == 11){
                                toNextActivity(null, MainActivity_original.this, hearAgeTestL.class);
                            }else{
                                ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                bondPhoneFunction();
                            }
                        }
                        break;
                    case 2:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null, MainActivity_original.this, HistroryL.class);
                        }
                        break;
                    case 3:
                        if (!AntiShakeUtils.isInvalidClick(view,800)) {
                            ShareWXQQ.getInstance().shareFunction(MainActivity_original.this);
                        }
                        break;
                    case 4:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null, MainActivity_original.this, L_Feedback.class);
                        }
                        break;
                    case 5:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            //TODO 去权限页面
                            // 启动应用的设置
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);

                                //跳转GPS设置界面 ACCESS_COARSE_LOCATION
                                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                //startActivityForResult(intent, AppConstant.NOT_NOTICE);
                        }
                        break;
                    case 6:
                        if (!AntiShakeUtils.isInvalidClick(view,800)){
                            toNextActivity(null, MainActivity_original.this, L_AboutUs.class);
                        }
                        break;
                    case 7:
                        if (ActivityStackManager.getActivityStackManager().checkActivity(Login.class))
                            ActivityStackManager.getActivityStackManager().popActivity(MainActivity_original.this);
                        else{
                            toNextActivity(null, MainActivity_original.this, Login.class);
                        }
                        SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                        break;
                    case 8:
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
                    alertDialog = new AlertDialogUtil(MainActivity_original.this, "温馨提示：",getResources().getString(R.string.global_agree)
                            ,"确定", "取消",ALEART_DIALOG_REQUEST_CODE , mDialogListenner);
                    alertDialog.show();
                    break;
            }
        }
    }

    //微信绑定手机号
    private void bondPhoneFunction() {
        Intent mIntent = null;
        switch (login_type){
            case MyData.WECHAT_LOGIN://微信登陆
            case MyData.QQ_LOGIN://QQ登陆
                mIntent = new Intent(MainActivity_original.this, WECHATbondPhoneL.class);
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
            return mIcons.length;
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
                convertView = LayoutInflater.from(MainActivity_original.this).inflate(R.layout.item_drawer, null);
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
                            if (MyApp.loginType == MyData.PHONE_LOGIN){
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("fromIndex",false);
                                toNextActivity(bundle, MainActivity_original.this, L_PureToneTest.class);
                            }else {
                                if (MyApp.loginType == MyData.QQ_LOGIN){
                                    String flag = (String) MyApp.mQQLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                    if (flag.length() == 11){
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("fromIndex",false);
                                        toNextActivity(bundle, MainActivity_original.this, L_PureToneTest.class);
                                    }else{
                                        ToastUtil.showShortToastCenter("开启该功能，请先绑定手机！");
                                        bondPhoneFunction();
                                    }
                                }
                                if (MyApp.loginType == MyData.WECHAT_LOGIN){
                                    String flag = (String) MyApp.mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_tel();
                                    if (flag.length() == 11){//!flag.isEmpty() && 去掉了
                                        Log.d("zyy","click verbalindexactivity");
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("fromIndex",false);
                                        toNextActivity(bundle, MainActivity_original.this, L_PureToneTest.class);
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
                        Intent settintIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(settintIntent);
                        Toast.makeText(MainActivity_original.this, "进入设置页面删除蓝牙耳机", Toast.LENGTH_SHORT).show();
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
            ShareWXQQ.getInstance().shareFunction(MainActivity_original.this);
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
            ToastUtil.showShortToast(getString(R.string.global_exit));
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
                selfDialog = new SelfDialog(MainActivity_original.this, R.style.dialog, updateDes);
                selfDialog.show();
                selfDialog.setYesOnclickListener("立即升级", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        new UpdateManager(MainActivity_original.this, MainActivity_original.this, mFinalDownloadUrl);
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