package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.victoralbertos.breadcumbs_view.BreadcrumbsView;
import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_ResultT4;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_Thread_StaticWAV;
import seeingvoice.jskj.com.seeingvoice.util.ArrayUtils;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L_AudioManager;
import seeingvoice.jskj.com.seeingvoice.util.PopupDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

/**
 * Conduct Pure Tone Hearing Test using Asset WAV files.
 *
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-09
 */

public class L_TestPTType4WAV_BAK extends MyTopBar implements View.OnClickListener {

    //region declares class variables
    private static final String TAG = "L_TestPTType4: ";

    private int hzIndex, dBIndex, progress;

    private Button btnStartPause,btnCanHear,btnNoCanHear;
    private MyCount mClock = null;
    private TextView tvCountDown,tvHzValue,tvDBValue;
    private ProgressBar mProgressBar;
    private Thread thread;
    private RadioGroup rg;
    private RadioButton rbLeft, rbRight;
    private BreadcrumbsView mLeftBreadcrumbsView;
    private BreadcrumbsView mRightBreadcrumbsView;
    private TextView tvFinishNumLeft,tvFinishNumRight;
    private Button btnCheckResult;
    private AudioManager mAudioManager = null;      //判断蓝牙耳机，有线耳机是否连接
    private BluetoothAdapter BlueAdapter;
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private TextView tvEarConnTips;
    private PopupDialog dialog;

    private final int[] dBArr = new int[]{10,  20, 25, 30, 35, 40, 45, 55, 70, 85, 100};
    private final int[] hzArr = MyData.getHz();

    private final int[][] lDBMinVal = new int[8][2];    //左耳阈值
    private final int[][] rDBMinVal = new int[8][2];    //右耳阈值

    public boolean isLeftFinished  = false;
    public L5_Thread_StaticWAV tWAVThread;

