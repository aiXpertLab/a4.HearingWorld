package seeingvoice.jskj.com.seeingvoice.blueTest;

import android.bluetooth.BluetoothDevice;

/**
 * Date:2018/12/19
 * Time:15:01
 * auther:zyy  监听蓝牙广播接收者
 */
public interface OnBluetoothListener {
    void bluetoothStateOn();
    void bluetoothStateOff();
    void deviceFound(BluetoothDevice bluetoothDevice);//发现设备
//    void deviceBonding(BluetoothDevice bluetoothDevice);
    void deviceBonded(BluetoothDevice bluetoothDevice);//设备配对
    void deviceBondNone(BluetoothDevice bluetoothDevice);//设备配对
    void deviceA2dpConned(BluetoothDevice bluetoothDevice);
    void deviceA2dpDisConned(BluetoothDevice bluetoothDevice);
    void deviceHeadsetConned(BluetoothDevice bluetoothDevice);
    void deviceHeadsetDisConned(BluetoothDevice bluetoothDevice);
    void deviceHeadsetBatteryLevel(int Procentlevel);//0-100之间
//    void deviceBondNone(BluetoothDevice bluetoothDevice);
//    void deviceConnected(BluetoothDevice bluetoothDevice);
//    void deviceDisConnected(BluetoothDevice bluetoothDevice);
//    void noDeviceDiscovered();
}
