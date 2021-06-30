package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

public class L_HearAgeResultExplain extends MyTopBar {
    private TextView mAttentionText;
    @Override
    protected int getContentView_sv() {
        return R.layout.a_hear_age_result_explain;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("听力常识");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);

        setToolBarMenuTwo("", R.mipmap.return_icon, null);

        mAttentionText = findViewById(R.id.tv_attention_text);
        mAttentionText.setMovementMethod(ScrollingMovementMethod.getInstance());
        //                mTv_result1.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
