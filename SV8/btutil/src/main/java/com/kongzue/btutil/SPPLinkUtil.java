package com.kongzue.btutil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.kongzue.btutil.interfaces.BtLinkReport;
import com.kongzue.btutil.interfaces.OnBLEStatusChangeListener;
import com.kongzue.btutil.interfaces.OnBtSocketResponseListener;
import com.kongzue.btutil.interfaces.OnDevFindListener;
import com.kongzue.btutil.interfaces.OnLinkStatusChangeListener;
import com.kongzue.btutil.interfaces.RssiTemp;
import com.kongzue.btutil.util.ConvertData;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/9/2 16:31
 */
public class SPPLinkUtil {
    
    public static boolean DEBUGMODE = false;                //是否打印日志
//    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static final int ERROR_NO_DEVICE = -1;           //附近无设备
    public static final int ERROR_START_BT = -2;            //无法启动蓝牙
    public static final int ERROR_NOT_FOUND_DEVICE = -3;    //未找到目标设备
    public static final int ERROR_NOT_CONNECTED = -4;       //未建立连接
    public static final int ERROR_CONNECTED = -5;           //连接失败
    public static final int ERROR_BREAK = -50;              //连接中断
    public static final int ERROR_SOCKET_ERROR = -70;       //Socket故障

    private String UUIDStr = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private String readEndStr;                              //接收终止符（默认不设置即“\n”或“\r”或“\r\n”或“\n\r”）
    
    private BluetoothAdapter bluetooth;                     //获取本地蓝牙适配器，即蓝牙设备
    private BluetoothDevice device = null;                  //蓝牙设备
    private BluetoothSocket socket = null;                  //蓝牙通信socket
    private String btName;
    private static String btPairingCode = "1234";
    private InputStream is;                                 //输入流，用来接收蓝牙数据
    private Context context;
    
    private BtLinkReport btLinkReport;
    private OnLinkStatusChangeListener onLinkStatusChangeListener;
    private OnBtSocketResponseListener onBtSocketResponseListener;
    private OnDevFindListener onDevFindLIstener;
    private OnBLEStatusChangeListener onBLEStatusChangeListener;
    //TODO 测试RSSI 删除
    private RssiTemp rssiTemp;
    private boolean isSending;
    private DataOutputStream mOut;
    
    public boolean link(Context c, String btName) {
        this.btName = btName;
        this.context = c;
        if (!openBt(context)) {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "无法打开蓝牙设备，请检查设备蓝牙是否可用";
                        loge(msg);
                        if (btLinkReport != null) btLinkReport.onError(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onFailed(ERROR_NO_DEVICE);
                    }
                });
            } else {
                String msg = "无法打开蓝牙设备，请检查设备蓝牙是否可用";
                loge(msg);
                if (btLinkReport != null) btLinkReport.onError(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onFailed(ERROR_NO_DEVICE);
            }
            return false;
        } else {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "正在打开蓝牙...";
                        log(msg);
                        if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                        doOpenBluetooth(context);
                    }
                });
            } else {
                String msg = "正在打开蓝牙...";
                log(msg);
                if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                doOpenBluetooth(context);
            }
        }
        
        return true;
    }
    
    private Timer linkTimer;
    
    private void doOpenBluetooth(final Context context) {
        if (linkTimer != null) linkTimer.cancel();
        linkTimer = new Timer();
        linkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bluetooth.isEnabled() != false) {
                    doFind(context);
                    if (context != null && context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onLinkStatusChangeListener.onStartLink();
                            }
                        });
                    } else {
                        onLinkStatusChangeListener.onStartLink();
                    }
                    linkTimer.cancel();
                }
            }
        }, 1000, 1000);
    }
    
    private boolean isFinded = false;        //是否已经找到目标设备
    
    public void doFind(Context context) {
        //TODO 搜索蓝牙设备
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "正在查找设备...";
                    if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                }
            });
        } else {
            String msg = "正在查找设备...";
            if (btLinkReport != null) btLinkReport.onStatusChange(msg);
        }
        
        isFinded = false;
        allDevice = new ArrayList<>();
        
        //注册接收查找到设备action接收器
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        //注册查找结束action接收器
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));


        context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        
        //先关闭正在进行的查找
        if(bluetooth == null) {
            bluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        
        //开始查找目标设备
        bluetooth.startDiscovery();
    }
    
    private List<Map<String, Object>> allDevice;
    private Map<String, Object> selectDevice;
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //接口回调
                onDevFindLIstener.onDeviceFind(device);
                Map<String, Object> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());

                //TODO 测试RSSI 需要删除
