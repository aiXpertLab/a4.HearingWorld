package com.HYPech.SV8.event;

public class ConnectEvent {
    private boolean connect;

    public boolean getMessage() {
        return connect;
    }

    public void setMessage(boolean connect) {
        this.connect = connect;
    }



    public ConnectEvent(boolean str){
        connect = str;
    }


}
