//package seeingvoice.jskj.com.seeingvoice.messagepush;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import seeingvoice.jskj.com.seeingvoice.R;
//import com.umeng.message.UmengNotifyClickActivity;
//
//import org.android.agoo.common.AgooConstants;
//
///**
// * Date:2019/7/22
// * Time:9:13
// * auther:zyy
// */
//public class MipushTestActivity extends UmengNotifyClickActivity {
//    private static String TAG = MipushTestActivity.class.getName();
//
//    @Override
//    protected void onCreate(Bundle bundle) {
//        super.onCreate(bundle);
//        setContentView(R.layout.activity_mipush); //这里设置不同的页面，为了区分是友盟推送进来的，还是通道推送进来的
//    }
//
//    @Override
//    public void onMessage(Intent intent) {
//        super.onMessage(intent);
//        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
//        Log.i(TAG, body);
//    }
//}
