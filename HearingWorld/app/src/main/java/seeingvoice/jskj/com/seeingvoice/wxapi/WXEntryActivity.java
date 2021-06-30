package seeingvoice.jskj.com.seeingvoice.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.MainActivity;
import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.beans.WechatLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.Login;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.smsverify.VerifyPopupWindow;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import okhttp3.MediaType;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI iwxapi;
    private final static String TAG = "WXEntryActivity";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpManager mOkHttpManager;
    private VerifyPopupWindow mVerifyPopupWindow;
    private WechatLoginInfoBean mWechatLoginInfoBean;
    private WechatLoginInfoBean.DataBean mDataBean;
    private WechatLoginInfoBean.DataBean.UserInfoBean mUserInfoBean;
    private static ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOkHttpManager = OkHttpManager.getInstence();

        //通过WXAPIFactory工厂获取IWXApI的示例
        iwxapi = WXAPIFactory.createWXAPI(this, MyData.WX_APP_ID, true);
        iwxapi.handleIntent(this.getIntent(), this);
        //将应用的appid注册到微信
        iwxapi.registerApp(MyData.WX_APP_ID);

        mVerifyPopupWindow = new VerifyPopupWindow(WXEntryActivity.this);
//        mUserInfoBean = new WechatLoginInfoBean.DataBean.UserInfoBean(0,"","","",
//                "",0,"","","",0,"");
//        mDataBean = new WechatLoginInfoBean.DataBean(1);
//        mWechatLoginInfoBean = new WechatLoginInfoBean("","","");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
//        Log.e(TAG, "onReq:" + baseReq.openId);
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     * app发送消息给微信，处理返回消息的回调
     * 处理微信授权后的回调
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        if (baseResp instanceof SendAuth.Resp) {//微信授权登陆
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK://用户同意授权
                    SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
                    //获取微信返回的code
                    final String code = newResp.code;
                    //get请求发送code到
//sv                    mOkHttpManager.getNet(L_Data.SEND_WECHAT_CODE + code, new OkHttpManager.ResultCallback() {
                case BaseResp.ErrCode.ERR_USER_CANCEL:                //用户取消授权登录
                    Log.e(TAG, "ErrCode.ERR_USER_CANCEL:");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_BAN:
                    //如果点击微信登录按钮没有调起微信授权界面，并且一直回调到这里，那很可能是手机问题，可以尝试重启手机
                    Log.e(TAG, "ErrCode.ERR_BAN");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    finish();
                default:
                    break;
            }

        } else {
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK://用户同意授权
                    result = "分享成功";
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:                //用户取消授权登录
                    result = "取消分享";
                    break;
                case BaseResp.ErrCode.ERR_BAN:
                    result = "位置错误";
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = "分享被拒绝";
                default:
                    result = "发送返回";
                    break;
            }
            finish();
        }
    }

    private void showDialog(String showMessageStr) {
        if (!(WXEntryActivity.this).isFinishing()) {
            //show dialog
            dialog = ProgressDialog.show(WXEntryActivity.this, "请稍等", showMessageStr, true, true);
        }
    }

    //检查微信登录
    public void checkWXLogin(String response) {
        showDialog("正在跳转");
        String messageCode;
        String errorInfo;
        String errorCode;
        try {
            Gson gson = new Gson();
            mWechatLoginInfoBean = gson.fromJson(response, WechatLoginInfoBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e(TAG, "服务端异常，运行到这里来了"+response);
        }
        if (mWechatLoginInfoBean == null) {
            return;
        }
        messageCode = mWechatLoginInfoBean.getMessage_code();
        errorInfo = mWechatLoginInfoBean.getError_info();
        errorCode = mWechatLoginInfoBean.getError_code();

        try {
            mUserInfoBean = mWechatLoginInfoBean.getData().getUser_info().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mUserInfoBean == null){
            return;
        }

        if (messageCode.equals(MyData.NET_STATE_SUCCESS)) {
            MyApp.setLoginSuccess(true, MyData.WECHAT_LOGIN, mUserInfoBean.getUid(), mWechatLoginInfoBean, System.currentTimeMillis());
            Intent mIntent = new Intent(WXEntryActivity.this, MainActivity.class);
            startActivity(mIntent);
            try {
                Login.instance.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        } else {
            //该微信号未被注册过,也可在设置中绑定微信号
            //UserEntity.setUserWxID(openid);
            if (null != dialog){
                dialog.dismiss();
            }
            ToastUtil.showLongToast("网络原因" + errorInfo + errorCode);
            finish();
        }
    }


//获取access_token
//    private void getAccessToken(String code) {

        //这个接口需用get请求
//        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AppConstant.WX_APP_ID + "&secret="
//                + AppConstant.WX_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
//
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(path)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.d(TAG, "onFailure: 失败");
//                    finish();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String result = response.body().string();
//                Log.d(TAG, "请求微信服务器成功: " + result);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    openid = jsonObject.getString("openid");
//                    accessToken = jsonObject.getString("access_token");
////                    UserEntity.setUserWxID(openid);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


//                httpWxLogin = new HttpWxLogin(openid);
//                httpWxLogin.setReturnHttpResult(new ReturnHttpResult() {
//                    @Override
//                    public void clickReturnHttpResult(String result) {
//                        Log.d(TAG, "clickReturnHttpResult:从服务器获取的数据： " + result);
//                        if (MyApplication.isWxRegister) {
//                            Message message = new Message();
//                            message.obj = result;
//                            WXHandler.sendMessage(message);
//                        } else {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    bindWX();
//                                }
//                            }).start();
//                        }
//                    }
//                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                        httpWxLogin.saveLoginWx();
//                    }
//                }).start();
//                getUserInfo();
//            }
//        });
//    }

        //获取用户信息
//    private void getUserInfo() {
//        String path = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid;
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(path)
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "onFailure: userinfo" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d(TAG, "onResponse: userinfo" + response.body().string());
//            }
//        });
//    }

//    @SuppressLint("HandlerLeak")
//    Handler WXHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String result = (String) msg.obj;
//            checkWXLogin(result);
//        }
//    };


        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (null != dialog)
                dialog.dismiss();
        }
}
