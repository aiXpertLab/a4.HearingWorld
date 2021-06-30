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
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_ResultT4;
import seeingvoice.jskj.com.seeingvoice.util.ArrayUtils;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L_AudioManager;
import seeingvoice.jskj.com.seeingvoice.util.PopupDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
/**
 * Date:2021/1/26
 * auther:LeoReny@hypech.com
 */

public class L_TestPTType4 extends MyTopBar implements View.OnClickListener {

    private static final String TAG = "L_TestPTType4: ";

    private boolean isCurrentTestLeft = true;
    private Button btnStartPause,btnCanHear,btnNoCanHear;
    private MyCount mClock = null;
    private TextView tvCountDown,tvHzValue,tvDBValue;
    public L_PlayingPureTone mPlaySinSoundThread = null;
    //sv private int[] hzArr = new int[]{250,500,1000,2000,3000,4000,6000};   //4型，筛查、监测类， 70db
    //sv private int[] hzArr = new int[]{250,500,1000,2000,3000,4000,6000,8000};   //3型，基本诊断，70-100db
    //sv private int[] hzArr = new int[]{125,250,500,1000,1500,2000,3000,4000,6000,8000};   //3型，基本诊断，70-100db
    //sv private final int[] dBArr = new int[]{-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};

    private final int[] hzArr = getMax();
    private static int[] getMax(){
        int q1 = (int) (Math.random() * 10);
        int[] hzArr1;
        switch (q1){
            case 0: hzArr1 = new int[]{250,1500,3000,4000,6000};                break;
            case 1: hzArr1 = new int[]{250, 500,2000,4000,6000};                break;
            case 2: hzArr1 = new int[]{250,1000,1500,3000,6000};                break;
            case 3: hzArr1 = new int[]{125,500,1500,4000,6000};                break;
            case 4: hzArr1 = new int[]{250,1000,3500,4500,6000};                break;
            case 5: hzArr1 = new int[]{250,1000,2500,6000,8000};                break;
            case 6: hzArr1 = new int[]{250,750, 2000,4000,6000};                break;
            case 7: hzArr1 = new int[]{250,500,1000,3000,6000};                break;
            case 8: hzArr1 = new int[]{250,1500,3000,4000,8000};                break;
            case 9: hzArr1 = new int[]{250,500,3000,4000,6000};                break;
            default:                hzArr1 = new int[]{250,1000,3000,4000,6000};
        }
        return hzArr1;
    }

