package seeingvoice.jskj.com.seeingvoice.l_audiometry;
/*
 * Conduct Pure Tone Hearing Test using Asset WAV files.
 *
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-09
 */

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static seeingvoice.jskj.com.seeingvoice.MyData.SAMPLE_RATE;     // 44100;
public class L5_Thread_StaticWAV extends Thread {

    private static final String TAG = "L_Thread_StaticWAV: ";
    private Activity    mActivity;
    private AudioTrack  mAudioTrack;
    private byte[]      audioData;
    private final String      sFileName;

    public L5_Thread_StaticWAV(Activity activity, String fileName, boolean isLeft) {
        mActivity = activity;
        sFileName = fileName;
        int iLR = isLeft ? 1 : 0;

        try {
            // InputStream in = mActivity.getResources().openRawResource(R.raw.ss);
            InputStream in = mActivity.getAssets().open(sFileName);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (int b; (b = in.read()) != -1; ) {
                    out.write(b);
                }
                audioData = out.toByteArray();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            Log.wtf(TAG, "Failed to read", e);
        }
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
        if (null != mAudioTrack) {
            mAudioTrack.setStereoVolume(iLR, 1-iLR);
            mAudioTrack.play();
        }
    }

    @Override
    public void run() {
        super.run();
        mAudioTrack.write(audioData, 44, audioData.length-44);
    }

    public void play() {
        if (null != mAudioTrack)
            mAudioTrack.play();
    }

    public void stopp() {
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}