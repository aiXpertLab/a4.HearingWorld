package com.hypech.SV3.event;

public class GlassesOrderEvent {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public GlassesOrderEvent(String str){
        message = str;
    }


}
