package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.MySQLite;
import seeingvoice.jskj.com.seeingvoice.MyUtil;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.Register;

public class MeMoreRegion extends MyBaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    TextView mTitleTv;
    EditText mRegion;
    View mNickView;
    TextView mSaveTv;

    MySP myUser = MySP.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_frag3_me_more_region);

        mTitleTv= findViewById(R.id.text_view_title);
        mRegion = findViewById(R.id.edit_text_region);
        mNickView= findViewById(R.id.v_nick);

        mSaveTv= findViewById(R.id.tv_save);
        mSaveTv.setTextColor(0xFFFFFFFF);
        mSaveTv.setEnabled(true);

        MySP.getInstance().init(this);

        initView();

        mSaveTv.setOnClickListener(view -> {
            Log.e(TAG, "savidd ng ... ");
            String userRegion = mRegion.getText().toString();
            updateUserRegion(myUser.getUPhone(), userRegion);
        });
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mRegion.setText(MySP.getInstance().getURegion());

        mRegion.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mNickView.setBackgroundColor(getColor(R.color.divider_green));
            } else {
                mNickView.setBackgroundColor(getColor(R.color.divider_grey));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void updateUserRegion(String userId, final String userRegion) {
        MySP.getInstance().setURegion(userRegion);
        finish();
    }
}