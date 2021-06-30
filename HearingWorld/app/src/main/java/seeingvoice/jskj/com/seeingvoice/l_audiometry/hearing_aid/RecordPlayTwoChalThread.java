package seeingvoice.jskj.com.seeingvoice.l_audiometry.hearing_aid;

import android.util.Log;

//开启线程用于边录边放
class RecordPlayTwoChalThread extends Thread{
    private static final String TAG = "RecordPlayTwoChalThread";
    private HearingAidL mDeafAid;
    private volatile static RecordPlayTwoChalThread mInstance;


    public RecordPlayTwoChalThread(HearingAidL main) {
        this.mDeafAid = main;
    }

    /**
     * 获取单例引用
     *
     * @return
     */
//    public static RecordPlayTwoChalThread getInstance() {
//        if (mInstance == null) {
//            synchronized (AudioTrackManager.class) {
//                if (mInstance == null) {
//                    mInstance = new RecordPlayTwoChalThread();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    public void SetContextFromDeafAid(DeafAid main){
//        mDeafAid = main;
//    }

    @Override
    public void run() {
        try{
            byte[] buffer = new byte[mDeafAid.recBufSize];
            mDeafAid.audioRecord.startRecording();//开始录制
            mDeafAid.audioTrack1.play();//开始播放
            mDeafAid.audioTrack2.play();//开始播放
            while (mDeafAid.isRecording){
                //从MIC保存数据到缓冲区
                int bufferReadResult = mDeafAid.audioRecord.read(buffer,0, mDeafAid.recBufSize);
                byte[] tmpBuf = new byte[bufferReadResult];
                System.arraycopy(buffer,0,tmpBuf,0,bufferReadResult);
                //写入数据即播放
                mDeafAid.audioTrack1.write(tmpBuf,0,tmpBuf.length);
                mDeafAid.audioTrack2.write(tmpBuf,0,tmpBuf.length);
            }
            mDeafAid.audioRecord.stop();
            mDeafAid.audioTrack1.stop();
            mDeafAid.audioTrack2.stop();
        }catch (Throwable t){
            Log.e(TAG, "抛出异常 "+t.toString());
        }
    }

    /**
     * 设置左右声道是否可用
     *
     * @param left  左声道
     * @param right 右声道
     */
    public void setChannel(boolean left, boolean right) {
        if (null != mDeafAid.audioTrack1 && null !=mDeafAid.audioTrack2) {
            mDeafAid.audioTrack1.setStereoVolume(left ? 1 : 0, right ? 1 : 0);
            mDeafAid.audioTrack1.play();
            mDeafAid.audioTrack2.setStereoVolume(left ? 0 : 1, right ? 0 : 1);
            mDeafAid.audioTrack2.play();
        }
    }

    /**
     * 设置左右耳机音量
     *
     * @param max     最大值
     * @param balance 当前值
     */
    public void setBalance(boolean isleftvolumn,int max, int balance) {
        float b = (float) balance / (float) max;
        Log.i(TAG, "setBalance: b = " + b);
        if (isleftvolumn && null != mDeafAid.audioTrack1) {
            mDeafAid.audioTrack1.setStereoVolume(b, 0);
        }else if (!isleftvolumn && null != mDeafAid.audioTrack2){
            mDeafAid.audioTrack2.setStereoVolume(0, b);
        }
    }

    public void stopp() {
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (null != mDeafAid.audioTrack1){
            mDeafAid.audioTrack1.stop();
            mDeafAid.audioTrack1.release();
            mDeafAid.audioTrack1 = null;
        }

        if (null!=mDeafAid.audioTrack2) {
            mDeafAid.audioTrack2.stop();
            mDeafAid.audioTrack2.release();
            mDeafAid.audioTrack2 = null;
        }

        if (null != mDeafAid.audioRecord){
            mDeafAid.audioRecord.stop();
            mDeafAid.audioRecord.release();
            mDeafAid.audioRecord = null;
        }
    }
}