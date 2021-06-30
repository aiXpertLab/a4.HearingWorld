package com.seeingvoice.www.svhearing;

/**
 * Date:2019/5/16
 * Time:14:51
 * auther:zyy
 */
public class AppConstant {
    /** 此APP所用到的常量*/
    public final static String SV_EARBUD_MAC = "9C:43:1E:05:4A:F1";
    public final static int MY_PERMISSIONS_REQUEST = 100;
    public static final int NOT_NOTICE = 2;//如果勾选了不再询问
    public static final int AUDIO_REQUEST_CODE = 3;//如果勾选了不再询问
    public static final int REQUEST_CODE_LOCATION_SETTINGS = 10;//
    public static final int REQUEST_ENABLE_BT = 200;//
    public static final int SCAN_PERIOD = 10000;//
    public static final int RESULT_SEARCH_SV = 300;//
    public static final int REQUEST_AUTO_SETTING = 400;//
    public static final int HEADSET_ALERT_RESULT = 500;//
    public static final int HZ_NUM = 7;//
    public static final String SEARCHED_INTENT_TAG = "SearchSVEarbudsActivity";
//    public static final String URL = "http://api.seeingvoice.com/";
//    public static final String URL = "http://114.115.131.152:8000/v1";//生产服务器不用了
//    public static final String URL = "http://114.116.148.87:8000/svheard";//开发服务器
//    public static final String URL = "http://api.seeingvoice.com/svheard";//生产服务器
//    public static final String URL = "http://114.115.131.152:7999/svheard";//生产服务器上线前测试用
//    public static final String URL = "http://114.115.131.152/svheard";//临时测试用
    public static final String URL = "http://192.168.1.239:10088";//239测试用
    public static final String LOGIN_URL = URL+"/user/tel/login";//手机登录
//    public static final String SEND_WECHAT_CODE = URL+"/user/wechat/login?wechat_code=";//微信登录
    public static final String SEND_WECHAT_CODE = URL+"/db/get_wechat_token?wechat_code=";//临时微信登录
    public static final String QQ_LOGIN_SEND_OPEN_ID = URL+"/user/qq/login";//QQ登录发送open ID
    public static final String FEEDBACK_URL = URL+"/policy/feedback/submit_feedback";//意见反馈接口
    public static final String GET_VERIFY_CODE_URL = URL+"/message/send_message_2";//找回密码，申请获得验证码
    public static final String USER_VERIFY_URL = URL+"/message/verify_message";//找回密码，验证验证码
    public static final String COMFIRM_FINDBACK_PWS_URL = URL+"/user/retrieve_password";//找回密码，验证验证码
    public static final String USER_REGISTER_GET_VERIFY_CODE_URL = URL+"/message/send_message";//注册时申请验证码
    public static final String USER_REGISTER_VERIFY_URL = URL+"/message/verify_message";//注册时验证验证码
    public static final String USER_REGISTER_INFO_SUBBMIT_URL = URL+"/user/message/register";// 注册手机信息

    public static final String URL_DISCLAIMER = URL+"/policy/privacy";//隐私政策
    public static final String URL_TERMS_OF_SERVICE = URL+"/policy/privacy";//隐私政策  ============这里找小耿改一下
    public static final String URL_PURE_COURSE = URL+"/policy/tutorial";//纯音测试教程
    public static final String URL_ABOUT_US = URL+"/policy/about";//关于我们
//    public static final String WECHAT_BOND_PHONE = URL+"/api/v0/user/binding_tel/binding_tel";//微信绑定手机
    public static final String NEW_PURE_TEST_RESULT_SUBBMIT = URL+"/report/simple/submit_simple_10";//提交新版纯音结果
    public static final String PURE_TEST_RESULT_SUBBMIT = URL+"/report/simple/submit_simple";//提交纯音结果
    public static final String PURE_TEST_HISTORY_URL = URL+"/report/simple/request_simple_list";//得到纯音测试结果
    public static final String PURE_HISTORY_ITEM_URL = URL+"/report/simple/request_simple_detail";//纯音测试结果item
    public static final String PURE_HISTORY_ADD_REMARK_URL = URL+"/report/simple/submit_remark";//纯音历史详情添加备注
    public static final String VERBAL_TEST_RESULT_URL = URL+"/report/language/submit_language";//言语测试结果提交
    public static final String VERBAL_TEST_RESULT_LIST = URL+"/report/language/request_language_list";//言语测试历史记录
    public static final String HEAR_AGE_TEST_RESULT_URL = URL+"/report/age/submit_age";//听力年龄测试结果提交
    public static final String AGE_TEST_RESULT_LIST = URL+"/report/age/request_age_list";//年龄测试历史记录
    public static final String AGE_TEST_DELETE = URL+"/report/age/delete";//年龄测试历史记录
//    public static final String PURE_RESULT_ADD_REMARK_URL = URL+"/api/v0/report/simple/add_remark";//添加备注
    public static final String CHECK_WECHAT_BOND_PHONE_STATE = URL+"/binding/verify_id";//检测微信账号是否绑定手机
    public static final String WECHAT_BOND_PHONE = URL+"/binding/set_pwd";//检测微信账号是否绑定手机
    public static final String WECHAT_BOND_PHONE_CHECK_PHONE = URL+"/binding/verify_tel";//微信绑定手机，检查手机是否能用
    public static final String WECHAT_START_BOND_PHONE = URL+"/binding/binding_tel";//微信执行绑定
    public static final String URL_APK_UPDATE = URL+"/update_apk/check_update";//APK升级
    public static final String URL_DOWNLOAD_URL = URL+"/app/download";//下载地址
    public static final String URL_DELETE_PURE_TEST_RESULT_URL = URL+"/report/simple/delete";//删除纯音测试结果



