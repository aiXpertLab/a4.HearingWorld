package com.kongzue.btutil.interfaces;

import android.bluetooth.BluetoothDevice;

public interface OnDevFindListener {
    void onDeviceFind(BluetoothDevice dev);
    void onDeviceFindFinish();
    void onNoDesDeviceFind();
}
