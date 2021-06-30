package com.seeingvoice.www.svhearing.heartests.verbaltests.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.heartests.verbaltests.Interface.OnPlayWhiteNoise;

/**
 * Date:2019/2/19
 * Time:17:10
 * auther:zyy
 */
public class WhiteNoiseService extends Service {
     @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new playWhiteNoise();
    }

    public class playWhiteNoise extends Binder implements OnPlayWhiteNoise {

        private final String TAG = "playWhiteNoise";
        private MediaPlayer player;
        private AudioManager audioManager;
        @Override
        public void play(Context context) {
            player = MediaPlayer.create(context, R.raw.white);
            player.start();
            Log.e(TAG, "Inner class playWhiteNoise ->play(): ",null);
        }

        @Override
        public void stop() {
            if (player != null){
                player.release();
                player = null;
            }
            Log.e(TAG, "Inner class playWhiteNoise ->stop(): ",null);
        }

        @Override
        public void adjustVolumn(float volumn) {
            player.setVolume(volumn,volumn);
            Log.e(TAG, "Inner class playWhiteNoise ->adjustVolumn(): ",null);
        }
    }
}
