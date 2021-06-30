package com.seeingvoice.www.svhearing.heartests.openslmediaplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.audiofx.IEqualizer;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.beans.PureHistoryItemBean;
import com.seeingvoice.www.svhearing.util.SpObjectUtil;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.io.IOException;
import java.util.List;

import static com.seeingvoice.www.svhearing.AppConstant.NEW_PURE_RESULT_SIZE;
import static com.seeingvoice.www.svhearing.AppConstant.OLD_PURE_RESULT_SIZE;

public class MusicService extends Service {

    public MusicBind musicBind = new MusicBind();
    //uri:/storage/emulated/0/学习英语毛蛋/初级教材家长COPY内容/Phonics Starter/05-音轨 5.mp3
    private IMediaPlayerFactory factory0, factory1;
    private IBasicMediaPlayer player0, player1;
    private Context context;
    private float rVolume = 0.5f, lVolume = 0.5f;
    private IEqualizer equalizer0, equalizer1;
    private int numOfBands;
    private short minBand, maxBand;
    private List<PureHistoryItemBean.DataBean.SimpleDetailBean> detailBeanList = null;//纯音结果历史列表
    private PureHistoryItemBean pureHistoryItemBean;
    private Float[] leftEarDatas, rightEarDatas;//服务端得到后存在SP中的左右耳的结果数组，

    @Override
    public void onCreate() {
        super.onCreate();
        //创建左右声道播放器
        createPlayers();
        //初始化均衡器
        createEQ();
    }

    private void createEQ() {
        equalizer0 = factory0.createHQEqualizer();
        equalizer1 = factory1.createHQEqualizer();
        numOfBands = equalizer0.getNumberOfBands();
        equalizer0.setEnabled(true);
        equalizer1.setEnabled(true);
        minBand = equalizer0.getBandLevelRange()[0];        //最小EQ带宽
        maxBand = equalizer0.getBandLevelRange()[1];        //最大的均衡器带宽
    }

