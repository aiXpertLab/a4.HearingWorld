package seeingvoice.jskj.com.seeingvoice.base.util;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;


/**
 * 作者：zyy
 * 日期：2018/8/29
 * 作用：屏幕管理类
 *  1.全屏； 2.沉浸式状态栏； 3.屏幕旋转
 */

public class ScreenManager {
    private static ScreenManager instance;

    private ScreenManager(){
    }

    //单例模式获取屏幕管理对象
    public synchronized static ScreenManager getInstance(){
        if(instance == null){
            instance = new ScreenManager();
        }
        return instance;
    }

    //窗口全屏
    public void setFullScreen(boolean isChange, MyBaseActivity mActivity){
        if(!isChange){
            return;
        }
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    //沉浸式状态栏
    public void setStatusBar(boolean isChange, MyBaseActivity mActivity){
        if(!isChange){
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    //旋转屏幕
    public void setScreenRotate(boolean isChange, MyBaseActivity mActivity){
        if(!isChange){
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}

