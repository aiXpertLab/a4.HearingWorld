package com.seeingvoice.www.svhearing;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.nanchen.crashmanager.UncaughtExceptionHandlerImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.seeingvoice.www.svhearing.beans.BaseBean;
import com.seeingvoice.www.svhearing.beans.QQLoginInfoBean;
import com.seeingvoice.www.svhearing.beans.TelLoginBean;
import com.seeingvoice.www.svhearing.beans.WechatLoginInfoBean;
import com.seeingvoice.www.svhearing.login.LoginActivity;
import com.seeingvoice.www.svhearing.util.AudioUtil;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;
import com.seeingvoice.www.svhearing.util.SpObjectUtil;


public class MyApplication extends Application{
    //public static final String DB_NAME = "mPureTestResult.db";
    //数据库操作
//    private AppDaoManager mAppDaoManager;
//    private DaoSession mDaoSession;

    public static int screenWidth,screenHeight,volume,verbal_music_volume,height,width,densityDpi;
    public static float density;
    public static boolean isLogin = false; //标记用户登录成功（账号密码，第三方登录）
    public static boolean isFirstLogin = true; //标记用户首次登录登录成功（账号密码，第三方登录）
    public static boolean isFirstLaunch = true; //标记APP首次启动
    public static String versionName = ""; //标记APP首次启动
    public static Long currentTimeStamp ; //标记用户首次登录登录成功（账号密码，第三方登录）
    public static int loginType = -1; //标记用户登录成功（账号密码，第三方登录）
    public static int userId = 3;
    public static int HzNums = 3;
    //三种登录方式 应该存储在SP中，程序启动时，调出，做登录保持
    public static TelLoginBean mTelLoginBean;
    public static WechatLoginInfoBean mWechatLoginInfoBean;
    public static QQLoginInfoBean mQQLoginInfoBean;
//    public static OkHttpManager mOkHttpManager;
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
//    public AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    // com.xiaomi.mipushdemo
    public static final String TAG = "MiPushBroadcastReceiver";
    //获取系统上下文，用于Toastutil类
    public static Context getAppContext(){
        return myApplication;
    }
    private List<Activity> activityList = new LinkedList<Activity>();
    private final static String PROCESS_NAME = "com.test";
    private static MyApplication myApplication = null;

