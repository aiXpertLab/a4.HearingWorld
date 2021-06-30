package com.seeingvoice.www.svhearing.noisetest.noiseUI;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.seeingvoice.www.svhearing.noisetest.SPLMeterActivity;

import java.lang.ref.WeakReference;

/**
 * Date:2019/3/6
 * Time:13:52
 * auther:zyy
 * 作用：分贝测量类
 */
public class AudioRecordDemo {

    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;
    private WeakReference<SPLMeterActivity> mActivity;

    public AudioRecordDemo(SPLMeterActivity activity) {
        mLock = new Object();
        mActivity = new WeakReference<>(activity);
    }

    public void stop() {
        this.isGetVoiceRun = false;
    }

    public void getNoiseLevel() {
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (isGetVoiceRun) {
            return;
        }
        if (mAudioRecord != null) {
            isGetVoiceRun = true;//录制声音线程正在运行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mAudioRecord.startRecording();
                    short[] buffer = new short[BUFFER_SIZE];
                    final SPLMeterActivity activity = mActivity.get();
                    while (isGetVoiceRun) {
                        int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                        long v = 0;
                        for (int i = 0; i < buffer.length; i++) {
                            v += buffer[i] * buffer[i];
                        }
                        double mean = v / (double) r;
                        final double volume = 10 * Math.log10(mean);
                        Log.d(TAG, "db value:" + volume);
                        if (null != activity) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (volume > 40) {
                                        activity.mNoiseHint.setText("找个安静的地方~");
                                        activity.mNoiseHint.setTextColor(Color.RED);
                                    } else {
                                        activity.mNoiseHint.setText("环境适合测试");
                                        activity.mNoiseHint.setTextColor(Color.GRAY);
                                    }
                                    activity.mNoiseboardView.setRealTimeValue((float) volume);
                                }
                            });
                        }
                        // 大概一秒十次
                        synchronized (mLock) {
                            try {
                                mLock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (null != activity) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.mNoiseboardView.setRealTimeValue(0f);
                            }
                        });
                    }
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;
                    if (null != activity) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
    //                            activity.plateView.setmMaxValue(0);
    //                            activity.plateView.setValue(1);
                            }
                        });
                    }
                }
            }).start();
        } else {
            return;
        }
    }
}