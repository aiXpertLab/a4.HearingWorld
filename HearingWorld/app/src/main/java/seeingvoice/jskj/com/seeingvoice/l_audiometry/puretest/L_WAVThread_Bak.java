package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;
/*
 * Conduct Pure Tone Hearing Test using Asset WAV files.
 *
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-09
 */

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import seeingvoice.jskj.com.seeingvoice.MyApp;

import static seeingvoice.jskj.com.seeingvoice.MyData.SAMPLE_RATE;

/**
 * Date:2021/2/7
 * Author:LeoReny@hypech.com
 */
public class L_WAVThread_Bak extends Thread {

    private static final String TAG = "L_WAVThread: ";
    private Activity    oActivity;
    private AudioTrack  oAudioTrack;
    private byte[]      aData;
    private String      sFileName;
    private boolean isLeft;

    public static int RATE = 0;
    private float[] mFsin;
    public static boolean ISPLAYSOUND;
    public static final boolean RIGHT = false;
    Context mContext = MyApp.getAppContext();
    private SettingsContentObserver mSettingsContentObserver;

    public L_WAVThread_Bak(Activity activity, String fileName, boolean left) {
        oActivity = activity;
        sFileName = fileName;
        isLeft     = left;

        int bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        oAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public void run() {
        super.run();
        try {
            if (null != oAudioTrack){
                setChannel(isLeft, !isLeft);
                oAudioTrack.play();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = oActivity.getResources().getAssets().open(sFileName);

            byte[] buffer = new byte[1024];
            int playIndex = 0;
            boolean isLoaded = false;
            while (null != oAudioTrack && AudioTrack.PLAYSTATE_STOPPED != oAudioTrack.getPlayState()) {
                // 字符长度
                int len;
                if (-1 != (len = inputStream.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    aData = byteArrayOutputStream.toByteArray();
                    Log.i(TAG, "run: 已缓冲 : " + aData.length);
                } else {
                    // 缓冲完成
                    isLoaded = true;
                }

                if (AudioTrack.PLAYSTATE_PLAYING == oAudioTrack.getPlayState()) {
                    Log.i(TAG, "run: 开始从 " + playIndex + " 播放");
                    playIndex += oAudioTrack.write(aData, playIndex, aData.length - playIndex);
                    Log.i(TAG, "run: 播放到了 : " + playIndex);
                    if (isLoaded && playIndex == aData.length) {
                        Log.i(TAG, "run: 播放完了");
                        oAudioTrack.stop();
                    }

                    if (playIndex < 0) {
                        Log.i(TAG, "run: 播放出错");
                        oAudioTrack.stop();
                        break;
                    }
                }
            }
            Log.i(TAG, "run: play end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置左右声道是否可用
     *
     * @param left  左声道
     * @param right 右声道
     */
    public void setChannel(boolean left, boolean right) {
        if (null != oAudioTrack) {
            oAudioTrack.setStereoVolume(left ? 1 : 0, right ? 1 : 0);
            oAudioTrack.play();
        }
    }

    public void play() {
        if (null != oAudioTrack)
            oAudioTrack.play();
    }

    public void stopp() {
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (null != oAudioTrack) {
            oAudioTrack.stop();
            oAudioTrack.release();
            oAudioTrack = null;
        }
    }
}
