package seeingvoice.jskj.com.seeingvoice;

/**
 * Data source including constant
 *
 * @author  LeoReny@hypech.com
 * @version 3.0.3
 * @since   2021-02-09
 */
public class MyData {
    //sv
    //public static final String URL = "http://seeingvoice.com";      //中文
    public static final String URL = "https://seeingvoice.gitee.io";       //English, Google
    public static final String URL_ABOUT_US         = URL+"/about/";        //关于我们
    public static final String URL_PRIVACY          = "file:///android_asset/privacy.html";      //隐私政策
    public static final String URL_USER_AGREEMENT   = "file:///android_asset/terms.html";        //用户协议
    //sv

    public static final int SAMPLE_RATE = 44100;
    public static final int AMPLITUDE16BIT = 65535;
    public static final double PI2 = 6.2831853;
    public static final int NOT_NOTICE = 2;//如果勾选了不再询问
    public static final int REQUEST_ENABLE_BT = 200;//
    public static final int SCAN_PERIOD = 10000;//
    public static final int REQUEST_AUTO_SETTING = 400;//
    public static final int HZ_NUM = 7;//

    public static final String USER_SEX_MALE = "男";
    public static final String USER_SEX_FEMALE = "女";
    public static final String USER_SEX_NOSAY = "保密";


    public static final String URL_APK_UPDATE   = URL+"/update_apk/check_update";//APK升级
    public static final String URL_DOWNLOAD_URL = URL+"/app/download";//下载地址
    public static final String URL_DELETE_PURE_TEST_RESULT_URL = URL+"/report/simple/delete";//删除纯音测试结果
    public static final int ALEART_DIALOG_REQUEST_CODE = 666;
    public static final String WX_APP_ID = "wxb3bb403ffb925611";//微信开发平台申请的ID, 听测
//    public static final String WX_APP_ID = "wx76b136486670b85a";  //听见
    public static final String QQ_APP_ID = "1107805693";
    //    public static final String QQ_APP_ID = "1108896176";
    public static final String WX_APP_SECRET = "7606977a5dbff2e1e95ffac6fd672a67";
    public static final int HEAR_AGE_REQUEST = 0x999;
    public static final String NET_STATE_SUCCESS = "A000000";
    public static final String NET_STATE_FAILED = "A000001";
    public static final String NET_STATE_BONDED = "E100402";
    public static final int PHONE_LOGIN = 1;
    public static final int WECHAT_LOGIN = 2;
    public static final int QQ_LOGIN = 3;
    public static final int XCM = 80;     //CHART_MARGINE
    public static final int YCM = 60;     //CHART_MARGINE
    public static final String[] Xlabel = {"125Hz","250", "500", "", "1000", "1500", "2000", "3000", "4000", "6000Hz", "8000Hz"};
    public static final String[] Ylabel = {MyApp.getAppContext().getString(R.string.chart_small), "10", "20", "30", "40" , "50" , "60" , "70", "80", "90", ""};

//    正常情况下 会有以下三种错误码：
    public static final String ERROR_CODE_UPDATE_IS_NEWEST = "E100520";// 当前已经是最新版本
    public static final String ERROR_CODE_FIND_UPDATE_NO_FORCE = "E100510";// 检测到新版本 不需要强制升级
    public static final String ERROR_CODE_FIND_UPDATE_FORCE = "E100511";// 检测到新版本 不需要强制升级
    public static final String ERROR_CODE_UPDATE_NOT_IN_DATABASE = "E100521";// 用户传上来的版本号在数据库中查不到
    public static final String ERROR_CODE_UPDATE_DATABASE = "E100901 ";// 数据库错误

