package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import seeingvoice.jskj.com.seeingvoice.MainActivity;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.noisetest.noiseUI.NoiseboardView;
import seeingvoice.jskj.com.seeingvoice.ui.AttentionView;


/**
 * Date:2021/2/28
 * Author:Leo Reny@hypech.com
 */
public class L_SPLMeterL extends MyTopBar implements View.OnClickListener {

    private L_DbMeter audioRecordDemo;
    private Button start_hearing_meter;
    public NoiseboardView mNoiseboardView;
    public TextView mNoiseHint;
    private Intent mIntent;
    private AttentionView mAttentionView;
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    protected int getContentView_sv() {
        return R.layout.a_spl_meter;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.drawer_noise_title));
        setToolbarBack(true);
        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
        requestAudioPermissions();
    }

    private void requestAudioPermissions() {
        int i = 1;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
//                Toast.makeText(this, "Please grant permissions to record audio to get noise level.", Toast.LENGTH_LONG).show();
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED) {
            //Go ahead with recording audio now
            initData();
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        int i = 1;
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    initData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getString(R.string.global_permission_denied), Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(L_SPLMeterL.this, MainActivity.class);//设置自己跳转到成功的界面
                    startActivity(intent1);
                    finish();
                }
                return;
            }
        }
    }

    private void initData() {
        mIntent = getIntent();
        boolean isFromIndex;
        isFromIndex = mIntent.getExtras().getBoolean("fromIndex");

        audioRecordDemo = new L_DbMeter(this);
        mNoiseboardView = findViewById(R.id.noiseboardView);
        start_hearing_meter = findViewById(R.id.btn_start_hearing_test_meter);
        mNoiseHint = findViewById(R.id.tv_noise_hint);
        audioRecordDemo.getNoiseLevel();
        start_hearing_meter.setOnClickListener(this);
        if (isFromIndex){
            start_hearing_meter.setVisibility(View.GONE);
        }else {
            start_hearing_meter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_hearing_test_meter) {
            toNextActivity(null, this, MainActivity.class);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecordDemo != null) {
            audioRecordDemo.stop();
        }
    }
}
