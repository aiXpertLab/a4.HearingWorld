package com.seeingvoice.www.svhearing.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;

import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Date:2018/12/19
 * Time:17:45
 * auther:zyy
 */
public class BluetoothManager {
    private static final String TAG = BluetoothManager.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothProfile mHfpProfile;
    private BluetoothHeadset mBluetoothHeadset;
    private Context mContext;
    private List<BluetoothDevice> mConnectedDevices;


    private BluetoothManager() {}

    private static class Holder{    //只有内部类可以为static
        private static BluetoothManager instance = new BluetoothManager();
    }

    public static BluetoothManager getInstance(){ return Holder.instance; }

    public void setContext(Context context){
        this.mContext = context;
    }

    /** 手机是否支持蓝牙*/
    public boolean isSupportBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter!=null){
            return true;
        }
        return false;
    }

    /** 手机蓝牙适配器是否开启了*/
    public boolean isBluetoothOpen(){
        if (mBluetoothAdapter != null){
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    /** 强制打开蓝牙*/
    public void openBluetooth(){
        if (isSupportBluetooth()){
            mBluetoothAdapter.enable();
        }
    }

    /** 请求启动蓝牙适配器*/
    public void requestStartBluetooth(int requestCode, Activity activity){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent,requestCode);
    }



    /** 关闭蓝牙*/
    public void closeBluetooth(){
        if (isSupportBluetooth() && mBluetoothAdapter.isEnabled()){
            boolean rec = mBluetoothAdapter.disable();
        }
    }

    /** 查询配对设备*/
    public List<BluetoothDevice> checkDevices() {
        List<BluetoothDevice> deviceslist = new ArrayList<>();
//        List<BluetoothDevice> temlist = new ArrayList<>();
//        String[] BlueHeadSetSupported = {"X-I8S","TicPods Free","W.mp","Hi-TWS R","Hi-TWS L","C7B","Hi-TWS","X8"};
        if (mBluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices != null && pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (!device.getName().isEmpty()) {
                        deviceslist.add(device);//把BondedDevices和名字非空的 蓝牙设备都添加到列表中
                    }
                }
            }
        }
        return deviceslist;
    }

    /**
     * mBluetoothAdapter.startDiscovery() 该方法启动一个异步线程，方法被执行后立即返回一个布尔值，表示是否成功启动
     * 大约12秒的查询扫描，随后扫描每个蓝牙设备以获取名称
     * */
    public void findBluetoothDevice(){
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && !mBluetoothAdapter.isDiscovering()){
            if (mBluetoothAdapter.startDiscovery()){
                ToastUtil.showShortToast("开始扫描见声蓝牙耳机...");
            }else {
                ToastUtil.showShortToast("启动蓝牙扫描功能失败！");
            }
        }
    }

    /** 反射来调用BluetoothDevice.removeBond取消设备的配对*/
//    public void removeNoSeeVisDevice(BluetoothDevice nodevice){
//        if (isBluetoothOpen()){
//            try {
//                Method m = nodevice.getClass().getMethod("removeBond", (Class[]) null);
//                m.invoke(nodevice, (Object[]) null);
//            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
//            }
//        }
//    }


    /** 取消搜索*/
    public void cancelDiscover(){
        if (isSupportBluetooth()){
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    public BluetoothDevice HaveConnedBlueDevice(final String address, Activity activity){
        if (mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED ){
            System.out.println("BluetoothProfile.STATE_CONNECTED   连接状态"+mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP));
            final List<BluetoothDevice> mConnedList = new ArrayList<>();
            mBluetoothAdapter.getProfileProxy(activity, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    for (BluetoothDevice device : proxy.getConnectedDevices()){
                        if (device.getAddress().equals(address)){
                            mConnedList.add(device);
                        }
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {

                }
            },BluetoothProfile.A2DP);
        }
        return null;
    }

    /** 获得当前设备的连接状态*/
    public List<BluetoothDevice> getConnectDevice(){
        int flag = -1;
        int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);

        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        }

        if (flag == a2dp) {
            mBluetoothAdapter.getProfileProxy(mContext, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceDisconnected(int profile) {
                }

                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    mBluetoothA2dp = (BluetoothA2dp) proxy;
                    mConnectedDevices = mBluetoothA2dp.getConnectedDevices();//当前手机蓝牙媒体音频连接的耳机设备
                    if (mConnectedDevices != null && mConnectedDevices.size() > 0) {
                        for (BluetoothDevice device : mConnectedDevices) {
                            if (device.getName().contains("SV-H1")){
                                ToastUtil.showLongToast("启动APP前已经连接了SV-H1耳机了");
                                return;
                            }
                        }
                    } else {
                        ToastUtil.showLongToast("启动APP前已经连接了SV-H1耳机了");
                        return;
                    }
                    System.out.println("之前连接成功过：DDDD"+mConnectedDevices);
                }
            }, flag);
            return mConnectedDevices;

        }
        return null;
    }


    /** 开始配对*/
