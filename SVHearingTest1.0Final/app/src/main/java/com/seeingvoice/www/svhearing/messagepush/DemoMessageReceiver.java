//package com.seeingvoice.www.svhearing.messagepush;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.util.List;
//
///**
// * Date:2019/7/22
// * Time:13:53
// * auther:zyy
// */
//
//public class DemoMessageReceiver extends PushMessageReceiver {
//
//
//    private String mRegId;
//    private long mResultCode = -1;
//    private String mReason;
//    private String mCommand;
//    private String mMessage;
//    private String mTopic;
//    private String mAlias;
//    private String mStartTime;
//    private String mEndTime;
//
//    // 用来接收服务器向客户端发送的消息
//
//    @Override
//    public void onReceiveMessage(Context context, MiPushMessage message) {
//
//        Log.v("MiPushBroadcastReceiver","onReceiveMessage is called. " + message.toString());
//        mMessage = message.getContent();
//
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
//    }
//
//
//    // 用来接收客户端向服务器发送命令消息后返回的响应
//
//    @Override
//    public void onCommandResult(Context context, MiPushCommandMessage message) {
//
//        Log.v("MiPushBroadcastReceiver","onCommandResult is called. " + message.toString());
//
//        String command = message.getCommand();
//        List<String> arguments = message.getCommandArguments();
//        if (arguments != null) {
//            if (MiPushClient.COMMAND_REGISTER.equals(command)
//                    && arguments.size() == 1) {
//                mRegId = arguments.get(0);
//            } else if ((MiPushClient.COMMAND_SET_ALIAS.equals(command) || MiPushClient.COMMAND_UNSET_ALIAS
//                    .equals(command)) && arguments.size() == 1) {
//                mAlias = arguments.get(0);
//            } else if ((MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command) || MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC
//                    .equals(command)) && arguments.size() == 1) {
//                mTopic = arguments.get(0);
//            } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)
//                    && arguments.size() == 2) {
//                mStartTime = arguments.get(0);
//                mEndTime = arguments.get(1);
//            }
//
//
//            // 设置别名alias   这个方法调用成功后会执行到onCommandResult方法，为了避免死循环执行，加了下面判断
//            if (mAlias.equals(""))
//                MiPushClient.setAlias(context, "shangqi", null);
//            // 指定标签   其它同上
//            // MiPushClient.subscribe(getApplicationContext(), "haokang", null);
//        }
//        mResultCode = message.getResultCode();
//        mReason = message.getReason();
//    }
//}
