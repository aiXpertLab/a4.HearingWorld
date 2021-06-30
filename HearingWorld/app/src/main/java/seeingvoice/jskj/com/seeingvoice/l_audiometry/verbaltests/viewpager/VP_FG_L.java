package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.viewpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.MenuItem;
import android.widget.LinearLayout;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_AboutUs;
import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.BroadCastManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Date:2019/2/11
 * Time:17:06
 * auther:zyy
 */
public class VP_FG_L extends MyTopBar {
    public static VP_FG_L instance = null;
    private ViewPager viewPager;    /** viewpager装fragments*/
    private LinearLayout dotHorizontal;    /** 指示器容器*/
    private Intent intent;    //意图
    private TextToSpeech mTextToSpeech;    /** TTS对象  TEXT to speach*/
    private VP_FG_Receiver mVP_FG_Receiver;    /** 广播接受者*/
    private Runnable runnable;
    private int volumn_value = 8;    /** 选定好的音量值*/
    private List<Fragment> list;
    public SharedPreferences Verbal_Volume_SP;    /** 在离开当前fragment之前，数据持久化存储设置好的音量值*/
    /** 默认一进第二个fragment 就播放TTS*/
    private boolean isPlaying;

    /** handler 管理主线程的消息队列*/
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0003:
                    final Bundle params = new Bundle();                    /** mTextToSpeech.speak的参数*/
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,(float) volumn_value/15);                    /** 设置 TextToSpeech.speak的，音量 float类型的*/
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            mTextToSpeech.speak(createRadomNumber(),TextToSpeech.QUEUE_ADD,params,"test");
                            mHandler.postDelayed(this,800);
                        }
                    };
                    mHandler.postDelayed(runnable,800);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_vp_fg;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("言语测试");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null, VP_FG_L.this, L_AboutUs.class);
            }
        });
        viewPager = findViewById(R.id.vp);        //获取VIEWPAGER实例
        dotHorizontal = findViewById(R.id.dot_horizontal);        /** 指示器 小圆点的容器*/
        /**
         * 装载好fragments
         */
        initFragments();
        instance = this;
        Verbal_Volume_SP = getSharedPreferences("volumePrefName",Context.MODE_PRIVATE);

        /** 从练习用的NumKeyBoard Activity 结束后，跳转到第四个fragment*/
        intent = getIntent();
        if (intent.getStringExtra("restart") != null && intent.getStringExtra("restart").equals("0x0009")) {
            viewPager.setCurrentItem(3);
        }

        /** 获得音量设置页面  的音量（从Application 分发出的消息）*/
        MyApp.setOnHandlerListener(new MyApp.HandlerListener() {
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                    case 347:
                        //获取bundle对象的值
                        Bundle b = msg.getData();
                        volumn_value = b.getInt("verbal_music_volume");
                        initVolume(volumn_value);
                        break;
                }
            }
        });


        /**
         * 接收广播,广播过滤器
         * */
        receiverBroadcast();
    }

    private void initVolume(int volume) {
        System.out.println(volume);
        /* 获得 音频管理器服务对象*/
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        /* 如果音频管理器对象不为空，则用传来的音量值开始调节  STREAM_MUSIC的音量*/
        if (am != null){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume,AudioManager.FLAG_PLAY_SOUND);
        }
    }

    /**
     * ViewPager 运行时接收广播
     */
    private void receiverBroadcast() {
        IntentFilter filter = new IntentFilter();
        /** 指示器 滑动到第二个fragment，开启音量调节功能，此时实例化TTS*/
        filter.addAction("SKIPTO-VOLUMN-SET-ON");
        /** 指示器 滑动离开第二个fragment，关闭音量调节功能，销毁TTS对象，释放资源*/
        filter.addAction("SKIPTO-VOLUMN-SET-OFF");
        /** 广播过滤条件写好后，开始动态注册广播，ondetrory中同时需要销毁动态注册的广播*/
        mVP_FG_Receiver = new VP_FG_Receiver();
        /** */
        BroadCastManager.getInstance().registerReceiver(this,mVP_FG_Receiver, filter);
    }

    class VP_FG_Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "SKIPTO-VOLUMN-SET-ON":
                    // 指示器滑动到第二个fragment了
                    isPlaying = true;
                    speakout(0x0003);
                    break;
                case "SKIPTO-VOLUMN-SET-OFF":
                    isPlaying = false;
                // 指示器离开了第二个fragment了
                    /** 在离开当前fragment之前，数据持久化存储设置好的音量值*/
                    SharedPreferences.Editor editor = Verbal_Volume_SP.edit();
                    editor.putInt("volume",volumn_value);
                    editor.commit();

                    /**切换到其他页面后，关闭这个异步任务，关闭定时器（如果需要关闭该定时器调用），不然会报错*/
                    releaseTTS();
                    break;
            }
        }
    }

        /**
         * ViewPager 滑动到调节音量页面fragment时，实例化mTextToSpeech，成功后给Handler 发送消息，开启mTextToSpeech.speak
         */
        private void speakout(final int flag) {
            mTextToSpeech = new TextToSpeech(VP_FG_L.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        //设置朗读语言
                        int supported = mTextToSpeech.setLanguage(Locale.CHINA);
                        if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                            ToastUtil.showShortToastCenter("不支持汉语");
                        }else {
                            /* 给mHandler发送消息，触发 mTextToSpeech.speak开始干活*/
                            Message msg = new Message();
                            msg.what = flag;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            });
        }

        /**
         * 装载好fragments
         */
        private void initFragments() {
            list= new ArrayList<>();
            Bundle bundle1 = new Bundle();            //bandle 绑定数据
            bundle1.putString("Title", "言语测试简介");
            bundle1.putInt("pager_num", 1);
            Fragment fg1 = FragmentView.newInstance(bundle1);

            Bundle bundle2 = new Bundle();
            bundle2.putString("Title", "音量调节");
            bundle2.putInt("pager_num", 2);
            Fragment fg2 = FragmentView.newInstance(bundle2);

            Bundle bundle3 = new Bundle();
            bundle3.putString("Title", "练习");
            bundle3.putInt("pager_num", 3);
            Fragment fg3 = FragmentView.newInstance(bundle3);

            Bundle bundle4 = new Bundle();
            bundle4.putString("Title", "开始测试");
            bundle4.putInt("pager_num", 4);
            Fragment fg4 = FragmentView.newInstance(bundle4);

            list.add(fg1);
            list.add(fg2);
            list.add(fg3);
            list.add(fg4);

            viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
            /** 指示器   目的是监听Viewpager 页面改变，参数是  ViewPager.OnPageChangeListener  接口的实现类对象，相关操作在类中定义*/
            viewPager.addOnPageChangeListener(new PageIndicator(VP_FG_L.this, dotHorizontal, 4));
        }

    /** 随机生成三个0到9之间的整数并保存再数组中*/
    private String createRadomNumber() {
        return String.valueOf((int) (Math.random()*10)).trim();
    }


    /**
     * 重写返回键的监听事件
     * */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            DisclaimerActivity.instance.finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onDestroy() {
        if (mVP_FG_Receiver != null) {
            BroadCastManager.getInstance().unregisterReceiver(this,mVP_FG_Receiver);
        }
        super.onDestroy();
        /**切换到其他页面后，关闭这个异步任务，关闭定时器（如果需要关闭该定时器调用），不然会报错*/
        releaseTTS();
    }

    private void releaseTTS() {
        if (mHandler !=null){
            mHandler.removeCallbacks(runnable);
        }
        if (mTextToSpeech != null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
}
