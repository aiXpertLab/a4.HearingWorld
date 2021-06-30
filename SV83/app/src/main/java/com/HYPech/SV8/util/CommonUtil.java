package com.HYPech.SV8.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AlertDialog;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.HYPech.SV8.App;
import com.HYPech.SV8.R;
import com.HYPech.SV8.constants.Constances;
import com.HYPech.SV8.event.GlassesOrderEvent;
import com.HYPech.SV8.service.NotificationService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommonUtil {
    public static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private final static int SPACE_TIME = 1000;//2次点击的间隔时间，单位ms
    private static long lastClickTime;

    private static Toast mToast;

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick;
        if (currentTime - lastClickTime > SPACE_TIME) {
            isClick = false;
            if(App.isDebug)Log.d("ljp","isDoubleclick:"+isClick);
        } else {
            isClick = true;
            if(App.isDebug)Log.d("ljp","isDoubleclick:"+isClick);
        }
        lastClickTime = currentTime;
        return isClick;
    }


    public static BluetoothAdapter getAdapter(Context context) {
        BluetoothAdapter bluetoothAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null)
                bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bluetoothAdapter;
    }
    public static void setEyeglassesFontSize(int position,SupportActivity _mActivity){
        //当前是3档位(0-7)
        int finalSize = 1;
        if(position == 0){
            finalSize = Constances.FONT_SIZE_GLASSES3 ;
        }else if(position == 1){
            finalSize = Constances.FONT_SIZE_GLASSES2 ;
        }else if(position == 2){
            finalSize = Constances.FONT_SIZE_GLASSES1;
        }
//        else if(position == 3){
//            finalSize = Constances.FONT_SIZE_GLASSES4 ;
//        }else if(position ==4){
//            finalSize = Constances.FONT_SIZE_GLASSES5 ;
//        }else if(position == 5){
//            finalSize = Constances.FONT_SIZE_GLASSES6 ;
//        }else if(position == 6){
//            finalSize = Constances.FONT_SIZE_GLASSES7 ;
//        }else if(position == 7){
//            finalSize = Constances.FONT_SIZE_GLASSES8 ;
//        }
        App.myApplication.setEyeglassesFontSize(finalSize);
        sendFondMsg(finalSize,_mActivity);
    }
    public static void setEyeglassesBright(int position,SupportActivity _mActivity){
        //当前是3档位(0-7)
        int finalSize = 5;
        if(position == 0){
            finalSize = 8 ;
        }else if(position == 1){
            finalSize = 7;
        }else if(position == 2){
            finalSize = 6 ;
        }else if(position == 3){
            finalSize = 5;
        }else if(position ==4){
            finalSize = 4 ;
        }else if(position == 5){
            finalSize = 3 ;
        }else if(position == 6){
            finalSize = 2 ;
        }else if(position == 7){
            finalSize = 1;
        }else if(position == 8){
            finalSize = 0;
        }
//        else if(position == 9){
//            finalSize = 0;
//        }
        if(App.isDebug) {
            Log.d("ljp", "设置亮度获取当前游标位置position：" + position + "finalsize:" + finalSize);
        }
        App.myApplication.setEyeglassesBright(finalSize);
        sendBrightMsg(finalSize,_mActivity);
    }
    public static String getPositionBright_str(){
        int size = App.myApplication.getEyeglassesBright();
        String str = "中亮";
        if(size >= 0 && size <=2){
            str = "高亮";
        }else if(size >= 3 && size <= 5){
            str = "中亮";
        }else if(size >= 6 && size <= 8){
            str = "低亮";
        }
        return str;
    }
    //亮度，眼镜端参数0标识最亮，8标识最暗
    public static int getPositionBright(){
        int size = App.myApplication.getEyeglassesBright();
        int position = 4;
//        if(size == 9){
//            position = 0;
//        }else
        if(size == 8){
            position = 0;
        }else if(size == 7){
            position = 1;
        }else if(size == 6){
            position = 2;
        }else if(size == 5){
            position = 3;
        }else if(size == 4){
            position = 4;
        }else if(size == 3){
            position = 5;
        }else if(size == 2){
            position = 6;
        }else if(size == 1){
            position = 7;
        }else if(size == 0){
            position = 8;
        }
        if(App.isDebug) {Log.d("ljp","亮度获取当前游标位置position："+position+"size:"+size);}
        return position;
    }
    //眼镜字体显示String形式
    public static String getPositionFontSize_Str(){
        int size = App.myApplication.getEyeglassesFontSize();
        String fontSize_str = "大号字体";
        if(size == Constances.FONT_SIZE_GLASSES1){
            fontSize_str = "大号字体";
        }else if(size == Constances.FONT_SIZE_GLASSES2){
            fontSize_str = "中号字体";
        }else if(size == Constances.FONT_SIZE_GLASSES3){
            fontSize_str = "小号字体";
        }
        return fontSize_str;
    }
    //眼镜字体设置1是最大字体，默认眼镜是1，3是最小字体
    public static int getPositionFontSize(){
        int size = App.myApplication.getEyeglassesFontSize();
        int position = 2;
        if(size == Constances.FONT_SIZE_GLASSES1){
            position = 2;
        }else if(size == Constances.FONT_SIZE_GLASSES2){
            position = 1;
        }else if(size == Constances.FONT_SIZE_GLASSES3){
            position = 0;
        }
//        else if(size == Constances.FONT_SIZE_GLASSES4){
//            position = 3;
//        }else if(size == Constances.FONT_SIZE_GLASSES5){
//            position = 4;
//        }else if(size == Constances.FONT_SIZE_GLASSES6){
//            position = 5;
//        }else if(size == Constances.FONT_SIZE_GLASSES7){
//            position = 6;
//        }else if(size == Constances.FONT_SIZE_GLASSES8){
//            position = 7;
//        }
        if(App.isDebug) {Log.d("ljp","获取当前游标位置："+position);}
        return position;
    }
    public static void rotateScreen(boolean leftBoolean ,SupportActivity _mActivity){
        String left = "7f00050600"+ConvertData.makeChecksum("00050600")+"f7";
        String right = "7f00050601"+ConvertData.makeChecksum("00050601")+"f7";
        //TODO 数据长度未计算
        String msg;
        if(leftBoolean){
            msg=left;
        }else{
            msg=right;
        }
        EventBusActivityScope.getDefault(_mActivity).post(new GlassesOrderEvent(msg));
    }
    public static void sendFondMsg(int position,SupportActivity _mActivity){

        String font = String.valueOf(position);
        String msg ="7f000503"+"0"+font+ ConvertData.makeChecksum("000503"+"0"+font)+"f7";
        EventBusActivityScope.getDefault(_mActivity).post(new GlassesOrderEvent(msg));
    }
    public static void sendBrightMsg(int position,SupportActivity _mActivity){
        String bright = String.valueOf(position);
        String msg ="7f000507"+"0"+bright+ ConvertData.makeChecksum("000507"+"0"+bright)+"f7";
        EventBusActivityScope.getDefault(_mActivity).post(new GlassesOrderEvent(msg));
    }
    public static String specialCharReplace(String str){

        StringBuilder strBuild = new StringBuilder();
        StringBuilder resultBuild = new StringBuilder();
        for(int i = 0;i<str.length();i++){
            strBuild.append(str.charAt(i)).append(str.charAt(i+1)).append(" ");
            i++;
//            Log.d("ljp",strBuild.toString());
        }
        String[] split = strBuild.toString().split(" ");
        for(int i=0 ;i<split.length;i++){
            if(split[i].equalsIgnoreCase("7e")){
                split[i] = "7e03";
            }else if(split[i].equalsIgnoreCase("7f")){
                split[i] = "7e01";
            }else if(split[i].equalsIgnoreCase("f7")){
                split[i] = "7e02";
            }
            resultBuild.append(split[i]);
        }

        return resultBuild.toString();
    }
