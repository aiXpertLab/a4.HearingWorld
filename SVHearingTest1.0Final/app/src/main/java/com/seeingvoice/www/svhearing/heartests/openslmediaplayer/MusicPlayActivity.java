//package com.seeingvoice.www.svhearing.heartests.openslmediaplayer;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.SeekBar;
//
//import androidx.annotation.Nullable;
//
//import com.seeingvoice.www.svhearing.R;
//import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
//import com.seeingvoice.www.svhearing.util.ToastUtil;
//
//import java.util.List;
//
///**
// * Created by initializing on 2018/4/25.
// */
//
//public class MusicPlayActivity extends TopBarBaseActivity implements View.OnClickListener {
//
//    private static final String TAG = MusicPlayActivity.class.getName();
//    private static final int RESULT_FROM_MUSIC_LIST = 700;
//    //音乐信息封装对象
//    private List<MusicInfo> musicInfosList = null;
//    private int currentProgress;//歌曲当前位置
//    private int totalProgress;//歌曲总长度
//    private int musicPosition;
//    //Bundle来获取启动Activity传递的参数
//    private Bundle bundle;
//    private Button playBtn,OpenMusicListBtn,openHistoryListBtn,fastSetEQBtn;
//    private SeekBar seekBar;
//    private static final int UPDATE_PROGRESS = 0;
//    private MyConnection conn;
//    private MusicService.MusicBind musicControl;
//    private Intent intentService;
//
//    //使用handler定时更新进度条
//     private Handler handler = new Handler() {
//         @Override
//         public void handleMessage(Message msg) {
//             switch (msg.what) {
//                 case UPDATE_PROGRESS:
//                     updateProgress();
//                     break;
//             }
//         }
//     };
//
//    @Override
//    protected int getConentView() {
//        return R.layout.activity_opensles_musicplayer;
//    }
//
//    @Override
//    protected void init(Bundle savedInstanceState) {
//        setTitle("助听播放器");
//        setTitleBack(true);
//
//        setToolBarMenuOne("", R.mipmap.share_icon, null);
//        setToolBarMenuTwo("", R.mipmap.return_icon,null);
//        playBtn = findViewById(R.id.play);
//        seekBar = findViewById(R.id.sb);
//
//        intentService = new Intent(this, MusicService.class);
//        conn = new MyConnection();
//        //使用混合的方法开启服务，既可保持在后台运行，又可以控制服务的bind
//        startService(intentService);
//        bindService(intentService, conn, BIND_AUTO_CREATE);
//        openHistoryListBtn = findViewById(R.id.btn_history_eq_set);
//        fastSetEQBtn = findViewById(R.id.btn_the_last_eq_set);
//        openHistoryListBtn.setOnClickListener(this);
//        fastSetEQBtn.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //进入到界面后开始更新进度条
//         if (musicControl != null){
//             handler.sendEmptyMessage(UPDATE_PROGRESS);
//         }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //停止更新进度条的进度
//        handler.removeCallbacksAndMessages(null);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_history_eq_set:
//                toNextActivity(null,this,SetupMusicHearAssistActivity.class);
//                break;
//            case R.id.btn_the_last_eq_set:
//                break;
//        }
//    }
//
//    private class MyConnection implements ServiceConnection{
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //获得service中的MyBinder
//             musicControl = (MusicService.MusicBind) service;//得到服务bind
//             //更新按钮的文字
////             updatePlayText();
//             //设置进度条的最大值
////             seekBar.setMax(musicControl.getDuration());
////             //设置进度条的进度
////             seekBar.setProgress(musicControl.getCurrentPosition());
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//        }
//    }
//
////    更新按钮的文字
//     public void updatePlayText() {
//        if (musicControl.isPlaying()) {
//            playBtn.setText("暂停");
//            handler.sendEmptyMessage(UPDATE_PROGRESS);
//        } else {
//            playBtn.setText("播放");
//        }
//    }
//
//    //调用MyBinder中的play()方法
//    public void play(View view) {
//        musicControl.play();
//        updatePlayText();
//    }
//
//    //打开音乐文件
//    public void openMusics(View view){
//        Intent intent1 = new Intent();
//        intent1.setClass(this,MusicListActivity.class);
//        startActivityForResult(intent1,RESULT_FROM_MUSIC_LIST);
//    }
//
//    //设置均衡器
//    public void setEQ(View view){
//        toNextActivity(null,this,TenEQsActivity.class);
//    }
//
//    //更新进度条
//     private void updateProgress() {
//        currentProgress = musicControl.getCurrentPosition();
//        if (currentProgress != totalProgress){
//            seekBar.setProgress(currentProgress);
//            //使用Handler每500毫秒更新一次进度条
//            handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
//        }else {
//            seekBar.setProgress(0);
//            updatePlayText();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode,  resultCode, data);
//        if (requestCode == RESULT_FROM_MUSIC_LIST){
//            //TODO 传送回音乐列表音乐信息集合，和位置，给当前页面播放
//            if (resultCode == RESULT_OK){
//                bundle = data.getExtras();//得到上个活动的数据
//                try {
//                    musicInfosList = (List<MusicInfo>) bundle.getSerializable("musicinfo");
//                    musicPosition = bundle.getInt("position");
//                    if (!musicInfosList.isEmpty()){
//                        musicControl.setMusicUri(musicInfosList.get(musicPosition).getData());
//                        totalProgress = musicControl.getDuration();
//                        seekBar.setMax(totalProgress);
//                    }else {
//                        ToastUtil.showShortToastCenter("音乐列表为空！");
//                    }
//                    /* 用输出来检测是否成功获取参数*/
//                    Log.e(TAG, "position is " + musicPosition + "" +"\ninit: MUSICINFOLIST DATA IS"+musicInfosList.get(musicPosition).getData());
////                    Log.e(TAG, "2222222222position is position isposition isposition isposition isposition is" );
//                } catch (Exception e) {
//                    System.out.println("获取音乐信息失败");
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //退出应用后与service解除绑定
//        unbindService(conn);
//        stopService(intentService);
//    }
//}
