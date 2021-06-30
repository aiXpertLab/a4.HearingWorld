package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import io.victoralbertos.breadcumbs_view.BreadcrumbsView;
import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.util.PopupDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

/**
 * Conduct Pure Tone Hearing Test using Asset WAV files.
 *
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-09
 */

public class L4_AudiometryT4 extends MyTopBar implements View.OnClickListener {

    L5_Thread_StaticWAV wavThread=null;
    private static final String TAG = "WAV === ";
    private int hzIndex, dBIndex, progress;
    private Button btnStart, btnYeHear, btnNoHear, btnCheckResult;
    private MyCount mClock = null;
    private RadioButton rbLeft, rbRight;
    private TextView tvCountDown,tvHzValue,tvDBValue;
    private ProgressBar mProgressBar;
    private Thread progressingThread;               //progressing thread
    private BreadcrumbsView mLeftBreadcrumbsView;
    private BreadcrumbsView mRightBreadcrumbsView;
    private TextView tvFinishNumLeft,tvFinishNumRight;
    private PopupDialog dialog;
    private final int[] hzArr = MyData.getHz();
    private final int[] dbArr = MyData.getDb();
    private final int[][] lDBMinVal = new int[10][2];    //ear threadshold
    private final int[][] rDBMinVal = new int[10][2];    //ear threadshold
    public int[] lDb = new int[10];
    public int[] rDb = new int[10];
    public boolean isLeftFinished  = false;
    private boolean isLeft = true;
    private boolean isRun = false;

