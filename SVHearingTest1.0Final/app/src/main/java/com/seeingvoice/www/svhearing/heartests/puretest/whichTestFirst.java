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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.puretest.sinwavesound.playingThreadtest;

/**
 * Date:2019/3/19
 * Time:13:46
 * auther:zyy
 */
public class whichTestFirst extends TopBarBaseActivity {

    private static final String TAG = whichTestFirst.class.getName();
    private Intent mIntent;
    private Button btn_left,btn_right,btn_test_earbuds;
    private playingThreadtest mPlayThread;
    private boolean isPlaying = false;
    private boolean isLeft = false;
//    private ProgressDialog dialog;
    private ProgressDialog progressDialog = null;
    private ConstraintLayout Step2Layout;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                Step2Layout.setVisibility(View.VISIBLE);
                if (null != progressDialog){
                    progressDialog.dismiss();
                }
            }
        }
    };

    private Thread thread;

    private AudioManager mAudioManager = null;
    private BluetoothAdapter BlueAdapter;
    private Context mContext;
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;

    @Override
    protected int getConentView() {
        return R.layout.activity_which_test_first;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("耳机连好了吗？");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null,whichTestFirst.this, PureTestCourse.class);
            }
        });
          initData();
    }

    protected void initData() {
        btn_left = findViewById(R.id.btn_go_left_test);
        btn_right = findViewById(R.id.btn_go_right_test);
        btn_test_earbuds = findViewById(R.id.btn_test_earbuds);
        btn_left.setOnClickListener(listener);
        btn_right.setOnClickListener(listener);
        btn_test_earbuds.setOnClickListener(listener);
        mIntent = new Intent(this, HearingThreshold.class);
        Step2Layout = findViewById(R.id.step2);

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mContext = getApplicationContext();
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        setHeadsetListner();
        isHeadsetConn();
    }

    //activity刚启动时，检测一下耳机是否连接
    private void isHeadsetConn() {
        boolean isWiredOn = isWiredOn();
        boolean isWirelessOn = isWirelessOn();
        if (isWiredOn || isWirelessOn){
            Log.e(TAG, "isHeadsetConn: 耳机已连接，可以测试啦~");
        }else {
            Log.e(TAG, "isHeadsetConn: 耳机未连接，请连接耳机~");
        }
    }

    //获取当前使用的麦克风，设置媒体播放麦克风
    private boolean isWiredOn() {
        if(mAudioManager.isWiredHeadsetOn()){
            return true;
        }else{
            return false;
        }
    }
    private boolean isWirelessOn() {
        if (null != BlueAdapter){
            int state = BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            if (BluetoothProfile.STATE_CONNECTED == state) {
                microSpeaker(whichTestFirst.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                Log.e(TAG, "isWirelessOn: BluetoothProfile.STATE_CONNECTED");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                Log.e(TAG, "isWirelessOn: BluetoothProfile.STATE_DISCONNECTED");
                loudSpeaker(whichTestFirst.this);                // 蓝牙设备未连接，声音外放，
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

    //耳机植入监听
    private void setHeadsetListner() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver(new HeadsetPlugReceiver.HeadsetPlugListener() {
            @Override
            public void onHeadsetPlug(boolean isPlug,boolean flag) {
                if (flag){//有线耳机
                    if (isPlug){
                        Log.e(TAG, "setHeadsetListner: 有线耳机已连接，可以测试啦~");
                    }else {
                        Log.e(TAG, "setHeadsetListner: 有线耳机已断开，请重新连接~");
                    }
                }else {//无线耳机
                    if (isPlug){
                        Log.e(TAG, "setHeadsetListner: 无线耳机已连接，可以测试啦~");
                    }else {
                        Log.e(TAG, "setHeadsetListner: 无线耳机已断开，请重新连接~");
                    }
                }
                isHeadsetConn();
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mHeadsetPlugReceiver,intentFilter);
    }

    OnMultiClickListener listener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.btn_go_left_test:
                    mIntent.putExtra("isLeft",true);
                    startActivity(mIntent);
                    finish();
                    break;
                case R.id.btn_go_right_test:
                    mIntent.putExtra("isLeft",false);
                    startActivity(mIntent);
                    finish();
                    break;
                case R.id.btn_test_earbuds:
                    Step2Layout.setVisibility(View.GONE);
                    showProgressDialog();
                    TestPlaying();
                    break;
            }
        }
    };

    private void showProgressDialog(){
        final int MAX = 100;
        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("请不要离开此页面");
        progressDialog.setMessage("请确保戴好耳机，耳机连好手机！");
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
        //p.alpha = 0.8f;//设置透明度
        dialogWindow.setAttributes(p);
//        Window window = progressDialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        window.setGravity(Gravity.LEFT | Gravity.TOP);
//        lp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.x = 10;
//        lp.y = 10;
//        lp.width = 200;
//        lp.height = 200;
//        lp.alpha = 0.6f;
//        window.setAttributes(lp);
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
                mHandler.sendEmptyMessage(0);
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
    private void releasePlay() {
        if(null != mPlayThread) {
            mPlayThread.stopPlay();
            mPlayThread = null;
        }
    }
    /**
     * Activity 销毁时不用的资源需要及时释放，以免造成内存泄露
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlay();
        destroyThread();
        if (null != mHeadsetPlugReceiver){
            unregisterReceiver(mHeadsetPlugReceiver);
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
}
