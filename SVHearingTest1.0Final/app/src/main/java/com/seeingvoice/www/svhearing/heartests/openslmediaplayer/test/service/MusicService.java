package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.audiofx.IEqualizer;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.MusicInfo;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.seeingvoice.www.svhearing.AppConstant.OLD_PURE_RESULT_SIZE;
import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities.ActivityChangeVolume.DEFALT_VOLUME;
import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities.musicPlayActivity.PARAM_MUSIC_LIST;
import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities.musicPlayActivity.PARAM_MUSIC_POSITION;

/**
 * Created by AchillesL on 2016/11/18.
 */

public class MusicService extends Service implements  MediaPlayer.OnErrorListener, IBasicMediaPlayer.OnCompletionListener {

    /*操作指令*/
    public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";
    public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";
    public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";
    public static final String ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST";
    public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";

    /*状态指令*/
    public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
    public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
    public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
    public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";

    public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
    public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
    public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
    public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";
    private static final int EQ_CAN_SET_NUMS = 7;
    public static final String VOLUME_PLAYER_LEFT = "VOLUME_PLAYER_LEFT";
    public static final String VOLUME_PLAYER_RIGHT = "VOLUME_PLAYER_RIGHT";
    private static final String SERVICE_MUSIC_EQ_BAND = "SERVICE_MUSIC_EQ_BAND";
    private static final String SERVICE_MUSIC_EQ_BAND_LENGTH = "SERVICE_MUSIC_EQ_BAND_LENGTH";
    private static final String SERVICE_MUSIC_EQ_DEFAULT_BAND = "0";

    private boolean mIsMusicPause = false;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private int musicListSize;

