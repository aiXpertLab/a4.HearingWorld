package com.seeingvoice.www.svhearing.heartests.puretest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import customprogressview.ProgressCircleView;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.puretest.sinwavesound.playingThreadtest;
import com.seeingvoice.www.svhearing.ui.AttentionView;
import com.seeingvoice.www.svhearing.util.AudioUtil;
import com.seeingvoice.www.svhearing.util.PopupDialog;

public class BeforePureTestActivity extends TopBarBaseActivity implements View.OnClickListener {

    private static final String TAG = BeforePureTestActivity.class.getName();
    private Button mBtnNotiContinue,mBtnVolumeContinue,mBtnTestHeadphone,mBtnChosRightEar,mBtnChosLeftEar;
    private ProgressCircleView progressCircleview;
    private AttentionView attentionView1,attentionView2,attentionView3;
    private MyRunnable mRunnable = null;
    private ConstraintLayout mLayoutChosEar;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                if (isWiredOn || isWirelessOn){
                    mLayoutChosEar.setVisibility(View.VISIBLE);
                }else {
                    mLayoutChosEar.setVisibility(View.INVISIBLE);
                }
            }
            super.handleMessage(msg);
        }
    };
    private int i = 0;
    private ConstraintLayout layout1, layout2,layout3;

    private AudioManager mAudioManager = null;
    private BluetoothAdapter BlueAdapter;
    private Context mContext;
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private boolean isWiredOn = false,isWirelessOn = false;
    private PopupDialog dialog;
    private ProgressDialog progressDialog = null;

    //发出声音给耳机
    private Thread thread;
    private boolean isLeft = false;
    private playingThreadtest mPlayThread;
    private Intent mIntent;

    @Override
    protected int getConentView() {
        return R.layout.activity_before_pure_test;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("测试前准备");
        setTitleBack(true);
        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.jiaocheng, null);

        mBtnNotiContinue = findViewById(R.id.mBtnNotiContinue);
        mBtnNotiContinue.setOnClickListener(this);
        mBtnVolumeContinue = findViewById(R.id.mBtnVolumeContinue);
        mBtnVolumeContinue.setOnClickListener(this);
        mBtnTestHeadphone = findViewById(R.id.btn_test_headphone);
        mBtnTestHeadphone.setVisibility(View.INVISIBLE);
        mBtnTestHeadphone.setOnClickListener(this);
        mBtnChosLeftEar = findViewById(R.id.btn_left_ear);
        mBtnChosLeftEar.setOnClickListener(this);
        mBtnChosRightEar = findViewById(R.id.btn_right_ear);
        mBtnChosRightEar.setOnClickListener(this);

        attentionView1 = findViewById(R.id.layout1_attention);
        attentionView1.setTitle(getResources().getString(R.string.notice_before_puretest));
        attentionView2 = findViewById(R.id.layout2_attention);
        attentionView2.setTitle(getResources().getString(R.string.finish_verify_volume));
        attentionView3 = findViewById(R.id.layout3_attention);
        attentionView3.setTitle(getResources().getString(R.string.notice_headphone_on));
        layout1 = findViewById(R.id.layout_1);
        layout2 = findViewById(R.id.layout_2);
        layout3 = findViewById(R.id.layout_3);
        layout2.setVisibility(View.INVISIBLE);
        layout3.setVisibility(View.INVISIBLE);
        mLayoutChosEar = findViewById(R.id.layout_choose_ear);
        progressCircleview = findViewById(R.id.progress_circleview);
        progressCircleview.setProgress(0);

        if (mRunnable == null) {
            mRunnable = new MyRunnable();
            mHandler.postDelayed(mRunnable, 0);
        }
        mIntent = new Intent(this, HearingThreshold.class);
    }

    //音量进度异步定时器
    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if (i < 50 || i == 50) {
                progressCircleview.setProgress(i++);
                mHandler.postDelayed(this, 50);
            }else {
                int haldMaxVol = AudioUtil.getInstance(BeforePureTestActivity.this).getMaxMediaVolume()/2;
                AudioUtil.getInstance(BeforePureTestActivity.this).setMediaVolume(haldMaxVol);
                mHandler.removeCallbacks(mRunnable);
                mRunnable = null;
                mBtnVolumeContinue.setVisibility(View.VISIBLE);
                attentionView2.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            dialog = PopupDialog.create(this, "温馨提示", "已经校验好音量，无需调节！", "确定", null,"取消",null,false,true,true);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtnNotiContinue:
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                break;
            case R.id.mBtnVolumeContinue:
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                initHeadset();//初始化判断耳机状态的管理器
                isHeadsetConn();
                HeadsetConnedChangeUI();
                break;
            case R.id.btn_test_headphone:
                showProgressDialog();
                TestPlaying();
                break;
            case R.id.btn_left_ear:
                mIntent.putExtra("isLeft",true);
                startActivity(mIntent);
                finish();
                break;
            case R.id.btn_right_ear:
                mIntent.putExtra("isLeft",false);
                startActivity(mIntent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化音频管理器和蓝牙适配器，为测试耳机做准备
     */
    private void initHeadset() {
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mContext = getApplicationContext();
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        setHeadsetListner();//设置监听耳机连接断开状态
    }

    //耳机植入监听
    private void setHeadsetListner() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver(new HeadsetPlugReceiver.HeadsetPlugListener() {
            @Override
            public void onHeadsetPlug(boolean isPlug,boolean flag) {
                if (flag){//有线耳机
                    isWiredOn = isPlug;
                    Log.e(TAG, "Listner: 有限耳机状态 isWiredOn~"+isWiredOn);
                }else {//无线耳机
                    isWirelessOn = isPlug;
                    Log.e(TAG, "Listner: 无线耳机状态 isWirelessOn~"+isWirelessOn);
                }
                HeadsetConnedChangeUI();//当有线无线耳机连接状态改变后，UI变化
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");//监听手机蓝牙开关状态
        registerReceiver(mHeadsetPlugReceiver,intentFilter);
    }

    private void HeadsetConnedChangeUI() {
        if (isWiredOn || isWirelessOn){
            mBtnTestHeadphone.setVisibility(View.VISIBLE);
            if (null != dialog){
                dialog.dismiss();
            }
        }
        if(!isWiredOn && !isWirelessOn){
            mBtnTestHeadphone.setVisibility(View.GONE);
            mLayoutChosEar.setVisibility(View.GONE);
            dialog = PopupDialog.create(this, "温馨提示", "请先连接耳机！", "确定", null,"",null,false,true,true);
            dialog.show();
        }
    }

    //activity刚启动时，检测一下耳机是否连接
    private void isHeadsetConn() {
        isWiredOn = isWiredOn();
        isWirelessOn = isWirelessOn();
    }

    //判断有线耳机是否连接
    private boolean isWiredOn() {
        if(mAudioManager.isWiredHeadsetOn()){
            return true;
        }else{
            return false;
        }
    }

    //设置媒体播放麦克风
    private boolean isWirelessOn() {//判断蓝牙耳机连接了没有
        if (null != BlueAdapter){
            int state = BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            if (BluetoothProfile.STATE_CONNECTED == state) {//A2DP连接上之后，声音从耳机里出来
                microSpeaker(BeforePureTestActivity.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                Log.e(TAG, "isWirelessOn: BluetoothProfile.STATE_CONNECTED");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {//A2DP断开之后，声音外放
                Log.e(TAG, "isWirelessOn: BluetoothProfile.STATE_DISCONNECTED");
                loudSpeaker(BeforePureTestActivity.this);                // 蓝牙设备未连接，声音外放，
                return false;
            }
            return false;
        }else {
            Log.e(TAG, "isWirelessOn: 该手机不支持蓝牙功能~");
            return false;
        }
    }

    //外放
    public void loudSpeaker(Activity context) {
        mAudioManager.setSpeakerphoneOn(true);
        context.setVolumeControlStream(0);
        mAudioManager.setMode(0);
    }

    //内放
    public void microSpeaker(Activity context) {
        mAudioManager.setSpeakerphoneOn(false);
        context.setVolumeControlStream(0);
        mAudioManager.setMode(0);
    }


    private void showProgressDialog(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("温馨提示");
        progressDialog.setMessage("正在播放测试音，请戴好耳机，不要离开此页面！");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = progressDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // 设置宽度
        p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
        p.gravity = Gravity.CENTER;//设置位置
        dialogWindow.setAttributes(p);
    }

    /**
     * 测试耳机是否能正常听到声音
     */
    private void TestPlaying(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                playSound(!isLeft,1000,40,600);
                playSound(!isLeft,1000,40,800);
                playSound(!isLeft,1000,40,1000);
                // 右耳
                playSound(isLeft,1000,40,600);
                playSound(isLeft,1000,40,800);
                playSound(isLeft,1000,40,1000);
                releasePlay();
                if (null != progressDialog){
                    progressDialog.dismiss();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        thread.start();
    }


    /**
     * 测试耳机用，播放40分贝纯音
     * @param isleft
     * @param f
     * @param db
     */
    private void playSound(boolean isleft,int f,int db,int time) {
        releasePlay();
        mPlayThread = new playingThreadtest(isleft,f,db);
        mPlayThread.start();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 声音线程播放完毕需要释放资源
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHeadsetPlugReceiver){
            unregisterReceiver(mHeadsetPlugReceiver);
        }
        releasePlay();
        destroyThread();
        if (dialog != null){
            dialog.dismiss();
        }
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        releasePlay();
        try {
            if (null != thread && Thread.State.RUNNABLE == thread .getState()) {
                try {
                    Thread.sleep(500);
                    thread .interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread = null;
        }
    }

    private void releasePlay() {
        if(null != mPlayThread) {
            mPlayThread.stopPlay();
            mPlayThread = null;
        }
    }
}
