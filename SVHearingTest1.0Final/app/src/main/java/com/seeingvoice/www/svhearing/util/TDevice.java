package com.seeingvoice.www.svhearing.util;

import android.content.pm.PackageManager;

import com.seeingvoice.www.svhearing.MyApplication;


/**
 * 得到设备的版本号
 * */
public class TDevice {
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = MyApplication
                    .getApplication()
                    .getPackageManager()
                    .getPackageInfo(MyApplication.getApplication().getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }
}
