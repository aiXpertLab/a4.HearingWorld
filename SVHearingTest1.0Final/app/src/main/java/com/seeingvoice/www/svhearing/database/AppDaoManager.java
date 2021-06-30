package com.seeingvoice.www.svhearing.database;

import android.content.Context;

import com.seeingvoice.www.svhearing.database.dao.DaoMaster;
import com.seeingvoice.www.svhearing.database.dao.DaoSession;

/**
 * Date:2019/3/14
 * Time:11:29
 * auther:zyy
 */
public class AppDaoManager {

    public Context mContext;
    private static final String DB_NAME = "mPureTestResult.db";    //创建数据库的名字
    private volatile static AppDaoManager mInstance;    //多线程要被共享的使用volatile关键字修饰GreenDao管理类
    private static DaoMaster mDaoMaster;    //保存数据库的对象
    private static DaoMaster.DevOpenHelper mHelper;    //创建数据库工具
    private DaoSession mDaoSession;    //管理@gen里生成的所有Dao对象

    public AppDaoManager() {
    }

    /**
     * 单例模式获得数据库操作对象
     * @return
     */
    public static AppDaoManager getInstance(){
        if (mInstance == null){
            synchronized (AppDaoManager.class){
                if (mInstance == null){
                    mInstance = new AppDaoManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化上下文，创建数据库的时候使用
     * @param context
     */
    public void init(Context context){
        this.mContext = context;
    }

    /**
     * 判断是否存在数据库，如果没有则创建
     * @return
     */
    public DaoMaster getDaoMaster() {
        if (mDaoMaster == null) {
            mHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
            mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，调用此函数返回数据库操作对象
     * 对外提供的数据库增删改查操作的接口
     * @return
     */
    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            if (mDaoMaster == null) {
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

    public void closeDaoSession() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