//                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);//获取额外rssi值
//                if(device.getName()!=null&&device.getName().toLowerCase().contains("seeingvoice")) {
//                   rssiTemp.onShow("发现蓝牙设备：name: " + device.getName() + "   address:" + device.getAddress()+" rssi:"+rssi);
//                }
                log("发现蓝牙设备：name: " + device.getName() + "   address:" + device.getAddress());

                allDevice.add(map);
                for (Map<String, Object> m : allDevice) {
                    if(btName !=null) {
                        if (btName.equals(m.get("address"))) {
                            if (!isFinded) {
                                doLink(m);
                            }
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                onDevFindLIstener.onDeviceFindFinish();
                if (allDevice != null) {
                    if (allDevice.size() == 0) {
                        String msg = "附近没有任何可以连接的蓝牙设备";
                        linkStatusChange(true, ERROR_NO_DEVICE, msg);
                        
                        return;
                    } else {
                        if (!isFinded) {
                            boolean isNotHave = true;
                            for (Map<String, Object> map : allDevice) {
                                if (btName != null) {
                                    if (btName.equals(map.get("address"))) {
                                        isNotHave = false;
//                                        doLink(map);
                                    }
                                }
                            }
                            if (isNotHave) {
                                String msg = "未找到要连接的目标设备";
                                linkStatusChange(true, ERROR_NOT_FOUND_DEVICE, msg);
                                onDevFindLIstener.onNoDesDeviceFind();
                                return;
                            }
                        }
                    }
                }
            }else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                onBLEStatusChangeListener.onBleConnect();
                Log.d("ljp","蓝牙连接状态：连接");
            }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                onBLEStatusChangeListener.onBleDisConnect();
                Log.d("ljp","蓝牙连接状态：断开连接");
            }
        }
    };
    
    private boolean alreadyThread = false;
    private boolean bRun = true;
    private String fmsg = "";                  //保存用数据缓存
    
    private void doLink(final Map<String, Object> map) {
        
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        
        isFinded = true;
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "正在连接...";
                    log(msg);
                    if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                }
            });
        } else {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "正在连接...";
                        log(msg);
                        if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                    }
                });
            } else {
                String msg = "正在连接...";
                log(msg);
                if (btLinkReport != null) btLinkReport.onStatusChange(msg);
            }
        }
        
        //故意延迟1秒执行，因为此处会卡
        Timer timer = new Timer();//实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                device = bluetooth.getRemoteDevice(map.get("address") + "");
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //开始连接
                        Log.d("ljp","进入SPPLinkUtil timer schedule.");
                        try {
                            //socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                            int sdk = Build.VERSION.SDK_INT;
                            if (sdk >= 10) {
                                socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                            } else {
                                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                            }
                        } catch (IOException e) {
                            String msg = "创建socket失败";
                            linkStatusChange(true, ERROR_CONNECTED, msg);
                            return;
                        }
                        
                        try {
                            socket.connect();
                            String msg = "连接成功";
                            linkStatusChange(false, 0, msg);
                            Log.d("ljp","连接成功1");
//                            checkLink();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            try {
                                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                socket = (BluetoothSocket) m.invoke(device, 1);
                                socket.connect();
                                
                                String msg = "连接成功";
                                linkStatusChange(false, 0, msg);
                                Log.d("ljp","连接成功2");
//                                checkLink();
                            } catch (Exception e2) {
                                try {
                                    String msg = "连接失败";
                                    linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                    
                                    if (socket != null) {
                                        socket.close();
                                        socket = null;
                                    }
                                    return;
                                } catch (IOException ee) {
                                    String msg = "连接失败";
                                    linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                    return;
                                }
                            }

                        }
                        
                        try {
                            Log.d("ljp","进入1");
                            is = socket.getInputStream();   //得到蓝牙数据输入流
                            mOut = new DataOutputStream(socket.getOutputStream());

                            byte[] data = new byte[1024];
                            int length;
                            while (true) { //死循环读取
                                try {
                                    Log.d("ljp","进入2");
                                    //跳出死循环
                                    length = is.read(data);
                                    byte[] read = new byte[length];
                                    System.arraycopy(data, 0, read, 0, length);
                                    Log.d("ljp","read:"+ ConvertData.bytesToHexString(read,true));
                                    if(onBtSocketResponseListener!=null) {
                                        onBtSocketResponseListener.onResponse(ConvertData.bytesToHexString(read, true));
                                    }else{
                                        Log.d("ljp","收到消息回调接口为null");
                                    }
                                } catch (IOException e) {
                                    Log.d("ljp","SPPLINKUtil loop read io exception:"+e.getMessage());
                                    break;
                                }

                            }

                        } catch (IOException e) {
                            String msg = "数据接收失败";
                            linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                            return;
                        }
                        
