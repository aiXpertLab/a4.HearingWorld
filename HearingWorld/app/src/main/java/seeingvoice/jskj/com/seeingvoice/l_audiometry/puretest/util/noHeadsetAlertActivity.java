package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.HeadsetPlugReceiver;

public class noHeadsetAlertActivity extends AppCompatActivity {

    private TextView mTvHeadsetHint;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private AudioManager mAudioManager = null;
    private BluetoothAdapter BlueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_headset_aleart);

        mTvHeadsetHint = findViewById(R.id.tv_headset_hint);

//        detectAudioOutputDevice();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        //activity刚启动时，检测一下耳机是否连接
        if (isHeadsetConn()){
            mTvHeadsetHint.setText("耳机已连接！");
            finish();
        }else {
            mTvHeadsetHint.setText("请连接耳机！有线耳机，蓝牙耳机均可。");
        }
        //监听耳机连接状态的变化
        setHeadsetListner();
    }

    //activity刚启动时，检测一下耳机是否连接
    private boolean isHeadsetConn() {
        return isWiredOn()||isWirelessOn();
    }

    //获取当前使用的麦克风，设置媒体播放麦克风
    private boolean isWiredOn() {
        if(mAudioManager.isWiredHeadsetOn()){
            mTvHeadsetHint.setText("有线耳机OK耳机已连接！");
            return true;
        }else{
            mTvHeadsetHint.setText("有线耳机不OK");
            return false;
        }
    }
    private boolean isWirelessOn() {
        if (null != BlueAdapter){
            if (BluetoothProfile.STATE_CONNECTED == BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP)) {
                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                microSpeaker(noHeadsetAlertActivity.this);
                mTvHeadsetHint.setText("无线耳机OK");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP)) {
                // 蓝牙设备未连接，声音外放，
                loudSpeaker(noHeadsetAlertActivity.this);
                mTvHeadsetHint.setText("无线耳机不OK");
                return false;
            } else {
                // 蓝牙设备未连接，声音外放，
                loudSpeaker(noHeadsetAlertActivity.this);
                mTvHeadsetHint.setText("无线耳机不OK");
                return false;
            }
        }else {
            mTvHeadsetHint.setText("该手机不支持蓝牙功能");
            return false;
        }
    }

    //耳机植入监听
    private void setHeadsetListner() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver(new HeadsetPlugReceiver.HeadsetPlugListener() {
            @Override
            public void onHeadsetPlug(boolean isPlug,boolean flag) {
                if (isPlug) {
//                    if (flag){//有线
//                        finish();
//                    }else {//无线
//                        finish();
//                    }
                }else {
                    mTvHeadsetHint.setText("请连接耳机");
                }
//                else {
//                    //耳机输出
//                    microSpeaker(noHeadsetAlertActivity.this);
//                    mTvHeadsetHint.setText("监听：有线耳机已断开连接");
//                }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mHeadsetPlugReceiver, intentFilter);
    }

    //外放
    public void loudSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        context.setVolumeControlStream(0);
        audioManager.setMode(0);
    }

    //内放
    public void microSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        context.setVolumeControlStream(0);
        audioManager.setMode(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHeadsetPlugReceiver);
    }
}