    public static int[] getHz() {
        int q1 = (int) (Math.random() * 10);
        int[] hzArr1;
        switch (q1) {
            //sv private int[] hzArr = new int[]{250,500,1000,2000,3000,4000,6000};   //4型，筛查、监测类， 70db
            //sv private int[] hzArr = new int[]{250,500,1000,2000,3000,4000,6000,8000};   //3型，基本诊断，70-100db
            //sv private int[] hzArr = new int[]{125,250,500,1000,1500,2000,3000,4000,6000,8000};   //3型，基本诊断，70-100db
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:                hzArr1 = new int[]{1000, 2000, 3000, 4000, 6000,  500, 250};                break;
            case 6:
            case 7:                hzArr1 = new int[]{1000, 2000, 3000, 4000, 8000,  250, 500};                break;
            case 8:
            case 9:                hzArr1 = new int[]{1500, 3000, 6000, 8000, 500,  250,  125};                 break;
            default:               hzArr1 = new int[]{1000, 2000, 4000, 6000, 125,  500,  1500};        }
        return hzArr1;
    }

    public static int[] getDb() {
        //sv private final int[] dBArr = new int[]{-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};
        return new int[]{10, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};  //50 60 65 75 80 90
    }

    public static String getWave(int hz, int db){
        int ihd = hz + db;
        String wavName;
        switch (ihd){
            case 125+10 : wavName = "125/seeingvoice.com_125Hz10_65_3s.wav";                break;
            case 125+20 : wavName = "125/seeingvoice.com_125Hz20_58_3s.wav";                break;
            case 125+25 : wavName = "125/seeingvoice.com_125Hz25_54_3s.wav";                break;
            case 125+30 : wavName = "125/seeingvoice.com_125Hz30_50_3s.wav";                break;
            case 125+35 : wavName = "125/seeingvoice.com_125Hz35_47_3s.wav";                break;
            case 125+40 : wavName = "125/seeingvoice.com_125Hz40_43_3s.wav";                break;
            case 125+45 : wavName = "125/seeingvoice.com_125Hz45_40_3s.wav";                break;
            case 125+50 : wavName = "125/seeingvoice.com_125Hz50_36_3s.wav";                break;
            case 125+55 : wavName = "125/seeingvoice.com_125Hz55_32_3s.wav";                break;
            case 125+60 : wavName = "125/seeingvoice.com_125Hz60_29_3s.wav";                break;
            case 125+65 : wavName = "125/seeingvoice.com_125Hz65_25_3s.wav";                break;
            case 125+70 : wavName = "125/seeingvoice.com_125Hz70_22_3s.wav";                break;
            case 125+75 : wavName = "125/seeingvoice.com_125Hz75_18_3s.wav";                break;
            case 125+80 : wavName = "125/seeingvoice.com_125Hz80_14_3s.wav";                break;
            case 125+85 : wavName = "125/seeingvoice.com_125Hz85_11_3s.wav";                break;
            case 125+90 : wavName = "125/seeingvoice.com_125Hz90_7_3s.wav";                break;
            case 125+95 : wavName = "125/seeingvoice.com_125Hz95_4_3s.wav";                break;
            case 125+100: wavName = "125/seeingvoice.com_125Hz100_0_3s.wav";                break;

            case 250+10 : wavName = "250/seeingvoice.com_250Hz10_65_3s.wav";                break;
            case 250+20 : wavName = "250/seeingvoice.com_250Hz20_58_3s.wav";                break;
            case 250+25 : wavName = "250/seeingvoice.com_250Hz25_54_3s.wav";                break;
            case 250+30 : wavName = "250/seeingvoice.com_250Hz30_50_3s.wav";                break;
            case 250+35 : wavName = "250/seeingvoice.com_250Hz35_47_3s.wav";                break;
            case 250+40 : wavName = "250/seeingvoice.com_250Hz40_43_3s.wav";                break;
            case 250+45 : wavName = "250/seeingvoice.com_250Hz45_40_3s.wav";                break;
            case 250+50 : wavName = "250/seeingvoice.com_250Hz50_36_3s.wav";                break;
            case 250+55 : wavName = "250/seeingvoice.com_250Hz55_32_3s.wav";                break;
            case 250+60 : wavName = "250/seeingvoice.com_250Hz60_29_3s.wav";                break;
            case 250+65 : wavName = "250/seeingvoice.com_250Hz65_25_3s.wav";                break;
            case 250+70 : wavName = "250/seeingvoice.com_250Hz70_22_3s.wav";                break;
            case 250+75 : wavName = "250/seeingvoice.com_250Hz75_18_3s.wav";                break;
            case 250+80 : wavName = "250/seeingvoice.com_250Hz80_14_3s.wav";                break;
            case 250+85 : wavName = "250/seeingvoice.com_250Hz85_11_3s.wav";                break;
            case 250+90 : wavName = "250/seeingvoice.com_250Hz90_7_3s.wav";                break;
            case 250+95 : wavName = "250/seeingvoice.com_250Hz95_4_3s.wav";                break;
            case 250+100: wavName = "250/seeingvoice.com_250Hz100_0_3s.wav";                break;

            case 500+10 : wavName = "500/seeingvoice.com_500Hz10_65_3s.wav";                break;
            case 500+20 : wavName = "500/seeingvoice.com_500Hz20_58_3s.wav";                break;
            case 500+25 : wavName = "500/seeingvoice.com_500Hz25_54_3s.wav";                break;
            case 500+30 : wavName = "500/seeingvoice.com_500Hz30_50_3s.wav";                break;
            case 500+35 : wavName = "500/seeingvoice.com_500Hz35_47_3s.wav";                break;
            case 500+40 : wavName = "500/seeingvoice.com_500Hz40_43_3s.wav";                break;
            case 500+45 : wavName = "500/seeingvoice.com_500Hz45_40_3s.wav";                break;
            case 500+50 : wavName = "500/seeingvoice.com_500Hz50_36_3s.wav";                break;
            case 500+55 : wavName = "500/seeingvoice.com_500Hz55_32_3s.wav";                break;
            case 500+60 : wavName = "500/seeingvoice.com_500Hz60_29_3s.wav";                break;
            case 500+65 : wavName = "500/seeingvoice.com_500Hz65_25_3s.wav";                break;
            case 500+70 : wavName = "500/seeingvoice.com_500Hz70_22_3s.wav";                break;
            case 500+75 : wavName = "500/seeingvoice.com_500Hz75_18_3s.wav";                break;
            case 500+80 : wavName = "500/seeingvoice.com_500Hz80_14_3s.wav";                break;
            case 500+85 : wavName = "500/seeingvoice.com_500Hz85_11_3s.wav";                break;
            case 500+90 : wavName = "500/seeingvoice.com_500Hz90_7_3s.wav";                break;
            case 500+95 : wavName = "500/seeingvoice.com_500Hz95_4_3s.wav";                break;
            case 500+100: wavName = "500/seeingvoice.com_500Hz100_0_3s.wav";                break;

            case 1000+10 : wavName = "1000/seeingvoice.com_1000Hz10_65_3s.wav";                break;
            case 1000+20 : wavName = "1000/seeingvoice.com_1000Hz20_58_3s.wav";                break;
            case 1000+25 : wavName = "1000/seeingvoice.com_1000Hz25_54_3s.wav";                break;
            case 1000+30 : wavName = "1000/seeingvoice.com_1000Hz30_50_3s.wav";                break;
            case 1000+35 : wavName = "1000/seeingvoice.com_1000Hz35_47_3s.wav";                break;
            case 1000+40 : wavName = "1000/seeingvoice.com_1000Hz40_43_3s.wav";                break;
            case 1000+45 : wavName = "1000/seeingvoice.com_1000Hz45_40_3s.wav";                break;
            case 1000+50 : wavName = "1000/seeingvoice.com_1000Hz50_36_3s.wav";                break;
            case 1000+55 : wavName = "1000/seeingvoice.com_1000Hz55_32_3s.wav";                break;
            case 1000+60 : wavName = "1000/seeingvoice.com_1000Hz60_29_3s.wav";                break;
            case 1000+65 : wavName = "1000/seeingvoice.com_1000Hz65_25_3s.wav";                break;
            case 1000+70 : wavName = "1000/seeingvoice.com_1000Hz70_22_3s.wav";                break;
            case 1000+75 : wavName = "1000/seeingvoice.com_1000Hz75_18_3s.wav";                break;
            case 1000+80 : wavName = "1000/seeingvoice.com_1000Hz80_14_3s.wav";                break;
            case 1000+85 : wavName = "1000/seeingvoice.com_1000Hz85_11_3s.wav";                break;
            case 1000+90 : wavName = "1000/seeingvoice.com_1000Hz90_7_3s.wav";                break;
            case 1000+95 : wavName = "1000/seeingvoice.com_1000Hz95_4_3s.wav";                break;
            case 1000+100: wavName = "1000/seeingvoice.com_1000Hz100_0_3s.wav";                break;

            case 1500+10 : wavName = "1500/seeingvoice.com_1500Hz10_65_3s.wav";                break;
            case 1500+20 : wavName = "1500/seeingvoice.com_1500Hz20_58_3s.wav";                break;
            case 1500+25 : wavName = "1500/seeingvoice.com_1500Hz25_54_3s.wav";                break;
            case 1500+30 : wavName = "1500/seeingvoice.com_1500Hz30_50_3s.wav";                break;
            case 1500+35 : wavName = "1500/seeingvoice.com_1500Hz35_47_3s.wav";                break;
            case 1500+40 : wavName = "1500/seeingvoice.com_1500Hz40_43_3s.wav";                break;
            case 1500+45 : wavName = "1500/seeingvoice.com_1500Hz45_40_3s.wav";                break;
            case 1500+50 : wavName = "1500/seeingvoice.com_1500Hz50_36_3s.wav";                break;
            case 1500+55 : wavName = "1500/seeingvoice.com_1500Hz55_32_3s.wav";                break;
            case 1500+60 : wavName = "1500/seeingvoice.com_1500Hz60_29_3s.wav";                break;
            case 1500+65 : wavName = "1500/seeingvoice.com_1500Hz65_25_3s.wav";                break;
            case 1500+70 : wavName = "1500/seeingvoice.com_1500Hz70_22_3s.wav";                break;
            case 1500+75 : wavName = "1500/seeingvoice.com_1500Hz75_18_3s.wav";                break;
            case 1500+80 : wavName = "1500/seeingvoice.com_1500Hz80_14_3s.wav";                break;
            case 1500+85 : wavName = "1500/seeingvoice.com_1500Hz85_11_3s.wav";                break;
            case 1500+90 : wavName = "1500/seeingvoice.com_1500Hz90_7_3s.wav";                break;
            case 1500+95 : wavName = "1500/seeingvoice.com_1500Hz95_4_3s.wav";                break;
            case 1500+100: wavName = "1500/seeingvoice.com_1500Hz100_0_3s.wav";                break;

            case 2000+10 : wavName = "2000/seeingvoice.com_2000Hz10_65_3s.wav";                break;
            case 2000+20 : wavName = "2000/seeingvoice.com_2000Hz20_58_3s.wav";                break;
            case 2000+25 : wavName = "2000/seeingvoice.com_2000Hz25_54_3s.wav";                break;
            case 2000+30 : wavName = "2000/seeingvoice.com_2000Hz30_50_3s.wav";                break;
            case 2000+35 : wavName = "2000/seeingvoice.com_2000Hz35_47_3s.wav";                break;
            case 2000+40 : wavName = "2000/seeingvoice.com_2000Hz40_43_3s.wav";                break;
            case 2000+45 : wavName = "2000/seeingvoice.com_2000Hz45_40_3s.wav";                break;
            case 2000+50 : wavName = "2000/seeingvoice.com_2000Hz50_36_3s.wav";                break;
            case 2000+55 : wavName = "2000/seeingvoice.com_2000Hz55_32_3s.wav";                break;
            case 2000+60 : wavName = "2000/seeingvoice.com_2000Hz60_29_3s.wav";                break;
            case 2000+65 : wavName = "2000/seeingvoice.com_2000Hz65_25_3s.wav";                break;
            case 2000+70 : wavName = "2000/seeingvoice.com_2000Hz70_22_3s.wav";                break;
            case 2000+75 : wavName = "2000/seeingvoice.com_2000Hz75_18_3s.wav";                break;
            case 2000+80 : wavName = "2000/seeingvoice.com_2000Hz80_14_3s.wav";                break;
            case 2000+85 : wavName = "2000/seeingvoice.com_2000Hz85_11_3s.wav";                break;
            case 2000+90 : wavName = "2000/seeingvoice.com_2000Hz90_7_3s.wav";                break;
            case 2000+95 : wavName = "2000/seeingvoice.com_2000Hz95_4_3s.wav";                break;
            case 2000+100: wavName = "2000/seeingvoice.com_2000Hz100_0_3s.wav";                break;

            case 3000+10 : wavName = "3000/seeingvoice.com_3000Hz10_65_3s.wav";                break;
            case 3000+20 : wavName = "3000/seeingvoice.com_3000Hz20_58_3s.wav";                break;
            case 3000+25 : wavName = "3000/seeingvoice.com_3000Hz25_54_3s.wav";                break;
            case 3000+30 : wavName = "3000/seeingvoice.com_3000Hz30_50_3s.wav";                break;
            case 3000+35 : wavName = "3000/seeingvoice.com_3000Hz35_47_3s.wav";                break;
            case 3000+40 : wavName = "3000/seeingvoice.com_3000Hz40_43_3s.wav";                break;
            case 3000+45 : wavName = "3000/seeingvoice.com_3000Hz45_40_3s.wav";                break;
            case 3000+50 : wavName = "3000/seeingvoice.com_3000Hz50_36_3s.wav";                break;
            case 3000+55 : wavName = "3000/seeingvoice.com_3000Hz55_32_3s.wav";                break;
            case 3000+60 : wavName = "3000/seeingvoice.com_3000Hz60_29_3s.wav";                break;
            case 3000+65 : wavName = "3000/seeingvoice.com_3000Hz65_25_3s.wav";                break;
            case 3000+70 : wavName = "3000/seeingvoice.com_3000Hz70_22_3s.wav";                break;
            case 3000+75 : wavName = "3000/seeingvoice.com_3000Hz75_18_3s.wav";                break;
            case 3000+80 : wavName = "3000/seeingvoice.com_3000Hz80_14_3s.wav";                break;
            case 3000+85 : wavName = "3000/seeingvoice.com_3000Hz85_11_3s.wav";                break;
            case 3000+90 : wavName = "3000/seeingvoice.com_3000Hz90_7_3s.wav";                break;
            case 3000+95 : wavName = "3000/seeingvoice.com_3000Hz95_4_3s.wav";                break;
            case 3000+100: wavName = "3000/seeingvoice.com_3000Hz100_0_3s.wav";                break;

            case 4000+10 : wavName = "4000/seeingvoice.com_4000Hz10_65_3s.wav";                break;
            case 4000+20 : wavName = "4000/seeingvoice.com_4000Hz20_58_3s.wav";                break;
            case 4000+25 : wavName = "4000/seeingvoice.com_4000Hz25_54_3s.wav";                break;
            case 4000+30 : wavName = "4000/seeingvoice.com_4000Hz30_50_3s.wav";                break;
            case 4000+35 : wavName = "4000/seeingvoice.com_4000Hz35_47_3s.wav";                break;
            case 4000+40 : wavName = "4000/seeingvoice.com_4000Hz40_43_3s.wav";                break;
            case 4000+45 : wavName = "4000/seeingvoice.com_4000Hz45_40_3s.wav";                break;
            case 4000+50 : wavName = "4000/seeingvoice.com_4000Hz50_36_3s.wav";                break;
            case 4000+55 : wavName = "4000/seeingvoice.com_4000Hz55_32_3s.wav";                break;
            case 4000+60 : wavName = "4000/seeingvoice.com_4000Hz60_29_3s.wav";                break;
            case 4000+65 : wavName = "4000/seeingvoice.com_4000Hz65_25_3s.wav";                break;
            case 4000+70 : wavName = "4000/seeingvoice.com_4000Hz70_22_3s.wav";                break;
            case 4000+75 : wavName = "4000/seeingvoice.com_4000Hz75_18_3s.wav";                break;
            case 4000+80 : wavName = "4000/seeingvoice.com_4000Hz80_14_3s.wav";                break;
            case 4000+85 : wavName = "4000/seeingvoice.com_4000Hz85_11_3s.wav";                break;
            case 4000+90 : wavName = "4000/seeingvoice.com_4000Hz90_7_3s.wav";                break;
            case 4000+95 : wavName = "4000/seeingvoice.com_4000Hz95_4_3s.wav";                break;
            case 4000+100: wavName = "4000/seeingvoice.com_4000Hz100_0_3s.wav";                break;

            case 6000+10 : wavName = "6000/seeingvoice.com_6000Hz10_65_3s.wav";                break;
            case 6000+20 : wavName = "6000/seeingvoice.com_6000Hz20_58_3s.wav";                break;
            case 6000+25 : wavName = "6000/seeingvoice.com_6000Hz25_54_3s.wav";                break;
            case 6000+30 : wavName = "6000/seeingvoice.com_6000Hz30_50_3s.wav";                break;
            case 6000+35 : wavName = "6000/seeingvoice.com_6000Hz35_47_3s.wav";                break;
            case 6000+40 : wavName = "6000/seeingvoice.com_6000Hz40_43_3s.wav";                break;
            case 6000+45 : wavName = "6000/seeingvoice.com_6000Hz45_40_3s.wav";                break;
            case 6000+50 : wavName = "6000/seeingvoice.com_6000Hz50_36_3s.wav";                break;
            case 6000+55 : wavName = "6000/seeingvoice.com_6000Hz55_32_3s.wav";                break;
            case 6000+60 : wavName = "6000/seeingvoice.com_6000Hz60_29_3s.wav";                break;
            case 6000+65 : wavName = "6000/seeingvoice.com_6000Hz65_25_3s.wav";                break;
            case 6000+70 : wavName = "6000/seeingvoice.com_6000Hz70_22_3s.wav";                break;
            case 6000+75 : wavName = "6000/seeingvoice.com_6000Hz75_18_3s.wav";                break;
            case 6000+80 : wavName = "6000/seeingvoice.com_6000Hz80_14_3s.wav";                break;
            case 6000+85 : wavName = "6000/seeingvoice.com_6000Hz85_11_3s.wav";                break;
            case 6000+90 : wavName = "6000/seeingvoice.com_6000Hz90_7_3s.wav";                break;
            case 6000+95 : wavName = "6000/seeingvoice.com_6000Hz95_4_3s.wav";                break;
            case 6000+100: wavName = "6000/seeingvoice.com_6000Hz100_0_3s.wav";                break;

            case 8000+10 : wavName = "8000/seeingvoice.com_8000Hz10_65_3s.wav";                break;
            case 8000+20 : wavName = "8000/seeingvoice.com_8000Hz20_58_3s.wav";                break;
            case 8000+25 : wavName = "8000/seeingvoice.com_8000Hz25_54_3s.wav";                break;
            case 8000+30 : wavName = "8000/seeingvoice.com_8000Hz30_50_3s.wav";                break;
            case 8000+35 : wavName = "8000/seeingvoice.com_8000Hz35_47_3s.wav";                break;
            case 8000+40 : wavName = "8000/seeingvoice.com_8000Hz40_43_3s.wav";                break;
            case 8000+45 : wavName = "8000/seeingvoice.com_8000Hz45_40_3s.wav";                break;
            case 8000+50 : wavName = "8000/seeingvoice.com_8000Hz50_36_3s.wav";                break;
            case 8000+55 : wavName = "8000/seeingvoice.com_8000Hz55_32_3s.wav";                break;
            case 8000+60 : wavName = "8000/seeingvoice.com_8000Hz60_29_3s.wav";                break;
            case 8000+65 : wavName = "8000/seeingvoice.com_8000Hz65_25_3s.wav";                break;
            case 8000+70 : wavName = "8000/seeingvoice.com_8000Hz70_22_3s.wav";                break;
            case 8000+75 : wavName = "8000/seeingvoice.com_8000Hz75_18_3s.wav";                break;
            case 8000+80 : wavName = "8000/seeingvoice.com_8000Hz80_14_3s.wav";                break;
            case 8000+85 : wavName = "8000/seeingvoice.com_8000Hz85_11_3s.wav";                break;
            case 8000+90 : wavName = "8000/seeingvoice.com_8000Hz90_7_3s.wav";                break;
            case 8000+95 : wavName = "8000/seeingvoice.com_8000Hz95_4_3s.wav";                break;
            case 8000+100: wavName = "8000/seeingvoice.com_8000Hz100_0_3s.wav";                break;

            default:     wavName = "mihuan.wav";
        }
        return wavName;
    }
}

