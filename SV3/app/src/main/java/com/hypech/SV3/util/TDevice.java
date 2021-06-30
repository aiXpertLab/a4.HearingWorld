package com.hypech.SV3.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.hypech.SV3.App;

public class TDevice {
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = App.myApplication
                    .getPackageManager()
                    .getPackageInfo(App.myApplication.getPackageName(),
                            0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }
    public static String getVersionName(){
        String versionName = "";
        try{
            versionName = App.myApplication.getPackageManager().getPackageInfo(App.myApplication.getPackageName(),0).versionName;
        }catch (PackageManager.NameNotFoundException ex){
            versionName = "";
        }
        return versionName;
    }
    public static void openDial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(it);
    }

}
