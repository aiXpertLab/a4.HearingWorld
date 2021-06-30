package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

/**
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-09
 */
public class L_Feedback extends MyTopBar {
    private static final String TAG = L_Feedback.class.getName();
    private Spinner mSpinner;
    private EditText mContent,mContact;
    private Button mSubbmit;
    boolean isSpinnerFirst = true;
    private String[] feedbackTypeAyyay;
    private int SpinnerPosition = 0;
    private String content;
    private String contact;

    @Override
    protected int getContentView_sv() {
        return R.layout.a_feedback;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        setToolbarTitle(getString(R.string.drawer_title_feedback));
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);


    }

    }

