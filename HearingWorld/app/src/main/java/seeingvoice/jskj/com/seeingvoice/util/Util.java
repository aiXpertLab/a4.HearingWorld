package seeingvoice.jskj.com.seeingvoice.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Date:2019/5/6
 * Time:14:40
 * auther:zyy
 */
public class Util {
    public static String TAG = "UTIL";
    public static Bitmap bitmap = null;

    public static Bitmap getbitmap(final String imageUri) {
//        Log.v(TAG, "getbitmap:" + imageUri);
        // 显示网络上的图片
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
//            Log.v(TAG, "image download finished." + imageUri);
        } catch (IOException e) {
            e.printStackTrace();
//            Log.v(TAG, "getbitmap bmp fail---");
        }
        return bitmap;
    }
}
