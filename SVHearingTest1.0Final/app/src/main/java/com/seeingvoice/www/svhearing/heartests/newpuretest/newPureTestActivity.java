package com.seeingvoice.www.svhearing.heartests.newpuretest;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
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
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.AntiShakeUtils;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.puretest.HeadsetPlugReceiver;
import com.seeingvoice.www.svhearing.heartests.puretest.ResultActivity;
import com.seeingvoice.www.svhearing.heartests.puretest.sinwavesound.playingThreadtest;
import com.seeingvoice.www.svhearing.util.ArrayUtils;
import com.seeingvoice.www.svhearing.util.AudioUtil;
import com.seeingvoice.www.svhearing.util.PopupDialog;
import com.seeingvoice.www.svhearing.util.ToastUtil;

public class newPureTestActivity extends TopBarBaseActivity implements View.OnClickListener {

    private boolean isCurrentTestLeft = false;
    private Button btnStartPause,btnCanHear,btnNoCanHear;
    private MyCount mClock = null;
    private TextView tvCountDown,tvHzValue,tvDBValue;
    public playingThreadtest mPlaySinSoundThread = null;
    private int[] hzArr = new int[]{1000,2000,4000,8000,500,250,125,1000};
    private int[] dBArr = new int[]{-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};
    private int defaudBVal = 10, defauHzVal = 0;
    private int hzIndex = defauHzVal,dBIndex = defaudBVal,
            leftTempDBIndex = defaudBVal,leftTempHzIndex = defauHzVal,
            rightTempDBIndex = defaudBVal, rightTempHzIndex = defauHzVal;
    private int[][] lDBMinVal = new int[8][2]; //左耳阈值
    private int[][] rDBMinVal = new int[8][2]; //右耳阈值
    private int curdBindex,curHzindex;
    private ProgressBar mProgressBar;
    private int progress = 0;
    private Thread thread;
    private boolean isRun = false;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    reFreshUi(progress);
                    break;
            }
        }
    };
    private boolean isNoHearFlag = false,
            isCanHearFlag = false,
            isFirst = true,isLeftFinished = false,
            isRightFinished = false;//标记是  听见 听不见 按钮的   isFirst 标记的是  是否  听不见 两边  听见的值 是否都取到了
    private RadioGroup rg;
    private RadioButton rbLeft,rbRight;
    private BreadcrumbsView mLeftBreadcrumbsView,mRightBreadcrumbsView;    //当前测试的进度
    private TextView tvFinishNumLeft,tvFinishNumRight;
    private Button btnCheckResult;
    private int minValueCanHearNo = 3;
    private AudioManager mAudioManager = null;//判断蓝牙耳机，有线耳机是否连接
    private BluetoothAdapter BlueAdapter;
    private boolean isWiredOn = false,isWirelessOn = false;
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private TextView tvEarConnTips;

    private boolean isNotTestRightEar = false,isNotTestLeftEar = false;//标记不测哪只耳
    private PopupDialog dialog;

    @Override
    protected int getConentView() {
        return R.layout.activity_new_pure_test;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
/** 标题栏设置*/
        setTitle("听力测试中");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.ic_home, null);

        setToolBarMenuTwo("", R.drawable.repeat_play_selecter, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                if (btnStartPause.getVisibility() == View.INVISIBLE){
                    if (btnCheckResult.getVisibility() == View.VISIBLE){
                        ToastUtil.showShortToastCenter("本次测试已结束，无法重播，请重新开始！");
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
        rg.check(R.id.rd_right);
        rbLeft.setOnClickListener(this);
        rbRight.setOnClickListener(this);
        tvFinishNumLeft = findViewById(R.id.tv_finish_num_left);
        tvFinishNumRight = findViewById(R.id.tv_finish_num_right);
        btnCheckResult = findViewById(R.id.btn_check_out_pure_result);
        btnCheckResult.setOnClickListener(this);

        /** 纯音测试分段进度条*/
        mLeftBreadcrumbsView = findViewById(R.id.left_breadcrumbs);
        mRightBreadcrumbsView = findViewById(R.id.right_breadcrumbs);

        hzIndex = defauHzVal;
        dBIndex = defaudBVal;
        tvHzValue.setText(hzArr[hzIndex]+"赫兹");
        tvDBValue.setText(dBArr[dBIndex]+"分贝");

        tvEarConnTips = findViewById(R.id.tv_earbud_conn_tips);

        initHeadset();//初始化耳机连接状态判断
        //activity刚启动时，检测一下耳机是否连接
        isHeadsetConn();
        HeadsetConnChangeUI();
        initVolume();
    }

    private void initVolume() {
        int haldMaxVol = AudioUtil.getInstance(newPureTestActivity.this).getMaxMediaVolume()/2;
        AudioUtil.getInstance(newPureTestActivity.this).setMediaVolume(haldMaxVol);
    }


    /** 判断有线耳机，蓝牙耳机是否连接了*/
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
                microSpeaker(newPureTestActivity.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_CONNECTED");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_DISCONNECTED");
                loudSpeaker(newPureTestActivity.this);                // 蓝牙设备未连接，声音外放，
                return false;
            }
        }else {
//            mHeadsetConnHintText.setText("该手机不支持蓝牙功能~");
            return false;
        }
        return false;
    }

    //耳机植入监听
    private void setHeadsetListner() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver(new HeadsetPlugReceiver.HeadsetPlugListener() {
            @Override
            public void onHeadsetPlug(boolean isPlug,boolean flag) {
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
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");//监听手机蓝牙开关状态
        registerReceiver(mHeadsetPlugReceiver,intentFilter);
    }

    /**
     * 耳机连接状态改变后，改变UI
     */
    private void HeadsetConnChangeUI() {
        if (btnStartPause.getVisibility() == View.INVISIBLE){
            if (isWiredOn || isWirelessOn){
                tvEarConnTips.setText("耳机已连接可测试");
                tvEarConnTips.setVisibility(View.GONE);
                btnCanHear.setVisibility(View.VISIBLE);
                btnNoCanHear.setVisibility(View.VISIBLE);
            }

            if(!isWiredOn && !isWirelessOn){{
                tvEarConnTips.setVisibility(View.VISIBLE);
                tvEarConnTips.setText("耳机未连接请连接");
                btnCanHear.setVisibility(View.INVISIBLE);
                btnNoCanHear.setVisibility(View.INVISIBLE);
            }}
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                case R.id.btn_start_pause:
                    showCanHearNoHearBtn();//显示听见听不见按钮
                    btnStartPause.setVisibility(View.INVISIBLE);
                    startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);//开始测试，从默认的开始测起，也就1K赫兹，40分贝开始
                    HeadsetConnChangeUI();
                    mClock.start();
                    break;
                case R.id.btn_canHear:
                    if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                        canHearFunc();
                    }
                    break;
                case R.id.btn_nocanHear:
                    if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                        cannotHearFunc();
                    }
                    break;
                case R.id.rd_left:
                    if (hzIndex > 0) {//测试中
                        if (isCurrentTestLeft) {//当前测试左耳
                            ToastUtil.showShortToastCenter("左耳测试中，无需选择");
                        } else {//当前测试右耳
                            if (isLeftFinished) {//左耳完成测试
                                if (btnCheckResult.getVisibility() == View.VISIBLE) {//最后出测试结果了，不应该再提示 切换了
                                    isCurrentTestLeft = true;
                                    hzIndex = defauHzVal;
                                    dBIndex = defaudBVal;
                                    tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                    tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                } else {
                                    ToastUtil.showShortToastCenter("左耳已完成测试");
                                    rg.check(R.id.rd_right);
                                }
                            } else {//左耳没完成
                                //当查看测试结果按钮出现的时候 不应该出现切换左右耳的 对话框了
                                if (btnCheckResult.getVisibility() == View.VISIBLE) {//最后出测试结果了，不应该再提示 切换了
                                    isCurrentTestLeft = true;
                                    hzIndex = defauHzVal;
                                    dBIndex = defaudBVal;
                                    tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                    tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                } else {//没出结果 弹出提示框
                                    AlertDialog.Builder builder = new AlertDialog.Builder(newPureTestActivity.this);
                                    builder.setTitle("右耳测试中");
                                    builder.setMessage("切换到左耳？右耳数据将丢弃");
                                    builder.setPositiveButton("切换", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            isFirst = true;
                                            rightTempHzIndex = hzIndex;
                                            rightTempDBIndex = dBIndex; //切换后记录下，左耳 断点位置
                                            Log.e("断点频率值", "onClick: 右换左，rightTampHzIndex" + hzIndex + "--leftTampHzIndex" + leftTempHzIndex);
                                            hzIndex = leftTempHzIndex;//因为右耳没有测试完 所以给一个默认值
                                            dBIndex = leftTempDBIndex;

                                            tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                            tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                            startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);//开始播放右耳的初始纯音
                                            isCurrentTestLeft = true;//测试左耳
                                        }
                                    });
                                    builder.setNegativeButton("不切换", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            rg.check(R.id.rd_right);
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        }
                    } else {
                        isCurrentTestLeft = true;
                        hzIndex = leftTempHzIndex;
                        dBIndex = leftTempDBIndex;
                        tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                        tvDBValue.setText(dBArr[dBIndex] + "分贝");
                    }

                    if (btnStartPause.getVisibility() == View.VISIBLE) {
                        isCurrentTestLeft = true;
                        hzIndex = defauHzVal;
                        dBIndex = defaudBVal;
                        tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                        tvDBValue.setText(dBArr[dBIndex] + "分贝");
                    }
                    break;
                case R.id.rd_right:
                    if (hzIndex > 0) {//测试中
                        if (isCurrentTestLeft) {//当前测试左耳
                            if (isRightFinished) {//当右耳测试完成，切换右耳将出现测试结果提示框
                                if (btnCheckResult.getVisibility() == View.VISIBLE) {//最后出测试结果了，不应该再提示 切换了
                                    isCurrentTestLeft = false;
                                    hzIndex = defauHzVal;
                                    dBIndex = defaudBVal;
                                    tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                    tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                } else {
                                    ToastUtil.showShortToastCenter("右耳已完成测试");
                                    rg.check(R.id.rd_left);
                                }
                            } else {//右耳还没有完成
                                //当查看测试结果按钮出现的时候 不应该出现切换左右耳的 对话框了
                                if (btnCheckResult.getVisibility() == View.VISIBLE) {//最后出测试结果了，不应该再提示 切换了
                                    isCurrentTestLeft = false;
                                    hzIndex = defauHzVal;
                                    dBIndex = defaudBVal;
                                    tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                    tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(newPureTestActivity.this);
                                    builder.setTitle("左耳测试中");
                                    builder.setMessage("切换到右耳？左耳数据将丢弃");
                                    builder.setPositiveButton("切换", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            isFirst = true;
                                            isCurrentTestLeft = false;
                                            rg.check(R.id.rd_right);
                                            leftTempHzIndex = hzIndex;
                                            leftTempDBIndex = dBIndex; //切换后记录下，左耳 断点位置
                                            Log.e("断点频率值", "onClick: 左换右，leftTampHzIndex" + hzIndex + "--rightTampHzIndex" + rightTempHzIndex);
                                            hzIndex = rightTempHzIndex;//因为右耳没有测试完 所以给一个上次记录的值
                                            dBIndex = rightTempDBIndex;
                                            tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                                            tvDBValue.setText(dBArr[dBIndex] + "分贝");
                                            startPureTonePlaying(hzArr[hzIndex], dBArr[dBIndex], isCurrentTestLeft);//开始播放左耳的初始纯音
                                        }
                                    });
                                    builder.setNegativeButton("不切换", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            rg.check(R.id.rd_left);
                                        }
                                    });
                                    builder.setCancelable(false);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        } else {//当前测试右耳
                            ToastUtil.showLongToastCenter("右耳测试中，无需选择");
                        }
                    } else {//如果当前测试右耳
                        isCurrentTestLeft = true;
                        hzIndex = rightTempHzIndex;
                        dBIndex = rightTempDBIndex;
                        tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                        tvDBValue.setText(dBArr[dBIndex] + "分贝");
                    }

                    if (btnStartPause.getVisibility() == View.VISIBLE) {
                        isCurrentTestLeft = false;
                        hzIndex = defauHzVal;
                        dBIndex = defaudBVal;
                        tvHzValue.setText(hzArr[hzIndex] + "赫兹");
                        tvDBValue.setText(dBArr[dBIndex] + "分贝");
                    }
                    break;

                case R.id.btn_check_out_pure_result:
                    if (!AntiShakeUtils.isInvalidClick(v,800)) {
                        toResult();
                    }
                    break;
            }
    }

    private void toResult() {    //测试完成进入结果页面
        int[] lDB = new int[7];
        int[] rDB = new int[7];

        for (int i = 0; i < lDB.length; i++) {
            lDB[i] = ArrayUtils.avg(lDBMinVal[i]);//lDBMinVal 二维数组
            rDB[i] = ArrayUtils.avg(rDBMinVal[i]);//rDBMinVal 二维数组
            Log.e("linechart", "initLineChartView:leftEarDatas[i] "+lDB[i]);
        }
        //只有三种情况
        //右耳测试完成，不测右耳
        if (isNotTestLeftEar){
            lDB[0] = 121;
        }
        //左耳测试完成，不测右耳
        if (isNotTestRightEar){
            rDB[0] = 121;
        }
        /* 送数据到结果页面*/
        Intent intent = new Intent(newPureTestActivity.this, ResultActivity.class);
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
//        isNoHearFlag = true;//标记点击了  听不见 按钮
        if (isCanHearFlag){//点 听不见之前   点了  听见了
            isFirst = false;//再点 听见了   就是取第二个 听见 的值了
        }else { //点  听不见 之前 没有点  听见了
            isFirst = true;
        }
        dBIndex = revise(dBIndex+1,0,dBArr.length-1);
        startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);//开始测试，从默认的开始测起，也就时1K赫兹，40分贝开始

        if (dBIndex == dBArr.length-1){ //当前是最大分贝时
            --minValueCanHearNo;//0分贝的索引减1，默认值是2 （-10，-5，0） 120分贝连续点击两次则认为120分贝也听不到
            if (minValueCanHearNo == 0){
                isFirst = false;//始终没有按“听见了”
                if (isCurrentTestLeft){//左耳
                    lDBMinVal[hzIndex][0] = dBArr[dBIndex];
                }else {//右耳
                    rDBMinVal[hzIndex][0] = dBArr[dBIndex];
                }
                canHearFunc();
            }
        }
    }

    /**
     * 如果能听见，计算下次播放的纯音
    */
    private void canHearFunc() {
        isCanHearFlag = true;// 听见了 按钮被点击
        int sub;
        if (isFirst){//只拿到第一个  听见了 的值
            if (isCurrentTestLeft){//当前测试左耳
                lDBMinVal[hzIndex][0] = dBArr[dBIndex]; // lDBMinVal 0 当前频率 听阈dB
            }else {//当前测试右耳
                rDBMinVal[hzIndex][0] = dBArr[dBIndex]; // rDBMinVal当前频率下的 听阈值
            }
            dBIndex = revise(dBIndex-2,0,dBArr.length-1);

            //当分贝是最小值的时候，再点两次“听见了”，最小值及为听阈
            if (dBIndex == 0){//当-10分贝始终可以听见
                --minValueCanHearNo;//-10分贝时连续点击两次后，确定阈值，第三次点击“听见了”之后进入到isFirst = false 的判断语句处理
                if (minValueCanHearNo == 0){
                    isFirst = false;
                }
            }
        }else { //第二个 听见了 的值 取到了
            if (isCurrentTestLeft){//当前测试左耳
                lDBMinVal[hzIndex][1] = dBArr[dBIndex]; // lDBMinVal 0 当前频率 听阈dB
                sub = lDBMinVal[hzIndex][1] - lDBMinVal[hzIndex][0];
            }else {//当前测试右耳
                rDBMinVal[hzIndex][1] = dBArr[dBIndex]; // rDBMinVal当前频率下的 听阈值
                sub = rDBMinVal[hzIndex][1] - rDBMinVal[hzIndex][0];
            }

            if (Math.abs(sub) > 10){ //如果两次结果(去绝对值) 相差10分贝，说明不准，归位重新开始测
//                ToastUtil.showShortToastCenter("两次结果相差太大，归位重新测试！");
                dBIndex = defaudBVal;
                //两个“听见了”，dB差值太大，重置二维数组
                if (isCurrentTestLeft){
                    lDBMinVal[hzIndex][0] = 0;
                    lDBMinVal[hzIndex][1] = 0;
                }else {
                    rDBMinVal[hzIndex][0] = 0;
                    rDBMinVal[hzIndex][1] = 0;
                }
                isCanHearFlag = false;//差值太大  归位 从头开始测试
//                isNoHearFlag = false;//差值太大  归位 从头开始测试
                isFirst = true;
            }else {//当差值在范围内
                minValueCanHearNo = 3;
                if (hzIndex < hzArr.length -1 ){//还有频段可以被测
                    if (hzIndex == 4){//当频率0.5K  使用40分贝作为默认的 音量
                        dBIndex = defaudBVal;
                    }
                    dBIndex = revise(dBIndex-2,0,dBArr.length-1);
                    try {
                        if (isCurrentTestLeft){
                            mLeftBreadcrumbsView.nextStep();
                            tvFinishNumLeft.setText("左耳完成:"+(hzIndex+1)+"/8"+",");
                        }else {
                            mRightBreadcrumbsView.nextStep();
                            tvFinishNumRight.setText("右耳完成:"+(hzIndex+1)+"/8");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        ToastUtil.showShortToastCenter("点击过快！");
                    }
                    isFirst = true;//下个频段重复上个频段的操作
                    hzIndex++;
                }else {//全部频段都已经测试完毕
                    if (isCurrentTestLeft){//如果当前正在测试的是左耳
                        isLeftFinished = true;//标记左耳已经完成
                        mLeftBreadcrumbsView.nextStep();
                        tvFinishNumLeft.setText("左耳完成:"+(hzIndex+1)+"/8");
//                        mLeftBreadcrumbsView.setCurrentStep(hzIndex+1);
                        if (isRightFinished){//如果右耳完成 则查看结果
                            //TODO 查看测试结果
                            btnCheckResult.setVisibility(View.VISIBLE);
                            btnNoCanHear.setVisibility(View.INVISIBLE);
                            btnCanHear.setVisibility(View.INVISIBLE);
                        }else {//去测试右耳
                            isFirst = true;
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("左耳测试完成");
                            builder.setMessage("左耳已经测试完成，开始右耳测试！");
                            builder.setPositiveButton("测试右耳", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    isCurrentTestLeft = false;
                                    rg.check(R.id.rd_right);
                                    hzIndex = rightTempHzIndex;
                                    dBIndex = rightTempDBIndex;
                                    tvHzValue.setText(hzArr[hzIndex]+"赫兹");
                                    tvDBValue.setText(dBArr[dBIndex]+"分贝");
                                    startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);
                                }
                            });
                            builder.setNegativeButton("不测了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isCurrentTestLeft = true;
                                    isNotTestRightEar = true;
                                    rg.check(R.id.rd_left);
                                    //TODO 查看测试结果
                                    btnCheckResult.setVisibility(View.VISIBLE);
                                    btnNoCanHear.setVisibility(View.INVISIBLE);
                                    btnCanHear.setVisibility(View.INVISIBLE);
                                }
                            });
                            builder.setCancelable(false);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }else {//如果当前正在测试的是右耳
                        isRightFinished = true;//标记右耳已经完成
                        mRightBreadcrumbsView.nextStep();
                        tvFinishNumRight.setText("右耳完成:"+(hzIndex+1)+"/8");
//                        mRightBreadcrumbsView.setCurrentStep(hzIndex+1);
                        if (isLeftFinished){//如果左耳完成 则查看结果
                            //TODO 查看测试结果
                            btnCheckResult.setVisibility(View.VISIBLE);
                            btnNoCanHear.setVisibility(View.INVISIBLE);
                            btnCanHear.setVisibility(View.INVISIBLE);
                        }else {//去测试左耳
                            isFirst = true;
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("右耳测试完成");
                            builder.setMessage("右耳已经测试完成，开始左耳测试！");
                            builder.setPositiveButton("测试左耳", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    isCurrentTestLeft = true;
                                    rg.check(R.id.rd_left);
                                    hzIndex = leftTempHzIndex;
                                    dBIndex = leftTempDBIndex;
                                    tvHzValue.setText(hzArr[hzIndex]+"赫兹");
                                    tvDBValue.setText(dBArr[dBIndex]+"分贝");
                                    startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);
                                }
                            });
                            builder.setNegativeButton("不测了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isCurrentTestLeft = true;
                                    isNotTestLeftEar = true;
                                    rg.check(R.id.rd_right);
                                    //TODO 查看测试结果
                                    btnCheckResult.setVisibility(View.VISIBLE);
                                    btnNoCanHear.setVisibility(View.INVISIBLE);
                                    btnCanHear.setVisibility(View.INVISIBLE);
                                }
                            });
                            builder.setCancelable(false);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            }
        }
        //dBIndex = revise(dBIndex-2,0,dBArr.length);
        //如能听见，降10分贝
        //播放下一个纯音
        startPureTonePlaying(hzArr[hzIndex],dBArr[dBIndex],isCurrentTestLeft);//开始测试，从默认的开始测起，也就时1K赫兹，40分贝开始
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
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                thread.stop();
                thread=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修正值，防止越界
     * 所以超过最大值，那么赋值最大，索引小于最小值，那么赋值最小
     * @param min
     * @param max
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
    private void startPureTonePlaying(int hzValue, int dBValue,boolean Left) {
        Log.e("当前分贝索引和值", "canHearFunc: [dBIndex]"+"["+dBIndex+"]--"+"dBArr[dBIndex]="+dBArr[dBIndex]);
        stopThread();//播放新纯音时应该把上次的进度条结束
        stopPureTonePlaying();//播放新纯音先结束上次纯音
        mClock.start();//倒计时开始
        /**  怀疑实例化线程这块比较浪费时间*/
//        if (isWiredOn){
//            if (Left){
//                Left = false;
//            }else {
//                Left = true;
//            }
//            if (isWirelessOn){
//                ToastUtil.showLongToastCenter("注意：请选择一种耳机连接！否则会出现未知错误！");
//            }
//        }
        tvHzValue.setText(hzValue+"赫兹");//显示当前纯音频率
        tvDBValue.setText(dBValue+"分贝");//显示当前纯音音量

        if (isFirst){//重新测试当前频率  或  开始下一频率的测试
            if (dBArr[dBIndex] >=65){//如果 担心切到下一个频段 后用户会感觉不适  所以还是调到40分贝没那么吓人
                dBValue = 10;//40dB
            }
        }

        mPlaySinSoundThread = new playingThreadtest(Left,hzValue,dBValue);//传入当前播放纯音的 频率 音量 左右声道属性
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
            tvCountDown.setText("倒计时"+(millisUntilFinished/1000+1)+"秒");
        }

        @Override
        public void onFinish() {
            progress = 0;//进度条归零
            mProgressBar.setProgress(0);//进度条归零
            isRun = false;//进度条的轮询线程结束轮询
            tvCountDown.setText("播放完毕！");
            stopPureTonePlaying();//停止播放纯音
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
