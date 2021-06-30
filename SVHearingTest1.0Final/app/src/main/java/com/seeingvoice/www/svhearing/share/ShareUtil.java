package com.seeingvoice.www.svhearing.share;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.seeingvoice.www.svhearing.wxapi.WXShareUtil;

/**
 * Date:2019/7/16
 * Time:9:46
 * auther:zyy
 */
public class ShareUtil {
    private static volatile ShareUtil instance;
    private ShareUtil(){}

    /*
    * 单例模式  获取实例的方法
    * */

    public static ShareUtil getInstance(){
        if (instance == null){
            synchronized (ShareUtil.class){
                if (instance == null){
                    instance = new ShareUtil();
                }
            }
        }
            return instance;
    }

    /**
     * 分享朋友圈操作
     */
    public void shareFunction(final Context context) {
        //分享
        ShareDialog shareDialog = new ShareDialog(context);
        shareDialog.show();
        shareDialog.setOnClickListener(new ShareDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(position == 0){
                    QQshareUtil.qqShareFriends((Activity) context, QQshareUtil.QQ_SHARE_TYPE.Type_QQFriends);
                }else  if(position == 1){
                    QQshareUtil.qqShareFriends((Activity)context, QQshareUtil.QQ_SHARE_TYPE.Type_QQZone);
                } else  if(position == 2){
                    WXShareUtil.shareWeb(context,WXShareUtil.SHARE_TYPE.Type_WXSceneSession);
                    Log.e("fenxiang", "OnClick: Type_WXSceneSession");
                }else  if(position == 3){
                    WXShareUtil.shareWeb(context, WXShareUtil.SHARE_TYPE.Type_WXSceneTimeline);
                    Log.e("fenxiang", "OnClick: Type_WXSceneTimeline");
                }
            }
        });
    }
}
