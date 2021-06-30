package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.viewpager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.numKeyboard.NumKeyboardL;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.ui.CustomVolumeControlBar;

/**
 * Date:2019/2/11
 * Time:16:16
 * auther:zyy
 */
public class FragmentView extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private Bundle arg;
    private Button Btn_Practice_or_Formal;
//    private RelativeLayout mRL;
    private TextView mTvTips,tv;
    private ImageView mTitleImg;
    private CustomVolumeControlBar customVolumeControlBar;
    private int volumeValue;

    /**
     * 实例化当前FragmentView 并传入Bundle数据
     * */
    public static FragmentView newInstance(Bundle args){
        FragmentView fragment = new FragmentView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arg = getArguments();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment,null);
        tv = view.findViewById(R.id.tv);
        mTvTips = view.findViewById(R.id.tv_content);
        mTvTips.setMovementMethod(ScrollingMovementMethod.getInstance());
        Btn_Practice_or_Formal = view.findViewById(R.id.btn_practice_or_formal);
        mTitleImg = view.findViewById(R.id.img_title);
        final Intent intent = new Intent(getActivity(), NumKeyboardL.class);

        int page = arg.getInt("pager_num");
        view.setBackgroundResource(R.color.login_blue);
        switch (page){
            case 1:
                mTitleImg.setBackground(getResources().getDrawable(R.mipmap.img_title_verbal_introduce,null));
                mTvTips.setVisibility(View.VISIBLE);
                mTvTips.setText("getResources().getString(R.string.verbelTest_brief)");
                break;
            case 2:
                mTitleImg.setBackground(getResources().getDrawable(R.mipmap.img_title_verbal_volume,null));
                mTvTips.setVisibility(View.VISIBLE);
                mTvTips.setText("getResources().getString(R.string.volume_adjust)");
                customVolumeControlBar = view.findViewById(R.id.Custom_Volume);
                customVolumeControlBar.setVisibility(View.VISIBLE);
                customVolumeControlBar.setOnVolumeChangeListener(new CustomVolumeControlBar.OnVolumeChangeListener() {
                    @Override
                    public void OnVolumeChange() {
                        volumeValue = customVolumeControlBar.mCount - customVolumeControlBar.mCurrentCount;
                        mTvTips.setText("当前音量为："+volumeValue+"/15");
                        MyApp.setVerbal_music_volume(volumeValue);
                    }
                });
                break;
            case 3:
                mTitleImg.setBackground(getResources().getDrawable(R.mipmap.img_title_verbal_practice,null));
                mTvTips.setVisibility(View.VISIBLE);
                mTvTips.setText("getResources().getString(R.string.start_practice)");
                Btn_Practice_or_Formal.setVisibility(View.VISIBLE);
                Btn_Practice_or_Formal.setText("言语测试练习");
                Btn_Practice_or_Formal.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        intent.putExtra("practiceorofficialtest",1);
                        startActivity(intent);
                    }
                });
                break;
            case 4:
                mTitleImg.setBackground(getResources().getDrawable(R.mipmap.img_title_verbal_test,null));
                mTvTips.setVisibility(View.VISIBLE);
                mTvTips.setText("getResources().getString(R.string.start_test)");
                Btn_Practice_or_Formal.setVisibility(View.VISIBLE);
                Btn_Practice_or_Formal.setText("开始言语测试");
                Btn_Practice_or_Formal.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        intent.putExtra("practiceorofficialtest",0);
                        startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
        tv.setText(arg.getString("Title"));
        return view;
    }

    /** 监听自定义音量控件的滑动操作*/

    /**
     * LinearLayout.LayoutParams  给Btn_Practice.setText("言语测试练习");
     mLL.addView(Btn_Practice,params); 设置样式
     * */
    @NonNull
    private LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
        params.gravity = Gravity.CENTER;
        params.width = 350;
        params.height = 100;
//        Btn_Practice = new Button(getActivity());
        return params;
    }

    @SuppressLint("ResourceAsColor")
    private RelativeLayout.LayoutParams getRelativeParams(){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父容器的左侧对齐
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//与父容器的上侧对齐
        lp.leftMargin=50;
        lp.rightMargin=50;
        lp.bottomMargin=200;
//        Btn_Practice = new Button(getActivity());
//        Btn_Practice.setBackground(getResources().getDrawable(R.drawable.shape_btn,null));
//        Btn_Practice.setTextColor(R.color.white);
        return lp;
    }


    /**
     * 监听第二个Fragment 的进度条开始 start
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.e("seekbar", "onProgressChanged: "+progress);
//        Intent intent = new Intent();
//        intent.setAction("SKIPTO-VOLUMN-SET-ON");
//        intent.putExtra("volume",volumeValue);
//        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 监听第二个Fragment 的进度条开始 end
     * */
}
