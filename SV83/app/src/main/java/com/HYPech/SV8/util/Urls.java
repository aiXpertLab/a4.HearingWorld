package com.HYPech.SV8.util;

public class Urls {


//    public static final String SERVER = "http://api.seeingvoice.com/svglass/";
    public static final String SERVER = "http://114.116.148.87/svglass/";
    //硬件测试升级
//public static final String SERVER = "http://192.168.1.239:5000/svglass/";


    public static final String URL_GET_UPDATE = SERVER + "update_firmware/check_update";

    public static final String URL_GET_MESSAGE_CODE = SERVER + "message/send_message";

    public static final String URL_GET_APP_UPDATE = SERVER + "update_apk/check_update";

    public static final String URL_GET_MESSAGE_CODE_VERFY = SERVER +"message/verify_message";

    public static final String URL_POST_REGISTER = SERVER +"user/message/register";

    public static final String URL_POST_FEEDBACK = SERVER + "feedback/submit_feedback";

    //TODO 废弃
    public static final String URL_GET_DOWNLOAD = SERVER + "glass_update/download/";


}