//                        if (!alreadyThread) {
//                            if(readThread!= null) {
//                                readThread.start();
//                                alreadyThread = true;
//                            }
//                        } else {
//                            bRun = true;
//                        }
                    }
                }).start();
            }
        }, 1000);
    }
    //TODO 增加直接连接
    public void connectImmediately(Context mContext,final String macAddress){

        if(openBt(mContext)) {
//        if(bluetooth == null) {
//            bluetooth = BluetoothAdapter.getDefaultAdapter();
//        }
//        if (bluetooth.isDiscovering()) {
//            bluetooth.cancelDiscovery();
//        }
            //故意延迟1秒执行，因为此处会卡
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    device = bluetooth.getRemoteDevice(macAddress);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //开始连接
                            Log.d("ljp", "进入SPPLinkUtil timer schedule.");
                            try {
                                //socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                                int sdk = Build.VERSION.SDK_INT;
                                if (sdk >= 10) {
                                    socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                                } else {
                                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                                }
                            } catch (IOException e) {
                                String msg = "创建socket失败";
                                linkStatusChange(true, ERROR_CONNECTED, msg);
                                return;
                            }

                            try {
                                socket.connect();
                                String msg = "连接成功";
                                linkStatusChange(false, 0, msg);
//                            checkLink();
                            } catch (IOException e) {
                                //e.printStackTrace();
                                try {
                                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                    socket = (BluetoothSocket) m.invoke(device, 1);
                                    socket.connect();

                                    String msg = "连接成功";
                                    linkStatusChange(false, 0, msg);
//                                checkLink();
                                } catch (Exception e2) {
                                    try {
                                        String msg = "连接失败";
                                        linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                        if (socket != null) {
                                            socket.close();
                                            socket = null;
                                        }
                                        return;
                                    } catch (IOException ee) {
                                        String msg = "连接失败";
                                        linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                    }
                                    return;
                                }
                            }

                            try {
                                is = socket.getInputStream();   //得到蓝牙数据输入流
                                mOut = new DataOutputStream(socket.getOutputStream());

                                byte[] data = new byte[1024];
                                int length;
                                while (true) { //死循环读取
                                    try {
                                        //跳出死循环
                                        length = is.read(data);
                                        byte[] read = new byte[length];
                                        System.arraycopy(data, 0, read, 0, length);
                                        Log.d("ljp", "read:" + ConvertData.bytesToHexString(read, true));
                                        if (onBtSocketResponseListener != null) {
                                            onBtSocketResponseListener.onResponse(ConvertData.bytesToHexString(read, true));
                                        } else {
                                            Log.d("ljp", "收到消息回调接口为null");
                                        }
                                    } catch (IOException e) {
                                        Log.d("ljp", "SPPLINKUtil loop read io exception:" + e.getMessage());
                                        break;
                                    }

                                }

                            } catch (IOException e) {
                                Log.d("ljp","数据接收失败");
                                String msg = "数据接收失败";
                                linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                return;
                            }
//
//                        if (!alreadyThread) {
//                            if(readThread!= null) {
//                                readThread.start();
//                                alreadyThread = true;
//                            }
//                        } else {
//                            bRun = true;
//                        }
                        }
                    }).start();
                }
            }, 1000);
        }
    }
    /**
     * 发送文件
     */