    public static final int ALEART_DIALOG_REQUEST_CODE = 666;
    public static final int CLOSE_LOAD = 500;
//    public static final String WX_APP_ID = "wxb3bb403ffb925611";//这个是见声听力测试的  后面需要改一下  从包名得到新的签名
    public static final String WX_APP_ID = "wx76b136486670b85a";

//    public static final String QQ_APP_ID = "1108896176";//这是旧的应用宝账号的 见声听见APP 的id
    public static final String QQ_APP_ID = "1110040011";//这是旧的应用宝账号的 见声听见APP 的id
//    public static final String WX_APP_SECRET = "c8bed1b3884a095c6748a738276d45c6";
    public static final String WX_APP_SECRET = "7606977a5dbff2e1e95ffac6fd672a67";
    public static final int HzNums = 9;//
    public static final int HEAR_AGE_ANSWER_NUMS = 2;
    public static final int HEAR_AGE_REQUEST = 0x999;
    public static final String XIAOMI_ID = "2882303761518070266";
    public static final String XIAOMI_KEY = "5841807023266";
    public static final String NET_STATE_SUCCESS = "A000000";
    public static final String NET_STATE_FAILED = "A000001";
    public static final String NET_STATE_BONDED = "E100402";
    public static final int PHONE_LOGIN = 1;
    public static final int WECHAT_LOGIN = 2;
    public static final int QQ_LOGIN = 3;
    public static final int QQ_DEFAULT_USER_ID = 1;
//    public static final String[] Xlabel = {"125","250", "500", "750","1K", "1.5K", "2K", "3K", "4K", "6K", "8K"};
    public static final String[] Xlabel = {"125","250", "500", "750", "1K", "1.5K", "2K", "3K", "4K", "6K", "8K"};
//    public static final String[] Xlabel7 = {"125","250", "0.5K", "1K", "2K", "4K","8K"};
    public static final int[] Ylabel = {-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50,
            55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};
//    正常情况下 会有以下三种错误码：
    public static final String ERROR_CODE_UPDATE_IS_NEWEST = "E100520";// 当前已经是最新版本
    public static final String ERROR_CODE_FIND_UPDATE_NO_FORCE = "E100510";// 检测到新版本 不需要强制升级
    public static final String ERROR_CODE_FIND_UPDATE_FORCE = "E100511";// 检测到新版本 不需要强制升级
//    非正常情况下
    public static final String ERROR_CODE_UPDATE_NOT_IN_DATABASE = "E100521";// 用户传上来的版本号在数据库中查不到
    public static final String ERROR_CODE_UPDATE_DATABASE = "E100901 ";// 数据库错误
    public static final Integer REQUEST_MUSIC_EQ_PARAMS = 600;// 数据库错误
    public static final Integer OLD_PURE_RESULT_SIZE = 9;// 旧版的纯音频段
    public static final Integer NEW_PURE_RESULT_SIZE = 10;// 新版的纯音频段
    public static final Integer NUM_BAND_VIEWS = 10;
}