    /**
     * 创建播放器
     */
    private void createPlayers() {
        context = getApplicationContext();
        if (factory0 == null) {
            factory0 = new OpenSLMediaPlayerFactory(context);
        }

        if (factory1 == null) {
            factory1 = new OpenSLMediaPlayerFactory(context);
        }

        if (player0 == null) {
            player0 = factory0.createMediaPlayer();
        }

        if (player1 == null) {
            player1 = factory1.createMediaPlayer();
        }

        player0.setVolume(0, rVolume);
        player1.setVolume(lVolume, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public class MusicBind extends Binder {
        /* 设置本地音乐地址给播放器*/
        public void setMusicUri(String path) {
            if (player0 != null) {
                if (player1 == null) {
                    player1 = factory1.createMediaPlayer();
                }
            } else {//如果播放器为空，则先实例化
                if (player1 != null) {
                    if (player1.isPlaying()) {
                        player1.stop();
                        player1.release();
                        player1 = null;
                    }
                }
                createPlayers();
            }

            try {
                if (player0.isPlaying() && player1.isPlaying()) {
                    player0.reset();
                    player0.setDataSource(path);
                    player0.prepare();
                    player1.reset();
                    player1.setDataSource(path);///storage/emulated/0/wzzyy/music/周杰伦 - 七里香.flac
                    player1.prepare();
                }

                if (!player0.isPlaying() && !player1.isPlaying()) {
                    player0.setDataSource(path);
                    player0.prepare();
                    player1.setDataSource(path);///storage/emulated/0/wzzyy/music/周杰伦 - 七里香.flac
                    player1.prepare();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //判断是否处于播放状态
        public boolean isPlaying() {
            return player0.isPlaying();
        }

        //播放或暂停歌曲
        public void play() {
            if (player0.isPlaying()) {
                if (player1.isPlaying()) {
                    player0.pause();
                    player1.pause();
                }
            } else {
                if (!player1.isPlaying()) {
                    player0.start();
                    player1.start();
                }
            }
        }

        public void SetupEQ() {
            pureHistoryItemBean = SpObjectUtil.getObject(MyApplication.getAppContext(), PureHistoryItemBean.class);
            if (null == pureHistoryItemBean) {
                ToastUtil.showLongToast("没有设置预置助听参数");
                return;
            }
            detailBeanList = pureHistoryItemBean.getData().getSimple_detail();
            int size = detailBeanList.size();
            leftEarDatas = new Float[size];
            rightEarDatas = new Float[size];
            for (int i = 0; i < size; i++) {
                leftEarDatas[i] = Float.valueOf(detailBeanList.get(i).getLeft_result());
                rightEarDatas[i] = Float.valueOf(detailBeanList.get(i).getRight_result());
                Log.e("测试测试测试哈哈哈", "setMediaPlayer: leftEarDatas[i]+rightEarDatas[i]" + i + leftEarDatas[i] + "====" + rightEarDatas[i] + "\n");
            }

            AutoSetupEqualize(leftEarDatas, rightEarDatas);
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration() {
            return player0.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrentPosition() {
            return player0.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc) {
            player0.seekTo(mesc);
            player1.seekTo(mesc);
        }

        public String[] getCenterBand() {
            Short band = equalizer0.getNumberOfBands();
            String[] str = new String[band];
            for (Short i = 0; i < band; i++) {
                str[i] = String.valueOf(equalizer0.getCenterFreq(i) / 1000);
            }
            return str;
        }

        public void setEqEvent(short band, short progress) {
            equalizer0.setBandLevel(band, (short) (progress + minBand));//bandLevel*1000)
            equalizer1.setBandLevel(band, (short) (progress + minBand));
            Log.e("设置均衡器", "test...." + band + "值" + "\n最大值：" + maxBand + ";最小值：" + minBand);
        }
    }

    //        public void exchange(){
    //            if (player0 != null){
    //                try {
    //                    player0.reset();
    //                    player0.setDataSource("/storage/emulated/0/音乐/周杰伦 - 七里 香.flac");
    //                    player0.prepare();
    //                    player1.reset();
    //                    player1.setDataSource("/storage/emulated/0/音乐/周杰伦 - 夜 曲.flac");///storage/emulated/0/wzzyy/music/周杰伦 - 七里香.flac
    //                    player1.prepare();
    //                    player0.setVolume(0,rVolume);
    //                    player1.setVolume(lVolume,0);
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }



    /**
     * 自动设置模式下的均衡器根据 左右耳听力阈值 进行补偿
     */
    private void AutoSetupEqualize(Float[] Larray,Float[] Rarray){
        float[] LTempArray = null,RTempArray = null;

        if (Larray != null && Rarray != null){
            //旧版数据
            if (Larray.length == OLD_PURE_RESULT_SIZE || Rarray.length == OLD_PURE_RESULT_SIZE){//如果是旧版的数据长度
                LTempArray = new float[OLD_PURE_RESULT_SIZE];
                RTempArray = new float[OLD_PURE_RESULT_SIZE];
                LTempArray[0] = -10;
                LTempArray[1] = (Larray[0] + Larray[1])/2;
                LTempArray[2] = (Larray[2] + Larray[3])/2;
                LTempArray[3] = (Larray[4] + Larray[5] + Larray[6] + Larray[7])/4;
                LTempArray[4] = Larray[8];

                RTempArray[0] = -10;
                RTempArray[1] = (Rarray[0] + Rarray[1])/2;
                RTempArray[2] = (Rarray[2] + Rarray[3])/2;
                RTempArray[3] = (Rarray[4] + Rarray[5] + Rarray[6] + Rarray[7])/4;
                RTempArray[4] = Rarray[8];
            }

            //新版数据
            if (Larray.length == NEW_PURE_RESULT_SIZE || Rarray.length == NEW_PURE_RESULT_SIZE){//如果是旧版的数据长度
                LTempArray = new float[NEW_PURE_RESULT_SIZE];
                RTempArray = new float[NEW_PURE_RESULT_SIZE];
                for (int i = 0; i < NEW_PURE_RESULT_SIZE; i++) {
                    LTempArray[i] = -10;
                    RTempArray[i] = -10;
                }

                if (Larray[0] != 121){//左耳有测试结果
                    LTempArray[0] = -10;
                    LTempArray[1] = (Larray[0] + Larray[1] + Larray[2])/3;
                    LTempArray[2] = Larray[3];
                    LTempArray[3] = (Larray[5] + Larray[7])/2;
                    LTempArray[4] = Larray[9];
                }

                if (Rarray[0] != 121){//右耳有测试结果
                    RTempArray[0] = -10;
                    RTempArray[1] = (Rarray[0] + Rarray[1]+ Rarray[2])/3;
                    RTempArray[2] = Rarray[3];
                    RTempArray[3] = (Rarray[5] + Rarray[7])/2;
                    RTempArray[4] = Rarray[9];
                }

                for (int i = 0; i < 5; i++) {
                    Log.e("测试测试测试哈哈哈", "\nsetMediaPlayer: LTempArray[i]+RTempArray[i]"+i+LTempArray[i]+"===="+RTempArray[i]+"\n");
                }
            }

            try {
//                Log.e("测试测试测试哈哈哈", "setMediaPlayer: minAutoEqualizer+maxAutoEqualizer"+minAutoEqualizer+"===="+maxAutoEqualizer+"\n");

                /** 公共参数用mEqualizer3 的**/
//                mRAutoEqualizer = new Equalizer(0, rSessionID);
//                mRAutoEqualizer.setEnabled(true);
                for (short i = 0; i < numOfBands; i++) {
                    if (LTempArray != null) {
                        if (LTempArray[i]>= -10 && LTempArray[i]<=25){
                            equalizer0.setBandLevel(i,minBand);
                        }else if (LTempArray[i]>25 && (LTempArray[i]< 55||LTempArray[i]==55)){
                            equalizer0.setBandLevel(i, (short) ((LTempArray[i]-25-15)*100));
                        }else if (LTempArray[i]>55){
                            equalizer0.setBandLevel(i,maxBand);
                        }
                    }
                    if (RTempArray != null) {
                        if (RTempArray[i]>= -10 && RTempArray[i]<=25){//0-25之间
                            equalizer1.setBandLevel(i,minBand);//不调 -15 0 15 就是0的位置
                        }else if (RTempArray[i]>25 && (RTempArray[i]<55||RTempArray[i]==55)){
                            equalizer1.setBandLevel(i,(short) ((RTempArray[i]-25-15)*100));
                        }else if (RTempArray[i]>55){
                            equalizer1.setBandLevel(i,maxBand);//均衡器调节到最大
                        }
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            //左耳数据，右耳数据，每个频段增益补偿
        }else {
            ToastUtil.showLongToast("音乐助听参数错误！请稍后再试！或者联系管理员。");
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayers();
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
