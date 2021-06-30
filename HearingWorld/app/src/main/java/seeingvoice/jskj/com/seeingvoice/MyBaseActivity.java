package seeingvoice.jskj.com.seeingvoice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import seeingvoice.jskj.com.seeingvoice.base.EventListener;
import seeingvoice.jskj.com.seeingvoice.base.util.ActivityStackManager;
import seeingvoice.jskj.com.seeingvoice.base.util.ScreenManager;

/**
 * 作者：zyy
 * 日期：2018/8/29
 * 作用：利用baseActivity抽象重复内容
 * 组件绑定、事件跳转、窗口管理（横竖屏。沉浸式，Theme）、生命周期这三个是每个Activity必有的
 * 注：可以继承Activity，或者FragmentActivty或者AppCompatActivity，但是现在都是Activity+Fragment模式开发
 * 所以推荐用AppCompatActivity ，FragmentActivty不支持tooslBar….）
 */

public abstract class MyBaseActivity extends AppCompatActivity{
    private static final String TAG = "BaseActivity";
    private boolean isDebug;    //是否输出日志信息
    private boolean isSetStatusBar = false;    //是否沉浸状态栏
    private boolean isAllowFullScreen = false;    //是否允许全屏
    private boolean isAllowScreenRotate = true;    //是否禁止旋转屏幕
    protected MyBaseActivity BaseContext;    //Context
    private ScreenManager screenManager;
    private static Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseContext = this;
        ActivityStackManager.getActivityStackManager().pushActivity(this);
        screenManager = ScreenManager.getInstance();
        screenManager.setStatusBar(isSetStatusBar,BaseContext);
        screenManager.setFullScreen(isAllowFullScreen,BaseContext);
        screenManager.setScreenRotate(isAllowScreenRotate,BaseContext);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        //设置此界面为竖屏
//sv        initView(savedInstanceState);
}

//sv    protected abstract void initView(Bundle savedInstanceState) ;    //初始化界面

    @Override
    protected void onStart() {        super.onStart();    }

    //跳过当前Activity
    public static void skipAnotherActivity(Activity activity, Class<? extends Activity> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
        activity.finish();
    }

    //
    public static void toNextActivity(Bundle bundle,Activity activity, Class<? extends Activity> cls){
        if (null != bundle){
            intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(activity,cls);
        }else {
            intent = new Intent(activity,cls);
        }
            activity.startActivity(intent);
    }

    public void allShare(String shareTitle){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.topbar_share) );  //添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, "见声听力测试App \n3分钟获得听力图。\n"+"http://seeingvoice.com/download");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, shareTitle);
        startActivity(share_intent);
    }

    //退出应用
    public void exitLogic(){
        System.exit(0); //system exit 0 表示正常关闭程序，1表示非正常关闭程序
    }

    //是否设置沉浸状态栏
    public void setStatusBar(boolean statusBar){
        this.isSetStatusBar = statusBar;
    }

    //是否设置全屏
    public void setFullScreen(boolean fullScreen){
        this.isAllowFullScreen = fullScreen;
    }

    //是否设置屏幕旋转
    public void setScreenRotate(boolean screenRotate){
        this.isAllowScreenRotate = screenRotate;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "---->onDestroy()");
        ActivityStackManager.getActivityStackManager().popActivity(this);
    }

    public <T extends View> T findClickView(int id) {
        T view = findViewById(id);
        view.setOnClickListener(new EventListener(this));
        return view;
    }

    private void initHuaweiPush(Context context) {    }
    private void getToken() {    }

}
