package seeingvoice.jskj.com.seeingvoice;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import java.util.LinkedList;
import java.util.List;

import seeingvoice.jskj.com.seeingvoice.beans.BaseBean;
import seeingvoice.jskj.com.seeingvoice.beans.QQLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.beans.TelLoginBean;
import seeingvoice.jskj.com.seeingvoice.beans.WechatLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L_AudioManager;
import seeingvoice.jskj.com.seeingvoice.util.SharedPreferencesHelper;
import seeingvoice.jskj.com.seeingvoice.util.SpObjectUtil;


public class MyApp extends Application{

    public static int screenWidth,screenHeight,volume,verbal_music_volume,height,width, densityDpi;
    public static float density;
    public static boolean isLogin = false; //标记用户登陆成功（账号密码，第三方登陆）
    public static boolean isFirstLogin = true; //标记用户首次登陆登陆成功（账号密码，第三方登陆）
    public static boolean isFirstLaunch = true; //标记APP首次启动
    public static String versionName = "";
    public static Long currentTimeStamp ; //标记用户首次登陆登陆成功（账号密码，第三方登陆）
    public static int loginType = -1; //标记用户登陆成功（账号密码，第三方登陆）
    public static int userId = 3;
    //三种登陆方式 应该存储在SP中，程序启动时，调出，做登陆保持
    public static TelLoginBean mTelLoginBean;
    public static WechatLoginInfoBean mWechatLoginInfoBean;
    public static QQLoginInfoBean mQQLoginInfoBean;
    public static final String TAG = "MiPushBroadcastReceiver";
    //获取系统上下文，用于Toastutil类
    public static Context getAppContext(){
        return myApplication;
    }
    private List<Activity> activityList = new LinkedList<>();
    private final static String PROCESS_NAME = "com.test";
    private static MyApp myApplication = null;

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
            if (msg.what == 347) {
                mListener.handleMessage(msg);
            }
        }
    };
    private List<Activity> activities = null;

    public static MyApp getApplication() {
        return myApplication;
    }

    /** 判断是不是UI主进程，因为有些东西只能在UI主进程初始化 */
    public static boolean isAppMainProcess() {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(MyApp.getApplication(), pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else return process.equalsIgnoreCase(myApplication.getPackageName());
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
//            closeAndroidPDialog();
            SharedPreferencesHelper.init(myApplication);    //初始化SP
            isFirstLaunch = (Boolean) SharedPreferencesHelper.getInstance().getData("isFirstLaunch",true);
            if (!isFirstLaunch){     //控制权限页面显示
                SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
                Intent intent = new Intent(myApplication, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myApplication.startActivity(intent);
            }
            isFirstLogin = (Boolean) SharedPreferencesHelper.getInstance().getData("isFirstLogin",true);//SP中取出 是否是首次登陆 默认为真
            if (isFirstLogin){//如果是首次登陆，SP记录首次登陆时间
                SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                SharedPreferencesHelper.getInstance().saveData("isFirstTime",System.currentTimeMillis());//默认设置首次登陆时间为当前
            }else {//如果不是首次登陆，计算时间差，超过15天再设置为首次登陆（取消免登录）
                Long currentTimeStamp = System.currentTimeMillis();
                Long firstLoginTime = (Long)SharedPreferencesHelper.getInstance().getData("isFirstTime",currentTimeStamp);
                double distanceTime = Math.floor((currentTimeStamp - firstLoginTime)/86400);//记录登录时间与第一次登陆的时间差 单位：天

                if (distanceTime > 15){
                    MyApp.isFirstLogin = true;//再设置标记 isFirstLogin （首次登陆）为真
                    SharedPreferencesHelper.getInstance().saveData("isFirstLogin",true);
                    SharedPreferencesHelper.getInstance().saveData("isFirstTime",System.currentTimeMillis());//默认设置首次登陆时间为当前
                }else {
                    int type = (int)SharedPreferencesHelper.getInstance().getData("loginType",0);
                    switch (type){
                        case MyData.PHONE_LOGIN:
                            MyApp.mTelLoginBean = SpObjectUtil.getObject(myApplication,TelLoginBean.class);
                            MyApp.setLoginSuccess(true, MyData.PHONE_LOGIN, MyApp.mTelLoginBean.getData().getUser_info().get(0).getUid(), MyApp.mTelLoginBean,firstLoginTime);
                            break;
                        case MyData.WECHAT_LOGIN:
                            MyApp.mWechatLoginInfoBean = SpObjectUtil.getObject(myApplication, WechatLoginInfoBean.class);
                            MyApp.setLoginSuccess(true, MyData.WECHAT_LOGIN, MyApp.mWechatLoginInfoBean.getData().getUser_info().get(0).getUid(), MyApp.mWechatLoginInfoBean,firstLoginTime);
                            break;
                        case MyData.QQ_LOGIN:
                            MyApp.mQQLoginInfoBean = SpObjectUtil.getObject(myApplication,QQLoginInfoBean.class);
                            MyApp.setLoginSuccess(true, MyData.QQ_LOGIN, MyApp.mQQLoginInfoBean.getData().getUser_info().get(0).getUid(), MyApp.mQQLoginInfoBean,firstLoginTime);
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
        MyApp.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //  监听到 Activity创建事件 将该 Activity 加入list
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
                //  监听到 Activity销毁事件 将该Activity 从list中移除
                activities.remove(activity);
            }
        });
    }

    /**
     * 绕过登陆页面跳转到新页面
     */
    private void toMainActivity() {
        Intent intent = new Intent(myApplication, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myApplication.startActivity(intent);
    }

    public static void setLoginSuccess(boolean is_Login, int login_Type, int user_id, BaseBean baseBean, Long TimeStamp){
        loginType = login_Type;
        isLogin = is_Login;
        userId = user_id;
        SharedPreferencesHelper.getInstance().saveData("isFirstLogin",false);//以后不是第一次登陆了
        SharedPreferencesHelper.getInstance().saveData("isFirstTime",TimeStamp);//第一次登陆的时间
        SharedPreferencesHelper.getInstance().saveData("loginType",login_Type);//登陆类型
        switch (loginType){
            case MyData.PHONE_LOGIN:
                mTelLoginBean = (TelLoginBean) baseBean;
                SpObjectUtil.putObject(myApplication,mTelLoginBean);
                break;
            case MyData.WECHAT_LOGIN:
                mWechatLoginInfoBean = (WechatLoginInfoBean) baseBean;
                SpObjectUtil.putObject(myApplication,mWechatLoginInfoBean);
                break;
            case MyData.QQ_LOGIN:
                mQQLoginInfoBean = (QQLoginInfoBean) baseBean;
                SpObjectUtil.putObject(myApplication,mQQLoginInfoBean);
                break;
        }
    }

    public static void setVerbal_music_volume(int volume) {
        MyApp.verbal_music_volume = volume;
        // 发送消息给主线程，通知开启APP前已经连接了目标耳机   开始*/
        Message msg = new Message();
        msg.what = 347;
        Bundle b = new Bundle();
        b.putInt("verbal_music_volume",verbal_music_volume);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    //  获得屏幕的宽和高*/
    public void getScreen(Context context) {
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        densityDpi = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
//        float xdpi = dm.xdpi;
  //      float ydpi = dm.ydpi;
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
        volume = L_AudioManager.getInstance(this).getMaxMediaVolume()/2;
        Log.e("L0_MyApplication", "initVolumn: "+volume);
//        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        return mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);// 获取系统音量
        return volume;
    }

    // 初始化GreenDao数据库*/
//    private void initGreenDao() {
//        //数据库操作
//        mAppDaoManager = AppDaoManager.getInstance();
//        mAppDaoManager.init(this);
//        mDaoSession = mAppDaoManager.getDaoSession();
//    }

    // 得到数据库操作类 DaSession*/
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
