package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.util.ToastUtil;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        getSharedPreferences("music_player_setting",MODE_PRIVATE);
        SharedPreferences spDatas = PreferenceManager.getDefaultSharedPreferences(this);
        spDatas.getAll();

        PreferenceManager.setDefaultValues(this, R.xml.music_player_setting, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ToastUtil.showShortToastCenter("设置发生了改变！");
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.music_player_setting, rootKey);
        }
    }
}