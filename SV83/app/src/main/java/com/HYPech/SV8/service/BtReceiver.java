package com.HYPech.SV8.service;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 监听蓝牙广播-各种状态
 */
public class BtReceiver extends BroadcastReceiver {
    private static final String TAG = BtReceiver.class.getSimpleName();
    private final Listener mListener;
    IntentFilter filter;

    public BtReceiver(Context cxt, Listener listener) {
        mListener = listener;
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开关状态
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//蓝牙开始搜索
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索结束

        filter.addAction(BluetoothDevice.ACTION_FOUND);//蓝牙发现新设备(未配对的设备)
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);//在系统弹出配对框之前(确认/输入配对码)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备配对状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//最底层连接建立
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//最底层连接断开

        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED); //BluetoothAdapter连接状态
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //BluetoothHeadset连接状态
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED); //BluetoothA2dp连接状态
//        cxt.registerReceiver(this, filter);
    }
    public void registerReceiver(Context cxt){
        cxt.registerReceiver(this, filter);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.d("ljp",TAG+"action == null");
            return;
        }
        Log.i(TAG, "===" + action);
        BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (dev != null)
            Log.i(TAG, "BluetoothDevice: " + dev.getName() + ", " + dev.getAddress());
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG, "STATE: " + state);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                mListener.foundDevFinish(dev);
                break;

            case BluetoothDevice.ACTION_FOUND:

                if(dev.getName()!= null) {
                    Log.d("ljp", "发现设备"+dev.getName());
                }
                mListener.foundDev(dev);
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST: //在系统弹出配对框之前，实现自动配对，取消系统配对框
                /*try {
                    abortBroadcast();//终止配对广播，取消系统配对框
                    boolean ret = dev.setPin("1234".getBytes()); //设置PIN配对码(必须是固定的)
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                Log.i(TAG, "BOND_STATE: " + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                mListener.blueConnectionStateChanged(BluetoothDevice.ACTION_ACL_CONNECTED,dev);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                mListener.blueConnectionStateChanged(BluetoothDevice.ACTION_ACL_DISCONNECTED,dev);
                break;

            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                break;
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0));
                break;
        }
    }

    public interface Listener {
        void foundDev(BluetoothDevice dev);
        void foundDevFinish(BluetoothDevice dev);
        void blueConnectionStateChanged(String state, BluetoothDevice dev);
    }
}