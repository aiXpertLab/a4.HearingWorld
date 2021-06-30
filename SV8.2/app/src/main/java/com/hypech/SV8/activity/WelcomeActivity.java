package com.hypech.SV8.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.click.guide.guide_lib.GuideCustomViews;
import com.click.guide.guide_lib.interfaces.CallBack;
import com.hypech.SV8.HomeActivity;
import com.hypech.SV8.R;
import com.hypech.SV8.util.SPHelper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by jhonjson on 2019/6/15.
 *
 * @Description: 引导页
 */
public class WelcomeActivity extends AppCompatActivity implements CallBack {

    private GuideCustomViews GuideCustomViews;
    private TextView mNextTv;
    private final int[] mPageImages = {
            R.drawable.welcome1,
            R.drawable.welcome2,
            R.drawable.welcome3
    };

    private final int[] mGuidePoint = {
            R.drawable.icon_guide_point_select,
            R.drawable.icon_guide_point_unselect
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        initView();
    }

    private void initView() {
        GuideCustomViews = findViewById(R.id.guide_CustomView);
        GuideCustomViews.setData(mPageImages, mGuidePoint, this);
        mNextTv = findViewById(R.id.next_tv);
    }

    @Override
    public void callSlidingPosition(int position) {
        Log.e("callSlidingPosition", "滑动位置 callSlidingPosition " + position);
        if(position == 2){
            mNextTv.setVisibility(View.VISIBLE);
        }else{
            mNextTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void callSlidingLast() {
        Log.e("callSlidingLast", "滑动到最后一个callSlidingLast");
    }

    @Override
    public void onClickLastListener() {
        Log.e("callSlidingLast", "点击最后一个view");
        SPHelper.getInst().saveString("WELCOME", "ok");
        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        GuideCustomViews.clear();
    }
}
