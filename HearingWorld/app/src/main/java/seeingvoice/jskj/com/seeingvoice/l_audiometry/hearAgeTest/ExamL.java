package seeingvoice.jskj.com.seeingvoice.l_audiometry.hearAgeTest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_AboutUs;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.L_PlayingPureTone;
import seeingvoice.jskj.com.seeingvoice.share.ShareWXQQ;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.List;

/**
 * Created by lenovo on 2017/12/4.
 */

public class ExamL extends MyTopBar {

    private int count;
    private int current,total = 7;
    private  boolean wrongMode;//标志变量，判断是否进入错题模式
    private TextView tv_question;
    private RadioButton[] radioButtons = new RadioButton[2];
    private TextView tv_explaination;
    private RadioGroup radioGroup;
    private Question q;
    private List<Question> list;
    private L_PlayingPureTone mPlayingThread;
    private boolean isPlaying = false;
    private ProgressBar mProgress;
    private Handler mHandler;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_exam;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("当前："+(current+1)+"/"+total);
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                ShareWXQQ.getInstance().shareFunction(ExamL.this);
            }
        });

        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null, ExamL.this, L_AboutUs.class);
            }
        });
        DBService dbService = new DBService();
        list = dbService.getQuestion();

        count = list.size();
        current = 0;
        mPlayingThread = new L_PlayingPureTone(list.get(current).frequency_band,40);
        mPlayingThread.start();
        isPlaying = true;

        mProgress = findViewById(R.id.progress_Bar);
        mProgress.setMax(7);
        mProgress.setProgress(1);

        wrongMode = false;//默认情况

        tv_question = findViewById(R.id.question);
        radioButtons = new RadioButton[2];
        radioButtons[0] = findViewById(R.id.answerA);
        radioButtons[1] = findViewById(R.id.answerB);

        tv_explaination = findViewById(R.id.explaination);
        radioGroup = findViewById(R.id.radioGroup);

        //为控件赋值
        q = list.get(current);
        tv_question.setText(q.question);
        tv_explaination.setText(q.explaination);
        radioButtons[0].setText(q.answerA);
        radioButtons[1].setText(q.answerB);

        //选择选项时更新选择
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (radioButtons[0].isChecked()) {//选择 “听见了”
                    if (current < count - 1) {//最后一题之前 全部听见了
                        list.get(current).selectedAnswer = 1;
                        current++;//计数器  转到下一题
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = current+1;
                                mHandler.sendMessage(msg);
                            }
                        }).start();
                        toNextQuestion(current);
                    }else if (current == count - 1){
                        toResult(list.get(current).hearage);
                    }
                }

                if (radioButtons[1].isChecked()) {//选择“听不见”
                    list.get(current).selectedAnswer = 0;//设置当前题目为听不见
                    if (current == 0) {//第一题就听不见
                        toResult(list.get(current).hearage+"，请保护好听力哦。");
                    }else if (current <= count - 1){
                        toResult(list.get(current-1).hearage);
                    }
                }
            }
        });

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                setToolbarTitle("当前："+msg.arg1+"/"+total);
                mProgress.setProgress(current+1);
            }
        };
    }

    /** 显示测试结果*/
    private void toResult(String hearage) {
        ToastUtil.showLongToast(hearage);
        stopPlaying();
        Intent data = new Intent();
        data.putExtra("HEAR_AGE_RESULT",hearage);
        setResult(RESULT_OK,data);
        ExamL.this.finish();
    }

    /** 跳到下一频段*/
    private void toNextQuestion(final int updatecurrent) {
        stopPlaying();
        mPlayingThread = new L_PlayingPureTone(list.get(updatecurrent).frequency_band,40);
        int hz = list.get(updatecurrent).frequency_band;
        Log.e("跳到下一频段", "toNextQuestion: 频率："+hz);
        mPlayingThread.start();
        isPlaying = true;
        //为控件赋值
        radioGroup.clearCheck();
        q = list.get(current);
        tv_question.setText(q.question);
        tv_explaination.setText(q.explaination);
        radioButtons[0].setText(q.answerA);
        radioButtons[1].setText(q.answerB);
    }

    private void stopPlaying() {
        if (null != mPlayingThread) {
            mPlayingThread.stopPlay();
            mPlayingThread = null;
            isPlaying = false;
        }
    }


