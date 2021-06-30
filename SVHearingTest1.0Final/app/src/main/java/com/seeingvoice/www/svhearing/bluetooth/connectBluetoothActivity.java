//package com.seeingvoice.www.svheard.bluetooth;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattDescriptor;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothProfile;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanResult;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.seeingvoice.www.svheard.R;
//import com.seeingvoice.www.svheard.base.TopBarBaseActivity;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Date:2019/8/1
// * Time:14:21
// * auther:zyy
// */
//public class connectBluetoothActivity extends TopBarBaseActivity {
//    private BluetoothAdapter mBluetoothAdapter;//手机蓝牙适配器
//    private BluetoothLeScanner scanner;//蓝牙扫描器
//    private Handler mHandler = new Handler();//线程通信
//    private boolean mScanning;//蓝牙状态
//    private BluetoothManager bluetoothManager;
//    private ScanCallback scanCallback;
//    private static final String TAG = connectBluetoothActivity.class.getName();
//
//    final UUID UUID_SERVICE = UUID.fromString("5052494D-2DAB-0141-6972-6F6861424C45");//服务的UUID
//    //
//    //  设备特征值UUID, 需固件配合同时修改
//    //
//    final UUID UUID_READ = UUID.fromString("43484152-2DAB-1441-6972-6F6861424C45");   // 用于从设备读取数据
//    final UUID UUID_WRITE = UUID.fromString("0000AE01-0000-1000-8000-00805F9B34FB");  // 用于发送数据到设备
//    final UUID UUID_NOTIFICATION = UUID.fromString("0000AE02-0000-1000-8000-00805F9B34FB"); // 用于接收设备推送的数据
//
//
//    private BluetoothGatt mBluetoothGatt;//GATT协议接口
//    private TextView deviceName;
//    private TextView textView1;
//
//    private boolean isServiceConnected;
//    private BluetoothGattCallback mGattCallback;
//    private BluetoothDevice mDevice;
//
//    @Override
//    protected int getConentView() {
//        return R.layout.activity_connect_bluetooth;
//    }
//
//    @Override
//    protected void init(Bundle savedInstanceState) {
//        setTitle("连接蓝牙耳机");
//        setTitleBack(true);
//
//        setToolBarMenuOne("", R.mipmap.return_icon, null);
//        setToolBarMenuTwo("", R.mipmap.return_icon, null);
//        //显示设备名称，消息状态
//        deviceName = (TextView) findViewById(R.id.device_name);
//        textView1 = (TextView) findViewById(R.id.recieve_text);
//
//        getBlueAdapter();
//        scanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                super.onScanResult(callbackType, result);
//                final BluetoothDevice bluetoothDevice = result.getDevice();
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android 5以上
////                    BluetoothDevice device = result.getDevice();
////                    if (null != device)
////                    Log.e(TAG,device.getName());
////                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (bluetoothDevice != null && bluetoothDevice.getName() != null) {
//                            if (bluetoothDevice.getName().equals("SV-H1")) {
//                                deviceName.setText(bluetoothDevice.getName() + bluetoothDevice.getAddress());
//                                Log.e(TAG, "蓝牙设备==" + bluetoothDevice.getName() + bluetoothDevice.getAddress());
//                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
////                                scanner.stopScan(scanCallback);
//                            }
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onBatchScanResults(List<ScanResult> results) {
//                super.onBatchScanResults(results);
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//                super.onScanFailed(errorCode);
//                Log.e(TAG,"搜索失败");
//            }
//        };
//
//        mGattCallback = new BluetoothGattCallback(){//GATT接口对调抽象对象
//
//            /*监听GATT连接状态*/
//            @Override
//            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                super.onConnectionStateChange(gatt, status, newState);
//                Log.d(TAG, "onConnectionStateChange: " + newState);
//
//                if (status != BluetoothGatt.GATT_SUCCESS) {
//                    String err = "Cannot connect device with error status: " + status;
//                    gatt.close();
//                    if (mBluetoothGatt != null) {
//                        mBluetoothGatt.disconnect();
//                        mBluetoothGatt.close();
//                        mBluetoothGatt = null;
//                    }
//                    if (mDevice != null) {
//                        mBluetoothGatt = mDevice.connectGatt(connectBluetoothActivity.this, false, mGattCallback);
//                    }
//                    Log.e(TAG, err);
//                    return;
//                }
//                if (newState == BluetoothProfile.STATE_CONNECTED) {//当蓝牙设备已经连接
//                    //获取ble设备上面的服务
//                    //Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                    Log.i("haha", "Attempting to start service discovery:" +
//                            mBluetoothGatt.discoverServices());
//                    Log.d("haha", "onConnectionStateChange: " + "连接成功");
//                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//当设备无法连接
//                    if (mBluetoothGatt != null) {
//                        mBluetoothGatt.disconnect();
//                        mBluetoothGatt.close();
//                        mBluetoothGatt = null;
//                    }
//                    gatt.close();
//                    if (mDevice != null) {
//                        mBluetoothGatt = mDevice.connectGatt(connectBluetoothActivity.this, false, mGattCallback);
//                    }
//                }
//            }
//
//            /*发现服务回调*/
//            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                Log.d(TAG, "onServicesDiscovered: " + "发现服务 : " + status);
//                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    isServiceConnected = true;
//                    boolean serviceFound;
//                    Log.d(TAG, "onServicesDiscovered: " + "发现服务 : " + status);
//                    Log.d(TAG, "onServicesDiscovered: " + "读取数据0");
//
//                    if (mBluetoothGatt != null && isServiceConnected) {
//                        BluetoothGattService gattService = mBluetoothGatt.getService(UUID_SERVICE);
//                        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID_NOTIFICATION);
//                        boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
//                        if (b) {
//                            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
//                            for (BluetoothGattDescriptor descriptor : descriptors) {
//                                boolean b1 = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                                if (b1) {
//                                    mBluetoothGatt.writeDescriptor(descriptor);
//                                    Log.d(TAG, "startRead: " + "监听收数据");
//                                }
//                            }
//                        }
//                    }
//                    serviceFound = true;
//                }
//            }
//
//            /*我们读取特征值*/
//            @Override
//            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicRead(gatt, characteristic, status);
//                Log.d(TAG, "read value: " + bytes2HexString(characteristic.getValue()));
//
//                Log.d(TAG, "callback characteristic read status " + status
//                        + " in thread " + Thread.currentThread());
//                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    Log.d(TAG, "read value: " + characteristic.getValue());
//                }
//            }
//
//            @Override
//            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//                super.onDescriptorWrite(gatt, descriptor, status);
//                Log.d(TAG, "onDescriptorWrite: " + "设置成功");
//            }
//
//            @Override
//            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//                super.onDescriptorRead(gatt, descriptor, status);
//
//            }
//
//            @Override
//            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicWrite(gatt, characteristic, status);
//                Log.d(TAG, "onCharacteristicWrite: " + "发送成功");
//
//                boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
//                mBluetoothGatt.readCharacteristic(characteristic);
//            }
//
//            @Override
//            public final void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
//                byte[] value = characteristic.getValue();
//                Log.d(TAG, "onCharacteristicChanged: " + value);
//                String s0 = Integer.toHexString(value[0] & 0xFF);
//                String s = Integer.toHexString(value[1] & 0xFF);
//                Log.d(TAG, "onCharacteristicChanged: " + s0 + "、" + s);
////            textView1.setText("收到: " + s0 + "、" + s);
//                for (byte b : value) {
//                    Log.d(TAG, "onCharacteristicChanged: " + b);
//                }
//            }
//        };
//    }
//
//    /**
//     * 字节数组转16进制字符串
//     */
//    public static String bytes2HexString(byte[] array) {
//        StringBuilder builder = new StringBuilder();
//
//        for (byte b : array) {
//            String hex = Integer.toHexString(b & 0xFF);
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//            builder.append(hex);
//        }
//        return builder.toString().toUpperCase();
//    }
//
//    /**
//     * 得到蓝牙适配器对象
//     */
//    private void getBlueAdapter() {
//        //通过获取系统服务得到蓝牙管理者对象
//        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//        //隐式打开蓝牙
//        if (!mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();
//        }
////        scanner = mBluetoothAdapter.getBluetoothLeScanner();
//    }
//
//    final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//
////            Log.d(TAG, "onLeScan:  " + device.getName() + " : " + rssi);
//            String name = device.getName();
////            Log.d(TAG, "名称:  " + name + " : ");
//            if (name != null) {
//                if (name.contains("SV-H1")) {
//                    deviceName.setText(name);
//                    Log.e(TAG, "名称:  " + name + ",远程设备广告记录的内容"+bytes2HexString(scanRecord));
//                    mDevice = device;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//            }
//        }
//    };
//
//
//    private void scanBlueDevice(){
//        scanner = mBluetoothAdapter.getBluetoothLeScanner();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScanning = false;
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
////                scanner.stopScan(scanCallback);
//            }
//        },3000);
//        //手机蓝牙扫描30秒结束
//        mScanning = true;
//        // 定义一个回调接口供扫描结束处理
//        mBluetoothAdapter.startLeScan(mLeScanCallback);
////        scanner.startScan(scanCallback);
//    }
//
//    public void startScan(View view) {
//        getBlueAdapter();
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
////            scanner.stopScan(scanCallback);
//        }
//        scanBlueDevice();
//    }
//
//    public void startConnect(View view) {
//        if (mDevice != null) {
//            if (mBluetoothGatt != null) {
//                mBluetoothGatt.disconnect();
//                mBluetoothGatt.close();
//                mBluetoothGatt = null;
//            }
//            mBluetoothGatt = mDevice.connectGatt(connectBluetoothActivity.this, false, mGattCallback);
//        }
//    }
//
//    public void startRead(View view) {
//        if (mBluetoothGatt != null && isServiceConnected) {
//            BluetoothGattService gattService = mBluetoothGatt.getService(UUID_SERVICE);
//            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID_READ);
//            mBluetoothGatt.readCharacteristic(characteristic);
//            if ((characteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                Log.e(TAG,"characteristic属性为可读"+bytes2HexString(characteristic.getValue()));
//                deviceName.setText(bytes2HexString(characteristic.getValue()));
//            }
//        }
//    }
//
//
//    public void startSend(View view) {
//        if (mBluetoothGatt != null && isServiceConnected) {
//            BluetoothGattService gattService = mBluetoothGatt.getService(UUID_SERVICE);
//            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID_WRITE);
//            byte[] bytes = new byte[2];
//            bytes[0] = 04;
//            bytes[1] = 01;
//            characteristic.setValue(bytes);
//            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//            mBluetoothGatt.writeCharacteristic(characteristic);
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        stopConnect();
//        android.os.Process.killProcess(android.os.Process.myPid());
//
//    }
//
//    public void stopConnect() {
//        if (mBluetoothGatt != null) {
//            mBluetoothGatt.close();
//        }
//        if (mScanning) {
////            scanner.stopScan(scanCallback);
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mBluetoothAdapter = null;
//            mScanning = false;
//        }
//    }
//}