//    public void sendFile(final String filePath) {
////        if (isSending) {
////            return;
////        }
////        isSending = true;
//        EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    FileInputStream in = new FileInputStream(filePath);
//                    File file = new File(filePath);
////                    mOut.writeInt(FLAG_FILE); //文件标记
////                    mOut.writeUTF(file.getName()); //文件名
////                    mOut.writeLong(file.length()); //文件长度
//
//                    Log.d("ljp","file length:"+file.length()+"");
//                    Log.d("ljp","inputstream length:"+in.available()+"");
//                    //得到分包总包数
//                    int count = in.available()%512;
//                    int currentNum = 1;
//                    int r;
//                    byte[] b = new byte[512];
//                    //TODO 文件开始发送
////                    notifyUI(Listener.MSG, "正在发送文件(" + filePath + "),请稍后...");
//                    while ((r = in.read(b)) != -1) {
//                        try {
//                            String replaceStr = ConvertData.bytesToHexString(b,false);
//
//                            StringBuilder resultStr = new StringBuilder();
//                            StringBuilder contentStr = new StringBuilder();
//
//                            contentStr.append(ConvertData.intToHexStringLe(ConvertData.hexStringToBytes(replaceStr).length + 8)).append("11").append(ConvertData.intToHexStringLe(count))
//                                    .append(ConvertData.intToHexStringLe(currentNum))
//                                    .append(replaceStr);
//
//                            resultStr.append("7f").append(ConvertData.specialCharReplace(contentStr.toString())).append(ConvertData.makeChecksum(contentStr.toString())).append("f7");
//                            currentNum++;
//                            Log.d("byteTag", "升级包数据：" + resultStr);
//                            //TODO  先不发送把消息加到队列中然后调用发送消息的函数进行发送，不对文件进行发送的直接操作，当普通消息进行处理
////                            mOut.write(ConvertData.hexStringToBytes(resultStr.toString()), 0, r);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
////                    mOut.flush();
//                    //TODO 文件发送完成
////                    notifyUI(Listener.MSG, "文件发送完成.");
//                } catch (Throwable e) {
//                    //TODO 发送异常处理
////                    close();
//                }
////                isSending = false;
//            }
//        });
//    }
    //TODO 由于硬件端无法解析 0 所以先取消
