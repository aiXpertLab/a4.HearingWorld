package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;

public class L6_Frag2 extends Fragment {

    public static String mText, mTextHF, mTextAll, mTextKey;

    public L6_Frag2() {    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            int minR =  bundle.getInt("pMinR");
            int maxR =  bundle.getInt("pMaxR");
            int allR =  bundle.getInt("pAvgAllR");
            int keyR =  bundle.getInt("pAvgKeyR");
            int hfR  =  bundle.getInt("pAvgHFR");

            String explain_keyR =  bundle.getString("pKeyR");
            String explain_hfR =  bundle.getString("pHFR");
            String explain_allR =  bundle.getString("pAllR");
            String ear = getContext().getString(R.string.global_rightear);

            mTextAll = getContext().getString(R.string.chart_all,minR,maxR,allR,ear);
            mTextKey = getContext().getString(R.string.chart_key,keyR);
            mTextHF  = getContext().getString(R.string.chart_hf, hfR);

            mText = mTextAll +explain_allR +
                    mTextKey + explain_keyR +
                    mTextHF + explain_hfR;
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
