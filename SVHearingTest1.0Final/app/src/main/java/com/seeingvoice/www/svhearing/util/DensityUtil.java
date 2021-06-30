package com.seeingvoice.www.svhearing.util;

import android.content.Context;

/**
 * Date:2019/5/15
 * Time:13:28
 * auther:zyy
 */
public class DensityUtil {
    /**      * 根据手机的分辨率从 dip 的单位 转成为 px(像素)      */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);      }

        /**      * 根据手机的分辨率从 px(像素) 的单位 转成为 dp==dip     */
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
}
