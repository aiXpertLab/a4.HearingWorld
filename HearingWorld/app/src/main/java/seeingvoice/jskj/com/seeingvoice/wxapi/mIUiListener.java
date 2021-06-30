package seeingvoice.jskj.com.seeingvoice.wxapi;

import android.util.Log;

import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Date:2019/7/12
 * Time:15:08
 * auther:zyy
 */
public class mIUiListener implements IUiListener {
    @Override
    public void onComplete(Object o) {
        ToastUtil.showShortToastCenter("分享成功");
    }

    @Override
    public void onError(UiError uiError) {
        Log.e("sssssss","分x享失败"+uiError.toString());
//        ToastUtil.showShortToastCenter("分享失败"+uiError);
    }

    @Override
    public void onCancel() {
        ToastUtil.showShortToastCenter("取消分享");
    }
}
