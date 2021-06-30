package com.reapex.sv.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.reapex.sv.App;

/**
 * Created by Administrator on 2016/10/13.
 */

public class SPHelper {
    public static final String KEY_AD_START_IMAGE = "ad_start_image";
    public static final String KEY_IS_FIRST_RUN = "is_first_run";

    private static SPHelper helper;

    private SharedPreferences mSp;

    public static SPHelper getInst(){
        if(helper == null){
            helper = new SPHelper(App.myApplication);
        }
        return helper;
    }

    public SPHelper(Context context){
        mSp = context.getSharedPreferences("eyeglasses_sp", Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value){
        mSp.edit().putString(key, value).commit();
    }

    public String getString(String key){
        return mSp.getString(key, "");
    }

    public void saveLong(String key, long value){
        mSp.edit().putLong(key, value).commit();
    }

    public long getLong(String key){
        return mSp.getLong(key, -1);
    }
    public void saveInt(String key, int value){
        mSp.edit().putInt(key, value).commit();
    }

    public int getInt(String key){
        return mSp.getInt(key, 0);
    }

    public void saveBoolean(String key, boolean value){
        mSp.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key){
        return mSp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue){
        return mSp.getBoolean(key, defValue);
    }
    public void cleanr(){
        mSp.edit().clear().commit();
    }
}
