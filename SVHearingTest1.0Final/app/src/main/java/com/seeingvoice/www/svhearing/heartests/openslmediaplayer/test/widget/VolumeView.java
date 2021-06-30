package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.seeingvoice.www.svhearing.R;

public class VolumeView extends ConstraintLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private SeekBar mL_Seekbar,mR_Seekbar;
    private ImageView mBtnDel;
    private OnVolumeBarPressedListener mOnVolumeBarPressedListener;
    private int maxVol = 100;
    private Float leftVol,rightVol;
    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_volume_control,this);
        mL_Seekbar = findViewById(R.id.skb_L_Volume);
        mR_Seekbar = findViewById(R.id.skb_R_Volume);
        mBtnDel = findViewById(R.id.imv_del);

        mL_Seekbar.setOnSeekBarChangeListener(this);
        mR_Seekbar.setOnSeekBarChangeListener(this);
        mBtnDel.setOnClickListener(this);

        mL_Seekbar.setMax(maxVol);
        mR_Seekbar.setMax(maxVol);

    }

    public void setOnVolumeBarPressedListener(OnVolumeBarPressedListener listener) {
        mOnVolumeBarPressedListener = listener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float max = maxVol;
        if (seekBar.getId() == R.id.skb_L_Volume){
            Log.e("onProgressChanged-----", "onProgressChanged: progress:"+progress);
//            BigDecimal.valueOf(maxVol);
            float volumeL = progress/max;
            mOnVolumeBarPressedListener.onLeftVolumeChanged(volumeL);
        }

        if (seekBar.getId() == R.id.skb_R_Volume){
            float volumeR = progress/max;
            mOnVolumeBarPressedListener.onRightVolumeChanged(volumeR);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if (v == mBtnDel){
            mOnVolumeBarPressedListener.onDelBtnPressed();
        }
    }

    public void setVolumeSKBMax(){
        mL_Seekbar.setMax((int) maxVol);
        mR_Seekbar.setMax((int) maxVol);
    }

    public void setLcurrentPro(float vol){
        mL_Seekbar.setProgress((int) (vol*maxVol));
        mOnVolumeBarPressedListener.onLeftVolumeChanged(vol);
        postInvalidate();
    }

    public void setRcurrentPro(float vol){
        mR_Seekbar.setProgress((int) (vol*maxVol));
        mOnVolumeBarPressedListener.onRightVolumeChanged(vol);
        postInvalidate();
    }
}