    private final int[] dBArr = new int[]{10,  20,  38,  59,  75,  90, 100};
    private final int iniDBIndex = 0;
    private final int iniHzIndex = 0;
    private int hzIndex     = iniHzIndex,       dBIndex = iniDBIndex,
            leftTempDBIndex = iniDBIndex,   leftTempHzIndex = iniHzIndex,
            rightTempDBIndex= iniDBIndex,  rightTempHzIndex = iniHzIndex;
    private final int[][] lDBMinVal = new int[8][2]; //左耳阈值
    private final int[][] rDBMinVal = new int[8][2]; //右耳阈值
    private ProgressBar mProgressBar;
    private int progress = 0;
    private Thread thread;
    private boolean isRun = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0) {reFreshUi(progress);}
        }
    };
    private final boolean isNoHearFlag = false;
    private boolean isFirst = true;
    public boolean isLeftFinished  = false;
    private boolean isRightFinished = false;
    private RadioGroup rg;
    private RadioButton rbLeft, rbRight;
    private BreadcrumbsView mLeftBreadcrumbsView;
    private BreadcrumbsView mRightBreadcrumbsView;
    private TextView tvFinishNumLeft,tvFinishNumRight;
    private Button btnCheckResult;
    private AudioManager mAudioManager = null;      //判断蓝牙耳机，有线耳机是否连接
    private BluetoothAdapter BlueAdapter;
    private boolean isWiredOn = false,isWirelessOn = false;
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private TextView tvEarConnTips;

    private boolean isNotTestRightEar = false, isNotTestLeftEar = false;    //标记不测哪只耳
    private PopupDialog dialog;

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

        btnStartPause = findViewById(R.id.btn_start_pause);
        btnStartPause.setOnClickListener(this);
        tvCountDown = findViewById(R.id.tv_count_down);
        mClock = new MyCount(3000,1000);
        btnCanHear = findViewById(R.id.btn_canHear);
        btnNoCanHear = findViewById(R.id.btn_nocanHear);
        mProgressBar = findViewById(R.id.progress_bar_h);
        tvHzValue = findViewById(R.id.tv_tone_value);
        tvDBValue = findViewById(R.id.tv_volume_value);
        btnCanHear.setOnClickListener(this);
        btnNoCanHear.setOnClickListener(this);
        rbLeft = findViewById(R.id.rd_left);
        rbRight = findViewById(R.id.rd_right);
        rg = findViewById(R.id.L_R_choose);
        rg.check(R.id.rd_left);
        //sv rbLeft.setOnClickListener(this);
        //sv rbRight.setOnClickListener(this);
        tvFinishNumLeft = findViewById(R.id.tv_finish_num_left);
        tvFinishNumRight = findViewById(R.id.tv_finish_num_right);
        btnCheckResult = findViewById(R.id.btn_check_out_pure_result);
        btnCheckResult.setOnClickListener(this);
        tvEarConnTips = findViewById(R.id.tv_earbud_conn_tips);

        // 初始化屏幕
        rbRight.setVisibility(View.INVISIBLE);
        //tvFinishNumRight.setVisibility(View.INVISIBLE);
        btnCanHear.setVisibility(View.INVISIBLE);
        btnNoCanHear.setVisibility(View.INVISIBLE);
        tvEarConnTips.setVisibility(View.INVISIBLE);
        btnCheckResult.setVisibility(View.INVISIBLE);

        // 纯音测试分段进度条
        mLeftBreadcrumbsView = findViewById(R.id.left_breadcrumbs);
        mRightBreadcrumbsView = findViewById(R.id.right_breadcrumbs);
        Log.e("hhhhhhhh2", String.valueOf(iniDBIndex));
        Log.e("hhhhhh1hh", String.valueOf(iniHzIndex));

        hzIndex = iniHzIndex;
        dBIndex = iniDBIndex;
        // tvHzValue.setText(hzArr[hzIndex]+"赫兹");
        tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
        // tvDBValue.setText(dBArr[dBIndex]+"分贝");
        tvDBValue.setText(getString(R.string.t4_db, dBArr[dBIndex]));

        initHeadset();//初始化耳机连接状态判断
        //activity刚启动时，检测一下耳机是否连接
        isHeadsetConn();
        HeadsetConnChangeUI();
        initVolume();
    }

    private void initVolume() {
        int halfMaxVol = L_AudioManager.getInstance(L_TestPTType4.this).getMaxMediaVolume()/2;
        L_AudioManager.getInstance(L_TestPTType4.this).setMediaVolume(halfMaxVol);
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
//        if(mAudioManager.isWiredHeadsetOn()){   //ok to check on
  //          return true;
    //    }else{
      //      return false;
        //}
        return (mAudioManager.isWiredHeadsetOn());
    }
    private boolean isWirelessOn() {
        if (null != BlueAdapter){
            int state = BlueAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            if (BluetoothProfile.STATE_CONNECTED == state) {
                microSpeaker(L_TestPTType4.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
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
    @SuppressLint("WrongConstant")
    public void microSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        context.setVolumeControlStream(0);
        audioManager.setMode(0);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.btn_start_pause) {
            showCanHearNoHearBtn();         //显示听见听不见按钮
            int i3 = v.getId();
            int i4 = R.id.btn_start_pause;
            btnStartPause.setVisibility(View.INVISIBLE);
            startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);    //开始测试，从默认的开始测起，也就1K赫兹，40分贝开始
            HeadsetConnChangeUI();
            mClock.start();
        }else if (vid == R.id.btn_canHear){
                if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                    canHearFunc();
                }
        }else if (vid == R.id.btn_nocanHear){
                if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                    cannotHearFunc();
                }
        }else if (vid == R.id.btn_check_out_pure_result){
                if (!AntiShakeUtils.isInvalidClick(v,800)) {
                    toResult();
                }
        }
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
        Intent intent = new Intent(L_TestPTType4.this, L5_ResultT4.class);
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

    /**
     * 如果能听见，db结束，频段next
     * 如果能听见，同样的db直接带入下一个频段
    */
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
                tvFinishNumLeft.setText("左耳进度：100%");
                tvCountDown.setText("点击开始，进行右耳测试...");
                ToastUtil.showShortToastCenter("左耳测试结束。右耳测试即将开始...");
                // 初始化
                hzIndex     = iniHzIndex;
                dBIndex = iniDBIndex;
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
                tvFinishNumRight.setText("右耳进度:100%");
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
    }

    /** 播放纯音*/
    private void startPureTonePlaying(int hzValue, int dBValue,  boolean Left) {
        Log.e("当前分贝索引和值", "canHearFunc: [dBIndex]"+"["+dBIndex+"]--"+"dBArr[dBIndex]="+dBArr[dBIndex]);
        stopThread();           //播放新纯音时应该把上次的进度条结束
        stopPureTonePlaying();  //播放新纯音先结束上次纯音
        mClock.start();         //倒计时开始

//        tvHzValue.setText(hzValue+"赫兹");    //显示当前纯音频率
//        tvDBValue.setText(dBValue+"分贝");    //显示当前纯音音量
        tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
        tvDBValue.setText(getString(R.string.t4_db, dBArr[dBIndex]));

        if (isFirst){                        //重新测试当前频率  或  开始下一频率的测试
            if (dBArr[dBIndex] >=65){       //如果 担心切到下一个频段 后用户会感觉不适  所以还是调到40分贝没那么吓人
                dBValue = dBArr[dBIndex] - 20;           //sv
            }
        }

        mPlaySinSoundThread = new L_PlayingPureTone(Left,hzValue,dBValue);//传入当前播放纯音的 频率 音量 左右声道属性
        mPlaySinSoundThread.start();//开始播放新纯音
        startThread();//新进度条从零开始运行
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
        if (null != mPlaySinSoundThread){
            mPlaySinSoundThread.stopPlay();
            mPlaySinSoundThread = null;
        }
    }
}
