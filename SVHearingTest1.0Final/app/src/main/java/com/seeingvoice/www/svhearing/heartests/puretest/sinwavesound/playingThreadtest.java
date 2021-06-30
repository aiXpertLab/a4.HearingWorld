package com.seeingvoice.www.svhearing.heartests.puretest.sinwavesound;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.heartests.puretest.SettingsContentObserver;

/**
 * Date:2019/5/27
 * Time:14:18
 * auther:zyy
 */
public class playingThreadtest extends Thread {
    /** 采样频率 */
    private static int RATE;
    private AudioTrack mAudioTrack;
    private short[] msin;
    public static boolean ISPLAYSOUND;
    private int bufferSize,waveLen,length;
    public static final boolean LEFT = true;
    public static final boolean RIGHT = false;
    private float volume;
    Context mContext = MyApplication.getAppContext();
    private SettingsContentObserver mSettingsContentObserver;

    public playingThreadtest(int Hz,int dB){
        initSampleRate();
        if (RATE == 0) RATE = 44100; // Use a default value if property not found

        waveLen = RATE / Hz;//一个波的采样点数
        length = waveLen * Hz;
        bufferSize = AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);//16bit音频的最小缓冲区
        mAudioTrack = new AudioTrack(AudioManager.STREAM_SYSTEM,RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,bufferSize,AudioTrack.MODE_STREAM);
        UpdateSinWave.updateDB(Hz,dB);
        msin = UpdateSinWave.getSinWave(waveLen,length);
        ISPLAYSOUND = true;//开始播放标记
        if(null != mAudioTrack){
            mAudioTrack.play();//audiotrack 开始工作
        }
    }

    /**
     * 该线程类的构造函数，初始化参数
     * @param isleft
     * @param Hz
     * @param dB  此处是dBHL 声压级单位
     */
    public playingThreadtest(boolean isleft,int Hz,int dB) {
        Log.e("45856", "playingThreadtest: 频率Hz是："+Hz);
        initSampleRate();
        this.volume = MyApplication.volume;//获得系统音频流音量  system_stream  默认就是最大值  无需调节
        Log.e("45856", "playingThreadtest: 音量是："+volume+isleft);
        waveLen = RATE / Hz;//一个波的采样点数RATE采样频率
        length = waveLen * Hz;
        bufferSize = AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);//16bit音频的最小缓冲区
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,2*bufferSize,AudioTrack.MODE_STREAM);
        UpdateSinWave.updateDB(Hz,dB);
        msin = UpdateSinWave.getSinWave(waveLen,length);
        ISPLAYSOUND = true;
        if(null != mAudioTrack && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            try {
                mAudioTrack.play();
            } catch (IllegalStateException e) {//AudioTrack 如果没有初始化成功会抛出分发状态异常，避免ANR
                e.printStackTrace();
            }
            setChannel(isleft);
        }
    }

    /**
     * 得到该手机最佳的采样率
     */
    private void initSampleRate() {
        AudioManager am = (AudioManager) MyApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        String sampleRateStr = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        RATE = Integer.parseInt(sampleRateStr);
        Log.e("哈哈哈哈哈", "playingThreadtest: RATE：" + sampleRateStr);
    }

    private void registerVolumeChangeReceiver() {
        mSettingsContentObserver = new SettingsContentObserver(mContext, new Handler());
        mContext.getContentResolver()
        .registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }

    private void unregisterVolumeChangeReceiver() {
        mContext.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    /** 设置左右声道是否零输出
     * @param left
     */
    private void setChannel(boolean left) {
        mAudioTrack.setStereoVolume(left ? 1 : 0, (!left) ? 1 : 0);
    }

    @Override
    public void run() {
        while (ISPLAYSOUND){
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//API>=23时候
                    mAudioTrack.write(msin,0,msin.length,AudioTrack.WRITE_BLOCKING);
                }else {//API<23时
                    mAudioTrack.write(msin,0,msin.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止播放当前纯音
     */
    public void stopPlay() {
        ISPLAYSOUND = false;
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (null != mAudioTrack) {
            try {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }
}
