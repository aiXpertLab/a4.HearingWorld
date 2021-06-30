package seeingvoice.jskj.com.seeingvoice.l_audiometry.hearing_aid;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_AboutUs;
import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.history.PureTestHistoryListL;
import seeingvoice.jskj.com.seeingvoice.util.SharedPreferencesHelper;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static seeingvoice.jskj.com.seeingvoice.MyData.REQUEST_AUTO_SETTING;

public class HearingAidL extends MyTopBar implements View.OnClickListener{
    public Button btnRecord,btnStop;
    public SeekBar mSkb_L_Volume,mSkb_R_Volume;//调节音量

    public boolean isRecording = false;    //标记是否在录放
    public static final int frequency = 48000;
    public static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    public int recBufSize,playBufSize;
    public AudioRecord audioRecord;
    public AudioTrack audioTrack1,audioTrack2;
    public RecordPlayTwoChalThread mRecordPlayTwoChalThread;
    private VisualizerView mvisualizerView1,mvisualizerView2;
    private Visualizer mVisualizer1,mVisualizer2;
    private Equalizer mLeftEqualizer,mRightEqualizer,mLAutoEqualizer,mRAutoEqualizer;
    private LinearLayout mLeftEQLayout,mRightEQLayout;
    private LinearLayout mTv_Suggest;

    //KEY值是SP存储的键值对的KEY，下面保存了一个boolean值在里面
    private String KEY="EQAUTOSET",RESULTKEY="ResultFromHearTest",SPKEY="AutoSetSP";
    //手动设置左右耳的EQ,boolean类型的键值
    //系统自带的Switch控件
    private Switch mSwitch;

