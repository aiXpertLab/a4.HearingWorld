package com.reapex.sv.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.reapex.sv.R;


public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    public final static String channelName="cn.george.app";
    private NotificationManager notificationManager;
    //通知的唯一标识号。
    private int NOTIFICATION = R.string.notification_live_start;


    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(){
        // PendingIntent如果用户选择此通知，则启动我们的活动
        Notification notification;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,NotificationService.class),0);
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
             notification =new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID).setSmallIcon(R.mipmap.ic_launcher).setTicker("正在通话")
                    .setContentTitle(getText(R.string.notification_live_start))
                    .setContentTitle("正在运行")
                    .setContentIntent(pendingIntent)
                    .build();
        }else {
            //设置通知面板中显示的视图的信息。
             notification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setTicker("正在通话")
                    .setContentTitle(getText(R.string.notification_live_start))
                    .setContentTitle("正在运行")
                    .setContentIntent(pendingIntent)
                    .build();
        }
        Log.d("ljp","显示通知");
        //发送通知
        notificationManager.notify(NOTIFICATION,notification);
        startForeground(R.string.notification_live_start,notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION);
    }
}