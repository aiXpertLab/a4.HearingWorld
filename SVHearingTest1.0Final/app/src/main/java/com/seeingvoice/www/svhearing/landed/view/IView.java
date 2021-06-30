package com.seeingvoice.www.svhearing.landed.view;

/**
 * Date:2019/4/29
 * Time:15:33
 * auther:zyy
 */

/**
 * View层接口---执行各种UI操作，定义的方法主要是给Presenter中来调用的
 */
public interface IView {
    void showLoadingProgress(String message);
    void showData(String text);
}
