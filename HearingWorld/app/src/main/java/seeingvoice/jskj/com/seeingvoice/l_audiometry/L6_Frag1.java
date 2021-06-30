package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;

public class L6_Frag1 extends Fragment {

    public static String mText, mTextHF, mTextAll, mTextKey;

    public L6_Frag1() {    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            int minL =  bundle.getInt("pMinL");
            int maxL =  bundle.getInt("pMaxL");
            int allL =  bundle.getInt("pAvgAllL");
            int keyL =  bundle.getInt("pAvgKeyL");
            int hfL  =  bundle.getInt("pAvgHFL");

            String explain_keyL =  bundle.getString("pKeyL");
            String explain_hfL =  bundle.getString("pHFL");
            String explain_allL =  bundle.getString("pAllL");
            String left_ear = getContext().getString(R.string.global_leftear);

            mTextAll = getContext().getString(R.string.chart_all,minL,maxL,allL,left_ear);
            mTextKey = getContext().getString(R.string.chart_key,keyL);
            mTextHF  = getContext().getString(R.string.chart_hf, hfL);

            mText = mTextAll +explain_allL +
                    mTextKey + explain_keyL +
                    mTextHF + explain_hfL;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vView = inflater.inflate(R.layout.a_fragment_left, container, false);
        TextView mtv_frag1 = vView.findViewById(R.id.text_fragment_left);
        mtv_frag1.setText(mText);
        mtv_frag1.setMovementMethod(ScrollingMovementMethod.getInstance());
        return vView;
    }
}
