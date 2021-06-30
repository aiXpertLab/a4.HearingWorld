package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.mpchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;


public class MyYFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value==0){
            return mFormat.format(value);
        }
        return  mFormat.format(value)+" dB" ;
    }
}