//        btn_next.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                btn_next.setText("下一频段");
//                if (current < count - 1) {//若当前题目不为最后一题，点击next按钮跳转到下一题；否则不响应
//                    current++;
//                    btn_previous.setVisibility(View.VISIBLE);
//                    //更新题目
//                    Question q = list.get(current);
//                    tv_question.setText(q.question);
//                    radioButtons[0].setText(q.answerA);
//                    radioButtons[1].setText(q.answerB);
//                    tv_explaination.setText(q.explaination);
//
//                    //若之前已经选择过，则应记录选择
//                    radioGroup.clearCheck();
//                    if (q.selectedAnswer != -1) {
//                        radioButtons[q.selectedAnswer].setChecked(true);
//                    }
//                } else if(current == count-1 && wrongMode == true){//错题模式的最后一题
//                    new AlertDialog.Builder(ExamActivity.this)
//                        .setTitle("提示")
//                        .setMessage("已经到达最后一题，是否退出？")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                ExamActivity.this.finish();
//                            }
//                        })
//                        .setNegativeButton("取消",null)
//                        .show();
//                    if (q.selectedAnswer == 1) {
//                        if (q.ID == 0){
//                            new AlertDialog.Builder(ExamActivity.this)
//                                    .setTitle("结果")
//                                    .setMessage("您无法听到"+q.frequency_band+"的声音!"+"8000赫兹是正常人能听到的声音。")
//                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            ExamActivity.this.finish();
//                                        }
//                                    }).show();
//                        }else if (q.ID <= count - 1){
//                            new AlertDialog.Builder(ExamActivity.this)
//                                    .setTitle("结果")
//                                    .setMessage("您能听到"+list.get(current-1).frequency_band+"的声音!"+list.get(current-1).hearage)
//                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            ExamActivity.this.finish();
//                                        }
//                                    }).show();
//                        }
//                        return;
//                    }
//
//                } else{
//                    btn_next.setText("查看结果");
//                    //当前题目为最后一题时，告知用户的听力年龄
//                    for (int i = 0; i < list.size() ; i++) {
//                        if (list.get(i).selectedAnswer == 1 && i == 0){
//
//                        }
//                        if (list.get(i).selectedAnswer == 1){//循环到题目答案为“听不见”时
//                            ToastUtil.showLongToast("您能听到"+list.get(i-1).frequency_band+"赫兹的声音!"+list.get(i-1).hearage);
//                            new AlertDialog.Builder(ExamActivity.this)
//                                    .setTitle("结果")
//                                    .setMessage("您能听到"+list.get(i).frequency_band+"的声音!"+list.get(i).hearage)
//                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            ExamActivity.this.finish();
//                                        }
//                                    }).show();
//                            break;
//                        }
//                    }

                    //当前题目为最后一题时，告知用户作答正确的数量和作答错误的数量，并询问用户是否要查看错题
//                    final List<Integer> wrongList = checkAnswer(list);
                    //作对所有题目
//                    if(wrongList.size()==0){
//                        new AlertDialog.Builder(ExamActivity.this)
//                                .setTitle("提示")
//                                .setMessage("恭喜你全部回答正确！")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        ExamActivity.this.finish();
//                                    }
//                                }).show();
//                    } else
//                        new AlertDialog.Builder(ExamActivity.this)
//                                .setTitle("提示")
//                                .setMessage("您答对了"+(list.size()-wrongList.size())+
//                                        "道题目；答错了"+wrongList.size()+"道题目。是否查看错题？")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int which) {
//
//                                        //判断进入错题模式
//                                        wrongMode=true;
//                                        List<Question> newList=new ArrayList<Question>();
//                                        //将错误题目复制到newList中
//                                        for(int i=0;i< wrongList.size();i++){
//                                            newList.add(list.get(wrongList.get(i)));
//                                        }
//                                        //将原来的list清空
//                                        list.clear();
//                                        //将错误题目添加到原来的list中
//                                        for(int i=0;i<newList.size();i++){
//                                            list.add(newList.get(i));
//                                        }
//                                        current=0;
//                                        count=list.size();
//                                        //更新显示时的内容
//                                        Question q = list.get(current);
//                                        tv_question.setText(q.question);
//                                        radioButtons[0].setText(q.answerA);
//                                        radioButtons[1].setText(q.answerB);
//                                        tv_explaination.setText(q.explaination);
//
//                                        //显示解析
//                                        tv_explaination.setVisibility(View.VISIBLE);
//                                    }
//                                })
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int which) {
//
//                                        //点击取消时，关闭当前activity
//                                        ExamActivity.this.finish();
//                                    }
//                                }).show();
//                }
//            }
//        });

//        btn_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (current > 0)//若当前题目不为第一题，点击previous按钮跳转到上一题；否则不响应
//                {
//                    current--;
//                    if (current == 0){
//                        btn_previous.setVisibility(View.GONE);
//                    }
//                    Question q = list.get(current);
//                    tv_question.setText(q.question);
//                    radioButtons[0].setText(q.answerA);
//                    radioButtons[1].setText(q.answerB);
//                    tv_explaination.setText(q.explaination);
//
//                    //若之前已经选择过，则应记录选择
//                    radioGroup.clearCheck();
//                    if (q.selectedAnswer != -1) {
//                        radioButtons[q.selectedAnswer].setChecked(true);
//                    }
//                }
//            }
//        });



    /**
     * 判断用户作答是否正确，并将作答错误题目的下标生成list,返回给调用者
     * */
//    private List<Integer> checkAnswer(List<Question> list) {
//        List<Integer> wrongList = new ArrayList<Integer>();
//        for(int i=0;i<list.size();i++)
//        {
//            if(list.get(i).answer!=list.get(i).selectedAnswer){
//                wrongList.add(i);
//            }
//        }
//        return wrongList;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }
}