package com.hypech.SV3.util;

import com.hypech.SV3.App;
import com.kongzue.btutil.SPPLinkUtil2;

public class BleMsgUtil {

    private static final String TAG = BleMsgUtil.class.getSimpleName();

    public static void sendMsgBle(byte[] data, SPPLinkUtil2 sppUtil) {

        LogUtils.d(TAG,"send Data ...///");
        sppUtil.sendMsg(data);
    }

    public static void loginRes(SPPLinkUtil2 sppUtil) {
        StringBuilder resultStr = new StringBuilder();
        StringBuilder contentStr = new StringBuilder();
        String timeStr = ConvertData.getCurrentTimeHexStr();
        //TODO 数据长度未计算
        contentStr.append("000981").append(timeStr).append("00");
        String replaceStr = contentStr.toString();
        resultStr.append("7f").append(CommonUtil.specialCharReplace(replaceStr+ConvertData.makeChecksum(contentStr.toString()))).append("f7");
        byte[] bytes = ConvertData.hexStringToBytes(resultStr.toString());
        LogUtils.d(TAG,"登录发送给眼镜数据："+ConvertData.bytesToHexString(bytes,false));
        sendMsgBle(bytes,sppUtil);
    }

    public static void HeartRes(SPPLinkUtil2 sppUtil) {
        String sendString = "7f0005820087f7";
        //TODO 数据长度未计算
        byte[] bytes = ConvertData.hexStringToBytes(sendString.toString());
        sendMsgBle(bytes,sppUtil);
    }
    public static void rotateScreen(SPPLinkUtil2 sppUtil){
        String left = "7f00050600"+ConvertData.makeChecksum("00050600")+"f7";
        String right = "7f00050601"+ConvertData.makeChecksum("00050601")+"f7";
        byte[] bytes;
        //TODO 数据长度未计算
        if(App.myApplication.getRotateDevice().equals("0")) {
            bytes = ConvertData.hexStringToBytes(left);
        }else{
            bytes = ConvertData.hexStringToBytes(right);
        }
        LogUtils.d(TAG,"旋转左右！"+ConvertData.bytesToHexString(bytes, false));
        sendMsgBle(bytes,sppUtil);
    }


}
