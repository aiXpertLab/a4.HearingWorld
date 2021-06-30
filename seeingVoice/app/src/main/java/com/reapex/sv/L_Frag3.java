package com.reapex.sv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import org.greenrobot.eventbus.Subscribe;

import com.reapex.sv.activity.FeedbackActivity;
import com.reapex.sv.activity.SettingActivity;
import com.reapex.sv.activity.WelcomeActivity;
import com.reapex.sv.base.BaseMainFragment;
import com.reapex.sv.event.TabSelectedEvent;
import com.reapex.sv.util.TDevice;
import com.reapex.sv.view.SelfDialogPhone;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class L_Frag3 extends BaseMainFragment {
//    private Toolbar mToolbar;
    private RelativeLayout mfeedBackRl;
    private RelativeLayout mSettingRl;
    private RelativeLayout mCallRl;
    private SelfDialogPhone selfDialog;
    private RelativeLayout mHelpCenterRl;
    public static L_Frag3 newInstance() {

        Bundle args = new Bundle();

        L_Frag3 fragment = new L_Frag3();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a_frag3, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mfeedBackRl = view.findViewById(R.id.feedback_center_container_rl);
        mSettingRl = view.findViewById(R.id.setting_center_container_rl);
        mCallRl = view.findViewById(R.id.connect_center_container_rl);
        mHelpCenterRl = view.findViewById(R.id.helpe_center_container_rl);

        mHelpCenterRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_mActivity, WelcomeActivity.class);
                startActivity(intent);
            }
        });
        mfeedBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_mActivity, FeedbackActivity.class);
                startActivity(intent);
            }
        });
        mSettingRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_mActivity, SettingActivity.class);
                startActivity(intent);
            }
        });
        mCallRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfDialog = new SelfDialogPhone(_mActivity, com.reapex.sv.R.style.dialog, "03515235518");
                selfDialog.show();
                selfDialog.setYesOnclickListener("确定", new SelfDialogPhone.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        TDevice.openDial(_mActivity,"03515235518");
                        selfDialog.dismiss();
                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialogPhone.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        selfDialog.dismiss();
                    }
                });

            }
        });
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {

    }

    @Override
    public void processHandlerMessage(Message msg) {

    }
}
