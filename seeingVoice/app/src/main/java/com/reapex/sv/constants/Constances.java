package com.reapex.sv.constants;

import java.util.UUID;

public class Constances {
    public static int BAIDU_SDK = 0;
    public static int TENGXUN_SDK=1;
    public static int XUNFEI_SDK = 2;
    public static UUID UUID_SERVICE_READ = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_CHARACTER_READ = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_SERVICE_WRITE = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_CHARACTER_WRITE = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
    public static int FONT_SIZE_GLASSES1 =1 ;
    public static int FONT_SIZE_GLASSES2 =2 ;
    public static int FONT_SIZE_GLASSES3 =3 ;
//    public static int FONT_SIZE_GLASSES4 =24 ;
//    public static int FONT_SIZE_GLASSES5 =28 ;
//    public static int FONT_SIZE_GLASSES6 =32 ;
//    public static int FONT_SIZE_GLASSES7 =36 ;
//    public static int FONT_SIZE_GLASSES8 =40 ;
    /**
     * 表明一帧数据的开始0x7F
     * 登录响应0x81
     心跳包响应0x82
     设置字体大小0x03
     文本消息0x04
     翻页0x05
     */
    public static final byte START_END =(byte)0x7F;
    public static final byte LOGIN_RESPONSE = (byte)0x81;
    public static final byte HEART_RESPONSE = (byte)0x82;
    public static final byte FONT_SIZE = (byte)0x03;
    public static final byte TEXT_MESSAGE = (byte)0x04;
    public static final byte  PAGE_TURNING= (byte)0x05;

    //一次发送最多字节数
    public static final int MAX_SIZE = 150;
    //多少个字放到一个队列中排队
    public static final int MAX_SIZE_CHAR = 150;
    public static final int MAX_SIZE_40 = 40 ;
    //1秒等不到服务器握手回应，自动发送下一条数据
    public static final int REQUEST_TIMEOUT = 2;
    public static final int REFRESH_UI_BATTERY = 21;
    public static final int CONNECT_SUCCESS = 22;
    public static final int GETDATA_FROM_SERVER = 23;
    public static final int CONNECT_FAILED = 24;
    public static final int START_BLE_DATA_SEND = 25;
    public static final int BLE_DISCONNECT = 26;
    public static final int UPDATE_FINISH_SUCCESS = 27;
    public static final int UPDATE_FINISH_FAILED = 28;
    public static final int NO_NEED_TO_UPDATE = 29;

    public static final int MSG_ON_DEVICE_FIND = 30;
    public static final int BLE_STATUS_OPEN = 31;
    public static final int BLE_STATUS_CLOSED = 32;
    public static final int OKGO_ON_ERROR = 33;
    public static final int GLASSES_SETTING_CHANGE = 34;
    public static final int GLASSES_SETTING_CHANGE2 = 35;
    public static final int NO_NEED_TO_UPDATE_GLASSES = 36;
    public static final int MAC_BIND_CHANGE = 37;
    public static final int CONNECT_STARTLINK = 38;




    private static final int REQUEST_ENABLE_BT = 1;
    private static final int   REQUEST_SETTING = 2;
    public static final int RESULT_ROTATE = 3;
    public static final int RESULT_MAC_CHANGE =4;
    public static final int REQUEST_CODE_UNKNOWN_APP = 5;

    public static final String FONT_CHANGE_INFO = "font";
    public static final String ROTATION_CHANGE_INFO = "rotation";
    public static final String BRIGHT_CHANGE_INFO = "font";
}
