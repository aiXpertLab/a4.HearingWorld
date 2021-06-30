package com.HYPech.SV8.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.HYPech.SV8.HomeActivity;
import com.HYPech.SV8.R;
import com.HYPech.SV8.util.LogUtils;
import com.HYPech.SV8.util.SPHelper;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    public static final int DELAY_2 = 1;
    public static final int NEXT_STEP = 2;
//    public static final int GOTO_MAIN = 3;
    private Handler mHandler;

    public static final String ACTION_WIFI_DISPLAY_SETTINGS = "android.settings.WIFI_DISPLAY_SETTINGS";
    public static final String ACTION_CAST_SETTINGS = "android.settings.CAST_SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        notificationPermission();
        // testBluData();
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DELAY_2:
                        if (voicePermission()) {
//                            notificationPermission();
//                            startHomeActivity();
                            startNextActivity();

                        } else {
                            initPermission();
                        }
                        break;
                    case NEXT_STEP:
//                        notificationPermission();
//                        startHomeActivity();
                        startNextActivity();
                        break;

//                    case GOTO_MAIN:
//                        startAllRecogActivity();
//                        break;
                    default:
                        break;
                }
            }
        };

        mHandler.sendMessageDelayed(mHandler.obtainMessage(DELAY_2), 3000);

    }

    private void startNextActivity(){
        //TODO 用户是否登录选择 startHomeActivity
//        Intent intent = new Intent(SplashActivity.this, LoginWithpwdActivity.class);
//        startActivity(intent);
//        finish();

        if(SPHelper.getInst().getString("WELCOME").equals("")){
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();

        }else{
            startHomeActivity();
        }

    }
    private void startHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        LogUtils.d(TAG,"initPermission");
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
//                 Manifest.permission.READ_PHONE_STATE,
                // Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET,
        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    private boolean voicePermission() {
        LogUtils.d(TAG,"voicePermission");
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.RECORD_AUDIO)
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LogUtils.d(TAG,requestCode + "requestCode");
        switch (requestCode) {
            case 123: {
                // 授权被允许
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.d(TAG,"-------->"+ "授权请求被允许");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mHandler.sendMessage(mHandler.obtainMessage(NEXT_STEP));
                } else {
                    Log.e("-------->", "授权请求被允许");
                    Toast.makeText(SplashActivity.this, "权限不足将无法正常使用应用程序", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }


    // @Override
    // public void onBackPressed()
    // {
    // //按返回键返回桌面
    // moveTaskToBack(true);
    // }

}
