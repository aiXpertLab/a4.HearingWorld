package com.seeingvoice.www.svhearing.heartests.freePureTest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.AboutUsActivity;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.util.ToastUtil;

public class FreePureTestIndexActivity extends TopBarBaseActivity {

    private AudioManager mAudioManager = null;
    private TextView tvHint;
    private Context mContext;
    private BluetoothAdapter ba;
    private int isheadset = 2;    //默认值为2，这样，软件启动时，默认耳机是正常的：

    @Override
    protected int getConentView() {
        return R.layout.activity_free_pure_test_index;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        tvHint = findViewById(R.id.hint);
        setTitle("耳机没有连接");
        setTitleBack(true);
        setToolBarMenuOne("",R.mipmap.share_icon,null);
        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null,FreePureTestIndexActivity.this, AboutUsActivity.class);
            }
        });
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mContext = getApplicationContext();
//        mContext.registerReceiver(mReceiver, makeFilter());
        if(mAudioManager.isWiredHeadsetOn()){
            ToastUtil.showLongToastCenter("耳机OK");
        }else{
            ToastUtil.showLongToastCenter("耳机不OK");
        }
//        detectAudioOutputDevice();
        registerHeadsetPlugReceiver();//注册监听耳机连接的广播
        isheadset = getheadsetStatsu();
    }

    public int getheadsetStatsu(){
        AudioManager  audoManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

//      IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//      Intent iStatus = registerReceiver(null, iFilter);
//      boolean isConnected = iStatus.getIntExtra("state", 0) == 1;
//
//      if(isConnected){
//         Toast.makeText(MainActivity.this,"耳机ok",Toast.LENGTH_SHORT).show();
//      }

        if(audoManager.isWiredHeadsetOn()){
            return 1;
        }else{
            ToastUtil.showLongToastCenter("耳机不OK");
        }

        ba = BluetoothAdapter.getDefaultAdapter();

        if (ba == null){    //int isBlueCon;//蓝牙适配器是否存在，即是否发生了错误
            //isBlueCon = -1;     //error
            return -1;
        }else if(ba.isEnabled()) {
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备

            //查看蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
            int flag = -1;
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                flag = a2dp;
            } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                flag = headset;
            } else if (health == BluetoothProfile.STATE_CONNECTED) {
                flag = health;
            }
            //说明连接上了三种设备的一种
            if (flag != -1) {
                //isBlueCon = 1;            //connected
                return 2;
            }
        }
        return -2;
    }

    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);

        // for bluetooth headset connection receiver
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(headsetPlugReceiver, bluetoothFilter);
    }

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if(BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    isheadset--; //Bluetooth headset is now disconnected
                }else{
                    isheadset++;
                }
            } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        isheadset--;
                    }else if(intent.getIntExtra("state", 0) == 1){
                        isheadset++;
                    }
                }
            }
        }
    };

//    private void detectAudioOutputDevice(){
//        if(isBluetoothEarphoneAvailable() && null != mAudioManager){//是否有蓝牙耳机
//            //这个函数会导致mAudioManager.isBluetoothScoOn()为true，要加强判断，使用其他的API。
//            mAudioManager.setBluetoothScoOn(true);
//            // 判断蓝牙音频设备是否链接 在程序启动执行一次
//            if (mAudioManager.isBluetoothA2dpOn()) {// || mAudioManager.isBluetoothScoOn()
//                setAudioManagerInCallMode();
//                // audioManager.startBluetoothSco();
//                // if(audioManager.isWiredHeadsetOn())
//                // audioManager.startBluetoothSco();
////                verifyBluetoothSupport();
//                tvHint.setText("当前蓝牙耳机已经连接");
//                try {
//                    mAudioManager.startBluetoothSco();
//                } catch (NullPointerException e) {
//                    // TODO This is a temp workaround for
//                    // Lollipop
//                    Log.d("mAudioManager", "startBluetoothSco() failed. no bluetooth device connected.");
//                }
//            }else {
//                tvHint.setText("当前蓝牙耳机没有连接");
//            }
//        }else if(mAudioManager.isWiredHeadsetOn()){
//            //判断耳机是否连接
//            setAudioManagerInCallMode();
//            tvHint.setText("当前线性耳机连接");
//
//        }else {
//            // 打开扬声器
//            mAudioManager.setSpeakerphoneOn(true);
//            tvHint.setText("当前蓝牙耳机没有连接，请到设置里连接蓝牙耳机，或者插入线性耳机");
//        }
//    }
//    private boolean isBluetoothEarphoneAvailable(){
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(mBluetoothAdapter == null){
//            return false;
//        }else if(mBluetoothAdapter.isEnabled()){
//            int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
//
//            //查看是否蓝牙是否连接到耳机设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
//            if (a2dp == BluetoothProfile.STATE_CONNECTED ||	a2dp== BluetoothProfile.STATE_CONNECTING) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void setAudioManagerInCallMode() {
//        Log.d("mAudioManager","[AudioManager] Mode: MODE_IN_COMMUNICATION");
//        if(mAudioManager.isWiredHeadsetOn() || isBluetoothEarphoneAvailable()){
//            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        }else{
//            mAudioManager.setMode(AudioManager.MODE_NORMAL);
//        }
//    }
//
//
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
//                if(intent.hasExtra("state")) {
//                    if(intent.getIntExtra("state", 0) == 0) {
//                        ToastUtil.showLongToastCenter("headset not connected");
//                    } else if(intent.getIntExtra("state", 0) == 1) {
//                        ToastUtil.showLongToastCenter("headset connected");
//                    }
//                }
//            }
//
//            if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)){
//                switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
//                    case BluetoothA2dp.STATE_CONNECTING:
//                        break;
//                    case BluetoothA2dp.STATE_CONNECTED:
//                        break;
//                    case BluetoothA2dp.STATE_DISCONNECTED:
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    };
}
