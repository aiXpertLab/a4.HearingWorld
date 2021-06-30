package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_ResultT4;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.view.OnCountDownListener;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.view.PlayingPanelView;
import seeingvoice.jskj.com.seeingvoice.share.ShareWXQQ;
import seeingvoice.jskj.com.seeingvoice.ui.SelfDialog;
import seeingvoice.jskj.com.seeingvoice.util.ArrayUtils;
import seeingvoice.jskj.com.seeingvoice.util.PopupDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import io.victoralbertos.breadcumbs_view.BreadcrumbsView;


public class HearingThreshold extends MyTopBar implements View.OnClickListener{

    public L_PlayingPureTone mPlaySinSoundThread;
    /** 有关纯音测试的初始声明 索引 start*/
    private static final int defCurDB = 10; //dB数组索引
    private static final int defCurHZ = 0;  //频率数组索引
    private int[] hzArr = new int[]{1000,1500, 2000,3000, 4000,6000, 8000,500,250,1000};
    private int[] dBArr = new int[]{-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50,
            55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};
    private int[][] lDBMinVal = new int[10][2]; //左耳阈值
    private int[][] rDBMinVal = new int[10][2]; //右耳阈值
    int curDBIndex = defCurDB;    /** 当前分贝索引 */
    int curHZIndex = defCurHZ;    /** 当前频率索引 */
    boolean isLeft = true;    /** 是否测试左耳 */
    boolean leftCheckOver = false;    /** 左耳测试完成 */
    boolean rightCheckOver = false;    /** 右耳测试完成 */
    /** 有关纯音测试的初始声明 end*/

    /** XML UI 控件声明*/
    private Button btn_CanHear,btn_NotHear;
    /** 倒计时表盘，播放进度条*/
    private PlayingPanelView mPlayingPanelView;
    private TextView mTv_HZ_DB_value,mTv_L_R_Ear;//播放进度条结束后，也就是纯音播放完成后，出现提示文字；显示当前频率和分贝值
    private BreadcrumbsView mLeftBreadcrumbsView,mRightBreadcrumbsView;    //当前测试的进度
    private TextView mFinishPlayingHintText,mHeadsetConnHintText;    //播放完成显示的文字
    private AlertDialog alert;  //刚开始进入页面时，倒计时提示框
    private int countdownNo = 3;

    private boolean flagNoPlay = false;  //标记不是一只耳测试完毕，另一只马上要开始
    private boolean isTestLeftFirst;   //从上个活动页面得到先测哪只耳朵

    private AudioManager mAudioManager = null;
    private BluetoothAdapter BlueAdapter;
    private int isheadset = 2;    //默认值为2，这样，软件启动时，默认耳机是正常的：

    Timer timer;    //倒计时  Android 计时器
    TimerTask task;
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x0001) {
                if (countdownNo >= 0) {
                    alert.setMessage((countdownNo) + "秒后，您将听到一段纯音，请选择听见或听不见");
                    if (countdownNo == 0) {
                        flagNoPlay = false;  //标记不是一只耳测试完毕，另一只马上要开始
                        alert.dismiss();
                        task.cancel();
                        timer.cancel();
                        playPureToneAtFirst();//第一次播放纯音
                    }
                    countdownNo--;
                }

//                    if (countdownNo < 0){
//                        btn_CanHear.setEnabled(true);
//                        btn_NotHear.setEnabled(true);
//                    }
            }
        }
    };
    private int minValueCanHearNo = 2;   //当分贝索引值是为0的时候，也就是-10分贝的时候
    private IntentFilter intentFilter;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private SelfDialog selfDialog;//是否放弃本次测试，提示框
    private boolean isWiredOn = false,isWirelessOn = false;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_hearing_threshold;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        /** 标题栏设置*/
        setToolbarTitle("纯音听力测试");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.ic_home, null);

