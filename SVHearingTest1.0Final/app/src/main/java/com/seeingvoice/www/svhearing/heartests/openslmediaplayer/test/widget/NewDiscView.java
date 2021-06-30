package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewpager.widget.ViewPager;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.MusicInfo;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.utils.DisplayUtil;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.util.ArrayList;

public class NewDiscView extends RelativeLayout {
    private ImageView mIvNeedle;//唱针
    private ViewPager mVpContain;//滑动视图
    private MyViewPagerAdapter mViewPagerAdapter;//滑动视图适配器
    private ObjectAnimator mNeedleAnimator;//对象动画

    private ArrayList<View> mDiscLayouts = new ArrayList<>();//磁碟布局集合
    private ArrayList<MusicInfo> mMusicInfos = new ArrayList<>();//歌曲信息集合
    private ArrayList<ObjectAnimator> mDiscAnimators = new ArrayList<>();//磁碟动画集合

    /*标记ViewPager是否处于偏移的状态*/
    private boolean mViewPagerIsOffset = false;
    /*标记唱针复位后，是否需要重新偏移到唱片处*/
    private boolean mIsNeed2StartPlayAnimator = false;
    private MusicStatus musicStatus = MusicStatus.STOP;//枚举类型 默认 音乐是停止状态

    public static final int DURATION_NEEDLE_ANIAMTOR = 500;//唱针动画时长
    private NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;//唱针动画状态

    private IPlayInfo mIPlayInfo;//更新播放状态

    private int mScreenWidth, mScreenHeight;//屏幕宽度和高度
    private int count = 0;
    private int mFirstMusicPosition = 0;
    private int mTotalMusicNums = 0;
    private MusicInfo musicInfo;//每条音乐的对象
    private int lastItem = 0;//记录当前VP停留的页面

    /*唱针当前所处的状态*/
    private enum NeedleAnimatorStatus {
        /*移动时：从唱盘往远处移动*/
        TO_FAR_END,
        /*移动时：从远处往唱盘移动*/
        TO_NEAR_END,
        /*静止时：离开唱盘*/
        IN_FAR_END,
        /*静止时：贴近唱盘*/
        IN_NEAR_END
    }

    /*音乐当前的状态：只有播放、暂停、停止三种*/
    public enum MusicStatus {
        PLAY, PAUSE, STOP
    }

    /*NewDiscView需要触发的音乐切换状态：播放、暂停、上/下一首、停止*/
    public enum MusicChangedStatus {
        PLAY, PAUSE, NEXT, LAST, STOP
    }

    public void setPlayInfoListener(IPlayInfo listener) {
        this.mIPlayInfo = listener;
    }


    public NewDiscView(Context context) {
        this(context,null);
    }

    public NewDiscView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NewDiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = DisplayUtil.getScreenWidth(context);
        mScreenHeight = DisplayUtil.getScreenHeight(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initDiscBlackground();//初始化碟盘背景
//        initViewPager();//初始化滑动控件
        mVpContain = findViewById(R.id.vpDiscContain);
        mViewPagerAdapter = new MyViewPagerAdapter(mDiscLayouts);
        initNeedle();//初始化指针
        initObjectAnimator();//初始化对象动画
    }

