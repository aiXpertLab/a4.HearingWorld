//package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget;
//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.LayerDrawable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AccelerateInterpolator;
//import android.view.animation.LinearInterpolator;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.core.graphics.drawable.RoundedBitmapDrawable;
//import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.seeingvoice.www.svhearing.R;
//import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.MusicInfo;
//import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.utils.DisplayUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by zyy on 2020/1/29.
// * 子控件包括：底盘、唱针、ViewPager等
// * 底盘和唱针均用ImageView实现，然后使用ViewPager加载ImageView实现唱片的切换。
// */
//
//public class DiscView extends RelativeLayout {
//
//    private ImageView mIvNeedle;//唱针
//    private ViewPager mVpContain;//滑动视图
//    private ViewPagerAdapter mViewPagerAdapter;//滑动视图适配器
//    private ObjectAnimator mNeedleAnimator;//对象动画  唱针
//
//    private List<View> mDiscLayouts = new ArrayList<>();//磁碟布局集合
//
////    private List<MusicData> mMusicDatas = new ArrayList<>();//歌曲信息集合
//    private List<MusicInfo> mMusicInfos = new ArrayList<>();//歌曲信息集合
//    private List<ObjectAnimator> mDiscAnimators = new ArrayList<>();//磁碟动画集合
//    /*标记ViewPager是否处于偏移的状态*/
//    private boolean mViewPagerIsOffset = false;
//
//    /*标记唱针复位后，是否需要重新偏移到唱片处*/
//    private boolean mIsNeed2StartPlayAnimator = false;
//    private MusicStatus musicStatus = MusicStatus.STOP;//枚举类型
//
//    public static final int DURATION_NEEDLE_ANIAMTOR = 500;//唱针动画时长
//    private NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;//唱针动画状态
//
//    private IPlayInfo mIPlayInfo;//更新播放状态
//
//    private int mScreenWidth, mScreenHeight;//屏幕宽度和高度
//
//    /*唱针当前所处的状态*/
//    private enum NeedleAnimatorStatus {
//        /*移动时：从唱盘往远处移动*/
//        TO_FAR_END,
//        /*移动时：从远处往唱盘移动*/
//        TO_NEAR_END,
//        /*静止时：离开唱盘*/
//        IN_FAR_END,
//        /*静止时：贴近唱盘*/
//        IN_NEAR_END
//    }
//
//    /*音乐当前的状态：只有播放、暂停、停止三种*/
//    public enum MusicStatus {
//        PLAY, PAUSE, STOP
//    }
//
//    /*DiscView需要触发的音乐切换状态：播放、暂停、上/下一首、停止*/
//    public enum MusicChangedStatus {
//        PLAY, PAUSE, NEXT, LAST, STOP
//    }
//
//    public DiscView(Context context) {
//        this(context, null);
//    }
//
//    public DiscView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mScreenWidth = DisplayUtil.getScreenWidth(context);
//        mScreenHeight = DisplayUtil.getScreenHeight(context);
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        initDiscBlackground();//初始化碟盘背景
//        initViewPager();//初始化滑动控件
//        initNeedle();//初始化唱针
//        initObjectAnimator();//初始化对象动画
//    }
//
//    //初始化碟盘背景
//    private void initDiscBlackground() {
//        ImageView mDiscBlackground = (ImageView) findViewById(R.id.ivDiscBlackgound);
//        mDiscBlackground.setImageDrawable(getDiscBlackgroundDrawable());
//
//        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
//        LayoutParams layoutParams = (LayoutParams) mDiscBlackground
//                .getLayoutParams();
//        layoutParams.setMargins(0, marginTop, 0, 0);
//
//        mDiscBlackground.setLayoutParams(layoutParams);
//    }
//
//    //初始化滑动控件
//    private void initViewPager() {
//        mViewPagerAdapter = new ViewPagerAdapter();
//        mVpContain = (ViewPager) findViewById(R.id.vpDiscContain);
//        mVpContain.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        mVpContain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            int lastPositionOffsetPixels = 0;
//            int currentItem = 0;
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int
//                    positionOffsetPixels) {
//                //左滑
//                if (lastPositionOffsetPixels > positionOffsetPixels) {
//                    if (positionOffset < 0.5) {
//                        notifyMusicInfoChanged(position);
//                    } else {
//                        notifyMusicInfoChanged(mVpContain.getCurrentItem());
//                    }
//                }
//                //右滑
//                else if (lastPositionOffsetPixels < positionOffsetPixels) {
//                    if (positionOffset > 0.5) {
//                        notifyMusicInfoChanged(position + 1);
//                    } else {
//                        notifyMusicInfoChanged(position);
//                    }
//                }
//                lastPositionOffsetPixels = positionOffsetPixels;
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                resetOtherDiscAnimation(position);
//                notifyMusicPicChanged(position);
//                if (position > currentItem) {
//                    notifyMusicStatusChanged(MusicChangedStatus.NEXT);
//                } else {
//                    notifyMusicStatusChanged(MusicChangedStatus.LAST);
//                }
//                currentItem = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                doWithAnimatorOnPageScroll(state);
//            }
//        });
//        mVpContain.setAdapter(mViewPagerAdapter);
//
//        LayoutParams layoutParams = (LayoutParams) mVpContain.getLayoutParams();
//        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
//        layoutParams.setMargins(0, marginTop, 0, 0);
//        mVpContain.setLayoutParams(layoutParams);
//    }
//
//    /**
//     * 取消其他页面上的动画，并将图片旋转角度复原
//     */
//    private void resetOtherDiscAnimation(int position) {
//        for (int i = 0; i < mDiscLayouts.size(); i++) {
//            if (position == i) continue;
//            mDiscAnimators.get(position).cancel();
//            ImageView imageView = (ImageView) mDiscLayouts.get(i).findViewById(R.id.ivDisc);
//            imageView.setRotation(0);
//        }
//    }
//
//    private void doWithAnimatorOnPageScroll(int state) {
//        switch (state) {
//            case ViewPager.SCROLL_STATE_IDLE:
//            case ViewPager.SCROLL_STATE_SETTLING: {
//                mViewPagerIsOffset = false;
//                if (musicStatus == MusicStatus.PLAY) {
//                    playAnimator();
//                }
//                break;
//            }
//            case ViewPager.SCROLL_STATE_DRAGGING: {
//                mViewPagerIsOffset = true;
//                pauseAnimator();
//                break;
//            }
//        }
//    }
//
//    //初始化唱针
//    private void initNeedle() {
//        mIvNeedle = (ImageView) findViewById(R.id.ivNeedle);
//
//        int needleWidth = (int) (DisplayUtil.SCALE_NEEDLE_WIDTH * mScreenWidth);
//        int needleHeight = (int) (DisplayUtil.SCALE_NEEDLE_HEIGHT * mScreenHeight);
//
//        /*设置手柄的外边距为负数，让其隐藏一部分*/
//        int marginTop = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_TOP * mScreenHeight) * -1;
//        int marginLeft = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_LEFT * mScreenWidth);
//
//        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
//                .ic_needle);
//        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, needleWidth, needleHeight, false);
//
//        LayoutParams layoutParams = (LayoutParams) mIvNeedle.getLayoutParams();
//        layoutParams.setMargins(marginLeft, marginTop, 0, 0);
//
//        int pivotX = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_X * mScreenWidth);
//        int pivotY = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_Y * mScreenWidth);
//
//        mIvNeedle.setPivotX(pivotX);
//        mIvNeedle.setPivotY(pivotY);
//        mIvNeedle.setRotation(DisplayUtil.ROTATION_INIT_NEEDLE);
//        mIvNeedle.setImageBitmap(bitmap);
//        mIvNeedle.setLayoutParams(layoutParams);
//    }
//
//    //初始化对象动画
//    private void initObjectAnimator() {
//        mNeedleAnimator = ObjectAnimator.ofFloat(mIvNeedle, View.ROTATION, DisplayUtil
//                .ROTATION_INIT_NEEDLE, 0);
//        mNeedleAnimator.setDuration(DURATION_NEEDLE_ANIAMTOR);
//        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
//        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                /**
//                 * 根据动画开始前NeedleAnimatorStatus的状态，
//                 * 即可得出动画进行时NeedleAnimatorStatus的状态
//                 * */
//                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
//                    needleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;
//                } else if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
//                    needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//
//                if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
//                    needleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
//                    int index = mVpContain.getCurrentItem();
//                    playDiscAnimator(index);
//                    musicStatus = MusicStatus.PLAY;
//                } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
//                    needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
//                    if (musicStatus == MusicStatus.STOP) {
//                        mIsNeed2StartPlayAnimator = true;
//                    }
//                }
//
//                if (mIsNeed2StartPlayAnimator) {
//                    mIsNeed2StartPlayAnimator = false;
//                    /**
//                     * 只有在ViewPager不处于偏移状态时，才开始唱盘旋转动画
//                     * */
//                    if (!mViewPagerIsOffset) {
//                        /*延时500ms*/
//                        DiscView.this.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                playAnimator();
//                            }
//                        }, 50);
//                    }
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//    }
//
//    public void setPlayInfoListener(IPlayInfo listener) {
//        this.mIPlayInfo = listener;
//    }
//
//    /*得到唱盘背后半透明的圆形背景*/
//    private Drawable getDiscBlackgroundDrawable() {
//        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
//        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
//                .drawable.ic_disc_blackground), discSize, discSize, false);
//        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
//                (getResources(), bitmapDisc);
//        return roundDiscDrawable;
//    }
//
//    /**
//     * 得到唱盘图片
//     * 唱盘图片由空心圆盘及音乐专辑图片“合成”得到
//     */
//    private Drawable getDiscDrawable(int musicPicRes) {
//        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
//        int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);
//
//        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
//                .drawable.ic_disc), discSize, discSize, false);
//        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize,musicPicRes);
//        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
//        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
//                (getResources(), bitmapMusicPic);
//
//        //抗锯齿
//        discDrawable.setAntiAlias(true);
//        roundMusicDrawable.setAntiAlias(true);
//
//        Drawable[] drawables = new Drawable[2];
//        drawables[0] = roundMusicDrawable;
//        drawables[1] = discDrawable;
//
//        LayerDrawable layerDrawable = new LayerDrawable(drawables);
//        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
//                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);//距离磁盘背景图的边距
//        //调整专辑图片的四周边距，让其显示在正中
//        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
//                musicPicMargin);
//
//        return layerDrawable;
//    }
//
//    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//
//        BitmapFactory.decodeResource(getResources(),musicPicRes,options);
//        int imageWidth = options.outWidth;
//
//        int sample = imageWidth / musicPicSize;
//        int dstSample = 1;
//        if (sample > dstSample) {
//            dstSample = sample;
//        }
//        options.inJustDecodeBounds = false;
//        //设置图片采样率
//        options.inSampleSize = dstSample;
//        //设置图片解码格式
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//
//        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
//                musicPicRes, options), musicPicSize, musicPicSize, true);
//    }
//
//    public void setMusicDataList(List<MusicInfo> musicInfoList,int position) {
//        if (musicInfoList.isEmpty()) return;
//        mDiscLayouts.clear();
//        mMusicInfos.clear();
//        mDiscAnimators.clear();
//        mMusicInfos.addAll(musicInfoList);
//        int i = 0;
////        for (MusicInfo musicInfo : mMusicInfos) {
////            View discLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,
////                    mVpContain, false);
////
////            ImageView disc = (ImageView) discLayout.findViewById(R.id.ivDisc);
//////            disc.setImageDrawable(getDiscDrawable(musicData.getMusicPicRes()));//标记1 得到音乐专辑图片
////            disc.setImageDrawable(getDiscDrawable(R.raw.ic_music1));//标记1 得到音乐专辑图片
////
////            mDiscAnimators.add(getDiscObjectAnimator(disc, i++));
////            mDiscLayouts.add(discLayout);
////        }
//        View discLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,
//        mVpContain, false);
//
//        ImageView disc = (ImageView) discLayout.findViewById(R.id.ivDisc);
////            disc.setImageDrawable(getDiscDrawable(musicData.getMusicPicRes()));//标记1 得到音乐专辑图片
//        disc.setImageDrawable(getDiscDrawable(R.raw.ic_music1));//标记1 得到音乐专辑图片
//
//        mDiscAnimators.add(getDiscObjectAnimator(disc, 1));
//        mDiscLayouts.add(discLayout);
//        mViewPagerAdapter.notifyDataSetChanged();
//
//        MusicInfo musicInfo = mMusicInfos.get(position);//标记2 mMusicDatas 是 MusicData 列表
//        if (mIPlayInfo != null) {
//            mIPlayInfo.onMusicInfoChanged(musicInfo.getMusicName(), musicInfo.getArtist());//标记2 MusicInfo 音乐名 音乐作者
////            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());//标记3 资源路径  可以给一个固定的图片
//            mIPlayInfo.onMusicPicChanged(R.raw.ic_music1);//标记3 资源路径  可以给一个固定的图片
//        }
//    }
//
//    private ObjectAnimator getDiscObjectAnimator(ImageView disc, final int i) {
//        //旋转动画，旋转一周
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
//        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        objectAnimator.setDuration(20 * 1000);//动画持续时间20秒
//        objectAnimator.setInterpolator(new LinearInterpolator());//插值器 加速度是线性的
//
//        return objectAnimator;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//    }
//
//    /*播放动画*/
//    private void playAnimator() {
//        /*唱针处于远端时，直接播放动画*/
//        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
//            mNeedleAnimator.start();
//        }
//        /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
//        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
//            mIsNeed2StartPlayAnimator = true;
//        }
//    }
//
//    /*暂停动画*/
//    private void pauseAnimator() {
//        /*播放时暂停动画*/
//        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
//            int index = mVpContain.getCurrentItem();
//            pauseDiscAnimatior(index);
//        }
//        /*唱针往唱盘移动时暂停动画*/
//        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
//            mNeedleAnimator.reverse();
//            /**
//             * 若动画在没结束时执行reverse方法，则不会执行监听器的onStart方法，此时需要手动设置
//             * */
//            needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
//        }
//        /**
//         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令
//         * */
//        if (musicStatus == MusicStatus.STOP) {
//            notifyMusicStatusChanged(MusicChangedStatus.STOP);
//        }else if (musicStatus == MusicStatus.PAUSE) {
//            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
//        }
//    }
//
//    /*播放唱盘动画*/
//    private void playDiscAnimator(int index) {
//        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
//        if (objectAnimator.isPaused()) {
//            objectAnimator.resume();
//        } else {
//            objectAnimator.start();
//        }
//        /**
//         * 唱盘动画可能执行多次，只有不是音乐不在播放状态，在回调执行播放
//         * */
//        if (musicStatus != MusicStatus.PLAY) {
//            notifyMusicStatusChanged(MusicChangedStatus.PLAY);
//        }
//    }
//
//    /*暂停唱盘动画*/
//    private void pauseDiscAnimatior(int index) {
//        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
//        objectAnimator.pause();
//        mNeedleAnimator.reverse();
//    }
//
//    public void notifyMusicInfoChanged(int position) {
//        if (mIPlayInfo != null) {
//            MusicInfo musicInfo = mMusicInfos.get(position);
//            mIPlayInfo.onMusicInfoChanged(musicInfo.getMusicName(), musicInfo.getArtist());
//        }
//    }
//
//    public void notifyMusicPicChanged(int position) {
//        if (mIPlayInfo != null) {
//            MusicInfo musicInfo = mMusicInfos.get(position);
////            mIPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());
//            mIPlayInfo.onMusicPicChanged(R.raw.ic_music1);
//        }
//    }
//
//    public void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus) {
//        if (mIPlayInfo != null) {
//            mIPlayInfo.onMusicChanged(musicChangedStatus);
//        }
//    }
//
//    private void play() {
//        playAnimator();
//    }
//
//    private void pause() {
//        musicStatus = MusicStatus.PAUSE;
//        pauseAnimator();
//    }
//
//    public void stop() {
//        musicStatus = MusicStatus.STOP;
//        pauseAnimator();
//    }
//
//    public void playOrPause() {
//        if (musicStatus == MusicStatus.PLAY) {
//            Log.e("56621", "playOrPause: 播放-暂停");
//            pause();
//        } else {
//            Log.e("56621", "playOrPause: 暂停-播放");
//            play();
//        }
//    }
//
//    public void next() {
//        int currentItem = mVpContain.getCurrentItem();
//        if (currentItem == mMusicInfos.size() - 1) {
//            Toast.makeText(getContext(), "已经到达最后一首", Toast.LENGTH_SHORT).show();
//        } else {
//            selectMusicWithButton();
//            mVpContain.setCurrentItem(currentItem + 1, true);
//        }
//    }
//
//    public void last() {
//        int currentItem = mVpContain.getCurrentItem();
//        if (currentItem == 0) {
//            Toast.makeText(getContext(), "已经到达第一首", Toast.LENGTH_SHORT).show();
//        } else {
//            selectMusicWithButton();
//            mVpContain.setCurrentItem(currentItem - 1, true);
//        }
//    }
//
//    public boolean isPlaying() {
//        return musicStatus == MusicStatus.PLAY;
//    }
//
//    private void selectMusicWithButton() {
//        if (musicStatus == MusicStatus.PLAY) {
//            mIsNeed2StartPlayAnimator = true;
//            pauseAnimator();
//        } else if (musicStatus == MusicStatus.PAUSE) {
//            play();
//        }
//    }
//
//    class ViewPagerAdapter extends PagerAdapter {
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View discLayout = mDiscLayouts.get(position);
//            container.addView(discLayout);
//            return discLayout;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mDiscLayouts.get(position));
//        }
//
//        @Override
//        public int getCount() {
//            return mDiscLayouts.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }
//}
