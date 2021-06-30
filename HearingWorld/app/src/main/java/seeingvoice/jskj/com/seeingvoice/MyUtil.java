package seeingvoice.jskj.com.seeingvoice;

import android.app.Activity;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1. 手机号校验
 * 2. 密码校验
 * 3. hide navigation bar
 */
public class MyUtil {

    private static Pattern CHINESE_PHONE_PATTERN = Pattern.compile("((13|15|17|18|19)\\d{9})|(14[57]\\d{8})");
    public static boolean isValidChinesePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }

        Matcher matcher = CHINESE_PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }

    public static boolean validatePassword(String password) {
        String regEx = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,16}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

        /* Hide both the navigation bar and the status bar.
    https://developer.android.com/training/system-ui/navigation
    // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
    // a general rule, you should design your app to hide the status bar whenever you
    // hide the navigation bar.
            And show all bars:

        public static void showSystemUI() {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
             */

    public static void hideNavigationBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
