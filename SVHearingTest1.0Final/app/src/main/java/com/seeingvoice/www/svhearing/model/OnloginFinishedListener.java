package com.seeingvoice.www.svhearing.model;

/**
 * Date:2019/4/30
 * Time:14:28
 * auther:zyy
 */
public interface OnloginFinishedListener {
    void onUsernameError();//名称错误
    void onPasswordError();//密码错误
    void onSuccess();//成功登录
}