//    public static void searchDevice(){
//        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
//        if (!bt.isDiscovering())
//            bt.startDiscovery();
//    }
    public static void mkdirs(String filePath) {
        boolean mk = new File(filePath).mkdirs();
        if(App.isDebug) {Log.d("ljp", "mkdirs: " + mk);}
    }

    public static void notificationPermission(final Activity mActivity){
        if (!NotificationManagerCompat.from(App.myApplication).areNotificationsEnabled()) {


            AlertDialog.Builder  builder = new AlertDialog.Builder(mActivity).setIcon(R.mipmap.ic_launcher).setTitle("见声看见")
                    .setMessage("为了在您手机息屏状态下也能正常使用，在点击下方确定按钮后进入设置，请点击通知->允许通知").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //ToDo: 你想做的事情
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", mActivity.getPackageName());
                                intent.putExtra("app_uid", mActivity.getApplicationInfo().uid);
                                mActivity.startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                mActivity.startActivity(intent);
                            } else {
                                Intent localIntent = new Intent();
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package",   mActivity.getPackageName(), null));
                                mActivity.startActivity(localIntent);
                            }

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //ToDo: 你想做的事情
                            dialogInterface.dismiss();

                        }
                    });
            builder.create().show();
        }else{
            Intent intent = new Intent(mActivity,NotificationService.class);
            //intent.putExtra("name","secondActivity");
            mActivity.startService(intent);
        }
    }
    public static int getUidFromStr(String str){
//        sid=iat00072bdd@dx16cbcef5fd27a1c802
        //十六进制转换为十进制
        String hexStr = str.substring(4,11);
//        Log.d("ljp","hexStr:"+hexStr+"10进制："+Integer.parseInt(hexStr, 16));
        return Integer.parseInt(hexStr, 16);

    }

    /**
     * 判断是否联网 0 没联网 1 联网 2 mobile 3 wifi 4 2g 5 3g 6 4g
     *
     *
     * @param
     * @return
     */
    public interface NetType {
        int NET_UNCONECTION = 0;
        int NET_CONECTION = 1;
        int NET_MOBILE = 2;
        int NET_WIFI = 3;
        int NET_2G = 4;
        int NET_3G = 5;
    }
    public static int IsHaveInternet(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return 0;
        }

        NetworkInfo info = connManager.getActiveNetworkInfo();
        int mState = NetType.NET_UNCONECTION;
        if (info != null && info.isConnected()) {
            mState = NetType.NET_CONECTION;
        } else {
            mState = NetType.NET_UNCONECTION;
            return mState;
        }

        boolean mIsWifi = false;
        boolean mIsMobile = false;
        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
            mIsWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnected();
        }
        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            mIsMobile = connManager.getNetworkInfo(
                    ConnectivityManager.TYPE_MOBILE).isConnected();
        }
        if (mIsWifi) {
            mState = NetType.NET_WIFI;
        } else if (mIsMobile) {
            mState = NetType.NET_MOBILE;
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
                    || info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                    || info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
                mState = NetType.NET_2G;
            } else if (IsFastMobileNetwork(context) == true) {
                mState = NetType.NET_3G;
            }
        }
        return mState;
    }
    /**
     * 判断是否快速网络
     */
    public static boolean IsFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
    public static boolean hasNet(){
        return IsHaveInternet(App.myApplication) != NetType.NET_UNCONECTION;
    }
    public static String getCurrentTime(){
        long now=System.currentTimeMillis();   //获取出来的是当前时间的毫秒值

        //把毫秒值转换成时间格式
        Date d=new Date();
        d.setTime(now);
        /**
         * 创建格式化时间日期类
         *构造入参String类型就是我们想要转换成的时间形式
         */
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return format.format(d);
    }
    public static boolean isBlueEnable(){
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
        if (bluetooth == null) {
            return false;
        }
        //启动蓝牙
        if (bluetooth.isEnabled() == false) {
            return false;
        }else {
            return true;
        }
    }
    public static  boolean catchNetException(String str){
        if(str.toLowerCase().contains("failed to connect to") || str.toLowerCase().contains("sockettimeoutexception")||str.toLowerCase().contains("connectexception")|| str.toLowerCase().contains("unknownhostexception")){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isEmpty(String str){

        if(str == null || str.trim().equals("")){
            return true;
        }else{
            return false;
        }
    }
    //隐藏键盘
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        View focusView = null;
        if (view instanceof EditText)
            focusView = view;
        Context context = view.getContext();
        if (context != null && context instanceof Activity) {
            Activity activity = ((Activity) context);
            focusView = activity.getCurrentFocus();
        }

        if (focusView != null) {
                /*
                if (focusView.isFocusable()) {
                    focusView.setFocusable(false);
                    focusView.setFocusable(true);
                }
                */
            if (focusView.isFocused()) {
                focusView.clearFocus();
            }
            InputMethodManager manager = (InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            manager.hideSoftInputFromInputMethod(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static void showSoftKeyboard(View view) {
        if (view == null)
            return;
        /*
        ((InputMethodManager) BaseApplication.context().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.SHOW_FORCED);
        */
        if (!view.isFocusable())
            view.setFocusable(true);
        if (!view.isFocusableInTouchMode())
            view.setFocusableInTouchMode(true);
        if (!view.isFocused()) {
            view.requestFocus();
        }
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static boolean isTouchInView(View view, MotionEvent event){
        if(view == null){
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return x<event.getRawX() && event.getRawX() < x+view.getWidth()
                && y<event.getRawY() && event.getRawY() <y+view.getHeight();
    }
    public static void showToast(Context context, String text){
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void showToast(Context context, int id){
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(context, context.getResources().getString(id), Toast.LENGTH_LONG);
        mToast.show();
    }
    public static boolean isHttpSuccess(String code){
        if(code.equalsIgnoreCase("A000000")){
            return true;
        }else{
            return false;
        }
    }

}
