package com.reapex.sv.event;

import android.bluetooth.BluetoothDevice;

public class GlassesConnectEvent {

    private BluetoothDevice device;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    //    private String message1;
//    private String message2;
//    private boolean isOpen;

//    public boolean isOpen() {
//        return isOpen;
//    }
//
//    public void setOpen(boolean open) {
//        isOpen = open;
//    }
//
//    public String getMessage1() {
//        return message1;
//    }
//
//    public void setMessage1(String message1) {
//        this.message1 = message1;
//    }
//
//    public String getMessage2() {
//        return message2;
//    }
//
//    public void setMessage2(String message2) {
//        this.message2 = message2;
//    }

//    public GlassesConnectEvent(String string1,String string2,boolean open){
//
//        message1 = string1;
//        message2 = string2;
//        isOpen = open;
//    }
public GlassesConnectEvent(BluetoothDevice dev){

    device = dev;
}



}
