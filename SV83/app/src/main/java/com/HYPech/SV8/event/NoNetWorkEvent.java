package com.HYPech.SV8.event;

public class NoNetWorkEvent {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public NoNetWorkEvent(String str){
        message = str;
    }

}
