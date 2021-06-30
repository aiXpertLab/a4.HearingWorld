package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.MusicInfo;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.MusicListActivity;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.SetupMusicHearAssistActivity;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.preferences.SettingsActivity;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.service.MusicService;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.BackgourndAnimationRelativeLayout;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.IPlayInfo;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.NewDiscView;
import com.seeingvoice.www.svhearing.history.PureTestHistoryListActivity;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.seeingvoice.www.svhearing.AppConstant.REQUEST_AUTO_SETTING;
import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.NewDiscView.DURATION_NEEDLE_ANIAMTOR;


public class musicPlayActivity extends AppCompatActivity implements IPlayInfo, View.OnClickListener {

    private static final String TAG = musicPlayActivity.class.getName();
    private static final int RESULT_FROM_MUSIC_LIST = 100;
    private static final int MUSIC_LIST_LOAD_SUCCESS = 200;
    private static final int STOP_CURRENT_MUSIC = 300;
    private static final int REQUEST_EQ_PARAMS = 500;
    private NewDiscView mDisc;//音乐碟盘视图
    private Toolbar mToolbar;//标题栏
    private SeekBar mSeekBar;//音乐进度条
    private ImageView mIvPlayOrPause, mIvNext, mIvLast,mIvEQsetting,mImvVolume;//音乐控制 图标
    private TextView mTvMusicDuration,mTvTotalMusicDuration;//显示音乐时长的文本控件
    private BackgourndAnimationRelativeLayout mRootLayout;//自定义的 背景布局
    public static final int MUSIC_MESSAGE = 0;
    public static final String PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST";
    public static final String PARAM_MUSIC_POSITION = "PARAM_MUSIC_POSITION";

    private ArrayList<MusicInfo> mMusicInfos = new ArrayList<>();
    private int mMusicPosition = 0,totalMusicNums = 0;
    //申明ContentResolver对象，用于访问系统数据库
    private ContentResolver contentResolver ;
    //用于存储从系统数据库查询的出结果
    private Cursor mCursor ;
    private MusicReceiver mMusicReceiver = new MusicReceiver();

    //Bundle来获取启动Activity传递的参数
    private Bundle bundle;

    private Handler mMusicHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MUSIC_LIST_LOAD_SUCCESS://开启播放音乐的服务
                    startMusicService();
                    break;
                case MUSIC_MESSAGE:
                    mSeekBar.setProgress(mSeekBar.getProgress() + 1000);
                    mTvMusicDuration.setText(duration2Time(mSeekBar.getProgress()));
                    startUpdateSeekBarProgress();
                    break;
                case STOP_CURRENT_MUSIC:
                    musicControl.stopCurrentMusic();//选择歌曲后，应该通知服务端停止播放上一周歌曲
                    mDisc.stop();//标记控件为音乐停止的状态
                    stop();//音乐控制控件  应该显示停止状态
                    break;
            }
        }
    };

    private int mCurrentMusicPosition = 0;
    private MusicService.MusicBind musicControl;
    private ServiceConnection conn;
    private Intent intentService;
    private boolean isSuccessGetMusicList = false;
