package com.seeingvoice.www.svhearing.heartests.verbaltests.numKeyboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.seeingvoice.www.svhearing.AboutUsActivity;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.verbaltests.Interface.OnPlayWhiteNoise;
import com.seeingvoice.www.svhearing.heartests.verbaltests.VerbalTestResultActivity;
import com.seeingvoice.www.svhearing.heartests.verbaltests.services.WhiteNoiseService;
import com.seeingvoice.www.svhearing.heartests.verbaltests.viewpager.VP_FG_Activity;

import java.util.Locale;

/**
 * Date:2019/2/13
 * Time:9:47
 * auther:zyy
 */
public class NumKeyboardActivity extends TopBarBaseActivity {

    private String[] numbers;
    private TextToSpeech mTextToSpeech;
    private int testNo = 1;
    private int testTotalNo,isPractice;
    private int testCorrectNo = 0;
    private Handler mHandler;
    private OnPlayWhiteNoise playWhiteNoise;
    private WhiteNoiseService whiteNoiseService;
    private float volumn = 0.5f;
    Bundle params;
    //service connection
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (playWhiteNoise == null){
                playWhiteNoise = (OnPlayWhiteNoise) service;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected int getConentView() {
        return R.layout.activity_numkey;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("言语测试");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null,NumKeyboardActivity.this, AboutUsActivity.class);
            }
        });
        /** 从数据持久化中得到  设置好的音量值*/
        SharedPreferences volumePref = getSharedPreferences("volumePrefName",MODE_PRIVATE);
        volumn = volumePref.getInt("volume",7)/15.0f;

        final NumInputView numInputView = findViewById(R.id.nums_view);

        /** 判断是练习还是正式的言语测试*/
        isPractice();
        numbers = new String[3];
        numbers = createRadomNumber();
        //朗读mNums三个随机数
        initTTS();
        /** 绑定白噪音服务*/
        bingWhiteNoiseService();
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    /** 输入完成后的操作*/
                    case 0x0001:
                        SystemClock.sleep(1000);
                        numInputView.ThreeTextViewEmpty();
                        if (testNo < testTotalNo) {//测试正在进行中
                            numInputView.setTvTestProgress(++testNo);
                            numbers = createRadomNumber();
                            //初始化TTS三个随机数
                            initTTS();
                        }else if (testNo == testTotalNo){
                            if (isPractice == 1){//言语测试练习
                                playWhiteNoise.stop();
                                finish();
                                Intent mIntent = new Intent(NumKeyboardActivity.this,VP_FG_Activity.class);
                                mIntent.putExtra("restart","0x0009");
                                startActivity(mIntent);
                            }else if (isPractice == 0){//正式言语测试
                                Intent intent = new Intent(NumKeyboardActivity.this, VerbalTestResultActivity.class);
                                intent.putExtra("result",testCorrectNo);
                                startActivity(intent);
                                finish();
                            }else {
                                displayToast("发生未知错误！请重启APP");
                            }
                        }else {
                            return;
                        }
                        break;
                    /** 调用随机生成三个数字initNums(numbers)后调用handler去读*/
                    case 0x0002:
                        playWhiteNoise.play(NumKeyboardActivity.this);
                        setWhiteNoiseVolumn();
                        params = new Bundle();
                        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,volumn);
                        params.putFloat(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                        for (int i = 0; i < numbers.length; i++) {
                            mTextToSpeech.speak(numbers[i],TextToSpeech.QUEUE_ADD,params,"朗读输入框中的内容");
                        }
                        break;
                }
            }
        };


        /** 添加密码输入完成的响应*/
        numInputView.setOnFinishInput(new OnNumInputFinish() {
            @Override
            public void inputFinish() {

                playWhiteNoise.stop();
                //输入完成后我们简单显示一下输入的密码
                //也就是说——>实现你的交易逻辑什么的在这里写
                StringBuffer sb = new StringBuffer();
                String s = "";
                for (int i = 0; i < numbers.length; i++) {
                    s += numbers[i].trim();
                }
                String nums = numInputView.getStrNums().trim();

                if (s.trim().equals(nums.trim())){
//                    Toast.makeText(NumKeyboardActivity.this, nums+"输入正确", Toast.LENGTH_SHORT).show();
                    ++testCorrectNo;
                }else {
//                    Toast.makeText(NumKeyboardActivity.this, nums+"输入不匹配", Toast.LENGTH_SHORT).show();
                }

                Message msg = new Message();
                msg.what = 0x0001;
                mHandler.sendMessage(msg);

            }
        });

        numInputView.getTvRePlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < numbers.length; i++) {
                    params = new Bundle();
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,volumn);
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                    mTextToSpeech.speak(numbers[i],TextToSpeech.QUEUE_ADD,null,"朗读输入框中的内容");
//                Toast.makeText(NumKeyboardActivity.this,"没听清，再播放一次！", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (isPractice == 1){
            numInputView.gettvNumKeyTitle().setText("言语测试练习");
            numInputView.gettvPraciceOrOfficialFinish().setText("练习完成:");
            numInputView.gettvTotalNo().setText("/4");
        }
        //第一种方式结束
    }

    /** 判断是练习还是正式的言语测试*/
    private void isPractice() {
        Intent intent = getIntent();
        isPractice = intent.getIntExtra("practiceorofficialtest",0);
        if (isPractice == 1){
            //练习
            testTotalNo = 4;

        }else if (isPractice == 0){
            //正式测试
            testTotalNo = 12;
        }
    }

    /** 控制背景噪音的音量*/
    private void setWhiteNoiseVolumn() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        int n = testNo%4;  //把12组测试分为四组
        if (n !=0) {
            playWhiteNoise.adjustVolumn(volumn * n / 4);
        }else if (n == 0){
            playWhiteNoise.adjustVolumn(volumn);
        }
    }

    /** 绑定白噪音服务*/
    private void bingWhiteNoiseService() {
        Intent intent = new Intent();
        intent.setClass(NumKeyboardActivity.this,WhiteNoiseService.class);
        /** 绑定白噪音服务*/
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }


    /** 朗读随机生成的三个整数*/
    private void initTTS() {
        mTextToSpeech = new TextToSpeech(NumKeyboardActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    //设置朗读语言
                    int supported = mTextToSpeech.setLanguage(Locale.CHINA);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)){
                        displayToast("不支持当前语言");
                    }else {
                        Message msg = new Message();
                        msg.what = 0x0002;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
    }

    /** 随机生成三个0到9之间的整数并保存再数组中*/
    private String[] createRadomNumber() {
        String[] mNumStr = new String[3];
        //将任意数赋值给数组
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mNumStr.length; i++) {
            mNumStr[i] = String.valueOf((int) (Math.random()*10)).trim();
        }

        String s = sb.toString();
        return mNumStr;
    }


    /** 显示土司提示*/
    private void displayToast(String s) {
        Toast.makeText(NumKeyboardActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        if (playWhiteNoise != null){
            playWhiteNoise.stop();
        }
        if (null != conn)
        unbindService(conn);
    }
}
