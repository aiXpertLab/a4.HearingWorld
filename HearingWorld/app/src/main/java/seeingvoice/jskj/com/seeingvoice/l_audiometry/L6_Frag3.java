package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;

public class L6_Frag3 extends Fragment {

    public L6_Frag3() {    }

    @Override
    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.a_fragment_faq, container, false);
        TextView mtv_frag3 = view.findViewById(R.id.text_fragment_faq);
        mtv_frag3.setText(getText(R.string.chart_analysis3));
        mtv_frag3.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }
}