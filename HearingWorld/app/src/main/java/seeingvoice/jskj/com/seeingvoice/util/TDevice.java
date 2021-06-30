package seeingvoice.jskj.com.seeingvoice.util;

import android.content.pm.PackageManager;

import seeingvoice.jskj.com.seeingvoice.MyApp;


/**
 * 得到设备的版本号
 * */
public class TDevice {
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = MyApp
                    .getApplication()
                    .getPackageManager()
                    .getPackageInfo(MyApp.getApplication().getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }
}
