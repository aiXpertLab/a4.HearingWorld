package com.reapex.sv.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.util.Log;

import com.reapex.sv.App;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 客户端和服务端的基类，用于管理socket长连接
 */
public class BtBase {
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/";
    private static final int FLAG_MSG = 0;  //消息标记
    private static final int FLAG_FILE = 1; //文件标记

    private BluetoothSocket mSocket;
    private DataOutputStream mOut;
    private Listener mListener;
    private boolean isRead;
    private boolean isSending;

    BtBase(Listener listener) {
        mListener = listener;
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    void loopRead(BluetoothSocket socket) {
        mSocket = socket;
        try {
            if (!mSocket.isConnected())
                mSocket.connect();
            notifyUI(Listener.CONNECTED, mSocket.getRemoteDevice());
            mOut = new DataOutputStream(mSocket.getOutputStream());
            InputStream in = mSocket.getInputStream();
            isRead = true;
            byte[] data = new byte[1024];
            int length;
            while (isRead) { //死循环读取
                try {
                    length = in.read(data);
                    byte[] read = new byte[length];
                    System.arraycopy(data, 0, read, 0, length);
//                    Log.d("ljp","data:"+ ConvertData.bytesToHexString(data,true));

//                    mHandler.obtainMessage(BluetoothSerial.MESSAGE_READ, length, -1, read).sendToTarget();
                    notifyUI(Listener.MSG, ConvertData.bytesToHexString(read,true));
                    if(App.isDebug) {Log.d("ljp","BT Base read:"+ConvertData.bytesToHexString(read,true));}
                } catch (IOException e) {
//                    reconnect(); // Connection lost
//                    SPPService.this.start();
                    if(App.isDebug) {Log.d("ljp","BtBase loop read io exception:"+e.getMessage());}
                    break;
                }
//                switch (in.readInt()) {
//                    case FLAG_MSG: //读取短消息
//                        String msg = String.valueOf(in.readByte());


//                Log.d("ljp","utf:数据："+in.readUTF());
//                Log.d("ljp","16进制"+in.readByte());
//                        break;
//                    case FLAG_FILE: //读取文件
//                        Util.mkdirs(FILE_PATH);
//                        String fileName = in.readUTF(); //文件名
//                        long fileLen = in.readLong(); //文件长度
//                        // 读取文件内容
//                        long len = 0;
//                        int r;
//                        byte[] b = new byte[4 * 1024];
//                        FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);
//                        notifyUI(Listener.MSG, "正在接收文件(" + fileName + "),请稍后...");
//                        while ((r = in.read(b)) != -1) {
//                            out.write(b, 0, r);
//                            len += r;
//                            if (len >= fileLen)
//                                break;
//                        }
//                        notifyUI(Listener.MSG, "文件接收完成(存放在:" + FILE_PATH + ")");
//                        break;
//                }
            }
        } catch (Throwable e) {
            close();
            notifyUI(Listener.DISCONNECTED, null);
            if(App.isDebug) {Log.d("ljp","BtBase loop read out throwable :"+e.getMessage());}
        }
    }

    /**
     * 发送短消息
     */
    public void sendMsg(byte[] msg) {
        if (checkSend()) return;
        isSending = true;
        try {
//            mOut.writeInt(FLAG_MSG); //消息标记
//            mOut.writeUTF(msg);
            mOut.write(msg);
            mOut.flush();
//            notifyUI(Listener.MSG, "发送短消息：" + msg);
        } catch (Throwable e) {
            close();
        }
        isSending = false;
    }

    /**
     * 发送文件
     */
    public void sendFile(final String filePath) {
        if (checkSend()) return;
        isSending = true;
//        Util.EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    FileInputStream in = new FileInputStream(filePath);
//                    File file = new File(filePath);
//                    mOut.writeInt(FLAG_FILE); //文件标记
//                    mOut.writeUTF(file.getName()); //文件名
//                    mOut.writeLong(file.length()); //文件长度
//                    int r;
//                    byte[] b = new byte[4 * 1024];
//                    notifyUI(Listener.MSG, "正在发送文件(" + filePath + "),请稍后...");
//                    while ((r = in.read(b)) != -1)
//                        mOut.write(b, 0, r);
//                    mOut.flush();
//                    notifyUI(Listener.MSG, "文件发送完成.");
//                } catch (Throwable e) {
//                    close();
//                }
//                isSending = false;
//            }
//        });
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void unListener() {
        mListener = null;
    }

    /**
     * 关闭Socket连接
     */
    public void close() {
        try {
            isRead = false;
            if (null != mSocket) {
//                if (mSocket.getConnectionType()) {
                    mSocket.close();
//                }
            }
//            mSocket.close();
//            notifyUI(Listener.DISCONNECTED, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (mSocket != null && mSocket.isConnected());
        if (dev == null)
            return connected;
        return connected && mSocket.getRemoteDevice().equals(dev);
    }
    // ============================================通知UI===========================================================
    private boolean checkSend() {
        if (isSending) {
//            APP.toast("正在发送其它数据,请稍后再发...", 0);
            return true;
        }
        return false;
    }

    private void notifyUI(final int state, final Object obj) {
        App.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mListener != null)
                        mListener.socketNotify(state, obj);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Listener {
        int DISCONNECTED = 0;
        int CONNECTED = 1;
        int MSG = 2;

        void socketNotify(int state, Object obj);
    }
}
