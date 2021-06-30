package com.seeingvoice.www.svhearing.heartests.verbaltests.Interface;

import android.content.Context;

/**
 * Date:2019/2/20
 * Time:10:34
 * auther:zyy
 */
public interface OnPlayWhiteNoise {
    /** 播放背景白噪音*/
    void play(Context context);
    /** 停止背景白噪音*/
    void stop();
    /** 调节背景噪音的音量*/
    void adjustVolumn(float volumn);
}
