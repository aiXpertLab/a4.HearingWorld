package com.seeingvoice.www.svhearing.model;

/**
 * Date:2019/4/30
 * Time:14:25
 * auther:zyy
 */
public interface LoginModel {
    /**
     * Class Note:模拟登录的操作的接口，实现类为LoginModelImpl.相当于MVP模式中的Model层
     */
    void login(String username, String password, OnloginFinishedListener listener); //第三个参数是登录操作完成后的监听器
}
