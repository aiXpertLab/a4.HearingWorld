package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_UserAgreement;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_Privacy;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;
import seeingvoice.jskj.com.seeingvoice.util.SharedPreferencesHelper;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

/**
 * Date:2021/2/28
 * author:Leo Reny@hypech.com
 */

public class L1_Disclaimer extends AppCompatActivity {

    private boolean isRequireCheck; // 是否需要系统权限检测
    private Button btnAccept,btnUnAccept;
    private TextView tvServiceTerms,tv_User_Agreement;

    private AlertDialogUtil alertDialog;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_disclaimer);

        // 打开SDK日志开关
        HiAnalyticsTools.enableLog();
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);

        btnAccept   = findViewById(R.id.btn_accept);
        btnUnAccept = findViewById(R.id.btn_unaccept);
        btnAccept.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startActivity(new Intent(L1_Disclaimer.this, Login.class));
                SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);//设置不是第一次登陆了
                finish();
            }
        });

        btnUnAccept.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                ToastUtil.showLongToast(getString(R.string.global_exitnow));
                finish();
            }
        });

        tvServiceTerms = findViewById(R.id.l_access_permission_privacy_policy);
        tv_User_Agreement = findViewById(R.id.l_access_permission_user_agreement);

        tvServiceTerms.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(L1_Disclaimer.this, L_Privacy.class);
                startActivity(intent);
            }
        });
        tv_User_Agreement.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(L1_Disclaimer.this, L_UserAgreement.class);
                startActivity(intent);
            }
        });
    }
}