    //设置左右耳五段均衡的五个参数
    private Float[] LBand = new Float[5];
    private Float[] RBand = new Float[5];
    private Short bands;
//    //从听力测试页面传来的值是String类型的，要先转化成数组
    private String[] LeftEarStr,RightEarStr;
    public boolean isAutoSettings = false;    //标记是否在录放

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_deaf_aid;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle();
        SharedPreferencesHelper.init(HearingAidL.this);
        initAudioSettings();
        initView();
    }

    private void setTitle() {
        setToolbarTitle("助听器");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon,null);

        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null, HearingAidL.this, L_AboutUs.class);
            }
        });
    }

    protected void initView() {

        /*与布局控件相关*/
        btnRecord = this.findViewById(R.id.btnRecord);
        btnStop = this.findViewById(R.id.btnStop);
        mSkb_L_Volume = this.findViewById(R.id.skb_L_Volume);//控制左耳的音量
        mSkb_R_Volume = this.findViewById(R.id.skb_R_Volume);//控制右耳的音量
        mTv_Suggest = this.findViewById(R.id.tv_suggest);

        /*均衡器的view*/
        mLeftEQLayout = this.findViewById(R.id.equalizer_left_container);
        mRightEQLayout = this.findViewById(R.id.equalizer_right_container);
        mSwitch = this.findViewById(R.id.aid_auto_btn);        //开关按钮

        /**
         * 1.通过getSharedPreferences(String name,int mode)得到SharedPreferences接口。该方法的第一个参数是文件名称，第二个参数是操作模式
           2.调用SharedPreferences.Editor方法对SharedPreferences进行修改
           3.往editor对象塞值并且提交
         * */
//        preferences = getSharedPreferences(SPKEY,MODE_MULTI_PROCESS);
//        editor = preferences.edit();
//        mSwitch.setChecked(preferences.getBoolean(KEY,false));
        initData();
    }

    private void initAudioSettings() {        //录音时最小缓冲区的size

        recBufSize = AudioRecord.getMinBufferSize(frequency,channelConfiguration,audioEncoding);
        playBufSize = AudioTrack.getMinBufferSize(frequency,channelConfiguration,audioEncoding);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER,frequency,channelConfiguration,audioEncoding,recBufSize);//MediaRecorder.AudioSource.CAMCORDER  或者试一试MediaRecorder.AudioSource.VOICE_COMMUNICATION
        audioTrack1 = new AudioTrack(AudioManager.STREAM_MUSIC,frequency,channelConfiguration,audioEncoding,playBufSize,AudioTrack.MODE_STREAM);
        audioTrack2 = new AudioTrack(AudioManager.STREAM_MUSIC,frequency,channelConfiguration,audioEncoding,playBufSize,AudioTrack.MODE_STREAM);
    }

    /**
     * activity一启动就判断Switch是关闭还是开启
     * 分三种情况
     * 1.第一次开程序并且没有进行heartest，而直接进到deafaid
     * 2.第一次开程序先进行了heartest,保存结果并跳转到deafaid
     * 3.第一次开程序进行了heartest，没有跳转到deafaid
     *
     * 第一种和第三种情况，SP中没数据
     * 第二种 heartest传过来数据，再deafaid中处理并且保存再SP中
     *
     * */
    protected void initData() {
        ifAutoSetupSP();
        /* 自动设置助听参数  监听*/
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isRecording){
                        if (isAutoSettings){
                            ToastUtil.showShortToastCenter("请先关闭助听器");
                            mSwitch.setChecked(true);
                        }else {
                            ToastUtil.showShortToastCenter("请先关闭助听器");
                            mSwitch.setChecked(false);
                        }
                    }else {
                        if (isChecked){
                            Intent intent = new Intent(HearingAidL.this, PureTestHistoryListL.class);
                            startActivityForResult(intent,REQUEST_AUTO_SETTING);
                        }else{
                            SharedPreferencesHelper.getInstance().saveData("isAutoSettings",false);
                            isAutoSettings = false;
                        }
                    }
                }
            });
        initVolume();//初始化音量
        setupVisualizerFxAndUi();//设置示波器
        mVisualizer1.setEnabled(true);//左耳的声波示波器
        mVisualizer2.setEnabled(true);//右耳的声波示波器
        setEvent();
    }

    /**
     * 判断助听器是否是自动状态
     */
    private void ifAutoSetupSP() {
        if ((boolean)SharedPreferencesHelper.getInstance().getData("isAutoSettings",false)){
            isAutoSettings = true;
            mSwitch.setChecked(true);
            LeftEarStr = new String[7];
            RightEarStr = new String[7];
            for (int i = 0; i < MyData.HZ_NUM; i++) {
                LeftEarStr[i] = String.valueOf(SharedPreferencesHelper.getInstance().getData("editor_left"+i,""));
                RightEarStr[i] = String.valueOf(SharedPreferencesHelper.getInstance().getData("editor_right"+i,""));
            }
            getLRBands(LeftEarStr,RightEarStr);
        }else {
            mSwitch.setChecked(false);
            SharedPreferencesHelper.getInstance().saveData("isAutoSettings",false);
            isAutoSettings = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_AUTO_SETTING) {
            LeftEarStr = data.getStringArrayExtra("leftear");
            RightEarStr = data.getStringArrayExtra("rightear");
            for (int i = 0; i < LeftEarStr.length; i++) {
                SharedPreferencesHelper.getInstance().saveData("editor_left" + i, LeftEarStr[i]);
                SharedPreferencesHelper.getInstance().saveData("editor_right" + i, RightEarStr[i]);
            }
            SharedPreferencesHelper.getInstance().saveData("isAutoSettings", true);
            mSwitch.setChecked(true);
            isAutoSettings = true;
            getLRBands(LeftEarStr, RightEarStr);
        } else {
            mSwitch.setChecked(false);
            isAutoSettings = false;
        }
    }

    private Set<String> StrArrayToSetStr(String[] staffs){
        Set<String> staffsSet = new HashSet<>(Arrays.asList(staffs));
        return staffsSet;
    }

    private void initVolume() {
        mSkb_L_Volume.setMax(100);//音量调节的极限
        mSkb_L_Volume.setProgress(70);//设置seekbar的位置值
        mSkb_R_Volume.setMax(100);//音量调节的极限
        mSkb_R_Volume.setProgress(70);//设置seekbar的位置值
        audioTrack1.setStereoVolume(0.7f,0.7f);//设置当前音量大小
        audioTrack2.setStereoVolume(0.7f,0.7f);//设置当前音量大小
    }

    /**
     * 得到左右耳五段均衡值，给EQ使用
     * */
    private void getLRBands(String[] StrLeft,String[] StrRight) {
        try {
            LBand[0] = Float.parseFloat(StrLeft[4]);
            LBand[1] = (Float.parseFloat(StrLeft[5])+Float.parseFloat(StrLeft[6]))/2;
            LBand[2] = (Float.parseFloat(StrLeft[0])+Float.parseFloat(StrLeft[1]))/2;
            LBand[3] = Float.parseFloat(StrLeft[2]);
            LBand[4] = Float.parseFloat(StrLeft[3]);

            RBand[0] = Float.parseFloat(StrRight[4]);
            RBand[1] = (Float.parseFloat(StrRight[5])+Float.parseFloat(StrRight[6]))/2;
            RBand[2] = (Float.parseFloat(StrRight[0])+Float.parseFloat(StrRight[1]))/2;
            RBand[3] = Float.parseFloat(StrRight[2]);
            RBand[4] = Float.parseFloat(StrRight[3]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }



    protected void setEvent() {
        btnRecord.setOnClickListener(ClickEvent);
        btnStop.setOnClickListener(ClickEvent);

        //左侧耳机音量
        mSkb_L_Volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float vol = (float) seekBar.getProgress()/(float) seekBar.getMax();
                audioTrack1.setStereoVolume(vol,0);//设置音量
            }
        });
        //右侧耳机音量
        mSkb_R_Volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float vol = (float) seekBar.getProgress()/(float) seekBar.getMax();
                audioTrack2.setStereoVolume(0,vol);//设置音量
            }
        });
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * 点击接口实现类
     */
    private OnMultiClickListener ClickEvent = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            if (isAutoSettings){
                if (v == btnStop && isRecording) {
                    isRecording = false;
                    killEqualizer();//
                }
                if (v.equals(btnRecord) && !isRecording) {
                    AutoSetupEqualize(LBand, RBand);
                    mRecordPlayTwoChalThread = new RecordPlayTwoChalThread(HearingAidL.this);                //开一条线程边录边放
                    mRecordPlayTwoChalThread.setChannel(true, false);
                    mRecordPlayTwoChalThread.start();
                    isRecording = true;
                }
            }else {
                if (v == btnStop && isRecording) {
                    isRecording = false;
                    killEqualizer();//
                }
                if (v.equals(btnRecord) && !isRecording) {
                    SetupEQ();//设置均衡器
                    isRecording = true;
                    mRecordPlayTwoChalThread = new RecordPlayTwoChalThread(HearingAidL.this);                //开一条线程边录边放
                    mRecordPlayTwoChalThread.setChannel(true, false);
                    mRecordPlayTwoChalThread.start();
                }
            }
        }
    };

    //声波示波器
    private void setupVisualizerFxAndUi() {
        mvisualizerView1 = new VisualizerView(this);
        mvisualizerView1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int) (50f * getResources()
                        .getDisplayMetrics().density)));
        mTv_Suggest.addView(mvisualizerView1);

        mVisualizer1 = new Visualizer(audioTrack2.getAudioSessionId());
        mVisualizer1.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer1.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

            @Override
            public void onWaveFormDataCapture(Visualizer visualizer,byte[] waveform, int samplingRate) {
                mvisualizerView1.updateVisualizer(waveform);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                                         int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mvisualizerView2 = new VisualizerView(this);
        mvisualizerView2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int) (30f * getResources()
                        .getDisplayMetrics().density)));
        mTv_Suggest.addView(mvisualizerView2);
        mVisualizer2 = new Visualizer(audioTrack1.getAudioSessionId());
        mVisualizer2.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer2.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

            @Override
            public void onWaveFormDataCapture(Visualizer visualizer,byte[] waveform, int samplingRate) {
                mvisualizerView2.updateVisualizer(waveform);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft,int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    /**
     * 设置均衡器
     * */
    private void SetupEQ(){
        setupLeftEqualizeFxAndUi();
        setupRightEqualizeFxAndUi();
    }

    /**
     * 手动模式下   设置左耳的均衡器
     */
    private void setupLeftEqualizeFxAndUi() {
        mLeftEqualizer = new Equalizer(0, audioTrack1.getAudioSessionId());//audioTrack1 左耳的audioTrack1
        mLeftEqualizer.setEnabled(true);//开启均衡器
        bands = mLeftEqualizer.getNumberOfBands();
        final short minBand = mLeftEqualizer.getBandLevelRange()[0];        //最小EQ带宽
        final short maxBand = mLeftEqualizer.getBandLevelRange()[1];        //最大的均衡器带宽
        TextView eqTextView = new TextView(this);
        eqTextView.setText("智能助听左耳频段调节：");
        eqTextView.setTextColor(Color.WHITE);
        mLeftEQLayout.addView(eqTextView);

        for (short i = 0; i < bands; i++) {
            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            String s = (mLeftEqualizer.getCenterFreq(i) / 1000)+"HZ";
            freqTextView.setText(s);
            mLeftEQLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minBand / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxBand / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar seekbar = new SeekBar(this);
            seekbar.setLayoutParams(layoutParams);
            seekbar.setMax(maxBand - minBand);
            seekbar.setProgress(mLeftEqualizer.getBandLevel(i));

            final short bandlevel = i;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                    mLeftEqualizer.setBandLevel(bandlevel,(short) (progress + minBand));
                    System.out.println("test...."+progress+minBand+maxBand);
                }
            });
            row.addView(minDbTextView);
            row.addView(seekbar);
            row.addView(maxDbTextView);
            mLeftEQLayout.addView(row);
        }

    }

    /**
     * 手动模式下   设置右耳的均衡器
     */
    private void setupRightEqualizeFxAndUi(){
        mRightEqualizer = new Equalizer(0, audioTrack2.getAudioSessionId());
        mRightEqualizer.setEnabled(true);//
        bands = mRightEqualizer.getNumberOfBands();
        TextView eqTextView = new TextView(this);
        eqTextView.setText("智能助听右耳频段调节：");
        eqTextView.setTextColor(Color.WHITE);
        mRightEQLayout.addView(eqTextView);
        //得到均衡器的频段
        //最小EQ带宽
        final short minRightEqualizer = mRightEqualizer.getBandLevelRange()[0];
        //Log.e("dddd", "setupEqualizeFxAndUi: "+mEqualizer.getBandLevelRange()[10]);
        //最大的均衡器带宽
        final short maxRightEqualizer = mRightEqualizer.getBandLevelRange()[1];

//        editor.putBoolean(RIGHTEARKEY,true);

        for (short i = 0; i < bands; i++) {
            final short band = i;
            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            //
            freqTextView.setText((mRightEqualizer.getCenterFreq(band) / 1000) + "HZ");
            mRightEQLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            minDbTextView.setText((minRightEqualizer / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxRightEqualizer / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.weight = 1;

            SeekBar seekbar = new SeekBar(this);
            seekbar.setLayoutParams(layoutParams);
            seekbar.setMax(maxRightEqualizer - minRightEqualizer);
            seekbar.setProgress(mRightEqualizer.getBandLevel(band));

            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mRightEqualizer.setBandLevel(band,
                            (short) (progress + minRightEqualizer));
//                    editor.putFloat("Rbands"+band,(float) progress+minRightEqualizer);
                }
            });
            row.addView(minDbTextView);
            row.addView(seekbar);
            row.addView(maxDbTextView);
            mRightEQLayout.addView(row);
        }
    }

    /**
     * 自动设置模式下的均衡器根据 左右耳听力阈值 进行补偿
     * @param LArray  左耳听力阈值
     * @param RArray  右耳听力阈值
     */
    private void AutoSetupEqualize(Float[] LArray, Float[] RArray){
        mLAutoEqualizer = new Equalizer(0, audioTrack1.getAudioSessionId());
        mLAutoEqualizer.setEnabled(true);
        short bands = mLAutoEqualizer.getNumberOfBands();        //得到均衡器的频段数量
        final short minAutoEqualizer = (short) (mLAutoEqualizer.getBandLevelRange()[0]/100);        //最小EQ带宽
        final short maxAutoEqualizer = (short) (mLAutoEqualizer.getBandLevelRange()[1]/100);        //最大的均衡器带宽
        System.out.println("KKK"+maxAutoEqualizer);

        /** 公共参数用mEqualizer3的**/
        mRAutoEqualizer = new Equalizer(0, audioTrack2.getAudioSessionId());
        mRAutoEqualizer.setEnabled(true);
        //左耳数据，右耳数据，每个频段增益补偿
        for (short i = 0; i < bands; i++) {
            if (LArray[i]>=0 && LArray[i]<=25){
                mLAutoEqualizer.setBandLevel(i, (short) (maxAutoEqualizer-15));
            }else if (LArray[i]>25 && (LArray[i]<40||LArray[i]==40)){
                mLAutoEqualizer.setBandLevel(i, (short) (LArray[i]-25));
            }else if (LArray[i]>40){
                mLAutoEqualizer.setBandLevel(i,maxAutoEqualizer);
            }

            if (RArray[i]>=0 && RArray[i]<=25){
                mRAutoEqualizer.setBandLevel(i, (short) (maxAutoEqualizer-15));
            }else if (RArray[i]>25 && (RArray[i]<40||RArray[i]==40)){
                mRAutoEqualizer.setBandLevel(i,(short) (RArray[i]-25));
            }else if (RArray[i]>40){
                mRAutoEqualizer.setBandLevel(i,maxAutoEqualizer);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mRecordPlayTwoChalThread) {
            mRecordPlayTwoChalThread.stopp();
        }
        killEqualizer();
    }

    /**
     * 1 杀死录播线程
     * 2 释放掉左右耳均衡器
     * 3 删除均衡器View
     */
    private void killEqualizer(){
        destroyThread();
        if (null != mLeftEqualizer){
            mLeftEqualizer = null;
            mLeftEQLayout.removeAllViews();
        }
        if (null != mRightEqualizer)
        {
            mRightEqualizer = null;
            mRightEQLayout.removeAllViews();
        }
        if (null != mLAutoEqualizer){
            mLAutoEqualizer = null;
        }
        if (null != mRAutoEqualizer){
            mRAutoEqualizer = null;
        }
    }

    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mRecordPlayTwoChalThread&& Thread.State.RUNNABLE == mRecordPlayTwoChalThread.getState()) {
                try {
                    Thread.sleep(1000);

                    mRecordPlayTwoChalThread.interrupt();
                } catch (Exception e) {
                    mRecordPlayTwoChalThread = null;
                }
            }
            mRecordPlayTwoChalThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordPlayTwoChalThread = null;
        }
    }
}