//    public void createBond(BluetoothDevice bluetoothDevice){
//        if (bluetoothDevice != null){
//            bluetoothDevice.createBond();
//            connect(bluetoothDevice);
//            Log.e(TAG, "createBond: 配对并且去连接蓝牙耳机");
//
//        }else{
//        }
//    }

    public void initBlueProfileA2dpHeadSet(Context context){
        getBluetoothA2DP(context);
        getBluetoothHeadset(context);
    }


    /** 建立蓝牙连接
     * connect 和disconnect都是hide方法，普通应用只能通过反射来调用
     * */
    public void connectBlueHeadset(BluetoothDevice bluetoothDevice){
        connectA2dp(bluetoothDevice);
        connectHeadset(bluetoothDevice);
        setPriority(bluetoothDevice,100);
    }

    /** 断开蓝牙连接
     * connect 和disconnect都是hide方法，普通应用只能通过反射来调用
     * */
    public void disconnectBlueHeadset(BluetoothDevice bluetoothDevice){
        setPriority(bluetoothDevice,0);
        disconnectA2dp(bluetoothDevice);
        disconnectHeadset(bluetoothDevice);
    }




    public void connectA2dp(BluetoothDevice bluetoothDevice){
        try {
            Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect",BluetoothDevice.class);
            connect.setAccessible(true);
            connect.invoke(mBluetoothA2dp,bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectHeadset(BluetoothDevice bluetoothDevice){
        try {
            Method connect = mBluetoothHeadset.getClass().getDeclaredMethod("connect",BluetoothDevice.class);
            connect.setAccessible(true);
            connect.invoke(mBluetoothHeadset,bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectA2dp(BluetoothDevice bluetoothDevice){
        try {
            Method disconnect = mBluetoothA2dp.getClass().getDeclaredMethod("disconnect",BluetoothDevice.class);
            disconnect.setAccessible(true);
            disconnect.invoke(mBluetoothA2dp,bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectHeadset(BluetoothDevice bluetoothDevice){
        try {
            Method disconnect = mBluetoothHeadset.getClass().getDeclaredMethod("disconnect",BluetoothDevice.class);
            disconnect.setAccessible(true);
            disconnect.invoke(mBluetoothHeadset,bluetoothDevice);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setPriority(BluetoothDevice device, int priority) {
        if (mBluetoothA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod =BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class,int.class);
            connectMethod.invoke(mBluetoothA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 连接蓝牙耳机*/
    public void getBluetoothA2DP(final Context context){
        if (mBluetoothAdapter == null){
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            ((BluetoothActivity)context).startActivityForResult(enableBtIntent,1);
            return;
        }

        mBluetoothAdapter.getProfileProxy(context, mListener, BluetoothProfile.A2DP);
    }

    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP){
                mBluetoothA2dp = (BluetoothA2dp)proxy;//获得A2DP，可以通过反射得到连接的method
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP){
                mBluetoothA2dp = null;
            }

        }
    };

    public void getBluetoothHeadset(final Context context) {
        if (mBluetoothAdapter == null){
            return;
        }
        mBluetoothAdapter.getProfileProxy(context, mHeadsetProfileListener, BluetoothProfile.HEADSET);
    }

    /**
     * @Fields mHeadsetProfileListener : BluetoothHeadset服务监听器
     */
    private BluetoothProfile.ServiceListener mHeadsetProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };

        /**
         * 断开蓝牙耳机
         * 注意：在程序退出之前（OnDestrory），需要断开蓝牙相关的Service
         * 否则，程序会报异常：server leaks
         * */


    public void disableAdapter(){
        if (mBluetoothAdapter == null){
            return;
        }
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //关闭ProfileProxy,也就是断开service连接
        if (mBluetoothA2dp != null){
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetoothA2dp);
        }

        if (mBluetoothHeadset != null){
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
        }
    }
}
