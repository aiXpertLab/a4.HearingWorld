package com.reapex.sv.event;

public class GlassesShowEvent {
    private String message;

    public GlassesShowEvent(String string){
        message = string;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