    //初始化对象动画
    private void initObjectAnimator() {
        mNeedleAnimator = ObjectAnimator.ofFloat(mIvNeedle, View.ROTATION, DisplayUtil
                .ROTATION_INIT_NEEDLE, 0);
        mNeedleAnimator.setDuration(DURATION_NEEDLE_ANIAMTOR);
        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                /**
                 * 根据动画开始前NeedleAnimatorStatus的状态，
                 * 即可得出动画进行时NeedleAnimatorStatus的状态
                 * */
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {//动画开始前的状态
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;//唱针像 唱盘移动
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {//唱针动画结束后 把状态改为在唱盘上
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
                    int index = mVpContain.getCurrentItem();//得到VP的位置  也就是歌曲的ID
//                    Log.e("动画动画动画动画", "onAnimationEnd: index"+index);
                    playDiscAnimator(index);//播放唱盘动画
                    musicStatus = MusicStatus.PLAY;//音乐状态改为播放
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
                    if (musicStatus == MusicStatus.STOP) {
                        mIsNeed2StartPlayAnimator = true;
                    }
                }

                if (mIsNeed2StartPlayAnimator) {
                    mIsNeed2StartPlayAnimator = false;
                    /**
                     * 只有在ViewPager不处于偏移状态时，才开始唱盘旋转动画
                     * */
                    if (!mViewPagerIsOffset) {
                        /*延时500ms*/
                        NewDiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 50);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    /*播放唱盘动画*/
    private void playDiscAnimator(int index) {

        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
//        Log.e("动画动画动画动画", "playDiscAnimator ObjectAnimator"+index+"::mDiscAnimators size"+mDiscAnimators.size());
        if (objectAnimator.isPaused()) {//如是暂停状态
//            Log.e("动画动画动画动画", "playDiscAnimator isPaused()"+index+"::mDiscAnimators size"+mDiscAnimators.size());
            objectAnimator.resume();//重新提交
        } else{//如是播放状态
//            Log.e("动画动画动画动画", "playDiscAnimator 播放状态 isRunning()"+index);
            objectAnimator.start();//开始播放动画
        }

        /**
         * 唱盘动画可能执行多次，只有音乐在播放的时候，回调执行播放
         */
        if (musicStatus != MusicStatus.PLAY) {//第一次是STOP状态
            notifyMusicStatusChanged(MusicChangedStatus.PLAY);//所以告诉Activity要开始播放了
        }
        //该动画执行后，音乐状态设置为PLAY状态
    }

    /*播放动画*/
    private void playAnimator() {
        /*唱针处于远端时，直接播放动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {//默认唱针 在远端位置
            mNeedleAnimator.start();
        }
        /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
            mIsNeed2StartPlayAnimator = true;
        }
    }

    /*暂停动画*/
    private void pauseAnimator() {
        /*播放时暂停动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
            int index = mVpContain.getCurrentItem();
            pauseDiscAnimatior(index);
        }
        /*唱针往唱盘移动时暂停动画*/
        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
            mNeedleAnimator.reverse();
            /**
             * 若动画在没结束时执行reverse方法，则不会执行监听器的onStart方法，此时需要手动设置
             * */
            needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
        }
        /**
         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令
         * */
        if (musicStatus == MusicStatus.STOP) {
            notifyMusicStatusChanged(MusicChangedStatus.STOP);
        }else if (musicStatus == MusicStatus.PAUSE) {
            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
        }
    }

    /*暂停唱盘动画*/
    private void pauseDiscAnimatior(int index) {
        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
        objectAnimator.pause();
        mNeedleAnimator.reverse();//重复模式 repeatMode 有两种：从头开始(RESTART ) 和 反向开始(REVERSE)
    }

    public void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus) {
        if (mIPlayInfo != null) {
            mIPlayInfo.onMusicChanged(musicChangedStatus);
        }
    }

    //初始化唱针
    private void initNeedle() {
        mIvNeedle = findViewById(R.id.ivNeedle);

        int needleWidth = (int) (DisplayUtil.SCALE_NEEDLE_WIDTH * mScreenWidth);
        int needleHeight = (int) (DisplayUtil.SCALE_NEEDLE_HEIGHT * mScreenHeight);

        /*设置手柄的外边距为负数，让其隐藏一部分*/
        int marginTop = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_TOP * mScreenHeight) * -1;
        int marginLeft = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_LEFT * mScreenWidth);

        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .ic_needle);
        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, needleWidth, needleHeight, false);

        LayoutParams layoutParams = (LayoutParams) mIvNeedle.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);

        int pivotX = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_X * mScreenWidth);
        int pivotY = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_Y * mScreenWidth);

        mIvNeedle.setPivotX(pivotX);
        mIvNeedle.setPivotY(pivotY);
        mIvNeedle.setRotation(DisplayUtil.ROTATION_INIT_NEEDLE);
        mIvNeedle.setImageBitmap(bitmap);
        mIvNeedle.setLayoutParams(layoutParams);
    }

    private void initListViews(int countItem) {
        View discLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,
                mVpContain, false);
        ImageView disc = discLayout.findViewById(R.id.ivDisc);
        disc.setImageDrawable(getDiscDrawable(R.raw.ic_music1));//标记1 得到音乐专辑图片
        mDiscAnimators.add(getDiscObjectAnimator(disc, 1));
        Log.e("动画动画动画", "initListViews: mDiscAnimators size:"+mDiscAnimators.size());
        mDiscLayouts.add(discLayout);

        mViewPagerAdapter.setListViews(mDiscLayouts);
        mViewPagerAdapter.notifyDataSetChanged();

        musicInfo = mMusicInfos.get(mFirstMusicPosition+countItem);//标记2 mMusicDatas 是 MusicData 列表
        if (mIPlayInfo != null) {
            mIPlayInfo.onMusicInfoChanged(musicInfo.getMusicName(), musicInfo.getArtist());//标记2 MusicInfo 音乐名 音乐作者
//            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());//标记3 资源路径  可以给一个固定的图片
            mIPlayInfo.onMusicPicChanged(R.raw.ic_music1);//标记3 资源路径  可以给一个固定的图片
        }
    }

    private void initViewPager() {
        mVpContain.setOverScrollMode(View.OVER_SCROLL_NEVER);
        lastItem = 0;
        mVpContain.addOnPageChangeListener(listener);

        mVpContain.setAdapter(mViewPagerAdapter);
        LayoutParams layoutParams = (LayoutParams) mVpContain.getLayoutParams();
        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        layoutParams.setMargins(0, marginTop, 0, 0);
        mVpContain.setLayoutParams(layoutParams);
    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == mVpContain.getAdapter().getCount()-1) {// 滑动到最后一页
                if (position + 1 < mTotalMusicNums){
                    initListViews(position);// listViews添加数据
                }else {
                    ToastUtil.showShortToastCenter("当前为最后一曲");
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            //不知道为啥 onPageSelected  在setCurrentItem后被调用两次
            //所以这里我用 count计数 第二次被调用的时候 才执行
            resetOtherDiscAnimation(position);
            notifyMusicPicChanged(position);

            Log.e("testtest", "onPageSelected----position=="+position+"-------currentItem=="+lastItem);
            if (position > lastItem) {
                notifyMusicStatusChanged(MusicChangedStatus.NEXT);
            } else if (position < lastItem){
                notifyMusicStatusChanged(MusicChangedStatus.LAST);
            }
            lastItem = position;
            if (position == 0){
                ToastUtil.showShortToastCenter("当前为第一曲");
            }
            if (position < mTotalMusicNums){
                musicInfo = mMusicInfos.get(mFirstMusicPosition + position);//标记2 mMusicDatas 是 MusicData 列表
                if (mIPlayInfo != null) {
                    mIPlayInfo.onMusicInfoChanged(musicInfo.getMusicName(), musicInfo.getArtist());//标记2 MusicInfo 音乐名 音乐作者
//            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());//标记3 资源路径  可以给一个固定的图片
                    mIPlayInfo.onMusicPicChanged(R.raw.ic_music1);//标记3 资源路径  可以给一个固定的图片
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            doWithAnimatorOnPageScroll(state);
        }
    };

    /**
     * 取消其他页面上的动画，并将图片旋转角度复原
     */
    private void resetOtherDiscAnimation(int position) {
        for (int i = 0; i < mDiscLayouts.size(); i++) {
            if (position == i) continue;
            mDiscAnimators.get(position).cancel();
            ImageView imageView = mDiscLayouts.get(i).findViewById(R.id.ivDisc);
            imageView.setRotation(0);
        }
    }

    public void notifyMusicPicChanged(int position) {
        if (mIPlayInfo != null) {
//            MusicData musicData = mMusicDatas.get(position);
//            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());
            //TODO 改变图片 以MusicInfo路径的方式
        }
    }

    private void doWithAnimatorOnPageScroll(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING: {
                mViewPagerIsOffset = false;
                if (musicStatus == MusicStatus.PLAY) {
                    playAnimator();
                }
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING: {
                mViewPagerIsOffset = true;
                pauseAnimator();
                break;
            }
        }
    }

    /**
     * 设置数据源
     * @param musicInfoList
     * @param position
     */
    public void setMusicDataList(ArrayList<MusicInfo> musicInfoList,int position) {
        mTotalMusicNums = musicInfoList == null ? 0: musicInfoList.size() - position;
        mMusicInfos.clear();
        mDiscLayouts.clear();
        mDiscAnimators.clear();
        mMusicInfos.addAll(musicInfoList);

        View discLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,mVpContain, false);
        ImageView disc = discLayout.findViewById(R.id.ivDisc);
        disc.setImageDrawable(getDiscDrawable(R.raw.ic_music1));//标记1 得到音乐专辑图片
        mDiscAnimators.add(getDiscObjectAnimator(disc, 1));
        mDiscLayouts.add(discLayout);

        mViewPagerAdapter.setListViews(mDiscLayouts);
        mViewPagerAdapter.notifyDataSetChanged();

        mFirstMusicPosition = position;
        musicInfo = mMusicInfos.get(mFirstMusicPosition);//标记2 mMusicDatas 是 MusicData 列表
        if (mIPlayInfo != null) {
            mIPlayInfo.onMusicInfoChanged(musicInfo.getMusicName(), musicInfo.getArtist());//标记2 MusicInfo 音乐名 音乐作者
//            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());//标记3 资源路径  可以给一个固定的图片
            mIPlayInfo.onMusicPicChanged(R.raw.ic_music1);//标记3 资源路径  可以给一个固定的图片
        }
        if (listener != null){
            mVpContain.removeOnPageChangeListener(listener);
        }
        initViewPager();
    }

    private ObjectAnimator getDiscObjectAnimator(ImageView disc, final int i) {
        //旋转动画，旋转一周
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);//动画持续时间20秒
        objectAnimator.setInterpolator(new LinearInterpolator());//插值器 加速度是线性的

        return objectAnimator;
    }

    /**
     * 得到唱盘图片
     * 唱盘图片由空心圆盘及音乐专辑图片“合成”得到
     */
    private Drawable getDiscDrawable(int musicPicRes) {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);

        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc), discSize, discSize, false);
        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize,musicPicRes);
        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapMusicPic);

        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundMusicDrawable;
        drawables[1] = discDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);//距离磁盘背景图的边距
        //调整专辑图片的四周边距，让其显示在正中
        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
                musicPicMargin);

        return layerDrawable;
    }

    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(),musicPicRes,options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                musicPicRes, options), musicPicSize, musicPicSize, true);
    }


        //初始化碟盘背景
    private void initDiscBlackground() {
        ImageView mDiscBlackground = (ImageView) findViewById(R.id.ivDiscBlackgound);
        mDiscBlackground.setImageDrawable(getDiscBlackgroundDrawable());

        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        LayoutParams layoutParams = (LayoutParams) mDiscBlackground
                .getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);

        mDiscBlackground.setLayoutParams(layoutParams);
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBlackgroundDrawable() {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Log.e("getDiscBlackground", "getDiscBlackgroundDrawable: "+discSize+"-------");
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc_blackground), discSize, discSize, false);
        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapDisc);
        return roundDiscDrawable;
    }

    private void play() {
        playAnimator();
    }

    private void pause() {
        musicStatus = MusicStatus.PAUSE;
        pauseAnimator();
    }

    public void stop() {
        musicStatus = MusicStatus.STOP;
        pauseAnimator();
    }

    public void playOrPause() {
        if (musicStatus == MusicStatus.PLAY) {
            pause();
        } else {//从这里开始，默认音乐是停止 状态
            play();
        }
    }

    public void next() {//TODO 判断条件有问题
        int currentItem = mVpContain.getCurrentItem();
        if (currentItem == mTotalMusicNums - 1) {
            Toast.makeText(getContext(), "已经到达最后一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            mVpContain.setCurrentItem(currentItem + 1, true);
        }
    }

    public void last() {
        int currentItem = mVpContain.getCurrentItem();
        if (currentItem == 0) {
            Toast.makeText(getContext(), "已经到达第一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            mVpContain.setCurrentItem(currentItem - 1, true);
        }
    }

    public boolean isPlaying() {
        return musicStatus == MusicStatus.PLAY;
    }

    private void selectMusicWithButton() {
        if (musicStatus == MusicStatus.PLAY) {
            mIsNeed2StartPlayAnimator = true;
            pauseAnimator();
        } else if (musicStatus == MusicStatus.PAUSE) {
            play();
        }
    }
}