    @Override
    protected int getContentView_sv() {
        return R.layout.a_puretone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // 标题栏设置
        setToolbarTitle(getString(R.string.t4_title));
        setToolbarBack(true);
        setToolBarMenuOne("", R.mipmap.ic_home, null);
        setToolBarMenuTwo("", R.mipmap.ic_share, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                allShare(getApplicationContext().getString(R.string.topbar_share));
            }
        });

        rbLeft      = findViewById(R.id.rd_left);
        rbRight     = findViewById(R.id.rd_right);
        btnCheckResult  = findViewById(R.id.btn_check_out_pure_result);
        btnStart        = findViewById(R.id.btn_start_pause);
        btnYeHear       = findViewById(R.id.btn_canHear);
        btnNoHear       = findViewById(R.id.btn_nocanHear);
        mProgressBar    = findViewById(R.id.progress_bar_h);
        mLeftBreadcrumbsView    = findViewById(R.id.left_breadcrumbs);
        mRightBreadcrumbsView   = findViewById(R.id.right_breadcrumbs);

        tvHzValue       = findViewById(R.id.tv_tone_value);
        tvDBValue       = findViewById(R.id.tv_volume_value);
        tvCountDown     = findViewById(R.id.tv_count_down);
        tvFinishNumLeft = findViewById(R.id.tv_finish_num_left);
        tvFinishNumRight= findViewById(R.id.tv_finish_num_right);

        btnYeHear.setOnClickListener(this);
        btnNoHear.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnCheckResult.setOnClickListener(this);

        mClock = new MyCount(3000,1000);

        // 初始化屏幕
        rbRight.setVisibility(View.INVISIBLE);
        btnYeHear.setVisibility(View.INVISIBLE);
        btnNoHear.setVisibility(View.INVISIBLE);
        btnCheckResult.setVisibility(View.VISIBLE);
        btnCheckResult.setVisibility(View.INVISIBLE);
        // 纯音测试分段进度条
        tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
        tvDBValue.setText(getString(R.string.t4_db, dbArr[dBIndex]));

        //s4 init volume
        int halfMaxVol = L_AudioManager.getInstance(L4_AudiometryT4.this).getMaxMediaVolume()/2+3;
        L_AudioManager.getInstance(L4_AudiometryT4.this).setMediaVolume(halfMaxVol);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_pause) {
            btnYeHear.setVisibility(View.VISIBLE);
            btnNoHear.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);
            tvHzValue.setText(getString(R.string.t4_hz,hzArr[hzIndex]));
            tvDBValue.setText(getString(R.string.t4_db, dbArr[dBIndex]));
            stopProgressingThread();
            if (null != wavThread) wavThread.stopp();       //sss
            wavThread = new L5_Thread_StaticWAV(this, MyData.getWave(hzArr[hzIndex], dbArr[dBIndex]), isLeft);
            wavThread.start();        //开始播放新纯音
            mClock.start();         //倒计时开始
            progressingThread();    //新进度条从零开始运行

        }else if (v.getId() == R.id.btn_canHear){
            if (!AntiShakeUtils.isInvalidClick(v, 700)) {
                canHearFunc();
            }
        }else if (v.getId() == R.id.btn_nocanHear){
            if (!AntiShakeUtils.isInvalidClick(v, 1500)) {
                cannotHearFunc();
            }
        }else if (v.getId() == R.id.btn_check_out_pure_result){
            if (!AntiShakeUtils.isInvalidClick(v,800)) {
                toResult();
            }
        }
    }

    //ss8:
    private void cannotHearFunc() {
        if (dBIndex == dbArr.length-1){     //当前是最大分贝时
            if (isLeft){         //左耳
                lDBMinVal[hzIndex][0] = hzArr[hzIndex];
                lDBMinVal[hzIndex][1] = dbArr[dBIndex];
            }else {                         //右耳
                rDBMinVal[hzIndex][0] = hzArr[hzIndex];
                rDBMinVal[hzIndex][1] = dbArr[dBIndex];
            }
            canHearFunc();
        }else {
            dBIndex = revise(dBIndex + 1, 0, dbArr.length - 1);
            tvHzValue.setText(getString(R.string.t4_hz, hzArr[hzIndex]));
            tvDBValue.setText(getString(R.string.t4_db, dbArr[dBIndex]));
            stopProgressingThread();
            if (null != wavThread) wavThread.stopp();   //sss
            wavThread = new L5_Thread_StaticWAV(this, MyData.getWave(hzArr[hzIndex], dbArr[dBIndex]), isLeft);
            wavThread.start();        //开始播放新纯音
            mClock.start();         //倒计时开始
            progressingThread();    //新进度条从零开始运行
        }
    }

    //ss7: if can hear, 1.record 2.db stop 3.hz next 4.same db value carry forward
    private void canHearFunc() {
        Log.e(TAG, String.valueOf(isLeft));
        if (isLeft){         //左耳
            lDBMinVal[hzIndex][0] = hzArr[hzIndex];
            lDBMinVal[hzIndex][1] = dbArr[dBIndex];
        }else {                         //右耳
            rDBMinVal[hzIndex][0] = hzArr[hzIndex];
            rDBMinVal[hzIndex][1] = dbArr[dBIndex];
        }

        //  如果还有频段可以被测，则继续下一个频段
        if (hzIndex < hzArr.length -1 ){
            //新频段，小2个db开始
            int diff = hzArr[hzIndex] - hzArr[hzIndex+1];
            if (diff > 4000) {
                dBIndex = revise(dBIndex - 10, 0, dbArr.length - 2);
            }else{
                dBIndex = revise(dBIndex - 3, 0, dbArr.length - 2);
            }
            hzIndex++;
            tvHzValue.setText(getString(R.string.t4_hz, hzArr[hzIndex]));
            tvDBValue.setText(getString(R.string.t4_db, dbArr[dBIndex]));
            stopProgressingThread();
            if (null != wavThread) wavThread.stopp();   //sss
            wavThread = new L5_Thread_StaticWAV(this, MyData.getWave(hzArr[hzIndex], dbArr[dBIndex]), isLeft);
            wavThread.start();        //开始播放新纯音
            mClock.start();         //倒计时开始
            progressingThread();    //新进度条从零开始运行

            try {
                int jindu =  (hzIndex)*14+ (int) (Math.random() * 10);
                if (isLeft){
                    mLeftBreadcrumbsView.nextStep();
                    tvFinishNumLeft.setText(getString(R.string.t4_progressing_left, jindu));
                }else {
                    mRightBreadcrumbsView.nextStep();
                    tvFinishNumRight.setText(getString(R.string.t4_progressing_right, jindu));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                ToastUtil.showShortToastCenter(getString(R.string.t4_toofast));
            }
        }else {                         //全部频段都已经测试完毕
            if (isLeft) {           //如果当前正在测试的是左耳, start right ear.
                mLeftBreadcrumbsView.nextStep();
                isLeftFinished = true;  //标记左耳已经完成
                isLeft = false;
                tvFinishNumLeft.setText(R.string.t4_progressing_left100);
                tvCountDown.setText(getString(R.string.t4_start_right));
                ToastUtil.showShortToastCenter(getString(R.string.t4_left_end));
                // 初始化
                hzIndex     = 0;
                dBIndex     = 0;

                rbLeft.setVisibility(View.INVISIBLE);
                rbRight.setVisibility(View.VISIBLE);

                btnYeHear.setVisibility(View.INVISIBLE);
                btnNoHear.setVisibility(View.INVISIBLE);

                btnStart.setVisibility(View.VISIBLE);
                mRightBreadcrumbsView.setVisibility(View.VISIBLE);
                mLeftBreadcrumbsView.setVisibility(View.INVISIBLE);

                tvHzValue.setText(getString(R.string.t4_hz, hzArr[hzIndex]));
                tvDBValue.setText(getString(R.string.t4_db, dbArr[dBIndex]));
            }else{
                //isRightFinished = true;
                mRightBreadcrumbsView.nextStep();
                tvFinishNumRight.setText(R.string.t4_progressing_right100);
                //TODO 查看测试结果
                btnCheckResult.setVisibility(View.VISIBLE);
                btnNoHear.setVisibility(View.INVISIBLE);
                btnYeHear.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void toResult() {    //测试完成进入结果页面
        int[] lrHz =         {125, 250, 500, 1000,    1500, 2000,3000,4000,6000,8000} ;
        //                     0     1   2     3        4    5    6     7   8    9
//        hzArr1 = new int[]{1000, 2000, 3000, 4000, 6000,  500, 250};                break;
//        hzArr1 = new int[]{1000, 2000, 3000, 4000, 8000,  250, 500};                break;
//        hzArr1 = new int[]{1500, 3000, 6000, 8000, 500,  250,  125};                 break;
/*        int[] lDb  =new int[]{4 ,22  ,23  ,34  ,  55 , 26 ,19  ,35  ,48  ,74  ,83} ;
        int[] rDb  ={25 ,30  ,40  ,60  ,  50 , 10 ,20  ,20  ,30  ,50  ,60} ;
/*/

        int[] z = new int[20];
        for(int i = 0; i < z.length; i++) {
            z[i] = (int)(Math.random()*16 - 6);
        }//end for loop

        if (hzArr[6]==125){
            lDb[0]  = lDBMinVal[6][1];
            lDb[1]  = lDBMinVal[5][1];
            lDb[2]  = lDBMinVal[4][1];
            lDb[3]  = revise(lDBMinVal[5][1]-z[0],10,100);
            lDb[4]  = lDBMinVal[0][1];
            lDb[5]  = revise(lDBMinVal[0][1]+z[1],10,100);
            lDb[6]  = lDBMinVal[1][1];
            lDb[7]  = revise(lDBMinVal[1][1]+z[2],10,100);
            lDb[8]  = lDBMinVal[2][1];
            lDb[9]  = lDBMinVal[3][1];

            rDb[0]  = rDBMinVal[6][1];
            rDb[1]  = rDBMinVal[5][1];
            rDb[2]  = rDBMinVal[4][1];
            rDb[3]  = revise(rDBMinVal[5][1]-z[3],10,100);
            rDb[4]  = rDBMinVal[0][1];
            rDb[5]  = revise(rDBMinVal[0][1]+z[4],10,100);
            rDb[6]  = rDBMinVal[1][1];
            rDb[7]  = revise(rDBMinVal[1][1]+z[5],10,100);
            rDb[8]  = rDBMinVal[2][1];
            rDb[9]  = rDBMinVal[3][1];
        }
        if (hzArr[6]==500){
            lDb[0]  = revise(lDBMinVal[5][1]+z[6],10,100);
            lDb[1]  = lDBMinVal[5][1];
            lDb[2]  = lDBMinVal[6][1];
            lDb[3]  = lDBMinVal[0][1];
            lDb[4]  = revise(lDBMinVal[0][1]+z[7],10,100);
            lDb[5]  = lDBMinVal[1][1];
            lDb[6]  = lDBMinVal[2][1];
            lDb[7]  = lDBMinVal[3][1];
            lDb[8]  = revise(lDBMinVal[3][1]+z[8],10,100);
            lDb[9]  = lDBMinVal[4][1];

            rDb[0]  = revise(rDBMinVal[5][1]+z[9],10,100);
            rDb[1]  = rDBMinVal[5][1];
            rDb[2]  = rDBMinVal[6][1];
            rDb[3]  = rDBMinVal[0][1];
            rDb[4]  = revise(rDBMinVal[0][1]+z[10],10,100);
            rDb[5]  = rDBMinVal[1][1];
            rDb[6]  = rDBMinVal[2][1];
            rDb[7]  = rDBMinVal[3][1];
            rDb[8]  = revise(rDBMinVal[3][1]+z[11],10,100);
            rDb[9]  = rDBMinVal[4][1];
        }

        if (hzArr[6]==250){
            lDb[0]  = revise(lDBMinVal[6][1]+z[12],10,100);
            lDb[1]  = lDBMinVal[6][1];
            lDb[2]  = lDBMinVal[5][1];
            lDb[3]  = lDBMinVal[0][1];
            lDb[4]  = revise(lDBMinVal[0][1]+z[13],10,100);
            lDb[5]  = lDBMinVal[1][1];
            lDb[6]  = lDBMinVal[2][1];
            lDb[7]  = lDBMinVal[3][1];
            lDb[8]  = lDBMinVal[4][1];
            lDb[9]  = revise(lDBMinVal[4][1]+z[14],10,100);

            rDb[0]  = revise(rDBMinVal[6][1]+z[15],10,100);
            rDb[1]  = rDBMinVal[6][1];
            rDb[2]  = rDBMinVal[5][1];
            rDb[3]  = rDBMinVal[0][1];
            rDb[4]  = revise(rDBMinVal[0][1]+z[16],10,100);
            rDb[5]  = rDBMinVal[1][1];
            rDb[6]  = rDBMinVal[2][1];
            rDb[7]  = rDBMinVal[3][1];
            rDb[8]  = rDBMinVal[4][1];
            rDb[9]  = revise(rDBMinVal[4][1]+z[17],10,100);
        }

        /* 送数据到结果页面*/
        Intent intent = new Intent(L4_AudiometryT4.this, L5_ResultT4.class);
        Bundle bundle = new Bundle();
        bundle.putIntArray("frequency"  , lrHz);
        bundle.putIntArray("leftDb"     , lDb);
        bundle.putIntArray("rightDb"    , rDb);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    //ss2 @param progress 更新纯音进度条
    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0) {
                if (progress > 30) {
                    progress = progress % 30;
                }
                mProgressBar.setProgress(progress);
            }
        }
    };

    //ss3 开启一个新线程每0.1秒告诉主线程更新下进度条UI，
    private void progressingThread(){
        isRun = true;
        progress = 0;
        progressingThread = new Thread(() -> {
            while (isRun){
                try {
                    progress++;
                    //reflashUI(progress);//这样更新会出错，不能在子线程更新UI
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        progressingThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != wavThread) wavThread.stopp();       //sss
        stopProgressingThread();
        stopClock();

        if (null != dialog){
            dialog.dismiss();
            dialog = null;
        }
    }

    private void stopClock() {
        if (null != mClock){
            mClock.cancel();
            mClock = null;
        }
    }

    //ss9 开启一个新线程每0.1秒告诉主线程更新下进度条UI，
    private void stopProgressingThread(){
        if (progressingThread!=null){
            isRun = false;
            try {
                progressingThread.interrupt();
                //主线程休眠0.1秒
                Thread.sleep(100);
                // thread.stop();
                progressingThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //ss5: CountDownTimer
    class MyCount extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCountDown.setText(getString(R.string.t4_countdown, millisUntilFinished/1000+1));
        }

        @Override
        public void onFinish() {
            progress = 0;//进度条归零
            mProgressBar.setProgress(0);//进度条归零
            isRun = false;//进度条的轮询线程结束轮询
            tvCountDown.setText(getString(R.string.t4_done));
        }
    }

    //ss6
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            dialog = PopupDialog.create(this, getString(R.string.global_remind), getString(R.string.t4_volume_change), getString(R.string.sure), null,getString(R.string.cancel),null,false,true,true);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    private int revise(int num, int min, int max) {
        if (num < min)             num = min;
        if (num > max)             num = max;
        return num;
    }
}