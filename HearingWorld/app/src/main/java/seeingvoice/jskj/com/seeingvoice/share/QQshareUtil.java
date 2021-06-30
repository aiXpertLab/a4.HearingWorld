package seeingvoice.jskj.com.seeingvoice.share;

import android.app.Activity;
import android.os.Bundle;

import seeingvoice.jskj.com.seeingvoice.wxapi.mIUiListener;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

import seeingvoice.jskj.com.seeingvoice.MyData;

import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
import static seeingvoice.jskj.com.seeingvoice.MyData.URL_DOWNLOAD_URL;

/**
 * Date:2019/7/12
 * Time:13:12
 * auther:zyy
 */
public class QQshareUtil {
    private static Tencent mTencent;
    public enum QQ_SHARE_TYPE{Type_QQFriends,Type_QQZone}

    // 应用宝 下载地址：https://sj.qq.com/myapp/detail.htm?apkName=seeingvoice.jskj.com.seeingvoice
    public static void qqShareFriends(Activity activity,QQ_SHARE_TYPE type) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(MyData.QQ_APP_ID,activity);
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "见声听力测试");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"便携式手机听力计");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,URL_DOWNLOAD_URL);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,URL_DOWNLOAD_URL);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "见声听力测试APP");

        //分享类型
        Bundle params1 = new Bundle();
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://s2.ax1x.com/2019/10/22/K8nHzD.png");//添加一个图片地址
        params1.putString(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, String.valueOf(QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT));
        params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
        params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        switch(type){
            case Type_QQFriends:
                mTencent.shareToQQ(activity, params, new mIUiListener());
                break;
            case Type_QQZone:
//                mTencent.shareToQzone(activity, params1, new mIUiListener());
                qqQzoneShare(activity);
//                mTencent.shareToQzone(activity,params,new mIUiListener());
                break;
        }
    }

//    public static class mIUiListener implements IUiListener{
//
//        @Override
//        public void onComplete(Object o) {
//            ToastUtil.showShortToastCenter("分享成功");
//        }
//
//        @Override
//        public void onError(UiError uiError) {
//            Log.e("sssssss","分x享失败"+uiError.toString());
//            ToastUtil.showShortToastCenter("分享失败"+uiError);
//        }
//
//        @Override
//        public void onCancel() {
//            ToastUtil.showShortToastCenter("取消分享");
//        }
//    }


    /**
     * 分享到QQ空间
     * @param
     */

    public static void qqQzoneShare(Activity activity) {

        int QzoneType = QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE;
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "见声听力测试APP官网");//分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "我看故我听！");//分享的内容摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,URL_DOWNLOAD_URL);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,URL_DOWNLOAD_URL);

        //分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://s2.ax1x.com/2019/10/22/K8nHzD.png");//添加一个图片地址
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL

        mTencent.shareToQzone(activity, params, new mIUiListener());
    }
}
