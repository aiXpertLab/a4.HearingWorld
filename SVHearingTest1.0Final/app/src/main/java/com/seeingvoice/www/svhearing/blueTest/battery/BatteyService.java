//package com.seeingvoice.www.svheard.blueTest.battery;
//
//
//import android.app.Service;
//import android.appwidget.AppWidgetManager;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.BatteryManager;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.widget.RemoteViews;
//
///**
// * Date:2019/8/7
// * Time:11:00
// * auther:zyy
// */
//public class BatteyService extends Service {
//
//    public static final String TAG = "PowerWidget";
//
//    public BroadcastReceiver battryReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.d(TAG, "intent->" + intent);
//            Log.d(TAG, "action->" + action);
//            ComponentName thiswidget = new ComponentName(context, PowerWidget.class);
//            AppWidgetManager appmanager = AppWidgetManager.getInstance(context);
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
//            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
//                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
//                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
//                Log.e(TAG, BatteryManager.EXTRA_LEVEL + "->" + level);
//                Log.e(TAG, BatteryManager.EXTRA_SCALE + "->" + scale);
//                Log.e(TAG, BatteryManager.EXTRA_STATUS + "->" + status);
//                views.setTextViewText(R.id.power_percent, "电量:");
//                //views.setFloat(R.id.power_percent, "setTextSize", 20);
//                //views.setTextColor(R.id.power_percent, 0xffff0000);
//                views.setInt(R.id.power_percent_image, "setImageResource", R.drawable.stat_sys_battery_000+level);
//            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
//                views.setInt(R.id.power_image, "setImageResource", R.drawable.charging);
//            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
//                views.setInt(R.id.power_image, "setImageResource", R.drawable.uncharge);
//            }
//            appmanager.updateAppWidget(thiswidget, views);
//        }
//    };
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//    public void onStart(Intent intent, int startId) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction(Intent.ACTION_POWER_CONNECTED);
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        registerReceiver(battryReceiver, filter);
//    }
//
//    public void onDestroy() {
//        unregisterReceiver(battryReceiver);
//        super.onDestroy();
//    }
//
//
//}
