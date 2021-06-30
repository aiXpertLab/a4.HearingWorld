package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;


import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 耳机状态监听
 * Date:2019/9/1
 * Time:17:29
 * auther:zyy
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    private static final String TAG = "BeforePureTestActivity";

    private HeadsetPlugListener mHeadsetPlugListener;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    public HeadsetPlugReceiver(HeadsetPlugListener headsetPlugListener) {
        this.mHeadsetPlugListener = headsetPlugListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null){
            Log.d(TAG, "action == null");
            return;
        }
        if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                int state = adapter.getProfileConnectionState(BluetoothProfile.A2DP);
//                if (BluetoothProfile.STATE_CONNECTED == state) {
//                    mHeadsetPlugListener.onHeadsetPlug(true,false);
//                }
//                if (BluetoothProfile.STATE_DISCONNECTED == state) {
//                    mHeadsetPlugListener.onHeadsetPlug(false,false);
//                }
//            }

            switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                case BluetoothA2dp.STATE_CONNECTING:
                    break;
                case BluetoothA2dp.STATE_CONNECTED:
                    Log.e(TAG, "onReceive: A2DP连接");
                    mHeadsetPlugListener.onHeadsetPlug(true,false);
                    break;
                case BluetoothA2dp.STATE_DISCONNECTED:
                    Log.e(TAG, "onReceive: A2DP断开连接");
                    mHeadsetPlugListener.onHeadsetPlug(false,false);
                    break;
                default:
                    break;
            }
        }


//        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
//            if (intent.hasExtra("state")){//如果手机蓝牙断开了
//                if (intent.getIntExtra("state", 0) == BluetoothAdapter.STATE_DISCONNECTED) {
//                    Log.e(TAG, "onReceive: 手机蓝牙断开了");
//                    mHeadsetPlugListener.onHeadsetPlug(false,false);
//                }
//            }
//        }


        if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {//监听手机蓝牙开关状态
            Log.d(TAG, "监测到手机蓝牙开关状态");
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
            switch (blueState) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    //手机蓝牙适配器打开了
                    // ToastUtil.showShortToast(context, " BluetoothAdapter.STATE_TURNING_ON:手机蓝牙适配器打开了");
                    Log.d(TAG, "STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
                case BluetoothAdapter.STATE_ON:
                    //手机蓝牙已经打开，开始搜索并连接耳机
                    Log.d(TAG, "STATE_ON 手机蓝牙开启");
//                    mHeadsetPlugListener.onHeadsetPlug(true,true);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    //手机蓝牙正在关闭
                    break;
                case BluetoothAdapter.STATE_OFF:
                    //手机蓝牙已关闭
                    Log.d(TAG, "STATE_OFF 手机蓝牙关闭");
                    mHeadsetPlugListener.onHeadsetPlug(false,false);
                    break;
            }
        }
            //判断有线耳机连接状态
        if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
            Log.e(TAG, "onReceive: "+action);
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {//有线耳机未插入
                    mHeadsetPlugListener.onHeadsetPlug(false,true);
                } else if (intent.getIntExtra("state", 0) == 1) {//有线耳机已插入
                    mHeadsetPlugListener.onHeadsetPlug(true,true);
                }
            }
        }
    }

    public interface HeadsetPlugListener {
        void onHeadsetPlug(boolean isPlug,boolean flag);//true说明没有耳机   false说明有耳机   flag标记有线还是无线耳机的
    }
}

