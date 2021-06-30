package com.seeingvoice.www.svhearing.heartests.hearAgeTest;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

public class hearAgeResultExplain extends TopBarBaseActivity {
    private TextView mAttentionText;
    @Override
    protected int getConentView() {
        return R.layout.activity_hear_age_result_explain;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("听力年龄-了解更多");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);

        setToolBarMenuTwo("", R.mipmap.return_icon, null);

        mAttentionText = findViewById(R.id.tv_attention_text);
        mAttentionText.setMovementMethod(ScrollingMovementMethod.getInstance());
        //                mTv_result1.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
