package com.seeingvoice.www.svhearing.heartests.openslmediaplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

import static com.seeingvoice.www.svhearing.AppConstant.NUM_BAND_VIEWS;

public class TenEQsActivity extends TopBarBaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private TextView[] mTextViewBandLevels = new TextView[NUM_BAND_VIEWS];
    private SeekBar[] mSeekBarBandLevels = new SeekBar[NUM_BAND_VIEWS];
    private MyConnection conn;
    private MusicService.MusicBind musicControl;
    private Intent intentService;
    private String[] centerBandV;
    private static final int SEEKBAR_MAX = 1000;
    private RadioButton RbtnL,RbtnR;
    private RadioGroup RG;


    @Override
    protected int getConentView() {
        return R.layout.activity_ten_eqs;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("智能助听调节");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon,null);
        intentService = new Intent(this, MusicService.class);
        conn = new MyConnection();
        bindService(intentService, conn, BIND_AUTO_CREATE);

        mTextViewBandLevels[0] = findViewById(R.id.textview_equalizer_band_0);
        mTextViewBandLevels[1] = findViewById(R.id.textview_equalizer_band_1);
        mTextViewBandLevels[2] = findViewById(R.id.textview_equalizer_band_2);
        mTextViewBandLevels[3] = findViewById(R.id.textview_equalizer_band_3);
        mTextViewBandLevels[4] = findViewById(R.id.textview_equalizer_band_4);
        mTextViewBandLevels[5] = findViewById(R.id.textview_equalizer_band_5);
        mTextViewBandLevels[6] = findViewById(R.id.textview_equalizer_band_6);
        mTextViewBandLevels[7] = findViewById(R.id.textview_equalizer_band_7);
        mTextViewBandLevels[8] = findViewById(R.id.textview_equalizer_band_8);
        mTextViewBandLevels[9] = findViewById(R.id.textview_equalizer_band_9);

        mSeekBarBandLevels[0] = findViewById(R.id.seekbar_equalizer_band_0);
        mSeekBarBandLevels[1] = findViewById(R.id.seekbar_equalizer_band_1);
        mSeekBarBandLevels[2] = findViewById(R.id.seekbar_equalizer_band_2);
        mSeekBarBandLevels[3] = findViewById(R.id.seekbar_equalizer_band_3);
        mSeekBarBandLevels[4] = findViewById(R.id.seekbar_equalizer_band_4);
        mSeekBarBandLevels[5] = findViewById(R.id.seekbar_equalizer_band_5);
        mSeekBarBandLevels[6] = findViewById(R.id.seekbar_equalizer_band_6);
        mSeekBarBandLevels[7] = findViewById(R.id.seekbar_equalizer_band_7);
        mSeekBarBandLevels[8] = findViewById(R.id.seekbar_equalizer_band_8);
        mSeekBarBandLevels[9] = findViewById(R.id.seekbar_equalizer_band_9);

        for (int i = 0; i < NUM_BAND_VIEWS; i++) {
            mSeekBarBandLevels[i].setOnSeekBarChangeListener(this);
        }
        RbtnL = findViewById(R.id.rd_left);
        RbtnR = findViewById(R.id.rd_right);
        RbtnL.setOnClickListener(this);
        RbtnR.setOnClickListener(this);
        RG = findViewById(R.id.RG_left_right);
        RG.check(RbtnL.getId());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Equalizer band
        short band = seekBarToBandNo(seekBar);
        musicControl.setEqEvent(band, (short) progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static short seekBarToBandNo(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekbar_equalizer_band_0:
                return 0;
            case R.id.seekbar_equalizer_band_1:
                return 1;
            case R.id.seekbar_equalizer_band_2:
                return 2;
            case R.id.seekbar_equalizer_band_3:
                return 3;
            case R.id.seekbar_equalizer_band_4:
                return 4;
            case R.id.seekbar_equalizer_band_5:
                return 5;
            case R.id.seekbar_equalizer_band_6:
                return 6;
            case R.id.seekbar_equalizer_band_7:
                return 7;
            case R.id.seekbar_equalizer_band_8:
                return 8;
            case R.id.seekbar_equalizer_band_9:
                return 9;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onClick(View v) {

    }


    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicBind) service;//得到服务bind
            centerBandV = musicControl.getCenterBand();
            for (int i = 0; i < centerBandV.length; i++) {
                mTextViewBandLevels[i].setText(centerBandV[i]+"hz");
                mSeekBarBandLevels[i].setMax(SEEKBAR_MAX);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
