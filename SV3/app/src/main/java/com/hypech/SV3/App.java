package com.hypech.SV3;

import android.os.Handler;
import androidx.multidex.MultiDexApplication;


import com.baidu.mobstat.StatService;
import com.hypech.SV3.constants.Constances;
import com.hypech.SV3.constants.PreferencesConstants;
import com.hypech.SV3.util.AppManager;
import com.hypech.SV3.util.PreferencesUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import tech.oom.idealrecorder.IdealRecorder;

public class App extends MultiDexApplication {

    public static App myApplication;
    public static final boolean isDebug = true;
    /**
     * 桌面字体大小
     */
    private int desktopLrcFontSize = 0;
    /**
     * 桌面颜色索引
     */
    private int desktopLrcColorIndex = 1;

    /**
     * 是否现实字幕
     */
    private String desktopSubtitleShow = "开启";

    /**
     * 绑定眼镜设备
     */
    private String bindMacDevice = "";
    /**
     * 绑定眼镜设备,0左眼，1右眼
     */
    private String rotateDevice = "1";

    /**
     * 眼镜软件版本
     */
    private String glassesSoftVersion = "";
    public boolean canRun = false;

    /**
     * 翻译SDK平台
     */

    private static final Handler sHandler = new Handler();
    public AppManager manager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication  = this;
        manager = new AppManager(getApplicationContext());
        initOKHttp();
        IdealRecorder.getInstance().init(this);
        StatService.start(this);
    }
    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
    //字体设置
    public int getDesktopLrcFontSize() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.desktopLrcFontSize_KEY, desktopLrcFontSize);
    }

    public void setDesktopLrcFontSize(int desktopLrcFontSize) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.desktopLrcFontSize_KEY, desktopLrcFontSize);

    }
    //眼镜字体设置
    public int getEyeglassesFontSize() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.eyeglassesFontSize_KEY, Constances.FONT_SIZE_GLASSES1);
    }

    public void setEyeglassesFontSize(int desktopLrcFontSize) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.eyeglassesFontSize_KEY, desktopLrcFontSize);

    }
    //眼镜亮度设置
    public int getEyeglassesBright() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.eyeglassesBright_KEY, 5);
    }

    public void setEyeglassesBright(int bright) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.eyeglassesBright_KEY, bright);

    }
    //绑定设备Mac，自动连接唯一设备
    public String getBindMacDevice(){
        return (String)PreferencesUtil.getValue(getApplicationContext(),PreferencesConstants.bindMacDevice_KEY,bindMacDevice);
    }
    public void setBindMacDevice(String mac){
        PreferencesUtil.saveValue(getApplicationContext(),PreferencesConstants.bindMacDevice_KEY,mac);
    }
    //左右眼切换旋转
    public String getRotateDevice(){
        return (String)PreferencesUtil.getValue(getApplicationContext(),PreferencesConstants.rotateDevice_KEY,rotateDevice);
    }
    public void setRotateDevice(String mac){
        PreferencesUtil.saveValue(getApplicationContext(),PreferencesConstants.rotateDevice_KEY,mac);
    }
    //眼镜软件版本
    public String getGlassesSoftVersion(){
        return (String)PreferencesUtil.getValue(getApplicationContext(),PreferencesConstants.glassesSoftVersion_KEY,glassesSoftVersion);
    }
    public void setGlassesSoftVersion(String version){
        PreferencesUtil.saveValue(getApplicationContext(),PreferencesConstants.glassesSoftVersion_KEY,version);
    }

    //桌面文字颜色
    public int getDesktopLrcColorIndex() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.desktopLrcColorIndex_KEY, desktopLrcColorIndex);
    }

    public void setDesktopLrcColorIndex(int desktopLrcColorIndex) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.desktopLrcColorIndex_KEY, desktopLrcColorIndex);

    }
    //是否现实桌面字幕
    public String getShowSubtitle(){
        return (String)PreferencesUtil.getValue(getApplicationContext(),PreferencesConstants.desktopSubtitleShow_KEY,desktopSubtitleShow);
    }
    public void setShowSubtitle(String open){
        PreferencesUtil.saveValue(getApplicationContext(),PreferencesConstants.desktopSubtitleShow_KEY,open);
    }

    public void initOKHttp(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //超时时间设置，默认60秒
        builder.readTimeout(20000, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(20000, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params);                       //全局公共参数
    }
}
