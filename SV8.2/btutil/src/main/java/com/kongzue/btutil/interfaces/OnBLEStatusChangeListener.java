package com.kongzue.btutil.interfaces;

import android.bluetooth.BluetoothDevice;

public interface OnBLEStatusChangeListener {
    void onBleConnect();
    void onBleDisConnect();
    void onBleOpen();
    void onBleClosed();
}