    private boolean isCurrentTestLeft = true;
    private boolean isRun = false;
    private boolean isRightFinished = false;
    private boolean isWiredOn, isWirelessOn;

//endregion declares

    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0) {reFreshUi(progress);}
        }
    };

    @Override
    protected int getContentView_sv() {
        return R.layout.a_puretone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // 标题栏设置
        setToolbarTitle("快速筛查测试中");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.ic_home, null);

        setToolBarMenuTwo("", R.drawable.repeat_play_selecter, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                if (btnStartPause.getVisibility() == View.INVISIBLE){
                    if (btnCheckResult.getVisibility() == View.VISIBLE){
                        ToastUtil.showShortToastCenter("结果已出！");
                    }else {
                        startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);
                    }
                }else {
                    ToastUtil.showShortToastCenter("开始测试后才能重播纯音！");
                }
            }
        });

        rbLeft      = findViewById(R.id.rd_left);
        rbRight     = findViewById(R.id.rd_right);
        rg          = findViewById(R.id.L_R_choose);
        btnCheckResult  = findViewById(R.id.btn_check_out_pure_result);
        btnStartPause   = findViewById(R.id.btn_start_pause);
        btnCanHear      = findViewById(R.id.btn_canHear);
        btnNoCanHear    = findViewById(R.id.btn_nocanHear);
        mProgressBar    = findViewById(R.id.progress_bar_h);
        mLeftBreadcrumbsView    = findViewById(R.id.left_breadcrumbs);
        mRightBreadcrumbsView   = findViewById(R.id.right_breadcrumbs);

        tvHzValue       = findViewById(R.id.tv_tone_value);
        tvDBValue       = findViewById(R.id.tv_volume_value);
        tvEarConnTips   = findViewById(R.id.tv_earbud_conn_tips);
        tvCountDown     = findViewById(R.id.tv_count_down);
        tvFinishNumLeft = findViewById(R.id.tv_finish_num_left);
        tvFinishNumRight= findViewById(R.id.tv_finish_num_right);

        mClock = new MyCount(3000,1000);

        rg.check(R.id.rd_left);

        btnCanHear.setOnClickListener(this);
        btnNoCanHear.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnCheckResult.setOnClickListener(this);

        // 初始化屏幕
        rbRight.setVisibility(View.INVISIBLE);
        btnCanHear.setVisibility(View.INVISIBLE);
        btnNoCanHear.setVisibility(View.INVISIBLE);
        tvEarConnTips.setVisibility(View.INVISIBLE);
        btnCheckResult.setVisibility(View.INVISIBLE);

        // 纯音测试分段进度条
        hzIndex = 0;
        dBIndex = 0;
        tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
        tvDBValue.setText(getString(R.string.t4_db, dBArr[dBIndex]));

        initHeadset();  //初始化耳机连接状态判断
        //activity刚启动时，检测一下耳机是否连接
        isHeadsetConn();
        HeadsetConnChangeUI();
        initVolume();
    }

    private void initVolume() {
        int halfMaxVol = L_AudioManager.getInstance(L_TestPTType4WAV_BAK.this).getMaxMediaVolume()/2;
        L_AudioManager.getInstance(L_TestPTType4WAV_BAK.this).setMediaVolume(halfMaxVol);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_pause) {
            showCanHearNoHearBtn();         //显示听见听不见按钮
            startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);    //开始测试，从默认的开始测起，也就1K赫兹，40分贝开始
            HeadsetConnChangeUI();
            mClock.start();
        }else if (v.getId() == R.id.btn_canHear){
            if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                canHearFunc();
            }
        }else if (v.getId() == R.id.btn_nocanHear){
            if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                cannotHearFunc();
            }
        }else if (v.getId() == R.id.btn_check_out_pure_result){
            if (!AntiShakeUtils.isInvalidClick(v,800)) {
                toResult();
            }
        }
    }

    /** 播放纯音*/
    private void startPureTonePlaying(int hzValue, int dBValue,  boolean Left) {
        Log.e("当前分贝索引和值", "canHearFunc: [dBIndex]"+"["+dBIndex+"]--"+"dBArr[dBIndex]="+dBArr[dBIndex]);
        stopThread();           //播放新纯音时应该把上次的进度条结束
        stopPureTonePlaying();  //播放新纯音先结束上次纯音
        mClock.start();         //倒计时开始

        tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
        tvDBValue.setText(getString(R.string.t4_db, dBArr[dBIndex]));

//        tWAVThread = new L_Thread_StaticWAV(this, L_Data.getWave(hzArr[hzIndex],dBArr[dBIndex]), isCurrentTestLeft);
        tWAVThread.start();     //开始播放新纯音
        startThread();          //新进度条从零开始运行
    }

    // 判断有线耳机，蓝牙耳机是否连接了
    private void initHeadset() {
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        //监听耳机连接状态的变化
        setHeadsetListner();
    }

    //activity刚启动时，检测一下耳机是否连接
    private void isHeadsetConn() {
        isWiredOn = isWiredOn();
        isWirelessOn = isWirelessOn();
    }

    //获取当前使用的麦克风，设置媒体播放麦克风
    private boolean isWiredOn() {
        return (mAudioManager.isWiredHeadsetOn());
    }
    private boolean isWirelessOn() {
        if (null != BlueAdapter){
            int state = BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            if (BluetoothProfile.STATE_CONNECTED == state) {
                microSpeaker(L_TestPTType4WAV_BAK.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_CONNECTED");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_DISCONNECTED");
                return false;
            }
        }else {
            return false;
        }
        return false;
    }

    //耳机植入监听
    private void setHeadsetListner() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver((isPlug, flag) -> {
            if (flag){//有线耳机
                if (isPlug){
                    tvEarConnTips.setText("有线耳机已连接，可以测试啦~");
                }else {
                    tvEarConnTips.setText("有线耳机已断开，请重新连接~");
                }
                isWiredOn = isPlug;
            }else {//无线耳机
                if (isPlug){
                    tvEarConnTips.setText("无线耳机已连接，可以测试啦~");
                }else {
                    tvEarConnTips.setText("无线耳机已断开，请重新连接~");
                }
                isWirelessOn = isPlug;
            }
            HeadsetConnChangeUI();//耳机连接状态改变后，改变UI
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");   //监听手机蓝牙开关状态
        registerReceiver(mHeadsetPlugReceiver,intentFilter);
    }

    /**
     * 耳机连接状态改变后，改变UI
     */
    private void HeadsetConnChangeUI() {
        if (btnStartPause.getVisibility() == View.INVISIBLE){
            //sv if (isWiredOn || isWirelessOn){
                tvEarConnTips.setText("耳机已连接可测试");
                tvEarConnTips.setVisibility(View.GONE);
                btnCanHear.setVisibility(View.VISIBLE);
                btnNoCanHear.setVisibility(View.VISIBLE);
           }
            if(!isWiredOn && !isWirelessOn){{
                tvEarConnTips.setVisibility(View.VISIBLE);
                tvEarConnTips.setText("请配带耳机后继续");
                btnCanHear.setVisibility(View.INVISIBLE);
                btnNoCanHear.setVisibility(View.INVISIBLE);
            }}
    }

    //内放
    public void microSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        context.setVolumeControlStream(0);
        //SV audioManager.setMode(0);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    private void toResult() {    //测试完成进入结果页面
        int[] lDB = new int[7];
        int[] rDB = new int[7];

        for (int i = 0; i < lDB.length; i++) {
            lDB[i] = ArrayUtils.avg(lDBMinVal[i]);  //lDBMinVal 二维数组
            rDB[i] = ArrayUtils.avg(rDBMinVal[i]);  //rDBMinVal 二维数组
            Log.e("linechart", "initLineChartView:leftEarDatas[i] "+lDB[i]);
        }
        /* 送数据到结果页面*/
        Intent intent = new Intent(L_TestPTType4WAV_BAK.this, L5_ResultT4.class);
        Bundle bundle = new Bundle();
        bundle.putIntArray("left", lDB);// 左耳听力数据
        bundle.putIntArray("right", rDB);// 右耳听力数据
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * 如果听不见，计算下次播放的纯音属性
     */
    private void cannotHearFunc() {
        if (dBIndex == dBArr.length-1){     //当前是最大分贝时
            if (isCurrentTestLeft){         //左耳
                lDBMinVal[hzIndex][0] = dBArr[dBIndex];
            }else {                         //右耳
                rDBMinVal[hzIndex][0] = dBArr[dBIndex];
            }
            canHearFunc();
        }else {
            dBIndex = revise(dBIndex + 1, 0, dBArr.length - 1);
            startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);//开始测试，从默认的开始测起，也就时1K赫兹，40分贝开始
        }
    }

    // if 能听见，db stop，hz next, same db value carry forward
    @SuppressLint("SetTextI18n")
    private void canHearFunc() {
        // 如果听见，即可记录听力曲线。
        // 左耳：lDBMinVal 0 当前频率 听阈dB;
        // 右耳：rDBMinVal当前频率下的 听阈值
        if (isCurrentTestLeft){
            lDBMinVal[hzIndex][0] = dBArr[dBIndex];
        } else {
            rDBMinVal[hzIndex][0] = dBArr[dBIndex];
        }

        //  如果还有频段可以被测，则继续下一个频段
        if (hzIndex < hzArr.length -1 ){
            //新频段，小2个db开始
            dBIndex = revise(dBIndex -2, 0, dBArr.length-2);
            hzIndex++;
            startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);

            try {
                if (isCurrentTestLeft){
                    mLeftBreadcrumbsView.nextStep();
                    int jindu =  (hzIndex)*15 + (int) (Math.random() * 10);
                    if (jindu>=100){jindu = 100;}
                    tvFinishNumLeft.setText("左耳进度:"+jindu +"%");
                }else {
                    mRightBreadcrumbsView.nextStep();
                    int jindu =  (hzIndex)*15 + (int) (Math.random() * 10);
                    if (jindu>=100){jindu = 100;}
                    tvFinishNumRight.setText("右耳进度:"+jindu +"%");
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                ToastUtil.showShortToastCenter("点击过快！");
            }
        }else {                         //全部频段都已经测试完毕
            if (isCurrentTestLeft) {    //如果当前正在测试的是左耳
                isLeftFinished = true;  //标记左耳已经完成
                isCurrentTestLeft = false;
                mLeftBreadcrumbsView.nextStep();
                tvFinishNumLeft.setText(R.string.t4_progressing_left100);
                tvCountDown.setText("点击开始，进行右耳测试...");
                ToastUtil.showShortToastCenter("左耳测试结束。右耳测试即将开始...");
                // 初始化
                hzIndex     = 0;
                dBIndex = 0;
                rbRight.setVisibility(View.VISIBLE);
                rbLeft.setVisibility(View.INVISIBLE);
                //tvFinishNumRight.setVisibility(View.INVISIBLE);
                btnCanHear.setVisibility(View.INVISIBLE);
                btnNoCanHear.setVisibility(View.INVISIBLE);
                tvEarConnTips.setVisibility(View.INVISIBLE);
                btnStartPause.setVisibility(View.VISIBLE);
                mRightBreadcrumbsView.setVisibility(View.VISIBLE);
                mLeftBreadcrumbsView.setVisibility(View.INVISIBLE);
            }else{
                isRightFinished = true;//标记左耳已经完成
                mRightBreadcrumbsView.nextStep();
                tvFinishNumRight.setText(R.string.t4_progressing_right100);
                //TODO 查看测试结果
                btnCheckResult.setVisibility(View.VISIBLE);
                btnNoCanHear.setVisibility(View.INVISIBLE);
                btnCanHear.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * @param progress 更新纯音进度条UI
     */
    public void reFreshUi(int progress) {
        setProgressBar(progress);
    }

    /**
     * @param progress 更新纯音进度条
     */
    public void setProgressBar(int progress) {
        if (progress > 30) {
            progress = progress % 30;
        }
        mProgressBar.setProgress(progress);
    }

    /**
     * 开启一个新线程每0.1秒告诉主线程更新下进度条UI，
     */
    private void startThread(){
        isRun = true;
        progress = 0;
        thread = new Thread(() -> {
            while (isRun){
                try {
                    progress++;
                    //reflashUI(progress);//这样更新会出错，不能在子线程更新UI
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void stopThread(){
        if (thread!=null){
            isRun = false;
            try {
                thread.interrupt();
                //主线程休眠0.1秒
                Thread.sleep(100);
                // thread.stop();
                thread=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修正值，防止越界
     * 所以超过最大值，那么赋值最大，索引小于最小值，那么赋值最小
     * @param min, zuixiao
     * @param max, zuida
     */
    private int revise(int num, int min, int max) {
        if (num < min) {
            num = min;
        }
        if (num > max) {
            num = max;
        }
        return num;
    }

    /**
     * 显示听见听不见按钮
     */
    private void showCanHearNoHearBtn() {
        btnCanHear.setVisibility(View.VISIBLE);
        btnNoCanHear.setVisibility(View.VISIBLE);
        btnStartPause.setVisibility(View.INVISIBLE);
    }

    /**
     * 自定义的倒计时计时器
     */
    class MyCount extends CountDownTimer{
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
//sv            tvCountDown.setText("倒计时"+(millisUntilFinished/1000+1)+"秒");
            tvCountDown.setText(getString(R.string.t4_countdown, millisUntilFinished/1000+1));
        }

        @Override
        public void onFinish() {
            progress = 0;//进度条归零
            mProgressBar.setProgress(0);//进度条归零
            isRun = false;//进度条的轮询线程结束轮询

            tvCountDown.setText("播放完毕，听见了么？");
            stopPureTonePlaying();//停止播放纯音
            if (isLeftFinished){
                tvCountDown.setText("继续右耳测试：");
            }
            if (isRightFinished) {
                tvCountDown.setText("测试完成。点击查看报告。");
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
    protected void onDestroy() {
        super.onDestroy();
        stopPureTonePlaying();
        stopThread();
        stopClock();
        if (null != mHeadsetPlugReceiver){
            unregisterReceiver(mHeadsetPlugReceiver);
        }

        if (null != dialog){
            dialog.dismiss();
            dialog = null;
        }
    }

    /** 停止倒计时*/
    private void stopClock() {
        if (null != mClock){
            mClock.cancel();
            mClock = null;
        }
    }

    /** 停止播放纯音*/
    public void stopPureTonePlaying(){
        if (null != tWAVThread){
            tWAVThread.stopp();
            tWAVThread = null;
        }
    }
}
