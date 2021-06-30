//package seeingvoice.jskj.com.seeingvoice.messagepush;
//
//import android.app.NotificationManager;
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.huawei.hms.support.api.push.PushReceiver;
//
///**
// * Date:2019/7/23
// * Time:14:34
// * auther:zyy
// */
///**
// * 应用需要创建一个子类继承com.huawei.hms.support.api.push.PushReceiver，
// * 实现onToken，onPushState ，onPushMsg，onEvent，这几个抽象方法，用来接收token返回，push连接状态，透传消息和通知栏点击事件处理。
// * onToken 调用getToken方法后，获取服务端返回的token结果，返回token以及belongId
// * onPushState 调用getPushState方法后，获取push连接状态的查询结果
// * onPushMsg 推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理。本接口必须被实现。 在开发者网站上发送push消息分为通知和透传消息
// *           通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
// *           透传消息不会展示在通知栏，应用会收到onPushMsg
// * onEvent 该方法会在设置标签、点击打开通知栏消息、点击通知栏上的按钮之后被调用。由业务决定是否调用该函数。
// */
//
//public class HuaweiPushReceiver extends PushReceiver {
//
//    private static final String TAG = "HuaweiPushReceiver";
//
//    @Override
//    public void onToken(Context context, String token, Bundle extras) {
//        String belongId = extras.getString("belongId");
//        String device_id = extras.getString("deviceToken");
//        String content = "get token and belongId successful, token = " + token + ",deviceToken = " + device_id;
//        Log.d(TAG, content);
//    }
//
//    @Override
//    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
//        try {
//            String content = "-------Receive a Push pass-by message： " + new String(msg, "UTF-8");
//            Log.d(TAG, content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public void onEvent(Context context, PushReceiver.Event event, Bundle extras) {
//        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
//            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
//            if (0 != notifyId) {
//                NotificationManager manager = (NotificationManager) context
//                        .getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(notifyId);
//            }
//
//            String content = "--------receive extented notification message: " + extras.getString
//                    (BOUND_KEY.pushMsgKey);
//            Log.d(TAG, content);
//        }
//        super.onEvent(context, event, extras);
//    }
//
//
//
//    @Override
//    public void onPushState(Context context, boolean pushState) {
//        try {
//            String content = "---------The current push status： " + (pushState ? "Connected" :
//                    "Disconnected");
//            Log.d(TAG, content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