//    private Timer checkTimer;
//
//    private void checkLink() {
//        if (checkTimer != null) checkTimer.cancel();
//        checkTimer = new Timer();
//        checkTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!bluetooth.isEnabled()) {
//                    Log.d("ljp","checkLink 1");
//                    doBreakLink();
//                    checkTimer.cancel();
//                } else {
//                    if (socket !=null && !socket.isConnected()) {
//                        Log.d("ljp","checkLink 2");
//                        doBreakLink();
//                        checkTimer.cancel();
//                    } else {
//                        try {
//                            byte buffer = 0;
////                            Log.d("ljp","socket getoutputstream:"+socket.getOutputStream());
//                            OutputStream os = socket.getOutputStream();   //蓝牙连接输出流
//                            os.write(buffer);
//                            os.flush();
//                        } catch (Exception e) {
//                            Log.d("ljp","checkLink 3");
//                            doBreakLink();
//                            checkTimer.cancel();
//                        }
//                    }
//                }
//            }
//        }, 500, 500);
//    }
//
    public void doBreakLink() {
        String msg = "连接中断";
        linkStatusChange(true, ERROR_BREAK, msg);
        close(context);
    }
    
    private StringBuffer resultMsgCache = new StringBuffer();
    
    //接收数据线程
    Thread readThread = new Thread() {

        public void run() {
            try {
                while (true) {
                    if (!alreadyThread)
                        return;

                    if (is != null) {
                        byte[] buffer = new byte[128];
                        int count = is.read(buffer);
                        String temp = new String(buffer, 0, count, "utf-8");
                        if (cleanFlag){
                            cleanFlag = false;
                            log("cleanFlag");
                            resultMsgCache = new StringBuffer();
                        }
                        resultMsgCache.append(temp);
                        log(count + "," + resultMsgCache);
                        if (readEndStr != null) {
                            if (resultMsgCache.toString().endsWith(readEndStr)) {
                                btSocketResponse();
                            }
                        } else {
                            if (resultMsgCache.toString().endsWith("\n") || resultMsgCache.toString().endsWith("\r") || resultMsgCache.toString().endsWith("\r\n") || resultMsgCache.toString().endsWith("\n\r")) {
                                btSocketResponse();
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    };
    
    private boolean cleanFlag = false;
    
    private void btSocketResponse() {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (btLinkReport != null)
                        btLinkReport.onGetData(resultMsgCache.toString());
                    if (onBtSocketResponseListener != null)
                        onBtSocketResponseListener.onResponse(resultMsgCache.toString());
                
                }
            });
        } else {
            if (btLinkReport != null)
                btLinkReport.onGetData(resultMsgCache.toString());
            if (onBtSocketResponseListener != null)
                onBtSocketResponseListener.onResponse(resultMsgCache.toString());
        }
        cleanFlag = true;
    }
    
//    public void send(String text) {
//        int i = 0;
//        int n = 0;
//        if (socket == null) {
//            String msg = "未连接";
//            linkStatusChange(true, ERROR_NOT_CONNECTED, msg);
//            return;
//        }
//        try {
//            OutputStream os = socket.getOutputStream();   //蓝牙连接输出流
//            byte[] bos = text.getBytes();
//            for (i = 0; i < bos.length; i++) {
//                if (bos[i] == 0x0a) n++;
//            }
//            byte[] bos_new = new byte[bos.length + n];
//            n = 0;
//            for (i = 0; i < bos.length; i++) { //手机中换行为0a,将其改为0d 0a后再发送
//                if (bos[i] == 0x0a) {
//                    bos_new[n] = 0x0d;
//                    n++;
//                    bos_new[n] = 0x0a;
//                } else {
//                    bos_new[n] = bos[i];
//                }
//                n++;
//            }
//
//            os.write(bos);
//            log(">>>send:" + text);
//            Log.d("ljp","发送消息："+text);
//        } catch (IOException e) {
//            if (DEBUGMODE) e.printStackTrace();
//        }
//    }
    public void sendMsg(byte[]  text) {
//        if (isSending) {
//            return;
//        }
//        isSending = true;
        if (socket == null) {
            String msg = "未连接";
            linkStatusChange(true, ERROR_NOT_CONNECTED, msg);
            return;
        }
        try {
            if(mOut == null) {
//                DataOutputStream os = new DataOutputStream(socket.getOutputStream());   //蓝牙连接输出流
                mOut = new DataOutputStream(socket.getOutputStream());   //蓝牙连接输出流
            }
            mOut.write(text);
            mOut.flush();
            log(">>>send:" + text);
            Log.d("dataSendMsg","发送消息："+ConvertData.bytesToHexString(text,false));
        } catch (IOException e) {
            if (DEBUGMODE) e.printStackTrace();
        }
//        isSending = false;
    }
    private boolean openBt(Context context) {
        bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
        if (bluetooth == null) {
            loge("无法打开蓝牙");
            if (onLinkStatusChangeListener != null)
                onLinkStatusChangeListener.onFailed(ERROR_START_BT);
            return false;
        }
        //启动蓝牙
        if (bluetooth.isEnabled() == false) {
            bluetooth.enable();
        }
        return true;
    }
    
    public SPPLinkUtil setBtLinkReport(BtLinkReport btLinkReport) {
        this.btLinkReport = btLinkReport;
        return this;
    }
    
    public SPPLinkUtil setOnLinkStatusChangeListener(OnLinkStatusChangeListener onLinkStatusChangeListener) {
        this.onLinkStatusChangeListener = onLinkStatusChangeListener;
        return this;
    }
    
    public SPPLinkUtil setOnBtSocketResponseListener(OnBtSocketResponseListener onBtSocketResponseListener) {
        this.onBtSocketResponseListener = onBtSocketResponseListener;
        return this;
    }
    public SPPLinkUtil setOnDevFindListener(OnDevFindListener onLinkStatusChangeListener) {
        this.onDevFindLIstener = onLinkStatusChangeListener;
        return this;
    }
    public SPPLinkUtil setOnBleStatusChangeListener(OnBLEStatusChangeListener listener){
        this.onBLEStatusChangeListener = listener;
        return this;
    }
    public SPPLinkUtil setRssiTemp(RssiTemp a){
        this.rssiTemp = a;
        return this;
    }
    public SPPLinkUtil setUUID(String UUID) {
        this.UUIDStr = UUID;
        return this;
    }
    
    public SPPLinkUtil setReadEndStr(String readEndStr) {
        this.readEndStr = readEndStr;
        return this;
    }
    
    public static void setBtPairingCode(String btPairingCode) {
        SPPLinkUtil.btPairingCode = btPairingCode;
    }
    
    public void close(Context context) {
        if (context == null) return;
        if (linkTimer != null) linkTimer.cancel();
//        if (checkTimer != null) checkTimer.cancel();
//        try {
//            context.unregisterReceiver(mReceiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        bRun = false;
        
        try {
            if (readThread != null) {
                readThread.interrupt();
                readThread = null;
                alreadyThread = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (is != null) {
                //TODO 一直连接不上试一下
                is.close();
                is = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (socket != null) {
                //TODO 一直连接不上试一下
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       //TODO 断开连接的时候不关闭蓝牙
//        try {
//            if (bluetooth != null) {
//                bluetooth.cancelDiscovery();
//                bluetooth.disable();
//                bluetooth = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        try {
            fmsg = "";
            
            isFinded = false;
            btLinkReport = null;
            device = null;
            btName = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice) throws Exception {
        if (btPairingCode == null || btPairingCode.isEmpty()) {
            return false;
        }
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(
                    btDevice,
                    new Object[]
                            {btPairingCode.getBytes()}
            );
            Log.e("returnValue111", "" + returnValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
        
    }
    
    private void log(String msg) {
        if (DEBUGMODE) Log.i(">>>", "BTLinkUtil:" + msg);
    }
    
    private static void loge(String msg) {
        if (DEBUGMODE) Log.e(">>>", "BTLinkUtil:" + msg);
    }
    
    private void linkStatusChange(final boolean isFailed, final int errorId, final String msg) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loge(msg);
                    if (isFailed) {
                        if (btLinkReport != null) btLinkReport.onError(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onFailed(errorId);
                    } else {
                        if (btLinkReport != null)
                            btLinkReport.onStatusChange(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onSuccess();
                    }
                }
            });
        } else {
            loge(msg);
            if (isFailed) {
                if (btLinkReport != null) btLinkReport.onError(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onFailed(errorId);
            } else {
                if (btLinkReport != null)
                    btLinkReport.onStatusChange(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onSuccess();
            }
        }
    }
}
