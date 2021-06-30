//package com.seeingvoice.www.svheard.blueTest.battery;
//
//import android.app.Activity;
//import android.appwidget.AppWidgetManager;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
///**
// * Date:2019/8/7
// * Time:10:46
// * auther:zyy
// */
//public class PowerWidgetActivity extends Activity {
//    public static final String TAG = "PowerWidget";
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.main);
//
//        overridePendingTransition(0, 0);
//        Log.i(TAG, " on WidgetConf ... ");
//        setResult(RESULT_CANCELED);
//
//        // Find the widget id from the intent.
//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        int mAppWidgetId = 0;
//        if (extras != null) {
//            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//        }
//
//        // If they gave us an intent without the widget id, just bail.
//        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
//            finish();
//        }
//
//        // return OK
//        Intent resultValue = new Intent();
//        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
//
//        setResult(RESULT_OK, resultValue);
//        finish();
//    }
//}
