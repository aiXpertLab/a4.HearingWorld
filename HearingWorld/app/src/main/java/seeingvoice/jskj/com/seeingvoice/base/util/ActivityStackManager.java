package seeingvoice.jskj.com.seeingvoice.base.util;


import android.app.Activity;

import java.util.Stack;

/**
 *   Activity管理类,栈管理
 *   作者：zyy
 *   日期：2018/8/29
 */
public class ActivityStackManager {
    private static Stack<Activity> activityStack;
    private static ActivityStackManager instance;

    ActivityStackManager(){
    }

    //为什么用同步机制?  防止其他线程同时实例化Activity 栈管理器
    public synchronized static ActivityStackManager getActivityStackManager(){
        if(instance == null){
            instance = new ActivityStackManager();
        }
        return instance;
    }

    //关闭activity
    public void popActivity(Activity activity){
        if(activityStack == null) return;
        if(activity != null){
            activity.finish();
            activity.overridePendingTransition(0,0);
            activityStack.remove(activity);
//            activity = null;
        }
    }

    //获取当前的activity
    public Activity currentActivity(){
        if(activityStack == null || activityStack.isEmpty()) return null;
        Activity activity = activityStack.firstElement();
        return activity;
    }

    //获取最后一个Activity
    public Activity firstActivity(){
        if(activityStack == null || activityStack.isEmpty())
            return null;
        Activity activity = activityStack.firstElement();
        return activity;
    }

    //添加activity到stack
    public void pushActivity(Activity activity){
        if(activityStack == null){
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }


    //remove掉所有的activity
    public void popAllActivity(){
        if(activityStack == null) return;  //return 后面的不执行了  函数的值返回给主调函数
        while (true){
            if(activityStack.empty()){
                break;
            }
            Activity activity = currentActivity();
            popActivity(activity);
        }
    }

    //remove 所有的activity但保持目前的activity

    public void popAllActivityWithOutCurrent(){
        Activity activity = currentActivity();
        while (true){
            if(activityStack.size() == 1){
                break;
            }
            if(firstActivity() == activity){
                break;
            }else{
                popActivity(firstActivity());
            }
        }
    }

    /**
     * 查找栈中是否存在指定的activity
     *
     * @param cls
     * @return
     */
    public boolean checkActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }
}
