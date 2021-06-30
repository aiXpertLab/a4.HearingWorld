package com.seeingvoice.www.svhearing.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.seeingvoice.www.svhearing.AppConstant;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.util.ToastUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * Date:2019/7/12
 * Time:10:53
 * auther:zyy
 */
public class WXShareUtil {
    public enum SHARE_TYPE {Type_WXSceneSession, Type_WXSceneTimeline}

    /**
     * 分享网页类型至微信
     *
     * @param context 上下文
     */
    public static void shareWeb(Context context, SHARE_TYPE type) {
        //通过appId得到WXAPI这个对象
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, AppConstant.WX_APP_ID);
        //检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled()) {
            ToastUtil.showShortToastCenter("您还没有安装微信");
            return;
        }

        //初始化一个WXWebpageObject 对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        // 填写网页的url
        webpageObject.webpageUrl = "https://sj.qq.com/myapp/detail.htm?apkName=seeingvoice.jskj.com.seeingvoice";
        // 用WXWebpageObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        // 填写网页标题、描述、位图
        msg.title = "见声听见APP";
        msg.description = "家用听力计，听力保健！";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.sv_2barcode1);
        // 如果没有位图，可以传null，会显示默认的图片
//        msg.thumbData = bmpToByteArray(thumb, true);
        msg.thumbData = WXShareUtil.bmpToByteArray(thumb,true);

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = buildTransaction("Req");
        req.message = msg;
        switch (type) {
            case Type_WXSceneSession:
                req.scene = WXSceneSession;
                break;
            case Type_WXSceneTimeline:
                req.scene = WXSceneTimeline;
                break;
        }
        wxapi.sendReq(req);
    }

    private static String buildTransaction(final String type1) {
        return (type1 == null) ? String.valueOf(System.currentTimeMillis()) : type1 + System.currentTimeMillis();
    }

//    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
//        if (needRecycle) {
//            bmp.recycle();
//        }
//        byte[] result = output.toByteArray();
//        try {
//            output.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        }  else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas =  new Canvas(localBitmap);

        while ( true) {
            localCanvas.drawBitmap(bmp,  new Rect(0, 0, i, j),  new Rect(0, 0,i, j),  null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream =  new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 10,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            }  catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
}
