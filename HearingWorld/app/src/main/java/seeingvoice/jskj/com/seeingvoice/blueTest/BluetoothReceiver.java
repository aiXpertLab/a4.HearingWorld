package seeingvoice.jskj.com.seeingvoice.blueTest;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 *
 *蓝牙广播接收者
 * Date:2019/8/2
 * Time:15:59
 * auther:zyy
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothReceiver.class.getName();
    String pin = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000,我们是0000
    private OnBluetoothListener mOnBluetoothListener;
    String deviceName;
    String deviceAddress;
    int deviceClassType;
    int deviceState;

    public void setOnBluetoothListener(OnBluetoothListener onBluetoothListener){
        this.mOnBluetoothListener = onBluetoothListener;
    }

    public BluetoothReceiver() {//空的构造器
    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice btDevice = null;  //创建一个蓝牙device对象
        String action = intent.getAction(); //得到action
        Log.d(TAG, "收到广播"+action);
        // 从Intent中获取设备对象
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (null != btDevice){
            deviceName = btDevice.getName();
            deviceAddress = btDevice.getAddress();
            deviceClassType = btDevice.getBluetoothClass().getMajorDeviceClass();//发现的设备类型
            deviceState = btDevice.getBondState();
        }
        if (action == null){
            Log.d(TAG, "action == null");
            return;
        }
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
                    mOnBluetoothListener.bluetoothStateOn();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    //手机蓝牙正在关闭
                    break;
                case BluetoothAdapter.STATE_OFF:
                    //手机蓝牙已关闭
                    Log.d(TAG, "STATE_OFF 手机蓝牙关闭");
                    mOnBluetoothListener.bluetoothStateOff();
                    break;
            }
        }

        if (!TextUtils.isEmpty(deviceName) && deviceName.contains("SV-H1") && deviceClassType == BluetoothClass.Device.Major.AUDIO_VIDEO){
//            && deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
//                    deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
//                    deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
            switch (action){
                case BluetoothDevice.ACTION_FOUND:
//                    Log.e(TAG, "onReceive: BluetoothDevice.ACTION_FOUND"+"发现了SV-H1"+deviceState);
//                    mOnBluetoothListener.deviceFound(btDevice);
                    if (deviceState == BluetoothDevice.BOND_NONE){//没有配对先进行配对
                        Log.e(TAG, "onReceive: BluetoothDevice.BOND_NONE"+"发现了SV-H1");
                        mOnBluetoothListener.deviceFound(btDevice);
                    }else if (deviceState == BluetoothDevice.BOND_BONDED){//如果已经配对了，则直接连接
                        mOnBluetoothListener.deviceBonded(btDevice);
                    }
                    break;
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                        case BluetoothA2dp.STATE_CONNECTING:
                            break;
                        case BluetoothA2dp.STATE_CONNECTED:
                            Log.e(TAG, "onReceive: A2DP连接");
                            mOnBluetoothListener.deviceA2dpConned(btDevice);
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTED:
                            Log.e(TAG, "onReceive: A2DP断开连接");
                            mOnBluetoothListener.deviceA2dpDisConned(btDevice);
                            break;
                        default:
                            break;
                    }
                    break;
                case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1)) {
                        case BluetoothHeadset.STATE_CONNECTING:
                            break;
                        case BluetoothHeadset.STATE_CONNECTED:
                            Log.e(TAG, "onReceive: HEADSET连接");
                            mOnBluetoothListener.deviceHeadsetConned(btDevice);
                            break;
                        case BluetoothHeadset.STATE_DISCONNECTED:
                            Log.e(TAG, "onReceive: HEADSET断开连接");
                            mOnBluetoothListener.deviceHeadsetDisConned(btDevice);
                            break;
                        default:
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
//                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDED:
                            //配对成功
                            mOnBluetoothListener.deviceBonded(btDevice);
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            //正在配对中
                            break;
                        case BluetoothDevice.BOND_NONE:
                            //配对不成功的话，重新尝试配对
                            mOnBluetoothListener.deviceBondNone(btDevice);
                            break;
                        default:
                            break;
                    }
                    break;
//                case BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT:
////                    Log.i("+IPHONEACCEV", "intent  "+intent);
//                    Log.i("耳机电量", "intent  "+intent);
//                    String command = intent.getStringExtra(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD);
////                    BluetoothHeadset.VENDOR_RESULT_CODE_COMMAND_ANDROID;
//                    Log.i("耳机电量", "command:"+command);
//                    String action1 = intent.getAction();
////                    if (action1.equals("ACTION_GET_BATTERY_FR_IND") || action1.equals("ACTION_REPORT_BATTERY_FR_STATUS")){
////                        byte status = intent.getByteExtra("EXTRA_BATTERY_LEVEL", (byte) 0);
////                    }
//                    if ("+IPHONEACCEV".equals(command)) {
//                        Object[] args = (Object[]) intent.getSerializableExtra(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS);
//                        if (args.length >= 3 && args[0] instanceof Integer && ((Integer)args[0])*2+1<=args.length) {
//                            for (int i=0;i<((Integer)args[0]);i++) {
//                                if (!(args[i*2+1] instanceof Integer) || !(args[i*2+2] instanceof Integer)) {
//                                    continue;
//                                }
//                                if (args[i*2+1].equals(1)) {
//                                    float level = (((Integer)args[i*2+2])+1)/10.0f;
//                                    int procentLevel = Math.round(level*100);
//                                    Log.i("耳机电量", "battery"+args.toString());
//                                    mOnBluetoothListener.deviceHeadsetBatteryLevel(procentLevel);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    break;
            }
        }
    }

//    private void addBluetoothUsage(long uSecNow) {
//        long btOnTimeMs = mStats.getBluetoothOnTime(uSecNow, mStatsType) / 1000;
//        double btPower = btOnTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_ON)
//                / 1000;
//        int btPingCount = mStats.getBluetoothPingCount();
//        btPower += (btPingCount
//                * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_AT_CMD)) / 1000;
//        BatterySipper bs = addEntry(getActivity().getString(R.string.power_bluetooth),
//                DrainType.BLUETOOTH, btOnTimeMs, R.drawable.ic_settings_bluetooth,
//                btPower + mBluetoothPower);
//        aggregateSippers(bs, mBluetoothSippers, "Bluetooth");
//    }


}