//        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
//            @Override
//            public void onMultiClick(MenuItem v) {
//                toNextActivity(null,HearingThreshold.this, PureTestCourse.class);
//            }
//        });

        setToolBarMenuTwo("", R.mipmap.jiaocheng,null);

        /** 绑定UI控件*/
        btn_CanHear = findViewById(R.id.btn_canHear);
        btn_NotHear = findViewById(R.id.btn_notHear);

        /** 监听控件*/
        btn_CanHear.setOnClickListener(this);
        btn_NotHear.setOnClickListener(this);


        /** 监听播放倒计时播放表盘事件*/
        mPlayingPanelView = findViewById(R.id.PlayingPanelView);
        mPlayingPanelView.setCountdownTime(2);        //设置2秒钟倒计时，两秒钟后结束播放纯音
        mPlayingPanelView.setModel(PlayingPanelView.MODEL_PLAY);

        //显示当前纯音的频率和分贝值
        mTv_HZ_DB_value = findViewById(R.id.Tv_HZ_DB_value);

        /** 纯音测试分段进度条*/
        mLeftBreadcrumbsView = findViewById(R.id.left_breadcrumbs);
        mRightBreadcrumbsView = findViewById(R.id.right_breadcrumbs);
        mFinishPlayingHintText = findViewById(R.id.finish_playing_hint_text);
        mHeadsetConnHintText = findViewById(R.id.headset_conn_hint_text);
        mTv_L_R_Ear = findViewById(R.id.tv_L_R_ear);        //默认显示测试的左耳，当测试完成后显示右耳（先测哪只耳，由上一个页面决定）
        DecideWhichEarTest();//是先测左耳还是右耳
        initHeadset();//初始化耳机连接状态判断
        //activity刚启动时，检测一下耳机是否连接
        isHeadsetConn();
        HeadsetConnChangeUI();
    }

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
                microSpeaker(HearingThreshold.this);                // 蓝牙设备已连接，声音内放，从蓝牙设备输出
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_CONNECTED");
                return true;
            } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                Log.e("hhhhhhhh", "isWirelessOn: BluetoothProfile.STATE_DISCONNECTED");
                loudSpeaker(HearingThreshold.this);                // 蓝牙设备未连接，声音外放，
                return false;
            }
        }else {
            mHeadsetConnHintText.setText("该手机不支持蓝牙功能~");
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
                        mHeadsetConnHintText.setText("有线耳机已连接，可以测试啦~");
                    }else {
                        mHeadsetConnHintText.setText("有线耳机已断开，请重新连接~");
                    }
                    isWiredOn = isPlug;
                }else {//无线耳机
                    if (isPlug){
                        mHeadsetConnHintText.setText("无线耳机已连接，可以测试啦~");
                    }else {
                        mHeadsetConnHintText.setText("无线耳机已断开，请重新连接~");
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
        if (isWiredOn || isWirelessOn){
            mHeadsetConnHintText.setText("耳机已连接可测试");
            btn_CanHear.setVisibility(View.VISIBLE);
            btn_NotHear.setVisibility(View.VISIBLE);
        }

        if(!isWiredOn && !isWirelessOn){{
            mHeadsetConnHintText.setText("耳机未连接请连接");
            btn_CanHear.setVisibility(View.INVISIBLE);
            btn_NotHear.setVisibility(View.INVISIBLE);
        }}
    }

//    private void startHeadsetAlert() {
//        Intent intent = new Intent(HearingThreshold.this, noHeadsetAlertActivity.class);
//        startActivityForResult(intent,HEADSET_ALERT_RESULT);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == HEADSET_ALERT_RESULT){
//            registerReceiver(mHeadsetPlugReceiver,intentFilter);
//        }
//    }

    //外放
    public void loudSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        context.setVolumeControlStream(0);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    //内放
    public void microSpeaker(Activity context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        context.setVolumeControlStream(0);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

//    /**
//     * 分享朋友圈操作
//     */
//    private void shareFunction() {
//        //分享
//        ShareDialog shareDialog = new ShareDialog(HearingThreshold.this);
//        shareDialog.show();
//        shareDialog.setOnClickListener(new ShareDialog.OnClickListener() {
//            @Override
//            public void OnClick(View v, int position) {
//                if(position == 0){
//                    QQshareUtil.qqShareFriends(HearingThreshold.this, QQshareUtil.QQ_SHARE_TYPE.Type_QQFriends);
//                }else  if(position == 1){
//                    QQshareUtil.qqShareFriends(HearingThreshold.this, QQshareUtil.QQ_SHARE_TYPE.Type_QQZone);
//                } else  if(position == 2){
//                    WXShareUtil.shareWeb(HearingThreshold.this,WXShareUtil.SHARE_TYPE.Type_WXSceneSession);
//                }else  if(position == 3){
//                    WXShareUtil.shareWeb(HearingThreshold.this, WXShareUtil.SHARE_TYPE.Type_WXSceneTimeline);
//                }
//            }
//        });
//    }

    /**
     * 活动初始化时决定先测左耳还是右耳
     */
    private void DecideWhichEarTest() {
        /**  从上个页面得知，先测左耳还是右耳*/
        isLeft = getIntent().getBooleanExtra("isLeft",true);
        if (isLeft){
            //如果先测左耳，则isLeft赋值为true
            mLeftBreadcrumbsView.setVisibility(View.VISIBLE);//另一只耳的测试进度条可见（默认是不可见的）
            mTv_L_R_Ear.setText("当前测试：左耳（注：如耳机右耳发出声音，请交换耳机左右位置。）");
        }else {
            //如果先测右耳，则为false
            mRightBreadcrumbsView.setVisibility(View.VISIBLE);//另一只耳的测试进度条可见（默认是不可见的）
            mTv_L_R_Ear.setText("当前测试：右耳（注：如耳机左耳发出声音，请交换耳机左右位置。）");
        }
    }


    @Override
    protected void onStart() {//当前activity 又可交互了
        super.onStart();
    }

    @Override
    protected void onResume() {//再次变为可交互后，默认倒计时3秒钟
        super.onResume();
        countdownNo = 3;
        if (isLeft){
            countDown("左耳");
        }else {
            countDown("右耳");
        }
        isHeadsetConn();
    }

    @Override
    protected void onPause() {//暂停时把倒计时初始化为3秒
        super.onPause();
        countdownNo = 3;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * @param s  倒计时方法
     */
    private void countDown(String s) {
        alert = new AlertDialog.Builder(HearingThreshold.this).create();        //倒计时对话框出现，三秒倒计时开始
        alert.setTitle(s+"测试，三秒倒计");
        alert.setMessage("倒计时");
        alert.show();

        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = alert.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // 设置宽度
        p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
        p.gravity = Gravity.CENTER;//设置位置
        //p.alpha = 0.8f;//设置透明度
        dialogWindow.setAttributes(p);

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {                //需要执行的代码
                Message msg = new Message();   //向handler发送msg
                msg.what = 0x0001;
                handler.sendEmptyMessage(msg.what); //发送空消息
            }
        };
        btn_CanHear.setEnabled(false);
        btn_NotHear.setEnabled(false);
        timer.schedule(task, 0, 1000); //延时为0秒的，一秒为周期的定时任务
        countdownNo = 3;//3秒倒计时
    }

    /** 倒计时结束后，首次播放纯音*/
    /** 倒计时结束后进入第一次进入测试页面，默认播放1000HZ 40dB的纯音 */
    /** 纯音播放进度条也开始工作*/
    private void playPureToneAtFirst(){
        mTv_HZ_DB_value.setText("当前频率："+hzArr[curHZIndex]+"Hz"+" 分贝："+dBArr[curDBIndex]+"dB HL");//显示当前纯音的频率和分贝属性值
        StartPlayingProgress(hzArr[curHZIndex],dBArr[curDBIndex],isLeft);
        mPlayingPanelView.setOnCountDownListener(new OnCountDownListener() {//当倒计时表盘到达终点时
            @Override
            public void onCountDown() {
                stopPureTonePlaying();//表盘到达终点时，停止播放纯音
                enableBtnOptions();//开启“听见了”，“听不见”，供用户选择
            }
        });
    }


    /**
     * 显示 “听见了”“听不见”
     */
    private void enableBtnOptions(){
        mFinishPlayingHintText.setText("播放完毕，请选择...");
        mFinishPlayingHintText.setTextColor(Color.RED);
        btn_CanHear.setEnabled(true);
        btn_NotHear.setEnabled(true);
    }
    /**
     * 隐藏“听见了”“听不见”
     */
    private void disenableBtnOptions(){
        mFinishPlayingHintText.setText("正在播放纯音...");
        mFinishPlayingHintText.setTextColor(getResources().getColor(R.color.transparentBlue));
        btn_CanHear.setEnabled(false);
        btn_NotHear.setEnabled(false);
    }


    /**
     * 监听UI控件的点击事件
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_canHear:
                StopPlayingProgress();           //结束当前播放线程和控件，以免造成内存泄漏
                CanHearAction();                 //用户点击了 “听见了”之后后台逻辑，确定好下次要播放的HZ和dB
                if (!flagNoPlay){                //如果当前没有播放纯音，则开始播放
                    StartPlayingProgress(hzArr[curHZIndex],dBArr[curDBIndex],isLeft);
                }
                mTv_HZ_DB_value.setText("当前频率："+hzArr[curHZIndex]+"Hz"+" 分贝："+dBArr[curDBIndex]+"dB HL");                //显示当前播放的HZ和dB
                break;
            case R.id.btn_notHear:
                StopPlayingProgress();         //结束当前播放，线程和控件，以免造成内存泄漏
                NoHearAction();                //用户点击了“听不见”，确定好下次要播放的HZ和dB
                StartPlayingProgress(hzArr[curHZIndex],dBArr[curDBIndex],isLeft);          //播放进度条开始，并且播放正弦波纯音开始
                mTv_HZ_DB_value.setText("当前频率："+hzArr[curHZIndex]+"Hz"+" 分贝："+dBArr[curDBIndex]+"dB HL");      //显示当前播放的HZ和dB
                break;
        }
    }


    private boolean isFirst = true;    /** 是否第一次听到 */
    private boolean inIsFirstFlag = false;//标记isFirst 执行过了
    /**
     * “听不见”之后的处理逻辑
     */
    private void NoHearAction() {
        if (inIsFirstFlag){//听见了 被点击过
            curDBIndex = revise(curDBIndex + 1, 0, dBArr.length - 1);//“听不见”，dBindex 应该上升
            isFirst = false;//
        }else {  //"听见了" 没被点击过
            curDBIndex = revise(curDBIndex + 1, 0, dBArr.length - 1);//分贝升五
            isFirst = true;
        }

        if (curDBIndex == 26){ //当前是最大分贝时
            --minValueCanHearNo;//0分贝的索引减1，默认值时2 （-10，-5，0） 120分贝连续点击两次则认为120分贝也听不到
            if (minValueCanHearNo == 0){
                inIsFirstFlag = false;//始终没有按“听见了”
                calculateThresholdValue();
            }
        }
    }

    /**
     * “听见了”之后的处理逻辑
     */
    private void CanHearAction() {
        if (isFirst){ //“听见了”第一次执行
            inIsFirstFlag = true; //标记“听见了”被点击过
            //记录“听见了”的值
            if (isLeft){// isLeft 是不是左耳标记，得到二维数组的，第一个值记录最小阈值
                lDBMinVal[curHZIndex][0] = dBArr[curDBIndex]; // lDBMinVal 0 当前频率 听阈dB
            }else {//右耳
                rDBMinVal[curHZIndex][0] = dBArr[curDBIndex];
            }
            // 计算下个dB
            curDBIndex = revise(curDBIndex-2,0,dBArr.length-1);    //分贝数组里取值    下降十个分贝的分贝数组索引  curDBIndex-2 分贝数减去10分贝（降十分贝）

            //当分贝是最小值的时候，再点两次“听见了”，最小值及为听阈
            if (curDBIndex == 0){//当-10分贝始终可以听见
                --minValueCanHearNo;//-10分贝时连续点击两次后，确定阈值，第三次点击“听见了”之后进入到isFirst = false 的判断语句处理
                if (minValueCanHearNo == 0){
                    isFirst = false;
                }
            }
        }else {
            calculateThresholdValue();
        }
    }

    /**
     * 记录最近一次“听见了”，和最近一次“听不见”到二维数组中，方便后面计算平均值
     * 对两种临界情况做了判断（1）连续三次点击-10分贝“听见了”，那么-10分贝为阈值；（2）连接三次点击120分贝“听不见”，那么120分贝为阈值
     * inIsFirstFlag  标记：是否点击过“听见了”
     */
    private void calculateThresholdValue() {
        int sub = 0;
        //lDBMinVal[curHZIndex][1] 既"听不见"后，首次“听见了”，或者“听见了”后，首次听不见
        if (isLeft){
            lDBMinVal[curHZIndex][1] = dBArr[curDBIndex];//第二次听见了
            sub = lDBMinVal[curHZIndex][1] -lDBMinVal[curHZIndex][0];//计算两次听见了差值
        }else {
            rDBMinVal[curHZIndex][1] = dBArr[curDBIndex];
            sub = rDBMinVal[curHZIndex][1] -rDBMinVal[curHZIndex][0];
        }
        if (!inIsFirstFlag){        //始终“听不到”，终于“听见了”
            if (isLeft){//左耳
                lDBMinVal[curHZIndex][0] = dBArr[curDBIndex];
                sub = lDBMinVal[curHZIndex][1] -lDBMinVal[curHZIndex][0];//计算两次听见了差值
            }else {//右耳
                rDBMinVal[curHZIndex][0] = dBArr[curDBIndex];
                sub = rDBMinVal[curHZIndex][1] -rDBMinVal[curHZIndex][0];
            }
        }
        if (Math.abs(sub) > 10){//当40分贝听到了，而55分贝听不到，差10分贝，不符合人类
            ToastUtil.showShortToast("两次听见阈值相差太大，重新测当前频率！");
            //两个“听见了”，dB差值太大，重置二维数组
            if (isLeft){
                lDBMinVal[curHZIndex][0] = 0;
                lDBMinVal[curHZIndex][1] = 0;
            }else {
                rDBMinVal[curHZIndex][0] = 0;
                rDBMinVal[curHZIndex][1] = 0;
            }
            curDBIndex = defCurDB; //重置分贝值，重新开始测试
            isFirst = true; //又是第一次测试
            inIsFirstFlag = false; //重置
        }else {//差值小于10做如下判断，也就是找到了阈值后的操作
            minValueCanHearNo = 2;//重置0dB的位置
            if (curHZIndex < hzArr.length-1){//还没测到最后一个频率
                //进度条往下移动
                if (isLeft){
                    mLeftBreadcrumbsView.nextStep();  //进度条向下移动，当前频率的纯音测试结束，开始下一个频率的纯音
                }else {
                    mRightBreadcrumbsView.nextStep();//进度条向下移动，当前频率的纯音测试结束，开始下一个频率的纯音
                }
                curDBIndex = defCurDB;//重置分贝
                isFirst = true;//当前频率测试完毕，进入下一频段，还是第一次
                inIsFirstFlag = false;//重置
                curHZIndex++;//测试下一个频率
            }else {//当所有频率都测完之后（curHZIndex = hzArr.length-1）
                if(isLeft){ //当前测试耳完成-左耳                     //切换左右耳或者直接进入结果页面
                    mLeftBreadcrumbsView.nextStep();
                    leftCheckOver = true;  //标记左耳测试完成，默认值是false
                    flagNoPlay = true;     //标记不要播放纯音
                    if(rightCheckOver){//如果右耳也测试完成，那么给出进入结果页面
                        toResult();//双耳都测试完成，进入结果页面
                    }else {
                        mLeftBreadcrumbsView.setVisibility(View.GONE);//左耳进度条隐藏
                        mRightBreadcrumbsView.setVisibility(View.VISIBLE);//显示右侧进度条
                        checkRight();//如果右耳还没测试，那么接着测试右耳
                    }
                    return;
                }else{//当前测试耳完成-右耳
                    mRightBreadcrumbsView.nextStep();
                    rightCheckOver = true;//标记右耳已经测试完毕
                    flagNoPlay = true;//标记关闭当前纯音播放
                    if (leftCheckOver){
                        toResult();
                    }else {
                        mRightBreadcrumbsView.setVisibility(View.GONE);
                        mLeftBreadcrumbsView.setVisibility(View.VISIBLE);
                        checkLeft();
                    }
                    return;
                }
            }
        }
    }

    //右耳完成测试，现在测试左耳
    private void checkLeft() {
        countDown("右耳完成，现在左耳");
        isLeft = true;
        isFirst = true;
        curDBIndex = defCurDB;
        curHZIndex = defCurHZ;
        mTv_L_R_Ear.setText("当前测试：左耳");
        mTv_HZ_DB_value.setText("当前频率："+hzArr[curHZIndex]+"Hz"+" 分贝："+dBArr[curDBIndex]+"dB");

    }

    //左耳完成测试，现在测试右耳
    private void checkRight() {
        countDown("左耳完成，现在右耳");
        isLeft = false;
        isFirst = true;
        curDBIndex = defCurDB;
        curHZIndex = defCurHZ;
        mTv_L_R_Ear.setText("当前测试：右耳");
        mTv_HZ_DB_value.setText("当前频率："+hzArr[curHZIndex]+"Hz"+" 分贝："+dBArr[curDBIndex]+"dB");

    }

    /**
     * 左耳或右耳测试完成后，跳转到另一只耳
     */
    private void toResult() {    //测试完成进入结果页面
        int[] lDB = new int[9];
        int[] rDB = new int[9];
        for (int i = 0; i < lDB.length; i++) {
            lDB[i] = ArrayUtils.avg(lDBMinVal[i]);//lDBMinVal 二维数组
            rDB[i] = ArrayUtils.avg(rDBMinVal[i]);//rDBMinVal 二维数组
        }
        /* 送数据到结果页面*/
        Intent intent = new Intent(HearingThreshold.this, L5_ResultT4.class);
        Bundle bundle = new Bundle();
        bundle.putIntArray("left", lDB);// 左耳听力数据
        bundle.putIntArray("right", rDB);// 右耳听力数据
        intent.putExtras(bundle);
        startActivity(intent);
        ToastUtil.showShortToastCenter("跳转到结果页面");
        finish();
//        finishedToResult(intent);
    }

    /**
     * 左右耳全部测完后弹出对话框，进入结果页面
     * @param mItent
     */
    private void finishedToResult(final Intent mItent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("测试完成");
        builder.setMessage("查看结果？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(mItent);
                dialog.dismiss();
                StopPlayingProgress();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    /** 自定义的播放进度条开始工作*/
    public void StartPlayingProgress(int Hz,int dB,boolean left){
        startPureTonePlaying(Hz,dB,left);
        mPlayingPanelView.start();
    }

    /** 播放纯音*/
    private void startPureTonePlaying(int hzValue, int dBValue,boolean Left) {
        /**  怀疑实例化线程这块比较浪费时间*/
        if (isWiredOn){
            if (Left){
                Left = false;
            }else {
                Left = true;
            }
            if (isWirelessOn){
                ToastUtil.showLongToastCenter("注意：请选择一种耳机连接！否则会出现未知错误！");
            }
        }

        mPlaySinSoundThread = new L_PlayingPureTone(Left,hzValue,dBValue);
        mPlaySinSoundThread.start();
    }

    /** 自定义的播放进度条停止工作*/
    public void StopPlayingProgress(){
        disenableBtnOptions();                //隐藏 听见 听不见的按钮，防止在播放过程中点击
        stopPureTonePlaying();
        mPlayingPanelView.cancel();             //停止倒计时表盘工作
    }

    /** 停止播放纯音*/
    public void stopPureTonePlaying(){
        if (null != mPlaySinSoundThread){
            mPlaySinSoundThread.stopPlay();
            mPlaySinSoundThread = null;
        }
    }


//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
//                if(intent.hasExtra("state")) {
//                    if(intent.getIntExtra("state", 0) == 0) {
//                        Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
//                    } else if(intent.getIntExtra("state", 0) == 1) {
//                        Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)){
//                if (intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1) == BluetoothHeadset.STATE_DISCONNECTED){
//                    ToastUtil.showLongToast("蓝牙耳机断开，请连接");
//                }
//            }
//
////            MyApplication.getAppInstance().mAudioManager.isWiredHeadsetOn();
//        }
//    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                giveUpSaveResultDialog();
                break;
            case R.id.menu_item_one:
                ShareWXQQ.getInstance().shareFunction(HearingThreshold.this);
                break;
            case R.id.menu_item_two:
                toNextActivity(null,HearingThreshold.this, PureTestCourse.class);
                break;
        }
        return true;//拦截系统处理事件
    }

    @Override
    public void onBackPressed() {
        giveUpSaveResultDialog();
    }

    /**
     * 放弃保存本次测试结果
     */
    private void giveUpSaveResultDialog() {
        selfDialog = new SelfDialog(HearingThreshold.this, R.style.dialog, "放弃后，当前测试结果被丢弃！","确定放弃本次测试？");
        selfDialog.show();
        selfDialog.setYesOnclickListener("放弃", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                finish();
            }
        });

        selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                selfDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPlaySinSoundThread){
            mPlaySinSoundThread.stopPlay();
            mPlaySinSoundThread = null;
        }
        if (null != mHeadsetPlugReceiver){
            unregisterReceiver(mHeadsetPlugReceiver);
        }
        if (null != selfDialog){
            selfDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            showVolumeDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showVolumeDialog() {
        PopupDialog.create(this, "温馨提示", "系统音量已经校验过，您无需再调节！", "确定", null,"取消",null,false,true,true).show();
    }
}