//    private boolean isMusicIDSetted = false;//标记是否 选择了歌曲  还是默认的歌曲


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMusicInfos = getLocalMusicList();
                if (mMusicInfos != null && !mMusicInfos.isEmpty()){
                    mMusicHandler.sendEmptyMessage(MUSIC_LIST_LOAD_SUCCESS);
                }
            }
        }).start();
        initView();//初始化视图
        initMusicReceiver();// 注册广播接受者
    }

    private void startMusicService(){
        intentService = new Intent(this, MusicService.class);
        intentService.putExtra(PARAM_MUSIC_POSITION,mCurrentMusicPosition);
        intentService.putExtra(PARAM_MUSIC_LIST, mMusicInfos);
        startService(intentService);
        conn = new MyConnection();
        bindService(intentService, conn, BIND_AUTO_CREATE);

        if (mMusicInfos == null){
            ToastUtil.showShortToastCenter("音乐列表为空，请下载音乐到本地！");
        }else {
            mDisc.setMusicDataList(mMusicInfos,mCurrentMusicPosition);
        }
    }

        private class MyConnection implements ServiceConnection {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicControl = (MusicService.MusicBind) service;//得到服务bind
//                musicControl.setCurrentPosition(mMusicPosition);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }

    private void initMusicDatas(ArrayList<MusicInfo> musicInfos,int position) {
        totalMusicNums = musicInfos.size();
        mDisc.setMusicDataList(musicInfos,position);
    }

    //打开音乐文件
    public void openMusics(View view){
        Intent intent1 = new Intent();
        intent1.setClass(this,MusicListActivity.class);
        startActivityForResult(intent1,RESULT_FROM_MUSIC_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FROM_MUSIC_LIST){
            if (resultCode == RESULT_OK){
                bundle = data.getExtras();//得到上个活动的数据
                try {
//                    isMusicIDSetted = true;
                    mMusicInfos = (ArrayList<MusicInfo>) bundle.getSerializable("musicinfo");
                    mMusicPosition = bundle.getInt("position");
                    initMusicDatas(mMusicInfos,mMusicPosition);
                    if (musicControl != null){
                        musicControl.setCurrentPosition(mMusicPosition);
                    }else {
                        ToastUtil.showShortToastCenter("音乐播放器故障，无法正常工作！");
                    }
                    mMusicHandler.sendEmptyMessage(STOP_CURRENT_MUSIC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == RESULT_CANCELED){
                ToastUtil.showShortToastCenter("取消选择歌曲！");
            }
        }

        if (requestCode == REQUEST_EQ_PARAMS){//EQ 参数选择页面得到  参数数组（纯音结果）
            //从EQ参数选择页面 得到左右耳的数据
            if (resultCode == REQUEST_AUTO_SETTING) {
                String left[] = data.getStringArrayExtra("leftear");
                String right[] = data.getStringArrayExtra("rightear");
//                for (int i = 0; i < left.length; i++) {
//                    Log.e("测试测试测试", "setEQsetting: left[]:"+left[i]);
//                    Log.e("测试测试测试", "setEQsetting: right[]:"+right[i]);
//                }
                musicControl.setEQsetting(left,right);
            }
        }
    }

    private ArrayList<MusicInfo> getLocalMusicList() {
        //获取系统的ContentResolver
        contentResolver = getContentResolver() ;
        //从数据库中获取指定列的信息
        mCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                new String[] {MediaStore.Audio.Media._ID ,
                        MediaStore.Audio.Media.TITLE , MediaStore.Audio.Media.ALBUM ,
                        MediaStore.Audio.Media.ARTIST , MediaStore.Audio.Media.DURATION ,
                        MediaStore.Audio.Media.DISPLAY_NAME , MediaStore.Audio.Media.SIZE ,
                        MediaStore.Audio.Media.DATA , MediaStore.Audio.Media.ALBUM_ID } , null ,null ,null) ;//9个关于音频文件的属性

//        List_map = new ArrayList<Map<String, String>>() ;
        if (mCursor.getCount() < 1){
//            MusicListEmptyView.setText("在您的手机上没有搜索到音频文件！");
            return null;
        }
        for (int i = 0 ; i < mCursor.getCount() ; i++) {
            Map<String, String> map = new HashMap<>();//哈希键值对
            MusicInfo musicInfo = new MusicInfo();

            //列表移动
            mCursor.moveToNext();

            //将数据装载到List<MusicInfo>中
            musicInfo.set_id(mCursor.getInt(0));//音乐的ID
            musicInfo.setTitle(mCursor.getString(1));//
            musicInfo.setAlbum(mCursor.getString(2));
            musicInfo.setArtist(mCursor.getString(3));
            musicInfo.setDuration(mCursor.getInt(4));
            musicInfo.setMusicName(mCursor.getString(5));
            musicInfo.setSize(mCursor.getInt(6));
            musicInfo.setData(mCursor.getString(7));
            //将数据装载到List<Map<String ,String>>中
            //获取本地音乐专辑图片
            String MusicImage = getAlbumArt(mCursor.getInt(8));//得到专辑图片
            //判断本地专辑的图片是否为空
            if (MusicImage == null)//本地数据库中没有专辑封面图片
            {
                //为空，用默认图片
                map.put("image", String.valueOf(R.mipmap.timg));
                musicInfo.setAlbum_id(String.valueOf(R.mipmap.timg));//设置专辑封面图片ID
            } else//本地数据库中有专辑封面图片
            {
                //不为空，设定专辑图片为音乐显示的图片
                map.put("image", MusicImage);
                musicInfo.setAlbum_id(MusicImage);
            }
            // musicInfo.setAlbum_id(mCursor.getInt(8));
            mMusicInfos.add(musicInfo);
        }
        return mMusicInfos;
    }

    /* 获取本地音乐专辑的图片*/
    private String getAlbumArt(int album_id)
    {
        String UriAlbum = "content://media/external/audio/albums" ;//URI文件地址
        String projecttion[] =  new String[] {"album_art"} ;//字符串数组  专辑
        Cursor cursor = contentResolver.query(Uri.parse(UriAlbum + File.separator + Integer.toString(album_id)) ,
                projecttion , null , null , null);//File.separator 文件分隔符  获取获得专辑 游标
        String album = null ;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0)//得到了数据  行不为0，列不为零
        {
            cursor.moveToNext() ;
            album = cursor.getString(0) ;//得到专辑名称
        }
        //关闭资源数据
        cursor.close();//关闭游标资源
        return album ;
    }

    /**
     * 注册本地广播 接收服务事件
     */
    private void initMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
        /*注册本地广播*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mDisc = findViewById(R.id.discview);
        mIvNext = findViewById(R.id.ivNext);
        mIvLast = findViewById(R.id.ivLast);
        mIvEQsetting = findViewById(R.id.ivEQ_SET);
        mIvPlayOrPause = findViewById(R.id.ivPlayOrPause);
        mTvMusicDuration = findViewById(R.id.tvCurrentTime);
        mTvTotalMusicDuration = findViewById(R.id.tvTotalTime);
        mSeekBar = findViewById(R.id.musicSeekBar);
        mRootLayout = findViewById(R.id.rootLayout);

        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        mImvVolume = mToolbar.findViewById(R.id.toolbar_imv_volume);
        mImvVolume.setOnClickListener(this);

        mDisc.setPlayInfoListener(this);
        mIvLast.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvEQsetting.setOnClickListener(this);
        mIvPlayOrPause.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvMusicDuration.setText(duration2Time(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdateSeekBarProgree();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
                startUpdateSeekBarProgress();
            }
        });

        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
    }

    /*根据时长格式化称时间文本*/
    private String duration2Time(int duration) {
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        return (min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }

    /**
     * 停止更新音乐进度条
     */
    private void stopUpdateSeekBarProgree() {
        mMusicHandler.removeMessages(MUSIC_MESSAGE);
    }

    /**
     * 开始更新进度条
     */
    private void startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgree();
        mMusicHandler.sendEmptyMessageDelayed(0,1000);
    }

    private void seekTo(int position) {
        //TODO 拖动进度条后  给服务发送广播 通知音乐播放进度
        Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO,position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /* start 此活动 是IPlay'Info 的接口实现类，该活动把自己传给自定义view 等view回调*/
    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        getSupportActionBar().setTitle(musicName);
        getSupportActionBar().setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
//        try2UpdateMusicPicBackground(musicPicRes);
    }

    @Override
    public void onMusicChanged(NewDiscView.MusicChangedStatus musicChangedStatus) {
        switch (musicChangedStatus) {
            case PLAY:
                play();//TODO 播放音乐
                break;
            case PAUSE:
                pause();//TODO 暂停播放
                break;
            case NEXT:
                next();
                break;
            case LAST:
                last();
                break;
            case STOP:
                stop();
                break;
        }
    }

    private void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        startUpdateSeekBarProgress();
    }

    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        stopUpdateSeekBarProgree();
    }

    private void stop() {
        stopUpdateSeekBarProgree();
        mIvPlayOrPause.setImageResource(R.drawable.ic_play);
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
        mSeekBar.setProgress(0);
    }

    private void next() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
        stopUpdateSeekBarProgree();
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
    }

    private void last() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
        stopUpdateSeekBarProgree();
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
    }

    private void optMusic(final String action) {
        Intent intent = new Intent(action);
        Log.e("index 值", "index 值optMusic: next() mMusicPosition"+mMusicPosition);
//        intent.putExtra(MUSIC_INDEX,mMusicPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /* 此活动是  onClickListener 的接口实现类  系统会调用*/
    @Override
    public void onClick(View v) {
        if (v == mIvPlayOrPause) {//TODO 音乐暂停或播放
            mDisc.playOrPause();
        } else if (v == mIvNext) {//下一曲
            mDisc.next();
//            Log.e("index 值：", "play: index 触发了mDisc.next()");
        } else if (v == mIvLast) {//上一曲
            mDisc.last();
        } else if (v == mIvEQsetting){
//            Intent intent = new Intent(this, PureTestHistoryListActivity.class);
            Intent intent = new Intent(this, SetupMusicHearAssistActivity.class);
            startActivityForResult(intent,REQUEST_EQ_PARAMS);
        } else if (v == mImvVolume){
            startActivity(new Intent(this,ActivityChangeVolume.class));
        }
    }
    /* end*/


    class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicService.ACTION_STATUS_MUSIC_PLAY)) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);
                int currentPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION, 0);
                mSeekBar.setProgress(currentPosition);
                if(!mDisc.isPlaying()){//TODO 音乐暂停或播放
                    mDisc.playOrPause();
                }
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_PAUSE)) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_play);
                if (mDisc.isPlaying()) {//TODO 音乐暂停或播放
                    mDisc.playOrPause();
                }
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_DURATION)) {
                int duration = intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION, 0);
                updateMusicDurationInfo(duration);
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_COMPLETE)) {
                boolean isOver = intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER, true);
                complete(isOver);
            }
        }
    }

    private void updateMusicDurationInfo(int totalDuration) {
        mSeekBar.setProgress(0);
        mSeekBar.setMax(totalDuration);
        mTvTotalMusicDuration.setText(duration2Time(totalDuration));
        mTvMusicDuration.setText(duration2Time(0));
        startUpdateSeekBarProgress();
    }

    private void complete(boolean isOver) {
        if (isOver) {
            mDisc.stop();
        } else {
            mDisc.next();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
        stopService(intentService);
        unbindService(conn);
    }
}
