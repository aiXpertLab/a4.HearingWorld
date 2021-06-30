package com.seeingvoice.www.svhearing.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Date:2018/12/19
 * Time:14:59
 * auther:zyy
 */
public class BluetoothReceiver extends BroadcastReceiver {
    OnBluetoothListener mOnBluetoothListener;

    public void setOnBluetoothListener(OnBluetoothListener onBluetoothListener){
        this.mOnBluetoothListener = onBluetoothListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device;
        String action = intent.getAction();
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int deviceClassType;
        if (null != device){
            if (!TextUtils.isEmpty(device.getName())){
//                if (device.getName().equals("SV-H1")||device.getName().equals("SV-L")||device.getName().equals("SV-R")){
                if (device.getName().equals("C7B") || device.getName().equals("C7")||device.getName().equals("TicPods Free")||device.getName().equals("AiroS-A")||device.getName().equals("SV-L")||device.getName().equals("SV-R")){
//                if (device.getName().equals("TOPPERS E1")){
                    switch (action) {
                        case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                            switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                                case BluetoothA2dp.STATE_CONNECTING:
                                    break;
                                case BluetoothA2dp.STATE_CONNECTED:
//                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                    mOnBluetoothListener.deviceConnected(device);
                                    break;
                                case BluetoothA2dp.STATE_DISCONNECTED:
                                    mOnBluetoothListener.deviceDisConnected(device);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
//                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            switch (state) {
                                case BluetoothA2dp.STATE_PLAYING:
                                    break;
                                case BluetoothA2dp.STATE_NOT_PLAYING:
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BluetoothDevice.ACTION_FOUND:
                            //找到指定的蓝牙<耳机>设备
                            deviceClassType = device.getBluetoothClass().getDeviceClass();//发现的设备类型
                            if (deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES || deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET || deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE) {
                                if (!device.getName().isEmpty()) {
                                    //当发现蓝牙耳机后，判断获得的名字不为空，接口实现发现耳机后的操作
                                    mOnBluetoothListener.deviceFound(device);
                                }
                            }else {
//                    mOnBluetoothListener.deviceNotFound();
                            }
                            break;
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
//                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            switch (bondState) {
                                case BluetoothDevice.BOND_BONDED:
                                    //配对成功
                                    mOnBluetoothListener.deviceBonded(device);
                                    break;
                                case BluetoothDevice.BOND_BONDING:
                                    mOnBluetoothListener.deviceBonding(device);
                                    //正在配对中
                                    break;
                                case BluetoothDevice.BOND_NONE:
                                    //不知道是蓝牙耳机的关系还是什么原因，经常配对不成功
                                    //配对不成功的话，重新尝试配对
                                    mOnBluetoothListener.deviceBondNone(device);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                            switch (state) {
                                case BluetoothAdapter.STATE_TURNING_ON:
                                    //手机蓝牙适配器打开了
//                        ToastUtil.showShortToast(context, " BluetoothAdapter.STATE_TURNING_ON:手机蓝牙适配器打开了");
                                    break;
                                case BluetoothAdapter.STATE_ON:
                                    //手机蓝牙已经打开，开始搜索并连接耳机
//                        ToastUtil.showShortToast(context, " BluetoothAdapter.STATE_TURNING_ON:手机蓝牙适配器打开了");
                                    mOnBluetoothListener.bluetoothStateOn();
                                    break;
                                case BluetoothAdapter.STATE_TURNING_OFF:
                                    //手机蓝牙正在关闭
                                    break;
                                case BluetoothAdapter.STATE_OFF:
                                    //手机蓝牙已关闭
                                    mOnBluetoothListener.bluetoothStateOff();
                                    break;
                            }
                    }
                }
            }
        }else {
            mOnBluetoothListener.noDeviceDiscovered();
        }
    }
}
