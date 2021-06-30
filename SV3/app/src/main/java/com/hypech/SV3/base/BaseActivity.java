package com.hypech.SV3.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.hypech.SV3.R;
import com.hypech.SV3.interf.IBaseActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity {

    private AppCompatActivity context = this;


    private Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                processHandlerMessage(msg);
            }
        };
        initViews();
        initDatas();
        installListeners();
    }
    @Override
    public Handler getHandler() {
        return mHandler;
    }
    @Override
    public AppCompatActivity getActivity(){
        return this;
    }


    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }
}
