package com.HYPech.SV8.event;

public class GlassesInfoChangeShowEvent {


    private String type;
    private String message;

    public GlassesInfoChangeShowEvent(String type,String str){

        this.type = type;
        message = str;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
