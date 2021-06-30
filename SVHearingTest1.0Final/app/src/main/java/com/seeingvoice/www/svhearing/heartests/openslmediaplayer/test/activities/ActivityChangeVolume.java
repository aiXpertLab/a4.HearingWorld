package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.service.MusicService;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.OnVolumeBarPressedListener;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget.VolumeView;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;

import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.service.MusicService.VOLUME_PLAYER_LEFT;
import static com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.service.MusicService.VOLUME_PLAYER_RIGHT;

public class ActivityChangeVolume extends AppCompatActivity implements OnVolumeBarPressedListener {

    public static final Float DEFALT_VOLUME = 0.5f;
    private VolumeView mVolumeView;
    private MusicService.MusicBind musicControl;
    private Intent intentService;
    private ServiceConnection conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_volumes);
        mVolumeView = findViewById(R.id.volumeView);
        mVolumeView.setOnVolumeBarPressedListener(this);
        mVolumeView.setVolumeSKBMax();

        Float leftVol = (Float) SharedPreferencesHelper.getInstance().getData(VOLUME_PLAYER_LEFT, DEFALT_VOLUME);
        Float rightVol = (Float) SharedPreferencesHelper.getInstance().getData(VOLUME_PLAYER_RIGHT,DEFALT_VOLUME);

        mVolumeView.setLcurrentPro(leftVol);
        mVolumeView.setRcurrentPro(rightVol);

        intentService = new Intent(this, MusicService.class);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicControl = (MusicService.MusicBind) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(intentService, conn, BIND_AUTO_CREATE);
    }

    @Override
    public void onLeftVolumeChanged(float volume) {
        if (musicControl != null){
            musicControl.setLVolume(volume);
        }
    }

    @Override
    public void onRightVolumeChanged(float volume) {
        if (musicControl != null){
            musicControl.setRVolume(volume);
        }
    }

    @Override
    public void onDelBtnPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