    private MusicReceiver mMusicReceiver = new MusicReceiver();
    //普通播放器 暂时不用  先用第三方 有10段均衡器效果
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    //uri:/storage/emulated/0/学习英语毛蛋/初级教材家长COPY内容/Phonics Starter/05-音轨 5.mp3
    private IMediaPlayerFactory factory0, factory1;
    private IBasicMediaPlayer player0, player1;
    private Context mContext;
    private float rVolume = 0.5f, lVolume = 0.5f;
    private IEqualizer equalizer0, equalizer1;
    private int numOfBands;
    private short minBand, maxBand;
//    private ArrayList<IMediaPlayerFactory> factoryList = new ArrayList<>();
//    private ArrayList<IBasicMediaPlayer> SLPlaterList = new ArrayList<>();
    private int audioSessionID0,audioSessionID1;
    public MusicBind musicBind = new MusicBind();
    private boolean reSetMusicIndexFlag = false;
    private int mCurrentMusicIndex = 0,mResetIndex = 0;
    private boolean isPlayer_0_prepared = false,isPlayer_1_prepared = false;
    private int playerPreparedNo = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playerPreparedNo++;
            if (playerPreparedNo == 2){
                player0.start();
                player1.start();
                mIsMusicPause = false;//正在播放
                mCurrentMusicIndex = msg.what;
                playerPreparedNo = 0;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMusicDatas(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBoardCastReceiver();//注册了本地广播 控制音乐播放
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        ToastUtil.showShortToastCenter("播放器出现未知错误,已重置");
        return false;
    }

    @Override
    public void onCompletion(IBasicMediaPlayer mp) {
        sendMusicCompleteBroadCast();
    }

    public class MusicBind extends Binder {
        public void setCurrentPosition(int position) {
            reSetMusicIndexFlag = true;//重置当前播放的歌曲
            mResetIndex = position;
        }

        public void stopCurrentMusic() {
            stop();
        }

        //控制左耳音量
        public void setLVolume(float volume){
            SharedPreferencesHelper.getInstance().saveData(VOLUME_PLAYER_LEFT, volume);
            if (player0 != null){
                player0.setVolume(volume,0);
            }
        }
        //控制右耳音量
        public void setRVolume(float volume){
            SharedPreferencesHelper.getInstance().saveData(VOLUME_PLAYER_RIGHT,volume);
            if (player1 != null){
                player1.setVolume(0,volume);
            }
        }

        public void setEQsetting(String[] left, String[] right) {
            Log.e("测试测试测试", "setEQsetting: maxBand:"+maxBand+"----minBand:"+minBand);
            setEQBands(left,right);
        }
    }

    private void setEQBands(String[] left, String[] right) {
        Short[] leftTemp = reCreateArray(left);
        Short[] rightTemp = reCreateArray(right);
        if (leftTemp != null) {
            if (leftTemp[0] == 121) {//左耳没有数据的
                ToastUtil.showShortToastCenter("左耳无测试结果，不设置助听！");
                for (int i = 0; i < numOfBands; i++) {
                    equalizer0.setBandLevel((short) i, minBand);
                }
            } else {//左耳有数据
                for (int i = 0; i < EQ_CAN_SET_NUMS; i++) {
                    if (leftTemp[i] >= -10 && leftTemp[i] <= 25) {
                        equalizer0.setBandLevel((short) (i + 2), minBand);
                    } else if (leftTemp[i] > 25 && (leftTemp[i] < 55 || leftTemp[i] == 55)) {
                        equalizer0.setBandLevel((short) (i + 2), (short) ((leftTemp[i] - 25 - 15) * 100));
                    } else if (leftTemp[i] > 55) {
                        equalizer0.setBandLevel((short) (i + 2), maxBand);
                    }
                }
            }
        }

        if (rightTemp != null) {
            if (rightTemp[0] == 121) {//右耳没有数据的
                ToastUtil.showShortToastCenter("右耳无测试结果，不设置助听！");
                for (int i = 0; i < numOfBands; i++) {
                    equalizer1.setBandLevel((short) i, minBand);
                }
            } else {//左耳有数据
                for (int i = 0; i < EQ_CAN_SET_NUMS; i++) {
                    if (rightTemp[i] >= -10 && rightTemp[i] <= 25) {
                        equalizer1.setBandLevel((short) (i + 2), minBand);
                    } else if (rightTemp[i] > 25 && (rightTemp[i] < 55 || rightTemp[i] == 55)) {
                        equalizer1.setBandLevel((short) (i + 2), (short) ((rightTemp[i] - 25 - 15) * 100));
                    } else if (rightTemp[i] > 55) {
                        equalizer1.setBandLevel((short) (i + 2), maxBand);
                    }
                }
            }
        }
        SharedPreferencesHelper.getInstance().saveData(SERVICE_MUSIC_EQ_BAND_LENGTH,left.length);
        for (int i = 0; i < left.length; i++) {
            SharedPreferencesHelper.getInstance().saveData(SERVICE_MUSIC_EQ_BAND+i,left[i]);
            SharedPreferencesHelper.getInstance().saveData(SERVICE_MUSIC_EQ_BAND+i,right[i]);
        }
    }

    /**
     * 得到EQ的参数数组 新旧听力测试结果
     * @param arr 纯音结果 数组 新旧两种
     * @return
     */
    private Short[] reCreateArray(String arr[]) {
        String Temp[] = new String[EQ_CAN_SET_NUMS];
        if (arr.length == OLD_PURE_RESULT_SIZE){//旧版9个  把第五个剔除掉
            Temp[0] = arr[0];
            Temp[1] = arr[1];
            Temp[2] = arr[2];
            Temp[3] = arr[3];
            Temp[4] = arr[4];
            Temp[5] = arr[6];
            Temp[6] = arr[8];
        }else {//新版本
            Temp[0] = arr[0];
            Temp[1] = arr[1];
            Temp[2] = arr[2];
            Temp[3] = arr[3];
            Temp[4] = arr[5];
            Temp[5] = arr[7];
            Temp[6] = arr[9];
        }
        Short tempShortArr[] = new Short[EQ_CAN_SET_NUMS];
        for (int i = 0; i < EQ_CAN_SET_NUMS; i++) {
            tempShortArr[i] = Short.valueOf(String.valueOf(Temp[i]));
        }
        return tempShortArr;
    }


    /**
     * 初始化控件和原始数据
     * @param intent 传递intent从启动服务的组件得到初始数据
     */
    private void initMusicDatas(Intent intent) {
        mContext = getApplicationContext();
        if (intent == null) return;
        List<MusicInfo> tempMusicInfos = (List<MusicInfo>) intent.getSerializableExtra(PARAM_MUSIC_LIST);
        mCurrentMusicIndex = intent.getIntExtra(PARAM_MUSIC_POSITION,0);//第几首音乐
        musicListSize = tempMusicInfos.size();
        mMusicInfos.addAll(tempMusicInfos);
        createPlayers();//创建左右声道播放器
        createEQ();//初始化均衡器
    }

    /**
     * 初始化控件和原始数据  创建均衡器
     */
    private void createEQ() {
        equalizer0 = factory0.createHQEqualizer();
        equalizer1 = factory1.createHQEqualizer();
        equalizer0.setEnabled(true);
        equalizer1.setEnabled(true);
        numOfBands = equalizer0.getNumberOfBands();
        minBand = equalizer0.getBandLevelRange()[0];        //最小EQ带宽
        maxBand = equalizer0.getBandLevelRange()[1];        //最大的均衡器带宽

        Integer resultLength = (Integer) SharedPreferencesHelper.getInstance().getData(SERVICE_MUSIC_EQ_BAND_LENGTH,0);
        String eqLeftBands[] = new String[resultLength];
        String eqRightBands[] = new String[resultLength];

        for (int i = 0; i < resultLength; i++) {
            eqLeftBands[i] = (String) SharedPreferencesHelper.getInstance().getData(SERVICE_MUSIC_EQ_BAND+i,SERVICE_MUSIC_EQ_DEFAULT_BAND);
            Log.e("eqLeftBandseqLeftBands"+i, "createEQ: "+eqLeftBands[i]);
            eqRightBands[i] = (String) SharedPreferencesHelper.getInstance().getData(SERVICE_MUSIC_EQ_BAND+i,SERVICE_MUSIC_EQ_DEFAULT_BAND);
            Log.e("eqLeftBandseqLeftBands"+i, "createEQ: "+eqRightBands[i]);
        }
        if (eqLeftBands.length > 0 && eqRightBands.length > 0){
            setEQBands(eqLeftBands,eqRightBands);
        }
    }

    /**
     * 创建播放器
     */
    private void createPlayers() {
        if (factory0 == null) {
            factory0 = new OpenSLMediaPlayerFactory(mContext);
        }
        if (factory1 == null) {
            factory1 = new OpenSLMediaPlayerFactory(mContext);
        }

        try {
            player0 = factory0.createMediaPlayer();
            player1 = factory1.createMediaPlayer();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        if (mMusicInfos != null && !mMusicInfos.isEmpty()){
            String path = mMusicInfos.get(0).getData();
            try {
                player0.setDataSource(path);
                player1.setDataSource(path);
                player0.prepareAsync();
                player1.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rVolume = (Float) SharedPreferencesHelper.getInstance().getData(VOLUME_PLAYER_LEFT, DEFALT_VOLUME);
        lVolume = (Float) SharedPreferencesHelper.getInstance().getData(VOLUME_PLAYER_RIGHT,DEFALT_VOLUME);

        player0.setVolume(0, rVolume);
        player1.setVolume(lVolume, 0);
    }

    /**
     * 注册本地广播  接收 音乐控件的 控制广播
     */
    private void initBoardCastReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);//播放
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);//暂停
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);//下一曲
        intentFilter.addAction(ACTION_OPT_MUSIC_LAST);//上一曲
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);//拖动

        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mMediaPlayer != null){
//            if (mMediaPlayer.isPlaying()){
//                mMediaPlayer.stop();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//            }else {
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//            }
//        }
        releasePlayers();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }

    /**
     * 播放函数
     * @param index
     */
    private void play(final int index) {

        if (index >= mMusicInfos.size()) return;
        if (mCurrentMusicIndex == index && mIsMusicPause) {//当前播放歌曲与控件指定歌曲一致，并且音乐处于暂停状态
            player0.start();
            player1.start();
        } else {//指定歌曲不是当前播放的，并且歌曲正在播放
            String path = mMusicInfos.get(index).getData();
            //当mCurrentMusicIndex 赋新值后，将不再标记被设置过
            reSetMusicIndexFlag = false;
            //重置播放器
            player0.reset();
            player1.reset();

            try {
                //initialize 播放器
                player0.setDataSource(path);
                player1.setDataSource(path);
                player0.prepare();
                player1.prepare();
                player0.setOnPreparedListener(new IBasicMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(IBasicMediaPlayer mp) {
                        isPlayer_0_prepared = true;
                        mHandler.sendEmptyMessage(index);
                    }
                });

                player1.setOnPreparedListener(new IBasicMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(IBasicMediaPlayer mp) {
                        isPlayer_1_prepared = true;
                        mHandler.sendEmptyMessage(index);
                    }
                });
                player0.setOnCompletionListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int duration = player0.getDuration();
            sendMusicDurationBroadCast(duration);
        }
        mHandler.removeMessages(index);
        mHandler.removeMessages(index);
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY);
//        if (index >= mMusicInfos.size()) return;
//        if (mCurrentMusicIndex == index && mIsMusicPause) {
//            mMediaPlayer.start();
//        } else {//(mCurrentMusicIndex == index && mIsMusicPause)  即：(mCurrentMusicIndex != index && !mIsMusicPause)
//            String path = mMusicInfos.get(index).getData();
//            reSetMusicIndexFlag = false;//当mCurrentMusicIndex 赋新值后，将不再标记被设置过
//            //重置播放器
//            mMediaPlayer.reset();
//                try {
//                    //initialize 播放器
//                    mMediaPlayer.setDataSource(path);
//                    mMediaPlayer.prepare();
//                    mMediaPlayer.setOnCompletionListener(this);
//                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            mMediaPlayer.start();
//                            mIsMusicPause = false;//正在播放
//                            int duration = mMediaPlayer.getDuration();
//                            sendMusicDurationBroadCast(duration);
//                            mCurrentMusicIndex = index;
//                        }
//                    });
//                    mMediaPlayer.setOnErrorListener(this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY);

    }

    private void pause() {
//        mMediaPlayer.pause();
//        mIsMusicPause = true;
//        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE);

        player0.pause();
        player1.pause();
        mIsMusicPause = true;
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE);
    }

    private void stop() {
//        mMediaPlayer.stop();
        player0.stop();
        player1.stop();
    }

    private void next() {
        if (reSetMusicIndexFlag){
            mCurrentMusicIndex = mResetIndex;
        }
        if (mCurrentMusicIndex+1 < musicListSize) {
            play(mCurrentMusicIndex+1);
        } else {
            stop();
        }
    }

    private void last() {
        if (mCurrentMusicIndex != 0) {
            play(mCurrentMusicIndex - 1);
        }
    }

    private void seekTo(Intent intent) {
//        if (mMediaPlayer.isPlaying()) {
//            int position = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0);
//            mMediaPlayer.seekTo(position);
//        }

        if (player0.isPlaying()) {
            int position = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0);
            player0.seekTo(position);
            player1.seekTo(position);
        }
    }