    //主线程消息分发
    private static HandlerListener mListener;
    public static void setOnHandlerListener(HandlerListener listener) {
        mListener = listener;
    }
    public interface HandlerListener {
        void handleMessage(Message msg);
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 347:
                    mListener.handleMessage(msg);
                    break;
            }
        }
    };
    private List<Activity> activities = null;

    public static MyApplication getApplication() {
        return myApplication;
    }

    /** 判断是不是UI主进程，因为有些东西只能在UI主进程初始化 */
    public static boolean isAppMainProcess() {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(MyApplication.getApplication(), pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if (process.equalsIgnoreCase(myApplication.getPackageName())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /** 根据Pid得到进程名 */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        getScreen(myApplication);//得到屏幕尺寸
        volume = initVolumn();//得到纯音测试的 系统音量

        //初始化Activity集合
//        activities = new ArrayList<>();
//        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
//        registerActivityListener();//在Application中有一个registerActivityLifecycleCallbacks方法，实现注册Activity生命周期回调
        // 禁止重启
        UncaughtExceptionHandlerImpl.getInstance().init(this,BuildConfig.DEBUG);//release的时候需要改一下
        if (isAppMainProcess()) {//在主线程中初始化资源
            Log.e("监控", "Application 监控几次访问");
            closeAndroidPDialog();
            SharedPreferencesHelper.init(myApplication);//初始化SP
            isFirstLaunch = (Boolean) SharedPreferencesHelper.getInstance().getData("isFirstLaunch",true);
            if (isFirstLaunch){//控制权限页面显示
            }else {
                SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
                Intent intent = new Intent(myApplication, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myApplication.startActivity(intent);
            }
            isFirstLogin = (Boolean) SharedPreferencesHelper.getInstance().getData("isFirstLogin",true);//SP中取出 是否是首次登录 默认为真
            if (isFirstLogin){//如果是首次登录，SP记录首次登录时间
                SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                SharedPreferencesHelper.getInstance().saveData("isFirstTime",System.currentTimeMillis());//默认设置首次登录时间为当前
            }else {//如果不是首次登录，计算时间差，超过15天再设置为首次登录（取消免登录）
                Long currentTimeStamp = System.currentTimeMillis();
                Long firstLoginTime = (Long)SharedPreferencesHelper.getInstance().getData("isFirstTime",currentTimeStamp);
                Double distanceTime = Math.floor((currentTimeStamp - firstLoginTime)/86400);//记录登录时间与第一次登录的时间差 单位：天

                if (distanceTime > 15){
                    MyApplication.isFirstLogin = true;//再设置标记 isFirstLogin （首次登录）为真
                    SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                    SharedPreferencesHelper.getInstance().saveData("isFirstTime",System.currentTimeMillis());//默认设置首次登录时间为当前
                }else {
                    Integer type = (int)SharedPreferencesHelper.getInstance().getData("loginType",0);
                    switch (type){
                        case AppConstant.PHONE_LOGIN:
                            MyApplication.mTelLoginBean = SpObjectUtil.getObject(myApplication,TelLoginBean.class);
                            MyApplication.setLoginSuccess(true, AppConstant.PHONE_LOGIN,MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUid(),MyApplication.mTelLoginBean,firstLoginTime);
                            break;
                        case AppConstant.WECHAT_LOGIN:
                            MyApplication.mWechatLoginInfoBean = SpObjectUtil.getObject(myApplication, WechatLoginInfoBean.class);
                            MyApplication.setLoginSuccess(true, AppConstant.WECHAT_LOGIN,MyApplication.mWechatLoginInfoBean.getData().getUser_info().get(0).getUid(),MyApplication.mWechatLoginInfoBean,firstLoginTime);
                            break;
                        case AppConstant.QQ_LOGIN:
                            MyApplication.mQQLoginInfoBean = SpObjectUtil.getObject(myApplication,QQLoginInfoBean.class);
                            MyApplication.setLoginSuccess(true, AppConstant.QQ_LOGIN,MyApplication.mQQLoginInfoBean.getData().getUser_info().get(0).getUid(),MyApplication.mQQLoginInfoBean,firstLoginTime);
                            break;
                    }
                }
            }
        }
//        initUM();//初始化友盟推送
    }


    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            //下面为调试用的代码，发布时可注释
            Writer info = new StringWriter();
            PrintWriter printWriter = new PrintWriter(info);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            //自定义的处理类中关闭栈中的activity，然后杀死当前app进程。此处result 是崩溃异常的信息。
            String result = info.toString();
            Log.i("sss",result);
            for (int i=0;i<activities.size();i++){
                Log.i("sss",activities.get(i).getLocalClassName());
                if (activities.get(i)!=null)
                    activities.get(i).finish();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };


    /**
     *   基类中，在oncreate中 把activity添加到list数组中，再在ondestry中删除remove这个activity
     *   此处采用atcivity生命回调监听方法，在android14版本以上有效。
     */
    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MyApplication.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
                    activities.add(activity);
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null==activities&&activities.isEmpty()){
                        return;
                    }
                    if (activities.contains(activity)){
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
                        activities.remove(activity);
                    }
                }
            });
        }
    }

    /**
     * 绕过登录页面跳转到新页面
     */
    private void toMainActivity() {
        Intent intent = new Intent(myApplication, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myApplication.startActivity(intent);
    }

//    private void initUM() {
//        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
//        // 参数一：当前上下文context；
//        // 参数二：应用申请的Appkey（需替换）；
//        // 参数三：渠道名称；
//        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
//        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
//        UMConfigure.init(this, "5d316eee0cafb228e4000dd8", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "5cd501431b80f1ce9127e0b159d75a15");
//        //获取消息推送代理示例
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.setResourcePackageName("com.seeingvoice.www.svheard");
//        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//                Log.i("222222","注册成功：deviceToken：-------->  " + deviceToken);
//            }
//            @Override
//            public void onFailure(String s, String s1) {
//                Log.e("222222","注册失败：-------->  " + "s:" + s + ",s1:" + s1);
//            }
//        });
//        MiPushRegistar.register(this,AppConstant.XIAOMI_ID,AppConstant.XIAOMI_KEY);
//        HuaWeiRegister.register(this);
//        VivoRegister.register(this);
//        closeAndroidPDialog();
//    }


//    public static String getProcessName(Context cxt, int pid) {
//        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
//        if (runningApps == null) {
//            return null;
//        }
//        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
//            if (procInfo.pid == pid) {
//                return procInfo.processName;
//            }
//        }
//        return null;
//    }

    public static void setLoginSuccess(boolean is_Login, int login_Type, int user_id, BaseBean baseBean, Long TimeStamp){
        loginType = login_Type;
        isLogin = is_Login;
        userId = user_id;
        SharedPreferencesHelper.getInstance().saveData("isFirstLogin",false);//以后不是第一次登录了
        SharedPreferencesHelper.getInstance().saveData("isFirstTime",TimeStamp);//第一次登录的时间
        SharedPreferencesHelper.getInstance().saveData("loginType",login_Type);//登录类型
        switch (loginType){
            case AppConstant.PHONE_LOGIN:
                mTelLoginBean = (TelLoginBean) baseBean;
                SpObjectUtil.putObject(myApplication,mTelLoginBean);
                break;
            case AppConstant.WECHAT_LOGIN:
                mWechatLoginInfoBean = (WechatLoginInfoBean) baseBean;
                SpObjectUtil.putObject(myApplication,mWechatLoginInfoBean);
                break;
            case AppConstant.QQ_LOGIN:
                mQQLoginInfoBean = (QQLoginInfoBean) baseBean;
                SpObjectUtil.putObject(myApplication,mQQLoginInfoBean);
                break;
        }
    }

    public static void setVerbal_music_volume(int volume) {
        MyApplication.verbal_music_volume = volume;
        /** 发送消息给主线程，通知开启APP前已经连接了目标耳机   开始*/
        Message msg = new Message();
        msg.what = 347;
        Bundle b = new Bundle();
        b.putInt("verbal_music_volume",verbal_music_volume);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

//    private boolean shouldInit() {
//        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
//        String mainProcessName = getPackageName();
//        int myPid = Process.myPid();
//        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
//            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 解决在Android P上的提醒弹窗 （Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
     * Android 9.0以后，打开APP开始出现以上提示，出现这种情况的原因是：
     * Android P 后谷歌限制了开发者调用非官方公开API 方法或接口，也就是说，你用反射直接调用源码就会有这样的提示弹窗出现，非 SDK 接口指的是 Android 系统内部使用、并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口。但是，这么做是很危险的：非 SDK 接口没有任何公开文档，必须查看源代码才能理解其行为逻辑。
     */
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /** 获得屏幕的宽和高*/
//    public void getScreen(Context context) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(dm);
//         width = dm.widthPixels;         // 屏幕宽度（像素）
//         height = dm.heightPixels;       // 屏幕高度（像素）
//         density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//         densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
//        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
//        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
//        screenHeight = (int) (height / density);// 屏幕高度(dp)
//    }

    /** 获得屏幕的宽和高*/
    public void getScreen(Context context) {
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
//        Log.e(TAG + " DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);
//        Log.e(TAG + " DisplayMetrics", "density=" + density + "; densityDPI=" + densityDPI);
        screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
        screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
//        Log.e(TAG + " DisplayMetrics(111)", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
    }

    @Override
    public void onTerminate() {//查资料说，这个方法 系统杀死进程时，不一定调用，后面再想办法吧  这不太确定
        super.onTerminate();
    }
    /**
     * 得到系统音频流音量，就不用设置音量50%,纯音测试音量控制
     */
    private int initVolumn() {
//        volume = AudioUtil.getInstance(this).getSystemMaxVolume();
        volume = AudioUtil.getInstance(this).getMaxMediaVolume()/2;
        Log.e("音量哈哈哈", "initVolumn: "+volume);
//        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        return mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);// 获取系统音量
        return volume;
    }

    /** 初始化GreenDao数据库*/
//    private void initGreenDao() {
//        //数据库操作
//        mAppDaoManager = AppDaoManager.getInstance();
//        mAppDaoManager.init(this);
//        mDaoSession = mAppDaoManager.getDaoSession();
//    }

    /** 得到数据库操作类 DaSession*/
//    public DaoSession getmDaoSession(){
//        return mDaoSession;
//    }
//
//    public static String getIMEI(Context context, int slotId) {
//        try {
//            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            Method method = manager.getClass().getMethod("getImei", int.class);
//            String imei = (String) method.invoke(manager, slotId);
//            return imei;
//        } catch (Exception e) {
//            return "";
//        }
//    }
}