//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        sendMusicCompleteBroadCast();
//    }

    class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_OPT_MUSIC_PLAY)) {
                if (reSetMusicIndexFlag){
                    play(mResetIndex);
                }else {
                    play(mCurrentMusicIndex);
                }
            } else if (action.equals(ACTION_OPT_MUSIC_PAUSE)) {
                pause();
            } else if (action.equals(ACTION_OPT_MUSIC_LAST)) {
                last();
            } else if (action.equals(ACTION_OPT_MUSIC_NEXT)) {
                next();
            } else if (action.equals(ACTION_OPT_MUSIC_SEEK_TO)) {
                seekTo(intent);
            }
        }
    }

    private void sendMusicCompleteBroadCast() {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_COMPLETE);
        intent.putExtra(PARAM_MUSIC_IS_OVER, (mCurrentMusicIndex == mMusicInfos.size() - 1));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicDurationBroadCast(int duration) {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_DURATION);
        intent.putExtra(PARAM_MUSIC_DURATION, duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicStatusBroadCast(String action) {
        Intent intent = new Intent(action);
        if (action.equals(ACTION_STATUS_MUSIC_PLAY)) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION, player0.getCurrentPosition());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 释放生成player的factory的资源和播放器资源
     */
    public void releasePlayers(){
        if (factory0 != null){
            factory0.release();
            factory0 = null;
        }
        if (factory1 != null){
            factory1.release();
            factory1 = null;
        }

        if (player0 != null){
            if (player0.isPlaying()){
                player0.pause();
                player0.stop();
                player0.release();
                player0 = null;
            }else {
                player0.release();
                player0 = null;
            }
        }

        if (player1 != null){
            if (player1.isPlaying()){
                player1.pause();
                player1.stop();
                player1.release();
                player1 = null;
            }else {
                player1.release();
                player1 = null;
            }
        }
    }

}
